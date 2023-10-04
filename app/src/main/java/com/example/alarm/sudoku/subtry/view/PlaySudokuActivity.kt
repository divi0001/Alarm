package com.example.alarm.sudoku.subtry.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.alarm.R
import com.example.alarm.sudoku.subtry.game.Cell
import com.example.alarm.sudoku.subtry.view.custom.SudokuBoardView
import com.example.alarm.sudoku.subtry.viewmodel.PlaySudokuViewModel


class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>
    private lateinit var sudokuBoardView: SudokuBoardView
    private lateinit var oneButton: Button
    private lateinit var twoButton: Button
    private lateinit var threeButton: Button
    private lateinit var fourButton: Button
    private lateinit var fiveButton: Button
    private lateinit var sixButton: Button
    private lateinit var sevenButton: Button
    private lateinit var eightButton: Button
    private lateinit var nineButton: Button
    private lateinit var notesButton: ImageButton
    private lateinit var deleteButton: ImageButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_sudoku)

        sudokuBoardView = findViewById(R.id.sudokuBoardView)
        oneButton = findViewById(R.id.oneButton)
        twoButton = findViewById(R.id.twoButton)
        threeButton = findViewById(R.id.threeButton)
        fourButton = findViewById(R.id.fourButton)
        fiveButton = findViewById(R.id.fiveButton)
        sixButton = findViewById(R.id.sixButton)
        sevenButton = findViewById(R.id.sevenButton)
        eightButton = findViewById(R.id.eightButton)
        nineButton = findViewById(R.id.nineButton)
        notesButton = findViewById(R.id.notesButton)
        deleteButton = findViewById(R.id.deleteButton)


        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProvider(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
                sevenButton, eightButton, nineButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.color_primary) else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.color_primary) else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}
