package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ActiveMathActivity extends AppCompatActivity {

    private TextView txtSnoozesLeft, txtMathTask;
    private Button btnSubmit, btnSnooze;
    private SeekBar seekBarSoundTurnOn = (SeekBar) findViewById(R.id.seekProgressBarMath);;
    private EditText editMathAnswer;
    private ValueAnimator anim = ValueAnimator.ofInt(0, seekBarSoundTurnOn.getMax());
    int tempProgress = 0;
    long taskTime = 30*1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_math);

        txtMathTask = (TextView) findViewById(R.id.txtMathTask);
        txtSnoozesLeft = (TextView) findViewById(R.id.txtMathSnoozesLeft);
        btnSubmit = (Button) findViewById(R.id.btnSubmitMathAnswer);
        btnSnooze = (Button) findViewById(R.id.btnMathSnooze);
        editMathAnswer = (EditText) findViewById(R.id.editMathAnswer);

        taskTime = getSharedPreferences(getString(R.string.settings_key),MODE_PRIVATE).getInt("time_per_task", 30)*1000L;

        Alarm alarm = new Alarm(-1);

        int alarmId = getIntent().getIntExtra("id",-1); // defVal 0?
        try(DBHelper db = new DBHelper(this, "Database.db")){
            alarm = db.getAlarm(alarmId);
        }


        String difficulty = Enums.Difficulties.ExEasy.name();
        int mode = 0;

        if(alarm.isHasLevels()){
            int lvlID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
            int queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            difficulty = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getDifficulty().name();
            mode = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getSubMethod().ordinal();
            txtSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID));
        }else {
            int queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            difficulty = alarm.getmQueue(-1).get(queueID).getDifficulty().name();
            mode = alarm.getmQueue(-1).get(queueID).getSubMethod().ordinal();
            txtSnoozesLeft.setText(alarm.getSnoozeAmount(-1));
        }

        String task = new MathMethodSetActivity().generateExample(difficulty, mode);

        txtMathTask.setText(task);

        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(txtSnoozesLeft.getText().toString())>0){
                    Ringtone r = (Ringtone) Objects.requireNonNull(getIntent().getExtras()).get("ringtone"); //todo not sure if Ringtone key (name:) was capitalized here
                    assert r != null;
                    r.stop(); //todo maybe dont use ringtone to play alarmsound, when turning off the alarm in app, rather mediaplayer or smth like that (for next time)
                }else{
                    Toast.makeText(ActiveMathActivity.this, "No snoozes left, get up!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        anim.setDuration(taskTime);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekBarSoundTurnOn.setProgress(animProgress);
            }
        });
        anim.start();

        seekBarSoundTurnOn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    resetToUserProgress(seekBar, progress);
                    tempProgress = progress;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    anim.cancel();
                    tempProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                resetToUserProgress(seekBar, seekBar.getProgress());
            }
        });










    }

    private void resetToUserProgress(SeekBar seekBar, int progress) {
        seekBar.setProgress(progress);
        anim.cancel();

        anim = ValueAnimator.ofInt(progress, seekBarSoundTurnOn.getMax());

        anim.setDuration((long) (taskTime-(progress/seekBarSoundTurnOn.getMax()*1F)));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekBarSoundTurnOn.setProgress(animProgress);
            }
        });
        anim.start();
    }
}