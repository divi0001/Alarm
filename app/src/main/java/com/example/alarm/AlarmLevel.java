package com.example.alarm;

import java.util.ArrayList;

public class AlarmLevel {

    private ArrayList<AlarmMethod> mQueue;
    private String label, soundPath;
    private boolean snoozable, extraAwakeCheck;
    private int ID, snoozeAmount, snoozeMinutes, minutesUntilTurnBackOn;


    public AlarmLevel(int id, ArrayList<AlarmMethod> mQueue, String label, String soundPath, boolean snoozable, boolean extraAwakeCheck){
        this.ID = id;
        this.mQueue = mQueue;
        this.label = label;
        this.snoozable = snoozable;
        this.soundPath = soundPath;
        this.extraAwakeCheck = extraAwakeCheck;
    }

    /**
     * @implNote sets everything to a standard value
     */
    public AlarmLevel(int id){
        this.ID = id;
        this.mQueue = new ArrayList<>();
        this.label = "Please set a unique label";
        this.snoozable = false;
        this.soundPath = "android.resource://com.example.alarm/"+ R.raw.weak_1; //should be the standard sound (R.raw.weak_1);
        this.extraAwakeCheck = false;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public int getSnoozeAmount() {
        return snoozeAmount;
    }

    public int getMinutesUntilTurnBackOn() {
        return minutesUntilTurnBackOn;
    }

    public void setMinutesUntilTurnBackOn(int minutesUntilTurnBackOn) {
        this.minutesUntilTurnBackOn = minutesUntilTurnBackOn;
    }

    public void setSnoozeAmount(int snoozeAmount) {
        this.snoozeAmount = snoozeAmount;
    }

    public int getSnoozeMinutes() {
        return snoozeMinutes;
    }

    public void setSnoozeMinutes(int snoozeMinutes) {
        this.snoozeMinutes = snoozeMinutes;
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
                "ID=" + ID +
                ", mQueue=" + mQueue +
                ", label='" + label + '\'' +
                ", soundPath='" + soundPath + '\'' +
                ", snoozable=" + snoozable +
                ", extraAwakeCheck=" + extraAwakeCheck +
                ", snoozeAmount=" + snoozeAmount +
                ", snoozeMinutes=" + snoozeMinutes +
                ", minutesUntilTurnBackOn=" + minutesUntilTurnBackOn +
                '}';
    }
}
