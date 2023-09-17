package com.example.alarm.sudokupkgkotlin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.alarm.sudokupkgkotlin.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}