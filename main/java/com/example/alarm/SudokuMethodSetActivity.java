package com.example.alarm;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SudokuMethodSetActivity extends AppCompatActivity {


    RadioGroup rgDiff;
    TextView txtExampleSudoku;
    Button btnAddSelected;
    String difficulty;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_method_set);

        rgDiff = (RadioGroup) findViewById(R.id.rgSudokuDifficulty);
        txtExampleSudoku = (TextView) findViewById(R.id.txtSudokuExample);
        btnAddSelected = (Button) findViewById(R.id.btnAddSudoku);


        RadioButton rbChecked = findViewById(rgDiff.getCheckedRadioButtonId());



        rgDiff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = findViewById(group.getCheckedRadioButtonId());
                difficulty = checkedRadioButton.getText().toString();

                txtExampleSudoku.setText(sudokuToString(generateSudoku(difficulty)));
            }
        });


    }


    private String sudokuToString(int[][] generateSudoku) {

        StringBuilder ret = new StringBuilder();

        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ret.append(generateSudoku[i][j]).append("   ");
            }
            ret.append("\n");
        }

        return ret.toString();
    }

    private int[][] generateSudoku(String difficulty) {

        int[][] sudoku = initSudoku();
        int[] randSort = new int[9];
        for(int i = 1; i < 10; i++) randSort[i-1] = i;


        randSort = randomize(randSort);

        int[] indices = new int[]{0,3,6,1,4,7,2,5,8};

        for(int i = 0; i < 9; i++) {
            sudoku[i] = shiftByX(Arrays.copyOf(randSort,9), indices[i]);
        }

        return swapLinesAndRows(sudoku);
        }

    private int[][] swapLinesAndRows(int[][] sudoku) {

        Random rand = new Random();
        int r = rand.nextInt(3);;
        while(r == 0){
            r = rand.nextInt(3);
        }

        for(int i = 0; i < r; i++){
            sudoku = swapRows(i%3, (i+i)%3, sudoku);
            sudoku = swapLines(i%3, (i+i)%3, sudoku);

            sudoku = swapRows((i%3)+3, ((i+i)%3)+3, sudoku);
            sudoku = swapLines((i%3)+3, ((i+i)%3)+3, sudoku);

            sudoku = swapRows((i%3)+6, ((i+i)%3)+6, sudoku);
            sudoku = swapLines((i%3)+6, ((i+i)%3)+6, sudoku);
        }
        return sudoku;

    }

    private int[][] swapLines(int i, int i1, int[][] sudoku) {
        int temp;
        for(int k = 0; k < sudoku.length; k++){
            temp = sudoku[i][k];
            sudoku[i][k] = sudoku[i1][k];
            sudoku[i1][k] = temp;
        }
        return sudoku;
    }

    private int[][] swapRows(int i, int i1, int[][] sudoku) {
        int temp;
        for(int k = 0; k < sudoku.length; k++){
            temp = sudoku[k][i];
            sudoku[k][i] = sudoku[k][i1];
            sudoku[k][i1] = temp;
        }
        return sudoku;
    }

    private int[] shiftByX(int[] randSort, int index) {

        int[] temp = new int[9];
        int ind = 0;

        for( int i = index ; i < 9; i++){
            temp[ind] = randSort[i];
            ind++;
        }

        for(int i = 0; i < index; i++){
            temp[ind] = randSort[i];
            ind++;
        }

        return temp;
    }

        /*




        delete me later:


        while(col < 10) {
            Log.d("Sudoku", "\nwhile col < 10 \n \n" + sudokuToString(sudoku) +"\n" + todo);

            rTodo = new ArrayList<>();
            for(int i = 1; i < 10; i++) rTodo.add(i);

            todo = randomize(rTodo);
            int row = 0;
        while (todo.size() > 0) {
                Log.d("Sudoku", "\nwhile todo size > 0 \n \n" + sudokuToString(sudoku) +"\n" + todo + "\n");
                int pos = 0;

                while (!isValid(todo.get(pos), row, col, sudoku)) {
                    Log.d("Sudoku", "\n!isValid \n \n" + sudokuToString(sudoku) + "\n" + todo);
                    pos++;
                    if(pos == todo.size()) break;
                }

                if(pos == todo.size()){
                    //backtracking needed
                    backtrack = true;
                    break;
                }else{
                    backtrack = false;
                }

                Log.d("Sudoku", "after");

                sudoku.get(col).set(row,todo.get(pos));
                todo.remove(pos);
                row++;

            }
            Log.d("Sudoku", "backtrack");
            if(!backtrack) col++;

        }

        return sudoku;




        */



    private int[][] initSudoku() {

        int[][] sudoku = new int[9][9];
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                sudoku[i][j] = 0;
            }
        }
        return sudoku;
    }
/*

    solution with backtracking though very intense on the cpu it seems, always throws eofException in 4th line wtf

    valid sudoku:

            5,3,7,8,2,4,6,9,1
            8,4,2,1,6,9,7,3,5
            1,9,6,5,7,3,2,4,8
            7,8,3,2,4,1,9,5,6
            6,5,9,7,3,8,4,1,2
            2,1,4,6,9,5,3,8,7
            4,6,1,9,5,7,8,2,3
            3,2,8,4,1,6,5,7,9
            9,7,5,3,8,2,1,6,4





    private boolean isValid(int num, int row, int col, ArrayList<ArrayList<Integer>> sudoku) {

        int quadCoordRow, quadCoordCol;
        quadCoordRow = (row/3)*3; //assuming 9x9 grid, so pos in array should never be >= 9
        quadCoordCol = (col/3)*3;

        for (int i = quadCoordRow; i < quadCoordRow + 3; i ++){
            for (int j = quadCoordCol; j < quadCoordCol + 3; j++){
                if(num == sudoku.get(j).get(i)) return false;
            }
        }

        for(int i = 0; i < 9; i++){
            if(num == sudoku.get(i).get(row) && i != col) return false;
        }

        for(int i = 0; i < 9; i++){
            if(num == sudoku.get(col).get(i) && i != row) return false;
        }


        return true;

    }
*/
    private int[] randomize(int[] todo){

        Random rand = new Random();
        int r1,r2;


        for(int i = 0; i < todo.length; i ++){

            r1 = rand.nextInt(todo.length-1);
            r2 = rand.nextInt(todo.length-1);

            int temp = todo[r1];
            todo[r1] = todo[r2];
            todo[r2] = temp;

        }
        return todo;
    }




    private ArrayList<ArrayList<Integer>> mkDifficulty(ArrayList<ArrayList<Integer>> preGen){

        Random rand = new Random();


        switch(difficulty){

            case "Extremely easy":

                return mkNulls(preGen, rand.nextInt(1)+1);

            case "Easy":

                return mkNulls(preGen, rand.nextInt(3)+1);

            case "Middle":

                return mkNulls(preGen, rand.nextInt(5)+1);

            case "Hard":

                return mkNulls(preGen, rand.nextInt(7)+1);

            case "Extremely Hard":

                return mkNulls(preGen, rand.nextInt(8)+1);

            default:
                return preGen;



        }


    }

    private ArrayList<ArrayList<Integer>> mkNulls(ArrayList<ArrayList<Integer>> preGen, int i) {

        Random rand = new Random();
        int r1, r2;

        for(int k = 0; k < 9; k++) {
            for (int j = 0; j < i; j++) {

                r1 = rand.nextInt(9);
                r2 = rand.nextInt(9);

                preGen.get(r2).set(r1, 0);

            }
        }

        return preGen;
    }

}