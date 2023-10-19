package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActiveTapOffActivity extends AppCompatActivity {

    AlarmMgr alarmMgr = new AlarmMgr(this);
    Alarm alarm;
    EditText editPassphrase;
    Button btnSnooze, btnOff;
    TextView txtSnoozeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_tap_off);

        editPassphrase = (EditText) findViewById(R.id.editPassphrase);
        btnSnooze = (Button) findViewById(R.id.btnTapOffSnooze);
        btnOff = (Button) findViewById(R.id.btnTapOff);
        txtSnoozeAmount = (TextView) findViewById(R.id.txtTapOffSnoozeAmount);


        //int alarmId = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("id",0);
        alarm = new Alarm(-1);
        int alarmId = getIntent().getIntExtra("id", -1);
        try(DBHelper db = new DBHelper(this, "Database.db")){
            alarm = db.getAlarm(alarmId);
        }
        if(alarmMgr.getAlarmMethod(alarm) == Enums.Method.Passphrase){
            editPassphrase.setVisibility(View.VISIBLE);
        }

        int lvlID = alarmMgr.getLvlID();

        if (alarm.isHasLevels() && alarm.isSnoozable(lvlID))
            txtSnoozeAmount.setText(alarm.getSnoozeAmount(lvlID));
        else if (alarm.isSnoozable(-1))
            txtSnoozeAmount.setText(String.valueOf(alarm.getSnoozeAmount(-1)));

        btnSnooze.setOnClickListener(view -> {
            // TODO Auto-generated method stub
        });

        btnOff.setOnClickListener(view -> {
            if(!alarmMgr.nextOrAlarmOff(alarm)) finish();
            // TODO launch next ActiveAlarmMethod
        });

    }
}