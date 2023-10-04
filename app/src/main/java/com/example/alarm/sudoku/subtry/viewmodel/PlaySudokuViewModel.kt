package com.example.alarm.sudoku.subtry.viewmodel


import androidx.lifecycle.ViewModel
import com.example.alarm.sudoku.subtry.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}