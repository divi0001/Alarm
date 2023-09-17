package com.example.alarm.SudokuPkgJava.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.alarm.Alarm;
import com.example.alarm.DBHelper;
import com.example.alarm.Enums;
import com.example.alarm.R;
import com.example.alarm.SudokuMethodSetActivity;
import com.example.alarm.SudokuPkgJava.Sudoku;
import com.example.alarm.SudokuPkgJava.game.Cell;
import com.example.alarm.SudokuPkgJava.view.custom.SudokuBoardView;
import com.example.alarm.SudokuPkgJava.viewmodel.PlaySudokuViewModel;

import java.util.Objects;
import java.util.Set;

public class ActiveSudokuActivity extends AppCompatActivity implements SudokuBoardView.OnTouchListener {

    private PlaySudokuViewModel viewModel;
    private SudokuBoardView sudokuBoardView;
    private Button btnSu1,btnSu2,btnSu3,btnSu4,btnSu5,btnSu6,btnSu7,btnSu8,btnSu9;
    private ImageButton notesButton;
    private Button[] buttons = new Button[]{btnSu1,btnSu2,btnSu3,btnSu4,btnSu5,btnSu6,btnSu7,btnSu8,btnSu9};
    int lvlID, queueID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_sudoku);

        notesButton = findViewById(R.id.imgBtnEdit);
        sudokuBoardView = (SudokuBoardView) findViewById(R.id.sudokuBoard);

        int id = getIntent().getIntExtra("id",-1);
        Log.d("mett", String.valueOf(id));
        Alarm alarm = new Alarm(-1);


        try(DBHelper db = new DBHelper(this, "Database.db")){
            alarm = db.getAlarm(id);
            Log.d("mett", "did fetching the alarm fail? Alarm is:\n"+alarm.toString());
        }
        Enums.Difficulties diff = Enums.Difficulties.ExEasy;

        if(alarm.isHasLevels()){
            lvlID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
            queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            diff = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getDifficulty();
            //txtSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID));

        }else {
            queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            Log.d("mett", String.valueOf(queueID));
            Log.d("mett", alarm.getmQueue(-1).toString());
            diff = alarm.getmQueue(-1).get(queueID).getDifficulty();
            //txtSnoozesLeft.setText(String.valueOf(alarm.getSnoozeAmount(-1)));

        }


        registerForViewID();

        sudokuBoardView.registerListener(this);

        viewModel = new ViewModelProvider(this).get(PlaySudokuViewModel.class);

        viewModel.sudokuGame.selectedCellLiveData.observe(this, (Observer<? super Pair<Integer, Integer>>) this::updateSelectedCellUI);
        viewModel.sudokuGame.cellsLiveData.observe(this, this::updateCells);

        Sudoku s = new Sudoku(diff.ordinal()*5);
        Sudoku sChallenge = new Sudoku(diff.ordinal()*5);
        sChallenge.mat = s.mat;
        s.removeKDigits();

        Cell[][] cells = new SudokuMethodSetActivity().intToCellSudoku(s.mat);
        s.printSudoku();

        viewModel.sudokuGame.cellsLiveData.postValue(cells);


        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, this::updateNoteTakingUI);
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, this::updateHighlightedKeys);


        int index = 0;
        for (Button b: buttons) {
            int finalIndex = index;
            b.setOnClickListener(v -> {
                viewModel.sudokuGame.handleInput(finalIndex+1);
            });


            index++;
        }

        notesButton.setOnClickListener(v -> viewModel.sudokuGame.changeNoteTakingState());




    }

    private void updateHighlightedKeys(Set<Integer> integers) {
        if(integers != null){
            int index = 0;
            for(Button b: buttons){
                int color = (integers.contains(index+1) ? ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary): Color.LTGRAY);
                index++;
                b.setBackgroundColor(color);
            }
        }
    }

    private void updateNoteTakingUI(Boolean aBoolean) {
        if(aBoolean != null){
            if(aBoolean){
                notesButton.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
            }else{
                notesButton.setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    private void registerForViewID() {
        buttons[0] = (Button) findViewById(R.id.btnSudoku1);
        buttons[1] = (Button) findViewById(R.id.btnSudoku2);
        buttons[2] = (Button) findViewById(R.id.btnSudoku3);
        buttons[3] = (Button) findViewById(R.id.btnSudoku4);
        buttons[4] = (Button) findViewById(R.id.btnSudoku5);
        buttons[5] = (Button) findViewById(R.id.btnSudoku6);
        buttons[6] = (Button) findViewById(R.id.btnSudoku7);
        buttons[7] = (Button) findViewById(R.id.btnSudoku8);
        buttons[8] = (Button) findViewById(R.id.btnSudoku9);
    }

    private void updateCells(@Nullable Cell[][] cells) {
        if(!Objects.equals(cells,null)){
            sudokuBoardView.updateCells(cells);
        }
    }

    private void updateSelectedCellUI(Pair<Integer, Integer> cell) {
        if(!Objects.equals(cell, null)){
            sudokuBoardView.updateSelectedCellUI(cell.first, cell.second);
        }
    }

    @Override
    public void onCellTouched(int row, int col){
        viewModel.sudokuGame.updateSelectedCell(row,col);
    }

}