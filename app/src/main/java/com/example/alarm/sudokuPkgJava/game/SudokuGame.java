package com.example.alarm.sudokuPkgJava.game;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.alarm.SudokuMethodSetActivity;
import com.example.alarm.sudokuPkgJava.Sudoku;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SudokuGame {

    public MutableLiveData<Pair<Integer,Integer>> selectedCellLiveData = new MutableLiveData<>();
    public MutableLiveData<Cell[][]> cellsLiveData = new MutableLiveData<>();
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isTakingNotes = false;
    private Board board;
    private Cell[][] solvedSudoku;
    public MutableLiveData<Boolean> isTakingNotesLiveData = new MutableLiveData<>();
    public MutableLiveData<Set<Integer>> highlightedKeysLiveData = new MutableLiveData<>();

    public SudokuGame(){
        this.selectedCellLiveData.postValue(new Pair<>(selectedRow,selectedCol));

        Sudoku sudoku = new Sudoku(20);
        this.solvedSudoku = new SudokuMethodSetActivity().intToCellSudoku(sudoku.mat);
        sudoku.removeKDigits();

        Cell[][] cells = new SudokuMethodSetActivity().intToCellSudoku(sudoku.mat);
        for(int i = 0; i <9;i++){
            for(int j = 0; j<9;j++){
                cells[i][j].isStarting = cells[i][j].value == 0;
            }
        }

        cells[0][0].notes = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));



        this.board = new Board(9, cells);
        cellsLiveData.postValue(board.cells);


        isTakingNotesLiveData.postValue(isTakingNotes);


    }

    public void updateSelectedCell(int row, int col){
        Cell cell = board.getCell(row, col);
        if(cell.isStarting){ //! (inverse?)
            this.selectedCol = col;
            this.selectedRow = row;
            this.selectedCellLiveData.postValue(new Pair<>(row,col));

            if(isTakingNotes){
                highlightedKeysLiveData.postValue(cell.notes);
            }

        }
    }
    public void handleInput(int num) {
        if (selectedCol == -1 || selectedRow == -1) return;
        Cell c = board.getCell(selectedRow, selectedCol);
        if (!c.isStarting) return;
        if (isTakingNotes) {
            if(c.notes.contains(num)){
                c.notes.remove(num);
            }else{
                c.notes.add(num);
            }
            highlightedKeysLiveData.postValue(c.notes);
        } else {
            c.value = num;
        }
        this.cellsLiveData.postValue(board.cells);
    }

    public void changeNoteTakingState(){
        isTakingNotes = !isTakingNotes;
        isTakingNotesLiveData.postValue(isTakingNotes);

        Set<Integer> curNotes = isTakingNotes ? board.getCell(selectedRow, selectedCol).notes : new HashSet<>();
        highlightedKeysLiveData.postValue(curNotes);

    }



}
