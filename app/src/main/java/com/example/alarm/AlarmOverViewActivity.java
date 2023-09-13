package com.example.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmOverViewActivity extends AppCompatActivity {

    private RecyclerView recView;
    private ImageView addAlarm;

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private final Context context = this;
    AlarmRecViewAdapter adapter = new AlarmRecViewAdapter(this);
    private AlarmManager alarmManager;
    private TextView txtExplainAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_over_view);
        //todo add possibility to start/end alarm on specific dates

        addAlarm = (ImageView) findViewById(R.id.btnAddAlarm);
        recView = (RecyclerView) findViewById(R.id.alarmRecView);


        createNotificationChannel();

        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try(DBHelper db = new DBHelper(context, "Database.db")) {

                    Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                    i.putExtra("createAlarm", "true");
                    getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putBoolean("edit_add", false).apply();
                    getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putInt("id", db.getMaxAlarmID() + 1).apply();
                    startActivity(i);
                }
            }
        });


        try(DBHelper db = new DBHelper(context, "Database.db")) {
            alarms = db.getAlarms();
        }

        adapter.setAlarms(alarms);


        txtExplainAdd = (TextView) findViewById(R.id.txtExplain);

        if(adapter.getItemCount()>0){
            txtExplainAdd.setText("");
        }else{
            txtExplainAdd.setText(R.string.empty);
        }

        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(context));


        //setAlarms();




    }



    @SuppressLint("ScheduleExactAlarm")
    private void setAlarms() {



        if(alarms.size() > 0) {

            for (Alarm alarm : alarms) {

                AlarmMgr mgr = new AlarmMgr(context);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("id", alarm.getID());
                PendingIntent pI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                if (alarm.isActive()) {
                    long time = mgr.calculateNextAlarmTime(alarm);

                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pI);

                }
            }
        }

    }

    private void createNotificationChannel() {

        CharSequence name = "AlarmReminderChannel";
        String description = "Channel for Alarmmanager";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("AlarmChannelID", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }






    @Override
    protected void onResume() {
        super.onResume();

        try(DBHelper db = new DBHelper(this, "Database.db")) {
            alarms = db.getAlarms();
            adapter.setAlarms(alarms);
        }
        txtExplainAdd = findViewById(R.id.txtExplain);
        if(adapter.getItemCount()>0){
            txtExplainAdd.setText("");
        }else{
            txtExplainAdd.setText(R.string.empty);
        }

        setAlarms();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        try(DBHelper db = new DBHelper(context, "Database.db")) {
            alarms = db.getAlarms();
        }
        adapter.setAlarms(alarms);


        addAlarm = (ImageView) findViewById(R.id.btnAddAlarm);
        recView = (RecyclerView) findViewById(R.id.alarmRecView);

        txtExplainAdd = findViewById(R.id.txtExplain);
        if(adapter.getItemCount()>0){
            txtExplainAdd.setText("");
        }else{
            txtExplainAdd.setText(R.string.empty);
        }



        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                try(DBHelper db = new DBHelper(context, "Database.db")) {
                    getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putInt("id", db.getMaxAlarmID() + 1).apply();
                }
                getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putBoolean("edit_add", false).apply();
                startActivity(i);
            }
        });

        adapter.setAlarms(alarms);



    }
}