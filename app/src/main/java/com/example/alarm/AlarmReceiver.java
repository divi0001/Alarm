package com.example.alarm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.alarm.sudoku.subtry.view.PlaySudokuActivity;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    int id = -1;
    public static Ringtone r;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:AlarmLock");
        wakeLock.acquire(30 * 60 * 1000L /*30 minutes*/);


        r = playSound(context);


        id = intent.getIntExtra("id", -1);
        Alarm ala;

        try(DBHelper db = new DBHelper(context, "Database.db")){
            ala = db.getAlarm(id);
            Log.d("mett","alarm:\n" + ala.toString());
        }

        if(ala.isHasLevels()){
            context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key),Context.MODE_PRIVATE).edit().putInt("curr_queue_id",0).apply();
            context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key),Context.MODE_PRIVATE).edit().putInt("curr_lvl_id",0).apply();
        }else{
            context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key),Context.MODE_PRIVATE).edit().putInt("curr_queue_id",0).apply();
        }


        AlarmMethod aM;
        Log.d("mett","alarm:\n" + ala.toString());

        if (ala.isHasLevels()) {
            ArrayList<AlarmLevel> alarmLevels = ala.getlQueue();
            AlarmLevel cL = alarmLevels.get(0);
            ArrayList<AlarmMethod> alarmMethods = cL.getmQueue();
            aM = alarmMethods.get(0);

        } else {
            ArrayList<AlarmMethod> alarmMethods = ala.getmQueue(-1);
            aM = alarmMethods.get(0);
        }

        Intent i = null;
        Activity act = null;
        boolean sk = false;
        switch (aM.getType()) {
            case TapOff:
            case Passphrase:
                act = new ActiveTapOffActivity();
                break;
            case Math:
                act = new ActiveMathActivity();
                break;
            case QRBar:
                act = new ActiveQRBarActivity();
                break;
            case Memory:
                act = new ActiveMemoryActivity();
                break;
            case Sudoku:
                //i = new Intent();
                //i.addCategory(Intent.CATEGORY_LAUNCHER);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                //i.setComponent(new ComponentName("com.example.alarm.sudoku.subtry.view","com.example.alarm.sudoku.subtry.view.PlaySudokuActivity"));
                //sk= true; //.subtry.view

                act = new PlaySudokuActivity(); //ActiveSudokuActivity
                break;
            case Location:
                act = new ActiveLocationActivity();
                break;
            default:
                act = new ActiveTapOffActivity();
                break;
        }
            if(!sk) i = new Intent(context, act.getClass()); //todo each activeActivity needs to have a logic in the end, checking for further queued methods and potentially launching the intent to the next in queue

            i.putExtra("id", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE); //todo not sure, if flagImmutable is right here

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmChannelID")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Alarm Manager")
                    .setContentText("Alarm")
                    .setAutoCancel(true) //this makes the notification go away after being clicked (if set to true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());

            wakeLock.release();


        }




    private Ringtone playSound(Context context) {

        Alarm alarm = new Alarm(-1);

        try(DBHelper db = new DBHelper(context, "Database.db")){
            alarm = db.getAlarm(id);
        }


        Uri uri = Uri.parse(alarm.getSoundPath(-1));



        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus
        int result = audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        );

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            r = RingtoneManager.getRingtone(context, uri);
            r.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.setLooping(true);
                return r;
            }

        }
        return RingtoneManager.getRingtone(context, Uri.parse("android.resource://com.example.alarm/"+ R.raw.weak_1));
    }






}
