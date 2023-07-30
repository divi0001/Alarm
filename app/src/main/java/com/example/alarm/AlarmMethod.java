package com.example.alarm;

import java.util.Arrays;
import java.util.Random;

public class AlarmMethod {

    private int id,difficulty;
    private Enums.Method method;
    private Enums.SubMethod subMethod;

    public AlarmMethod(int id, int difficulty, Enums.Method method, Enums.SubMethod subMethod){
        this.id = id;
        this.difficulty = difficulty;
        this.method = method;
        this.subMethod = subMethod;
    }

    public void calcExample(int type){
        if(!(type == 0) && !(type == 2) && !(type == 3) && !(type == 6) ){

            if(type == 1) {
                MathMethodSetActivity math = new MathMethodSetActivity();
                math.generateExample(translationDiffList[this.difficulty], this.subType); //todo check if snd arg is correct
            } else if (type == 4) {
                SudokuMethodSetActivity sudo = new SudokuMethodSetActivity();
                sudo.sudokuToString(sudo.generateSudoku(translationDiffList[difficulty]));
            } else if (type == 5) {
                generatePointerMemory(difficulty);
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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getTranslationTypeList() {
        return translationTypeList;
    }

    public String[] getTranslationDiffList() {
        return translationDiffList;
    }

    @Override
    public String toString() {
        return "AlarmMethod{" +
                "id=" + id +
                ", difficulty=" + difficulty +
                ", type=" + type +
                ", translationTypeList=" + Arrays.toString(translationTypeList) +
                ", translationDiffList=" + Arrays.toString(translationDiffList) +
                '}';
    }
}
