package com.example.alarm;

import android.location.Address;

import java.util.Arrays;
import java.util.Random;

public class AlarmMethod {

    private double locationRadius;
    private int id;
    private Enums.Difficulties difficulty;
    private Enums.Method method;
    private Enums.SubMethod subMethod;

    private Address adress;

    /*
    private final String[] translationTypeList = new String[]{"TapOff", "Math", "QRCode", "Location", "Sudoku", "Memory", "Passphrase"};
    private final String[] translationDiffList = new String[]{"exEasy","easy","middle","hard","exHard"};
*/

    public AlarmMethod(int id, Enums.Difficulties difficulty, Enums.Method method, Enums.SubMethod subMethod){
        this.id = id;
        this.difficulty = difficulty;
        this.method = method;
        this.subMethod = subMethod;
    }

    public void calcExample(int type){
        if(!(type == 0) && !(type == 2) && !(type == 3) && !(type == 6) ){

            if(type == 1) {
                MathMethodSetActivity math = new MathMethodSetActivity();
                math.generateExample(difficulty.toString(), subMethod.ordinal()); //todo check if snd arg is correct
            } else if (type == 4) {
                SudokuMethodSetActivity sudo = new SudokuMethodSetActivity();
                sudo.sudokuToString(sudo.generateSudoku(difficulty.toString()));
            } else if (type == 5) {
                generatePointerMemory(difficulty.ordinal());
            }
        }
    }

    public String[][] generatePointerMemory(int difficulty){
        //difficulty+x is size here
        int size = difficulty+3;
        if(size%2!=0) size++;
        String[][] memory = new String[size][size];

        for (int i = 0; i < (size*size)/2; i++){
            memory[i/size][i%size] = String.valueOf(i);
        }

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++){

                Random rand = new Random();
                int randi = rand.nextInt(size);
                int randj = rand.nextInt(size);
                String temp = memory[i][j];
                memory[i][j] = memory[randi][randj];
                memory[randi][randj] = temp;

            }
        }

        return memory;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Enums.Difficulties getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Enums.Difficulties difficulty) {
        this.difficulty = difficulty;
    }

    public Enums.Method getType() {
        return this.method;
    }

    public void setType(Enums.Method type) {
        this.method = type;
    }

    public Enums.SubMethod getSubType() {
        return subMethod;
    }

    public void setSubType(Enums.SubMethod subType) {
        this.subMethod = subType;
    }

    public Address getAdress() {
        return adress;
    }

    public double getLocationRadius() { //todo this is in no way implemented, just here, to keep away the errors so i can have a bootable app for once, so this will need further coding in locationsetactivity and editalarmactivity
        return locationRadius;
    }

    public void setLocationRadius(double locationRadius) {
        this.locationRadius = locationRadius;
    }

    public void setAdress(Address adress) {
        this.adress = adress;
    }

    @Override
    public String toString() {
        return "AlarmMethod{" +
                "id=" + id +
                ", difficulty=" + difficulty +
                ", type=" + method +
                ", subType=" + subMethod +
                '}';
    }
}
