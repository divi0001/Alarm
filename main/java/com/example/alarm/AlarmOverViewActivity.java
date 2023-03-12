package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AlarmOverViewActivity extends AppCompatActivity {

    private RecyclerView recView;
    private ImageView addAlarm;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_over_view);

        addAlarm = (ImageView) findViewById(R.id.btnAddAlarm);
        recView = (RecyclerView) findViewById(R.id.alarmRecView);

        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                i.putExtra("createAlarm","true");
                startActivity(i);
            }
        });

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            JSONArray jsonArray2 = new JSONArray(prefs.getString("AlarmObjectArray", "[]"));
            for (int i = 0; i < jsonArray2.length(); i++) {
                Log.d("your JSON Array", (String) jsonArray2.get(i));
            }

            JSONHandler j = new JSONHandler();
            ArrayList<Alarm> alarmList = j.fromJAlarmArray(jsonArray2);
            System.out.println(alarmList);

        } catch (Exception e) {
            e.printStackTrace();
        }








    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            JSONArray jsonArray2 = new JSONArray(prefs.getString("AlarmObjectArray", "[]"));
            for (int i = 0; i < jsonArray2.length(); i++) {
                Log.d("your JSON Array", (String) jsonArray2.get(i));
            }
            JSONHandler j = new JSONHandler();
            ArrayList<Alarm> alarmList = j.fromJAlarmArray(jsonArray2);
            System.out.println(alarmList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addAlarm = (ImageView) findViewById(R.id.btnAddAlarm);
        recView = (RecyclerView) findViewById(R.id.alarmRecView);

        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmOverViewActivity.this, EditAlarmActivity.class);
                i.putExtra("createAlarm","true");
                startActivity(i);
            }
        });




    }
}