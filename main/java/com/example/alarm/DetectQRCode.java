package com.example.alarm;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;

import java.util.Arrays;


public class DetectQRCode {

    boolean diag = false;
    private int[][] blackWhiteMapPicture;
    private int count, border, firstBorderBlack, secondBorderWhite, thirdBorderBlack, fourthBorderWhite, fifthBorderBlack, currLine, startX, startY, qrSize, tileSize;

    //(example qr code)
    //squares are 7*151x7*151pixels big (plus 3 outlines so 154), + 1 pixel black outline (1pixel border (grey)outtermost only horizontal has other gray tone than vertical (or might even be different on all sides)
    //all squares have outlines
    //(1px outline inbetween each, and outside)151pixel corner black, 151 px same shape, but inner & white, 3*151 x 3*151 px² square black
    //x 151 + outline fields inbetween corners, corners are up left, up right, down left, also have white outline 9x9 151px's, outside of white w/o outline
    // 8,85 megapixel at most per pic, better to downscale? I actually don't think, because if we downscale, we might aswell just scan the whole image first
    //how to work with rotation/camera position/perspective?


    public DetectQRCode() {
    }

    public int[][] getBlackWhiteMapPicture() {
        return blackWhiteMapPicture;
    }

    public int[] detect(Bitmap bitmap){
        int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()+bitmap.getHeight()];
        Arrays.fill(pixels,-1);
        bitmap.getPixels(pixels, 0, 1, 0, 0, bitmap.getWidth(), bitmap.getHeight()); //pixels is now set to packed ints represeting a Color, each row is separated with one entry that is left empty in between

        int[][] arr = new int[bitmap.getHeight()][];

