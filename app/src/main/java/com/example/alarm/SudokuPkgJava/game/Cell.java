package com.example.alarm.SudokuPkgJava.game;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    public int row;
    public int col;
    public int value;
    public boolean isStarting = false;
    public Set<Integer> notes = new HashSet<>();

    public Cell(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}
