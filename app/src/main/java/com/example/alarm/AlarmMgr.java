package com.example.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
public class AlarmMgr {
    private Context context;

    public AlarmMgr(Context context) {
        this.context = context;
    }


    @SuppressLint("ScheduleExactAlarm")
    public long calculateNextAlarmTime(Alarm alarm) {
        Calendar currentCalendar = Calendar.getInstance();


        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        alarmTime.set(Calendar.MINUTE, alarm.getMinute());
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);

        // (index+1)%7 == index_of_calendar_day_of_week --> reformatting the list, so sundays value comes first, then the other weekdays in order

        boolean[] we = alarm.getWeekDays();
        for(int i = we.length-1; i > 0; i--){
            boolean temp = we[i-1];
            we[i-1] = we[i];
            we[i] = temp;
        }


        while (!we[alarmTime.get(Calendar.DAY_OF_WEEK)-1]) {
            alarmTime.add(Calendar.DAY_OF_WEEK, 1);
        }

        //int daysPassed = 0;
        //int daysToNextAlarm;
        //int turnusDays = alarm.getTurnus();

        //todo(set something, that schedules a toggle of turnusToggle, when its due, together with setting dateOfToggle and scheduling the alarm in question)

        //daysPassed = (int) ((currentCalendar.getTimeInMillis() - alarm.dateOfToggle.getTimeInMillis()) / (24 * 60 * 60 * 1000));
        //daysToNextAlarm = (turnusDays - (daysPassed % turnusDays)) % turnusDays;

        //if (!alarm.turnusToggle && turnusDays > -1) {
        //    daysToNextAlarm += turnusDays;
        //}


        if(!alarm.turnusToggle) {


            int daysToNextAlarm = (int) ((currentCalendar.getTimeInMillis() - alarmTime.getTimeInMillis()) / (24 * 60 * 60 * 1000));

            alarmTime.add(Calendar.DAY_OF_YEAR, daysToNextAlarm);

        }

        if(alarm.alarmManager != null){
            alarm.alarmManager.cancel(PendingIntent.getBroadcast(context, 0 , new Intent("com.example.alarm.IntentAction.RECEIVE_TURNUS_UPDATE"), PendingIntent.FLAG_IMMUTABLE));
        }


        Intent bI = new Intent("com.example.alarm.IntentAction.RECEIVE_TURNUS_UPDATE");
        bI.putExtra("id",alarm.getID());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, bI, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //todo waaaay later try to implement this without needing exactAlarm Permission
        am.setExactAndAllowWhileIdle(AlarmManager.RTC, alarmTime.getTimeInMillis(), pi);



        alarm.alarmManager = am;
        DBHelper db = new DBHelper(context, "Database.db");
        db.saveAlarmToDB(alarm, true);

        return alarmTime.getTimeInMillis();
    }
}
