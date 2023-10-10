package com.example.alarm.sudoku.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.alarm.Alarm
import com.example.alarm.AlarmReceiver
import com.example.alarm.DBHelper
import com.example.alarm.Enums.Difficulties
import com.example.alarm.R
import com.example.alarm.sudoku.viewmodel.PlaySudokuViewModel
import com.example.alarm.sudoku.Sudoku
import com.example.alarm.sudoku.view.custom.SudokuBoardView
import com.example.alarm.sudoku.game.Board
import com.example.alarm.sudoku.game.Cell


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
    private lateinit var seekTurnSoundOn: SeekBar
    private lateinit var btnDone: Button
    private lateinit var txtSnoozesLeft: TextView
    var tempProgress = 0
    private var taskTime = 30*1000
    private var anim: ValueAnimator? = null
    private var lvlID = -1
    private var queueID = -1




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
        seekTurnSoundOn = findViewById(R.id.seekBarSudoku)
        btnDone = findViewById(R.id.btnSubmitSudoku)
        txtSnoozesLeft = findViewById(R.id.txtSudokuSnoozesLeft)



        taskTime = (getSharedPreferences(
            getString(R.string.settings_key),
            MODE_PRIVATE
        ).getInt("time_per_task", 30) * 1000L).toInt()


        lvlID = -1
        queueID = -1
        var alarm = Alarm(-1)

        val alarmId = intent.getIntExtra("id", -1) // defVal 0?

        DBHelper(this, "Database.db").use { db -> alarm = db.getAlarm(alarmId) }

        //com.example.alarm.AlarmReceiver.r.stop(); pausing the alarm sound
        //com.example.alarm.AlarmReceiver.r.play(); repeating alarm sound from start

        val difficulty: Difficulties?

        if (alarm.isHasLevels) {
            lvlID = getSharedPreferences(
                getString(R.string.active_alarm_progress_key),
                MODE_PRIVATE
            ).getInt("curr_lvl_id", 0)
            queueID = getSharedPreferences(
                getString(R.string.active_alarm_progress_key),
                MODE_PRIVATE
            ).getInt("curr_queue_id", 0)
            difficulty = alarm.getlQueue()[lvlID].getmQueue()[queueID].difficulty
            txtSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID))
        } else {
            queueID = getSharedPreferences(
                getString(R.string.active_alarm_progress_key),
                MODE_PRIVATE
            ).getInt("curr_queue_id", 0)
            difficulty = alarm.getmQueue(-1)[queueID].difficulty
            txtSnoozesLeft.text = alarm.getSnoozeAmount(-1).toString()
        }

        val sudo = toCellList(Sudoku((difficulty.ordinal+3)*6).mat)



        anim = ValueAnimator.ofInt(0, seekTurnSoundOn.max)
        anim?.duration = taskTime.toLong()
        anim?.addUpdateListener {
            val animProgress = it.animatedValue
            seekTurnSoundOn.progress = animProgress as Int
        }
        anim?.start()


        seekTurnSoundOn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    resetToUserProgress(seekBar, progress)
                    tempProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                anim?.cancel()
                tempProgress = seekBar.progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                resetToUserProgress(seekBar, seekBar.progress)
            }
        })



        sudokuBoardView.registerListener(this)


        viewModel = ViewModelProvider(this)[PlaySudokuViewModel::class.java]
        sudokuBoardView.updateCells(sudo)
        viewModel.sudokuGame.board = Board(9, sudo)  //setting the sudoku to be solved from the generator
        viewModel.sudokuGame.cellsLiveData.postValue(sudo)
        viewModel.sudokuGame.selectedCellLiveData.observe(this) { updateSelectedCellUI(it) }
        viewModel.sudokuGame.cellsLiveData.observe(this) { updateCells(it) }
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this) { updateNoteTakingUI(it) }
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this) { updateHighlightedKeys(it) }


        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
                sevenButton, eightButton, nineButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        findViewById<Button>(R.id.btnSubmitSudoku).setOnClickListener { if(viewModel.sudokuGame.cellsLiveData.value?.let { it1 ->
                isCorrect(it1)} == true){
            doneOrNextAlarm(alarm)
        }
        }

        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun doneOrNextAlarm(alarm: Alarm) {

        //lvlID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
        //queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
        if (alarm.isHasLevels) {
            if (alarm.getlQueue()[lvlID].getmQueue().size - 1 == queueID) {
                if (alarm.getlQueue().size - 1 == lvlID) {
                    getSharedPreferences(
                        getString(R.string.active_alarm_progress_key),
                        MODE_PRIVATE
                    ).edit().putInt("curr_lvl_id", -1).apply()
                    getSharedPreferences(
                        getString(R.string.active_alarm_progress_key),
                        MODE_PRIVATE
                    ).edit().putInt("curr_queue_id", 0).apply()
                }
                lvlID++
                getSharedPreferences(
                    getString(R.string.active_alarm_progress_key),
                    MODE_PRIVATE
                ).edit().putInt("curr_lvl_id", lvlID).apply()
                getSharedPreferences(
                    getString(R.string.active_alarm_progress_key),
                    MODE_PRIVATE
                ).edit().putInt("curr_queue_id", 0).apply()
            } else if (lvlID != -1) {
                queueID++
                getSharedPreferences(
                    getString(R.string.active_alarm_progress_key),
                    MODE_PRIVATE
                ).edit().putInt("curr_queue_id", queueID).apply()
            } else {
                if (alarm.getmQueue(-1).size - 1 == queueID) {
                    //todo cleanup after alarm is done
                    AlarmReceiver.r.stop()
                    finish()
                } else {
                    queueID++
                    getSharedPreferences(
                        getString(R.string.active_alarm_progress_key),
                        MODE_PRIVATE
                    ).edit().putInt("curr_queue_id", queueID).apply()
                }
            }
        } else {
            if (alarm.getmQueue(-1).size - 1 == queueID) {
                //todo cleanup after alarm is done
                AlarmReceiver.r.stop()
                finish()
            } else {
                queueID++
                getSharedPreferences(
                    getString(R.string.active_alarm_progress_key),
                    MODE_PRIVATE
                ).edit().putInt("curr_queue_id", queueID).apply()
            }
        }
    }

    private fun toCellList(mat: Array<IntArray>?): List<Cell> {

        val list: MutableList<Cell> = ArrayList()

        for(i in 0 until 9){
            for(j in 0 until 9){
                list.add(mat?.get(j)?.get(i)?.let { Cell(i,j, it, it != 0) }!!)
            }
        }

        var str = ""

        list.forEachIndexed{ i, c ->if (i % 9 != 0 || i == 0) c.value.toString().let { str += "$it "; str } else str +=  "\n"+c.value.toString() + " "}
        //Log.d("matt", Sudoku(9).printSudoku(mat))
        Log.d("matt", str)

        return list
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

    private fun resetToUserProgress(seekBar: SeekBar, progress: Int) {
        seekBar.progress = progress
        anim?.cancel()
        anim = ValueAnimator.ofInt(progress, seekTurnSoundOn.max)
        anim?.duration = (taskTime - (progress / seekTurnSoundOn.max.toFloat() * 1F)).toLong()
        anim?.addUpdateListener { animation ->
            val animProgress = animation.animatedValue as Int
            seekTurnSoundOn.progress = animProgress
        }
        anim?.start()
    }

    private fun isCorrect(cellList : List<Cell>): Boolean {
        cellList.forEachIndexed { i, c -> //i = index, c = cell
            if(!(colSave(c, i, cellList)) || !rowSave(c, i, cellList) || !boxSave(c, i, cellList)){
                return false
            }
        }
        return true
    }

    private fun boxSave(c: Cell, i: Int, cellList: List<Cell>): Boolean {
        var numCount = 0
        val row = ((i%9)%3)*3 //(=box offset)
        val col = ((i/27)*3)

        if(cellList[(row)*9+col].value == c.value) numCount++
        if(cellList[(row)*9+col+1].value == c.value) numCount++
        if(cellList[(row)*9+col+2].value == c.value) numCount++
        if(cellList[(row+1)*9+col].value == c.value) numCount++
        if(cellList[(row+1)*9+col+1].value == c.value) numCount++
        if(cellList[(row+1)*9+col+2].value == c.value) numCount++
        if(cellList[(row+2)*9+col].value == c.value) numCount++
        if(cellList[(row+2)*9+col+1].value == c.value) numCount++
        if(cellList[(row+2)*9+col+2].value == c.value) numCount++


        if(numCount != 1){
            Toast.makeText(this,"Box unsafe: value of i (expected number): " + c.value+ " at row: " + row + " col: " + col,Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun rowSave(c: Cell, i: Int, cellList: List<Cell>): Boolean {
        var numCount = 0
        val row = i%9
        val col = i/9
        for(j in 0 until 9){
            if(cellList[j*9 + col].value == c.value) numCount++
        }

        if(numCount != 1){
            Toast.makeText(this,"Row unsafe: value of i (expected number): " + c.value+ " at row: " + row + " col: " + i/9,Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun colSave(c: Cell, i: Int, cellList: List<Cell>): Boolean {
        var numCount = 0
        val row = i%9
        val col = i/9
        for(j in 0 until 9){
            if(cellList[row*9 + j].value == c.value) numCount++
        }

        if(numCount != 1){
            Toast.makeText(this,"Column unsafe: value of i (expected number): " + c.value+ " at row: " + i%9 + " col: " + col,Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}
