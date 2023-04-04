package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

public class AlarmSoundSetActivity extends AppCompatActivity {

    MediaPlayer player;
    Button btnAddSound, btnSetSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound_set);

        if(player == null){
            player = MediaPlayer.create(this,R.raw.medium_1);
        }


    }
}