        for(int i = 0; i < pixels.length; i++){ //populate arr with 1,0 for black/white pixels, -1 for colored
            int p = pixels[i];

            if(p == -1){

                arr[i/bitmap.getWidth()][i%bitmap.getWidth()] = -1;

            }else if (Color.red(p) >= 33 && Color.red(p) < 190 && Color.blue(p) > 30 && Color.blue(p) < 190 && Color.green(p) >= 33 && Color.green(p) < 190) {

                arr[i/bitmap.getWidth()][i%bitmap.getWidth()] = -1;

            }else{
                if(Color.red(p) < 33) {
                    arr[i / bitmap.getWidth()][i % bitmap.getWidth()] = 0;
                }else{
                    arr[i / bitmap.getWidth()][i % bitmap.getWidth()] = 1;
                }
            }
        }
        this.blackWhiteMapPicture = arr;
        //.setColorSpace(ColorSpace colorspace)
        //.getPixel(int x, int y) or .getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) , seconds gets an array of ints (stored to pixels[])...but how is this 2 dimensional, or how is the
        //picture being 2d displayed in 1d? separator? --> stride sets how many positions in the pixels array are skipped on reading in a new line
        //.getWidth, .get(Scaled?)Height()
        if(isQRInArr(arr)) {
            int[] o = new int[7];
            o[0] = 1; //(0/1) for if qr is in pic; (int) for X start reading coord; (int) for Y start reading coord; (int) for height of pic; (int) for width of pic; (int) for tile distance of qr pos markers (qr size essentially); (int) for num of pixels for one tile
            o[1] = this.startX;
            o[2] = this.startY;
            o[3] = bitmap.getHeight();
            o[4] = bitmap.getWidth();
            o[5] = this.qrSize;
            o[6] = this.tileSize;
            return o;
        }else{
            return new int[1];
        }

    }


    public boolean detect_horizontal(int[][] bitMap, int startstart, int start, int stopstop, int stop){ //startstart & start are starting indexes to search at for finding a qr code
        this.blackWhiteMapPicture = bitMap; //TODO: stopping functionality, aborting at (stopstop, stop) coord reached
        border = 0;
        count = 0;
        int lastChance = bitMap[bitMap.length/2].length/1000;

        for (int v = startstart; v < bitMap.length; v++) {

            if(v > stopstop){
                return false;
            }

            this.currLine = v;
            int[] bitM = bitMap[v];
            boolean endOfLine = false;
            boolean isThisTileSizeSet = false;

            for ( int w = start; w < bitM.length; w++) {

                if(w > stop){
                    return false;
                }

                int bit = bitM[w];
                boolean foundLine = bit == 0;

                if (!endOfLine) {

                    if (count < 40) {
                        count++;

                        if (!foundLine) {
                            lastChance --;

                            if (lastChance < 0){
                                count = 0;
                                lastChance = bitMap[v].length;
                            }
                        }


                    } else if (foundLine) {
                        count++;


                    } else {
                        if (!isThisTileSizeSet){
                            this.tileSize = count;
                        }

                        lastChance--;

                        if(lastChance < 0){
                        endOfLine = true;
                        border = count;
                        count = 0;
                        }

                        lastChance = bitMap[v].length;

                    }


                } else {

                    this.firstBorderBlack = border;

                    if(        nextPixel(1, v, w,1,1)
                            && nextPixel(0, v, w+border,3,2)
                            && nextPixel(1, v, w + border + this.secondBorderWhite,1 , 3)
                            && nextPixel(0, v, w + border + this.secondBorderWhite + this.thirdBorderBlack,1 , 4)
                            && nextPixel(1, v, w + border + this.secondBorderWhite + this.thirdBorderBlack + this.fourthBorderWhite,1 , 5)){

                        return true;

                        }else{
                            count = 0;
                            border = 0;
                            endOfLine = false;
                        }

                    }

                }
            }
        return false;
    }

    private boolean nextPixel(int zo, int ii, int i, int factor, int ith) {


        int[] bitMap = this.blackWhiteMapPicture[ii];
        boolean isNext = true;
        int b = this.border;
        if(b*factor+i + 2*factor > bitMap.length){
            return false;
        }

        for(int g = i; g < i + b*factor + 2*factor; g++) {

            isNext = bitMap[g] == zo;

            if (g <= 2*factor) {

                if (!isNext){
                    if(ith == 1){
                    this.secondBorderWhite = g;}
                    else if(ith == 2){
                        this.thirdBorderBlack = g;
                    } else if (ith == 3) {
                        this.fourthBorderWhite = g;
                    }else{
                        this.fifthBorderBlack = g;
                    }
                    return true;
                }

            }else{
                if(isNext){
                    return false;
                }
            }
        }
        return false;
    }

    public boolean detect_vertical(int[][] bitMap, int startstart, int start){ //TODO make sure every call of this function gets the right coords, so this is also never called before horizontal
        int[][] newBitMap = new int[bitMap.length][bitMap[0].length];
        for(int i = 0; i < bitMap.length; i++){
            for (int ii = 0; ii < bitMap[i].length; ii++){
                newBitMap[ii][i] = bitMap[i][ii]; //this should flip the bitMap by 90 degree right?
            }
        }
        return detect_horizontal(newBitMap, startstart, start, newBitMap.length, newBitMap[0].length);
    }


    private boolean detect_diags(int[][]bitMap, int startstart, int start){

        //flip the array by 45°
        float fall = (float)bitMap.length / (float) bitMap[0].length;

        int[][] diag_arr;

        int[][] bitMapNew = new int[bitMap[0].length][bitMap.length];
        for (int k = 0; k < bitMap.length; k++){
            for(int l = 0; l < bitMap[0].length; l++){
                bitMapNew[k][l] = bitMap[l][k];
            }
        }

        if((float) (int) fall == fall){ //if fall is smaller than 1 (array is wider than tall, then an int fall will only occur, if the array doesn't have elements in it, so this case is irrelevant here)
            diag_arr = mk_diag_arr(bitMap.length, (int) fall);
            int[][][] divided_fall = new int[bitMapNew.length][bitMapNew.length/(int)fall][(int) fall];
            int fall_c = 0;
            int pos = 0;

            for(int m = 0; m < bitMapNew.length; m++){
                for(int n = 0; n < bitMapNew[0].length; n++){
                    if(fall_c > fall){
                        fall_c = 0;
                        pos++;
                    }
                    divided_fall[m][pos][fall_c] = bitMapNew[m][n];
                    fall_c++;
                }
            }



            for(int a = 0; a < divided_fall.length; a++){
                for(int b = 0; b < divided_fall[0].length; b++){
                    for(int c = 0; c < fall; c++){
                        diag_arr[indexX((a* divided_fall[0].length+b*(int) fall+c)/(int)fall)][indexY((a* divided_fall[0].length+b*(int) fall+c)/(int)fall)] = divided_fall[a][b][c];
                    }
                }
            }


        }
        else {

            if(fall < 1.0){
                int[][] new_arr = new int[bitMap.length][bitMap[0].length];
                for(int i = 0; i < bitMap.length; i++){

                    for(int j = 0; j < bitMap[0].length; j++){

                        new_arr[i][j] = bitMap[j][i];
                    }
                }
                bitMap = new_arr;
            }

            int maxDiag = bitMap[0].length;
            diag_arr = mk_uneven_diag_arr(maxDiag, bitMap.length);

            int c = 0;
            for(int i = bitMap.length-1; i > bitMap.length-maxDiag; i--){
                int z = 0;
                for(int h = i; h < bitMap.length; h++){
                    diag_arr[c][z] = bitMap[h][z];
                    z++;
                }
                c++;
            }

            for(int i = bitMap.length-maxDiag-1; i > 0; i++){ //TODO i think there is a bug here, this is not setting the diag_arr correctly i think

                for(int g = 0; g < maxDiag; g++){
                    diag_arr[c][g] = bitMap[i][g];
                }
                c++;
            }

            for(int i = 0; i < bitMap[0].length; i++){

                for(int h = 0; h < bitMap[0].length-i; h++){
                    diag_arr[c][h] = bitMap[h][i];
                }
                c++;

            }

        }


        if (detect_horizontal( diag_arr, startstart, start, diag_arr.length, diag_arr[diag_arr.length/2].length)){
            if(detect_vertical(diag_arr, startstart, start)){
                this.diag = true;
                return true;
            }

        }
        return false;
    }

    private int[][] mk_uneven_diag_arr(int maxDiag, int len) {


        int[][] arr = new int[maxDiag + len-1][]; //maxDiag + len - 1 - maxDiag - 1 +maxDiag = maxDiag + len-1-1 as length of arr of arrs


        for(int i = 0; i < maxDiag; i++){
            arr[i] = new int[i+1];
        }


        for(int i = 0; i < len-maxDiag; i++){
            arr[i+maxDiag] = new int[maxDiag];
        }

        int counter = len-1;

        for(int i = maxDiag; i > 0; i-- ){
            arr[counter] = new int[i];
            counter++;
        }
        return arr;
    }




    private int indexY(int d) {

        int count = 0;
        if(d == 2){
            return 1;
        }
        for(int i = 0; i < d; i++){
            for (int j = 0; j<i;j++){
                if(count == d){
                    return j;
                }
                count++;
            }
        }
        return 0;
    }

    private int indexX(int d) {
        if(d==0){
            return 0;
        }
        return binlog(d);
    }

    public static int binlog( int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    private int[][] mk_diag_arr(int len, int fall) {
        int[][] arr = new int[len][];

        for(int i = 0; i < len/2; i++){
            arr[i] = new int[(i+1)*fall];
        }

        for(int i = (len/2)+1; i < len ; i++){
            arr[i] = new int[(len-i+1)*fall];
        }

        return arr;
    }

    private boolean isQRInArr(int[][] bitMap){

        if(detect_horizontal(bitMap,0,0, bitMap.length, bitMap[0].length)){
            int middleX,startstart,start,dist;
            middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack)/2;
            dist = this.secondBorderWhite - this.firstBorderBlack;
            startstart = firstBorderBlack-dist;
            start = middleX -3*dist - (dist*3)/2;

            if(detect_vertical(bitMap,startstart,start)){

                for(int i = 5; i < 161; i+=4) {

                    this.qrSize = i;
                    //first marker found yay :D
                    boolean up, down, left, right;
                    up = detect_horizontal(bitMap, startstart + dist * i, start, start + dist*(i+1), bitMap[bitMap.length/2].length);
                    down = detect_horizontal(bitMap, startstart - dist * i, start,start - dist*(i+1), bitMap[bitMap.length/2].length);
                    left = detect_horizontal(bitMap, startstart, start - dist * i, bitMap.length, startstart - dist*(i+1));
                    right = detect_horizontal(bitMap, startstart, start + dist * i, bitMap.length, startstart + dist*(i+1));
                    //TODO: detect_horizontal edit, so it can stop at specific coords, so it doesnt find the same marker twice

                    if (up && left) { //for efficiency reasons, i will only scan horizontally after the first marker was verified

                        //qr code verified, set read-starting coords and return true
                        detect_horizontal(bitMap, startstart + dist * i, start, start + dist*(i+1), bitMap[bitMap.length/2].length); //this sets the borders again
                        middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                        start = middleX - 3 * dist - (dist * 3) / 2;   //X
                        this.startX = start + dist * (i + 7);
                        detect_horizontal(bitMap, startstart, start - dist * i, bitMap.length, startstart - dist*(i+1));
                        startstart = firstBorderBlack - dist;     //Y
                        this.startY = startstart - dist * (i + 7);    //TODO: check if this actually holds true for the correct coords
                        return true;

                    } else if (up & right) {

                        //qr code verified, set read-starting coords and return true
                        detect_horizontal(bitMap, startstart + dist * i, start, start + dist*(i+1), bitMap[bitMap.length/2].length); //this sets the borders again
                        middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                        start = middleX - 3 * dist - (dist * 3) / 2;   //X
                        this.startX = start + dist * (i + 7);
                        detect_horizontal(bitMap, startstart, start + dist * i, bitMap.length, startstart + dist*(i+1));
                        startstart = firstBorderBlack - dist;     //Y
                        this.startY = startstart + dist * (i + 7);
                        return true;

                    } else if (left & down) {

                        //qr code verified, set read-starting coords and return true
                        detect_horizontal(bitMap, startstart - dist * i, start,start - dist*(i+1), bitMap[bitMap.length/2].length); //this sets the borders again
                        middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                        start = middleX - 3 * dist - (dist * 3) / 2;   //X
                        this.startX = start - dist * (i + 7);
                        detect_horizontal(bitMap, startstart, start - dist * i, bitMap.length, startstart - dist*(i+1));
                        startstart = firstBorderBlack - dist;     //Y
                        this.startY = startstart - dist * (i + 7);
                        return true;

                    } else if (right & down) {
                        //qr code verified, set read-starting coords and return true
                        detect_horizontal(bitMap, startstart - dist * i, start,start - dist*(i+1), bitMap[bitMap.length/2].length); //this sets the borders again
                        middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                        start = middleX - 3 * dist - (dist * 3) / 2;   //X
                        this.startX = start - dist * (i + 7);
                        detect_horizontal(bitMap, startstart, start + dist * i, bitMap.length, startstart + dist*(i+1));
                        startstart = firstBorderBlack - dist;     //Y
                        this.startY = startstart + dist * (i + 7);
                        return true;
                        //this should match any case, in that a qr code is findable and set the coords according
                    }

                }
            }

                //if all this didn't find a qr code, then try diagonal
        }else{
            //check diags
            if(detect_diags(bitMap,0,0)){

                int middleX,startstart,start,dist;
                middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack)/2;
                dist = this.secondBorderWhite - this.firstBorderBlack;
                startstart = firstBorderBlack-dist;
                start = middleX -3*dist - (dist*3)/2;

                if(detect_diags(flipBitMap(bitMap), startstart, start)){
                    for(int i = 2; i < 80; i+=2) {

                        this.qrSize = i;
                        //first marker found yay :D
                        boolean up, down, left, right;
                        up = detect_horizontal(bitMap, startstart + dist * i, start); //TODO: make detect_horizontal instantly return false, if any of the position int args are smaller or greater than bitMap size
                        down = detect_horizontal(bitMap, startstart - dist * i, start);
                        left = detect_horizontal(bitMap, startstart, start - dist * i);
                        right = detect_horizontal(bitMap, startstart, start + dist * i); //TODO: detect_horizontal edit, so it can stop at specific coords, so it doesnt find the same marker twice

                        if (up && left) { //for efficiency reasons, i will only scan horizontally after the first marker was verified

                            //qr code verified, set read-starting coords and return true
                            detect_horizontal(bitMap, startstart + dist * i, start); //this sets the borders again
                            middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                            start = middleX - 3 * dist - (dist * 3) / 2;   //X
                            this.startX = start + dist * (i + 7);
                            detect_horizontal(bitMap, startstart, start - dist * i);
                            startstart = firstBorderBlack - dist;     //Y
                            this.startY = startstart - dist * (i + 7);    //TODO: check if this actually holds true for the correct coords
                            return true;

                        } else if (up & right) {

                            //qr code verified, set read-starting coords and return true
                            detect_horizontal(bitMap, startstart + dist * i, start); //this sets the borders again
                            middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                            start = middleX - 3 * dist - (dist * 3) / 2;   //X
                            this.startX = start + dist * (i + 7);
                            detect_horizontal(bitMap, startstart, start + dist * i);
                            startstart = firstBorderBlack - dist;     //Y
                            this.startY = startstart + dist * (i + 7);
                            return true;

                        } else if (left & down) {

                            //qr code verified, set read-starting coords and return true
                            detect_horizontal(bitMap, startstart - dist * i, start); //this sets the borders again
                            middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                            start = middleX - 3 * dist - (dist * 3) / 2;   //X
                            this.startX = start - dist * (i + 7);
                            detect_horizontal(bitMap, startstart, start - dist * i);
                            startstart = firstBorderBlack - dist;     //Y
                            this.startY = startstart - dist * (i + 7);
                            return true;

                        } else if (right & down) {
                            //qr code verified, set read-starting coords and return true
                            detect_horizontal(bitMap, startstart - dist * i, start); //this sets the borders again
                            middleX = this.thirdBorderBlack + (this.fourthBorderWhite - this.thirdBorderBlack) / 2;
                            start = middleX - 3 * dist - (dist * 3) / 2;   //X
                            this.startX = start - dist * (i + 7);
                            detect_horizontal(bitMap, startstart, start + dist * i);
                            startstart = firstBorderBlack - dist;     //Y
                            this.startY = startstart + dist * (i + 7);
                            return true;
                            //this should match any case, in that a qr code is findable and set the coords according
                        }
                    }
                }
            }
        }

        //diagonal + orthogonal failed, there is no qr code. return false
        return false;

    }

    private int[][] flipBitMap(int[][] bitMap){
        int[][] arr = new int[bitMap[bitMap.length/2].length][];
        for(int i = 0; i < bitMap.length; i++){
            for(int j = 0; j < bitMap[i].length; j++){
                arr[i][j] = bitMap[j][i];
            }
        }
        return arr;
    }


}






