package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActiveQRBarActivity extends AppCompatActivity {
    ValueAnimator anim;
    AlarmMgr alarmMgr;
    Alarm alarm;

    TextView txtQRBarSnoozesLeft;
    Button btnReadyToScam, btnSnooze;
    SeekBar seekBar;
    long taskTime = 30*1000;
    int tempProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_qrbar);

        txtQRBarSnoozesLeft = (TextView) findViewById(R.id.txtQRBarSnoozeAmount);
        btnReadyToScam = (Button) findViewById(R.id.btnQRBarScanReady);
        btnSnooze = (Button) findViewById(R.id.btnQRBarSnooze);
        seekBar = (SeekBar) findViewById(R.id.seekBarQRBar);

        anim = alarmMgr.makeAnim(seekBar.getMax(), seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    alarmMgr.resetToUserProgress(seekBar, progress, anim, taskTime);
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
                alarmMgr.resetToUserProgress(seekBar, seekBar.getProgress(), anim, taskTime);
            }
        });

        int lvlID = alarmMgr.getLvlID();

        alarm = new Alarm(-1);

        int alarmId = getIntent().getIntExtra("id", -1); // defVal 0?
        try (DBHelper db = new DBHelper(this, "Database.db")) {
            alarm = db.getAlarm(alarmId);
        }


        if (alarm.isHasLevels() && alarm.isSnoozable(lvlID))
            txtQRBarSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID));
        else if (alarm.isSnoozable(-1))
            txtQRBarSnoozesLeft.setText(String.valueOf(alarm.getSnoozeAmount(-1)));


        btnReadyToScam.setOnClickListener(view -> {

        });

        btnSnooze.setOnClickListener(view -> {

        });


    }
}