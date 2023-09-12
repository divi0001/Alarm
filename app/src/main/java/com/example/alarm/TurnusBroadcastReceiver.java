package com.example.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class TurnusBroadcastReceiver extends BroadcastReceiver {

    private static final String TURNUS_ACTION = "com.example.alarm.IntentAction.RECEIVE_TURNUS_UPDATE";


    //TODO if an alarm is deleted or edited in its time, unschedule this first, it will throw errors, or corrupt the data otherwise

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(TURNUS_ACTION.equals(intent.getAction())){

            //setting the new turnus & dateOfToggle

            DBHelper db = new DBHelper(context, "Database.db");
            Alarm alarm = db.getAlarm(intent.getIntExtra("id",-1));
            alarm.turnusToggle = !alarm.turnusToggle;
            alarm.dateOfToggle = Calendar.getInstance(); //returns calendar object set to now


            //scheduling the next broadcast doing the above
            Intent broadcastTurnusIntent = new Intent("com.example.alarm.IntentAction.RECEIVE_TURNUS_UPDATE");
            broadcastTurnusIntent.putExtra("id", alarm.getID());
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, broadcastTurnusIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //todo waaaay later, try to make this able to also work, when ScheduleExactAlarm Permission is revoked

            am.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + (alarm.getTurnus()* 24L*3600*1000), pi);

            db.saveAlarmToDB(alarm, true);

        }
    }
}