/*
length - maxDiag until 0, then in row 1

[01,02,03,04],
[05,06,07,08],
[09,10,11,12],
[13,14,15,16],
[17,18,19,20],
[21,22,23,24],
[25,26,27,28]

--> fall = 1.75


[
[01,02,03,04],
[05,06,07,08],
[09,10,11,12],
[13,14,15,16],
[17,18,19,20],
[21,22,23,24],
[25,26,27,28],
[29,30,31,32]
] (8) Listen


flip 90°
divide into arrays of length fall-1, append to row-th + pos-th array of arrays




for (x=0;x<ll;x++):
    for(y=l;y>0;y--):
        arr[x][y] = flip array split by fallth index, append to array in (y/fall)+xth row





arr.length := l
arr[0].length := ll

x = arr.length
for(y = 0, x > 0 ,y++):
    arr[][] = (x-fall-y, y)


arr[x][y] = valueOfX(m,b) ==> b= ll-fall*try






(l-fall,0), (l-fall-1,0) , ... (l-1,0)
(l-2*fall,0), (l-2*fall -1,0), ... (l-fall-1,0), (l-fall,1), (l-fall-1,1) , ... (l-1,1)
...
(0,0), (1,0), ..., (fall-1,0), (fall,1), (fall+1,1), ..., (2*fall -1,1), ..., (...), ...(l-1,ll-1)

--> flip the arr by 180°
do the indexes from above again exept the one with (0,0)
flip the resulting arr + append to the one from the first steps

--> or
(1,0), (1,1), ..., (1,fall-1), (2,fall), ... (2,2*fall-1), ..., (...), ...(l-fall-1,ll)
...
(ll-2,0), (ll-2,1), ..., (ll-2,fall-1), (ll-1, fall), ..., (ll-1, fall*2-1)
(ll-1,0), (ll-1,1), ..., (ll-1, fall-1)






[
[25,29],
[17,21,26,30],
[09,13,18,22,27,31],
[01,05,10,14,19,23,28,32],
[02,06,11,15,20,24],
[03,07,12,16],
[04,08],
] (7) Listen


Steigung m = delta y / delta x

dabei ist delta z = koord am b - koord am a Punkt

also für 1. 2d array:

m = -(arr.length)/(arr[0].length)
if m = y,x... , then one round with round up, one with down, but make sure it is fitting

 */























