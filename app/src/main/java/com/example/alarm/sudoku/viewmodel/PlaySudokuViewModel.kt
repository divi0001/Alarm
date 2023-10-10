package com.example.alarm.sudoku.viewmodel


import androidx.lifecycle.ViewModel
import com.example.alarm.sudoku.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}