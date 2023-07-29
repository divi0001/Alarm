package com.example.alarm;

import androidx.annotation.Nullable;

public class Alarm implements java.io.Serializable {

    private CharSequence t;
    private String turnOffMethod;
    int ID;
    private String soundPath;
    private boolean extendedPrivileges;
    private boolean isActive, snoozable, extraAwakeCheck, hasLevels;
    private String difficulty,type,label;
    private boolean[] weekDays;


    public Alarm(CharSequence t, int id, String soundPath, boolean extendedPrivileges, boolean isActive, boolean[] weekDays, boolean snoozable, String label,
                 boolean extraAwakeCheck, boolean hasLevels, @Nullable AlarmMethod[] mQueue, @Nullable AlarmLevel[] lQueue) {
        this.t = t; //t = time?
        this.ID = id;
        this.soundPath = soundPath;
        this.extendedPrivileges = extendedPrivileges;
        this.isActive = isActive;
        this.weekDays = weekDays;
        this.snoozable = snoozable;
        this.label = label;
        this.extraAwakeCheck = extraAwakeCheck;
        this.hasLevels = hasLevels;
        if (hasLevels) this.lQueue = lQueue;
        this.mQueue = mQueue;
    }

    public Alarm(int id){

        this.ID = id;

    }


}
