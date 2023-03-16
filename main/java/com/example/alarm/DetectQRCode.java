package com.example.alarm;

import java.util.Arrays;

public class DetectQRCode {

    boolean diag = false;
    private int[][] blackWhiteMapPicture;
    private int count, border, resolutionX, resolutionY, firstBorderBlack, secondBorderWhite, thirdBorderBlack, fourthBorderWhite, fifthBorderBlack, currLine;
    //squares are 7*151x7*151pixels big (plus 3 outlines so 154), + 1 pixel black outline (1pixel border (grey)outtermost only horizontal has other gray tone than vertical (or might even be different on all sides)
    //all squares have outlines
    //(1px outline inbetween each, and outside)151pixel corner black, 151 px same shape, but inner & white, 3*151 x 3*151 px² square black
    //x 151 + outline fields inbetween corners, corners are up left, up right, down left, also have white outline 9x9 151px's, outside of white w/o outline
    // 8,85 megapixel at most per pic, better to downscale? I actually don't think, because if we downscale, we might aswell just scan the whole image first
    //how to work with rotation/camera position/perspective?




    public boolean detect_horizontal(int[][] bitMap, int startstart, int start){ //startstart & start are starting indexes to search at for finding a qr code
        this.blackWhiteMapPicture = bitMap;
        border = 0;
        count = 0;
        int lastChance = bitMap[0].length/1000;

        boolean found = false;
        for (int v = startstart; v < bitMap.length; v++) {

            this.currLine = v;
            int[] bitM = bitMap[v];
            boolean endOfLine = false;

            for ( int w = start; w < bitM.length; w++) {

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
        return detect_horizontal(newBitMap, start, startstart);
    }


    private boolean detect_diags(int[][]bitMap, int startstart, int start){

        //flip the array by 45°
        float fall = (float)bitMap.length / (float) bitMap[0].length;

        int[][] diag_arr = mk_diag_arr(bitMap.length, (int) fall);

        int[][] bitMapNew = new int[bitMap[0].length][bitMap.length];
        for (int k = 0; k < bitMap.length; k++){
            for(int l = 0; l < bitMap[0].length; l++){
                bitMapNew[k][l] = bitMap[l][k];
            }
        }

        if((float) (int) fall == fall){ //if fall is smaller than 1 (array is wider than tall, then an int fall will only occur, if the array doesn't have elements in it, so this case is irrelevant here)

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


        }else if(fall>1.0){
                //TODO: this is just copied, make sure, this is actually doing the correct array sizes
            int[][][] divided_fall = new int[bitMapNew.length][bitMapNew.length/(int)fall-1][(int) fall];
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
            //TODO: uneven fall




        }else{


            //TODO: wider than long






        }


        if (detect_horizontal( diag_arr, startstart, start)){
            if(detect_vertical(diag_arr, startstart, start)){
                this.diag = true;
                return true;
            }

        }
        return false;
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


}




/*

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























