package com.example.alarm;

import static com.example.alarm.Enums.Method.None;
import static com.example.alarm.Enums.Method.TapOff;

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
import android.os.Parcelable;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    int id = -1;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:AlarmLock");
        wakeLock.acquire(30 * 60 * 1000L /*30 minutes*/);


        Ringtone r = playSound(context);


        id = intent.getIntExtra("id", -1);
        Alarm ala = new DBHelper(context, "Database.db").getAlarm(id);

        AlarmMethod aM = new AlarmMethod(0, Enums.Difficulties.None, TapOff, Enums.SubMethod.None);

        if (ala.isHasLevels()) {
            ArrayList<AlarmLevel> alarmLevels = ala.getlQueue();
            AlarmLevel cL = alarmLevels.get(0);
            ArrayList<AlarmMethod> alarmMethods = cL.getmQueue();
            aM = alarmMethods.get(0);

        } else {
            ArrayList<AlarmMethod> alarmMethods = ala.getmQueue(0);
            aM = alarmMethods.get(0);
        }


        Activity act;
        switch (aM.getType()) {
            case TapOff:
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
                act = new ActiveSudokuActivity();
                break;
            case Location:
                act = new ActiveLocationActivity();
                break;
            case Passphrase:
                act = new ActiveTapOffActivity(); //todo build this into ActiveTapOffActivity
                break;
            default:
                act = new ActiveTapOffActivity();
                break;
        }

            Intent i = new Intent(context, act.getClass()); //todo each activeActivity needs to have a logic in the end, checking for further queued methods and potentially launching the intent to the next in queue

            i.putExtra("id", id);
            i.putExtra("ringtone", (Parcelable) r);
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


    private Ringtone playSound(Context context) {

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
            Ringtone r = RingtoneManager.getRingtone(context, uri);
            r.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.setLooping(true);
                return r;
            }

        }
        return RingtoneManager.getRingtone(context, Uri.parse("android.resource://com.example.alarm/"+ R.raw.weak_1));
    }






}
