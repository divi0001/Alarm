package com.example.alarm.SudokuPkg.game;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.example.alarm.SudokuMethodSetActivity;

import java.util.Random;

public class SudokuGame {

    public MutableLiveData<Pair<Integer,Integer>> selectedCellLiveData = new MutableLiveData<>();
    public MutableLiveData<Cell[][]> cellsLiveData = new MutableLiveData<>();
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Board board;

    public SudokuGame(){
        this.selectedCellLiveData.postValue(new Pair<>(selectedRow,selectedCol));

        Cell[][] cells = new SudokuMethodSetActivity().generateSudoku().clone();
        for(int i = 0; i <9;i++){
            for(int j = 0; j<9;j++){
                cells[i][j] = new Random().nextInt(10) < 2 ? new Cell(i,j,0) : new Cell(i,j,cells[i][j].value);
                cells[i][j].isStarting = cells[i][j].value == 0;
            }
        }



        this.board = new Board(9, cells);
        cellsLiveData.postValue(board.cells);
    }

    public void updateSelectedCell(int row, int col){
        if(board.getCell(row, col).isStarting){
            this.selectedCol = col;
            this.selectedRow = row;
            this.selectedCellLiveData.postValue(new Pair<>(row,col));
        }
    }
    public void handleInput(int num){
        if(selectedCol==-1||selectedRow==-1) return;
        if(!board.getCell(selectedRow, selectedCol).isStarting) return;
        board.cells[selectedRow][selectedCol].value = num;
        this.cellsLiveData.postValue(board.cells);
    }


}
