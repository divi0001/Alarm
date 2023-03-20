package com.example.alarm;

import java.util.Arrays;

public class ReadQRCode {

    public int[][] cropQRCode(int[][] bitMap, int size){

        boolean[] booleansVertical = new boolean[bitMap.length];
        boolean[] booleansHorizontal = new boolean[bitMap[bitMap.length/2].length];
        int posVertical = 0;
        int trueCountVertical = 0;
        int posHorizontal = 0;
        int trueCountHorizontal = 0;


        for (int[] bitM: bitMap ) {
            int[] minusOnes = new int[bitM.length];
            Arrays.fill(minusOnes,-1);

            if (Arrays.equals(bitM, minusOnes)){
                booleansVertical[posVertical] = false;
            }else{
                booleansVertical[posVertical] = true;
                trueCountVertical++;
            }
            posVertical++;
        }

        for (int[] bitM: bitMap ) {
            int[] minusOnes = new int[bitM.length];
            Arrays.fill(minusOnes,-1);

            if (Arrays.equals(bitM, minusOnes)){
                booleansHorizontal[posHorizontal] = false;
            }else{
                booleansHorizontal[posHorizontal] = true;
                trueCountHorizontal++;
            }
            posHorizontal++;
        }




        int[][] newArr = new int[trueCountVertical][trueCountHorizontal];
        int skippedX = 0;
        int skippedY = 0;

        for(int i = 0; i < trueCountVertical ;i++){
            for (int j = 0; j < trueCountHorizontal; j++){
                if(booleansVertical[i] || booleansHorizontal[j]) {
                    newArr[i][j] = bitMap[i+skippedX][j+skippedY];
                }else{
                    skippedX++;
                    skippedY++;
                }
            }
        }

        int[][] compactArr

        return newArr;

    }



}
