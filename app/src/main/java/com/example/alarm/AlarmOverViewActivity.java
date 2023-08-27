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

public class AlarmOverViewActivity extends AppCompatActivity {

    private RecyclerView recView;
    private ImageView addAlarm;
    private String path;

    private ArrayList<Alarm> alarms = new ArrayList<>();
    private Context context = this;
    AlarmRecViewAdapter adapter = new AlarmRecViewAdapter(this);
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar cal;


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
//        getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putBoolean("edit_add", false).apply();
//        getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE).edit().putInt("id", db.getMaxAlarmID()).apply();


        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE); //immutable instead of 0 again todo
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show();


        setEditAlarm();


        //cancelAlarm();

    }

    private void cancelAlarm() {

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0 ,intent, PendingIntent.FLAG_IMMUTABLE); //todo
        if(alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();

    }

    private void setEditAlarm() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE);
        if(sp.contains("edit_add")){

            int id = sp.getInt("id",-1);
            int hh = -1;
            int mm = -1;
            Alarm al;
            cal = null;

            for(Alarm a: alarms){
                if(id == a.getID()){
                    al = a;
                    hh = Integer.parseInt(al.getT().subSequence(11, 13).toString());
                    mm = Integer.parseInt(al.getT().subSequence(14, 16).toString());
                    cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR, hh);
                    cal.set(Calendar.MINUTE, mm+1);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }
            }

            if(sp.getBoolean("edit_add", false)){



            }else {

                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE); //immutable instead of 0 again todo
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show();

            }
            sp.edit().remove("id").apply();
            sp.edit().remove("edit_add").apply();
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