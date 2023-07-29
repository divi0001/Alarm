package com.example.alarm;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Objects;
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






        rgDiff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = findViewById(group.getCheckedRadioButtonId());
                difficulty = checkedRadioButton.getText().toString();

                int[][] gene = generateSudoku(difficulty);

                txtExampleSudoku.setText(sudokuToString(gene));
                txtExampleSudoku.setPaintFlags(txtExampleSudoku.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            }
        });


        btnAddSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBHelper db = new DBHelper(SudokuMethodSetActivity.this, "Database.db");

                SharedPreferences sp = SudokuMethodSetActivity.this.getSharedPreferences(getString(R.string.queue_key), MODE_PRIVATE);
                int queueId = sp.getInt("queue_id",-1);

                if(getIntent().hasExtra("edit_add") && getIntent().hasExtra("id")){
                    int row_id = getIntent().getIntExtra("id",-1);

                    RadioButton rbCurr = findViewById(rgDiff.getCheckedRadioButtonId());
                    difficulty = rbCurr.getText().toString();

                    db.editMethoddatabase(queueId, 5, -1, db.findIdByDifficulty(difficulty), "-1", -1, row_id);
                    finish();
                }else {
                    int l = db.getMaxTableId("Methoddatabase")+1;
                    db.addMethod(l, queueId, 5, -1, db.findIdByDifficulty(difficulty), "-1", -1);
                    finish();
                }

            }
        });


    }


    public String sudokuToString(int[][] gen) {

        StringBuilder ret = new StringBuilder();

        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if(gen[i][j] == 0){
                    ret.append("   | ");
                }else{
                    ret.append(gen[i][j]).append(" | ");
                }
            }
            ret.append("\n");
        }

        return ret.toString();
    }

    public int[][] generateSudoku(String difficulty) {

        int[][] sudoku = initSudoku();
        int[] randSort1 = new int[9];
        for(int i = 1; i < 10; i++) randSort1[i-1] = i;


        int[] randSort = randomize(randSort1);

        int[] indices = new int[]{0,3,6,1,4,7,2,5,8};

        for(int i = 0; i < 9; i++) {
            sudoku[i] = shiftByX(Arrays.copyOf(randSort,9), indices[i]);
        }

        int[] temp;
        temp = sudoku[1];
        sudoku[1] = sudoku[3];
        sudoku[3] = temp;




        temp = sudoku[0];
        sudoku[0] = sudoku[4];
        sudoku[4] = temp;


        for (int k = 0; k < sudoku.length; k++) { //needs to be this way cause rows ain't addressable as arrays Q_Q
            int temp1 = sudoku[k][2];
            sudoku[k][2] = sudoku[k][5];
            sudoku[k][5] = temp1;

            temp1 = sudoku[k][3];
            sudoku[k][3] = sudoku[k][0];
            sudoku[k][0] = temp1;

        }


        temp = sudoku[5];
        sudoku[5] = sudoku[7];
        sudoku[7] = temp;

        temp = sudoku[8];
        sudoku[8] = sudoku[6];
        sudoku[6] = temp;


        for (int k = 0; k < sudoku.length; k++) {
            int temp1 = sudoku[k][6];
            sudoku[k][6] = sudoku[k][7];
            sudoku[k][7] = temp1;

            temp1 = sudoku[k][8];
            sudoku[k][8] = sudoku[k][5];
            sudoku[k][5] = temp1;

        }




        return mkDifficulty(sudoku, difficulty);
    }



    private int[][] swapAll(int[][] s1) {



        return s1;
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




    private int[][] initSudoku() {

        int[][] sudoku = new int[9][9];
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                sudoku[i][j] = 0;
            }
        }
        return sudoku;
    }

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




    private int[][] mkDifficulty(int[][] preGen, String difficulty){

        Random rand = new Random();


        switch(difficulty){

            case "Extremely easy":

                return mkNulls(preGen, 2);

            case "Easy":

                return mkNulls(preGen, 3);

            case "Middle":

                return mkNulls(preGen, 5);

            case "Hard":

                return mkNulls(preGen, 6);

            case "Extremely hard":

                return mkNulls(preGen, 8);

            default:
                return preGen;



        }


    }

    private int[][] mkNulls(int[][] preGen, int i1) {

        Random rand = new Random();
        int r1;
        int i = rand.nextInt(i1)+1;

        for(int k = 0; k < 9; k++) {
            for (int j = 0; j <= i; j++) {

                r1 = rand.nextInt(9);

                preGen[k][r1] = 0;

            }
        }

        return preGen;
    }

}