package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alarm.databinding.ActivityMainBinding;

import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AlarmOverViewActivity extends AppCompatActivity {

    private RecyclerView recView;
    private ImageView addAlarm;
    private String path;

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private final Context context = this;
    AlarmRecViewAdapter adapter = new AlarmRecViewAdapter(this);
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar cal;
    ArrayList<PendingAlarm> pendingAlarms = new ArrayList<>();


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

                DBHelper db = new DBHelper(context, "Database.db");

                Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                i.putExtra("createAlarm","true");
                getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putBoolean("edit_add", false).apply();
                getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putInt("id", db.getMaxAlarmID()+1).apply();
                startActivity(i);
            }
        });


        DBHelper db = new DBHelper(context, "Database.db");
        alarms = db.getAlarms();


        adapter.setAlarms(alarms);

        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(context));


        setAlarms();




    }



    @SuppressLint("ScheduleExactAlarm")
    private void setAlarms() {

        if(pendingAlarms.size() != 0){
            for(PendingAlarm pA: pendingAlarms){
                pA.getAlarmManager().cancel(pA.getPendingIntent());
            }
        }

        for(Alarm alarm: alarms){

            AlarmMgr mgr = new AlarmMgr(context);
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("id", alarm.getID());
            PendingIntent pI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            if(alarm.isActive()){
                long time = mgr.calculateNextAlarmTime(alarm, System.currentTimeMillis());

                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pI);

                PendingAlarm pA = new PendingAlarm(alarmManager, alarm.getID(), alarm);
                pA.setPendingIntent(pI);

                pendingAlarms.add(pA);
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

        setAlarms();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);


        addAlarm = (ImageView) findViewById(R.id.btnAddAlarm);
        recView = (RecyclerView) findViewById(R.id.alarmRecView);



        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper db = new DBHelper(AlarmOverViewActivity.this, "Database.db");
                Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                i.putExtra("createAlarm","true");
                getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putInt("id", db.getMaxAlarmID()+1).apply();
                startActivity(i);
            }
        });

        adapter.setAlarms(alarms);



    }
}