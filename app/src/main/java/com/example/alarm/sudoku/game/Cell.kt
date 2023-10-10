package com.example.alarm.sudoku.game

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf<Int>()
) {
    @JvmField
    var isStarting: Boolean = false
}