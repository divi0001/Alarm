package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

public class AlarmSoundSetActivity extends AppCompatActivity {

    Button btnAddSound, btnSetSound;
    ArrayList<String> soundNames;
    RecyclerView recViewSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound_set);

        recViewSound = (RecyclerView) findViewById(R.id.recViewSounds);

        soundNames = new ArrayList<>();

        soundNames.add("weak_2");

        SoundRecViewAdapter adapter = new SoundRecViewAdapter(this);
        adapter.setSounds(soundNames);

        recViewSound.setAdapter(adapter);
        recViewSound.setLayoutManager(new LinearLayoutManager(this));




    }
}