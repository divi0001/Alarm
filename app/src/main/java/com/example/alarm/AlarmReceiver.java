package com.example.alarm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    int id = -1;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:AlarmLock");
        wakeLock.acquire(30*60*1000L /*30 minutes*/);

        playSound(context);

        Intent i = new Intent(context, ActiveAlarmActivity.class);
        id = intent.getIntExtra("id", -1);
        i.putExtra("id", id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE); //todo not sure, if flagImmutable is right here

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmChannelID")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alarm Manager")
                .setContentText("Alarm")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

        wakeLock.release();


    }

    private void playSound(Context context) {

        Alarm alarm = new DBHelper(context, "Database.db").getAlarm(id);
        Uri uri = Uri.parse(alarm.getSoundPath(-1));

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus
        int result = audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                // Play the sound
                MediaPlayer mediaPlayer = new MediaPlayer();

                try{
                    mediaPlayer.setDataSource(context, uri);
                    mediaPlayer.prepare();
                }catch (IOException e){
                    e.printStackTrace();
                }

                // Release audio focus after playback is complete
                mediaPlayer.setOnCompletionListener(mp -> {
                    audioManager.abandonAudioFocus(null);
                    mediaPlayer.release();
                });

                mediaPlayer.start();
                mediaPlayer.setLooping(true);


            } catch (Exception e) {
                // Handle sound playback errors
                e.printStackTrace();
                audioManager.abandonAudioFocus(null);
            }
        }
    }






}
