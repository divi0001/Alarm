package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;

public class PendingAlarm {

    private AlarmManager alarmManager;
    private int id;
    private Alarm alarm;
    private PendingIntent pendingIntent;

    public PendingAlarm(AlarmManager alarmManager, int id, Alarm alarm) {
        this.alarmManager = alarmManager;
        this.id = id;
        this.alarm = alarm;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    public void setAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    @Override
    public String toString() {
        return "PendingAlarm{" +
                "alarmManager=" + alarmManager +
                ", id=" + id +
                ", alarm=" + alarm +
                ", pendingIntent=" + pendingIntent +
                '}';
    }
}
