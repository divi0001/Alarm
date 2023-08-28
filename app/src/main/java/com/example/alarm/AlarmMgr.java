package com.example.alarm;

import android.content.Context;

import java.util.Calendar;
public class AlarmMgr {
    private Context context;

    public AlarmMgr(Context context) {
        this.context = context;
    }


    public long calculateNextAlarmTime(Alarm alarm, long currentTimeMillis) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTimeMillis);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        alarmTime.set(Calendar.MINUTE, alarm.getMinute());
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);

        while (!alarm.getWeekDays()[alarmTime.get(Calendar.DAY_OF_WEEK) - 1]) {
            alarmTime.add(Calendar.DAY_OF_WEEK, 1);
        }

        int daysPassed = (int) ((currentCalendar.getTimeInMillis() - alarm.dateOfToggle.getTimeInMillis()) / (24 * 60 * 60 * 1000));
        int turnusDays = alarm.getTurnus();
        int daysToNextAlarm = (turnusDays - (daysPassed % turnusDays)) % turnusDays;

        if (!alarm.turnusToggle) {
            daysToNextAlarm += turnusDays;
        }

        alarmTime.add(Calendar.DAY_OF_YEAR, daysToNextAlarm);

        return alarmTime.getTimeInMillis();
    }
}
