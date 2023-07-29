package com.example.alarm;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmLevel {

    private ArrayList<AlarmMethod> mQueue;
    private String label, soundPath;
    private boolean snoozable, extraAwakeCheck;


    public AlarmLevel(ArrayList<AlarmMethod> mQueue, String label, String soundPath, boolean snoozable, boolean extraAwakeCheck){
        this.mQueue = mQueue;
        this.label = label;
        this.snoozable = snoozable;
        this.soundPath = soundPath;
        this.extraAwakeCheck = extraAwakeCheck;
    }

    public ArrayList<AlarmMethod> getmQueue() {
        return mQueue;
    }

    public void setmQueue(ArrayList<AlarmMethod> mQueue) {
        this.mQueue = mQueue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLvlSoundPath() {
        return soundPath;
    }

    public void setLvlSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public boolean isSnoozable() {
        return snoozable;
    }

    public void setSnoozable(boolean snoozable) {
        this.snoozable = snoozable;
    }

    public boolean isExtraAwakeCheck() {
        return extraAwakeCheck;
    }

    public void setExtraAwakeCheck(boolean extraAwakeCheck) {
        this.extraAwakeCheck = extraAwakeCheck;
    }

    @Override
    public String toString() {
        return "AlarmLevel{" +
                "mQueue=" + mQueue +
                ", label='" + label + '\'' +
                ", soundPath='" + soundPath + '\'' +
                ", snoozable=" + snoozable +
                ", extraAwakeCheck=" + extraAwakeCheck +
                '}';
    }
}
