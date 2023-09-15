package com.example.alarm.SudokuPkg.game;

public class Board {
    int size;
    Cell[][] cells;

    public Board(int size, Cell[][] cells) {
        this.size = size;
        this.cells = cells;
    }

    public Cell getCell(int row, int col){
        return cells[row][col];
    }


}
