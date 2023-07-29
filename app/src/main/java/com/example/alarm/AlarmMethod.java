package com.example.alarm;

import java.util.Arrays;

public class AlarmMethod {

    private int id,difficulty, type;

    private String[] translationTypeList = new String[]{"TapOff", "Math", "QRCode", "Location", "Sudoku", "Memory", "Passphrase"};
    private String[] translationDiffList = new String[]{"exEasy","easy","middle","hard","exHard"};

    public AlarmMethod(int id, int difficulty, int type){
        this.id = id;
        this.difficulty = difficulty;
        this.type = type;
    }

    public String calcExample(int type){
        if(!(type == 0) && !(type == 2) && !(type == 3) && !(type == 6) ){

            if(type == 1) {
                MathMethodSetActivity math = new MathMethodSetActivity();
                math.generateExample(translationDiffList[this.difficulty], this.type);
            } else if (type == 4) {
                SudokuMethodSetActivity sudo = new SudokuMethodSetActivity();
                sudo.sudokuToString(sudo.generateSudoku(translationDiffList[difficulty]));
            } else if (type == 5) {
                generatePointerMemory(difficulty);
            }
        }else{




        }
    }

    public String[][]  generatePointerMemory(int difficulty){
        //difficulty+x is size here
        String[][] memory = new String[((difficulty+3)*2)/2][((difficulty+3)*2)/2];
        



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

    @Override
    public String toString() {
        return "AlarmMethod{" +
                "id=" + id +
                ", difficulty=" + difficulty +
                ", type=" + type +
                ", translationList=" + Arrays.toString(translationList) +
                '}';
    }

    public String[] getTranslationList() {
        return translationList;
    }
}
