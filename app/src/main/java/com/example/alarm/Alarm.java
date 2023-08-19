package com.example.alarm;

import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Alarm implements java.io.Serializable  {

    private CharSequence t;
    int ID,selectedLvl, snoozeAmount, snoozeMinutes, minutesUntilTurnBackOn, turnus;

    //todo extendedPriveleges as a global setting, not per Alarm
    private boolean isActive, snoozable, extraAwakeCheck, hasLevels;
    private String soundPath, label;
    private boolean[] weekDays;
    private ArrayList<AlarmMethod> mQueue = new ArrayList<>();
    private ArrayList<AlarmLevel> lQueue = new ArrayList<>();


    public Alarm(CharSequence t, int id, String soundPath, boolean isActive, boolean[] weekDays, boolean snoozable, String label,
                 boolean extraAwakeCheck, boolean hasLevels, @Nullable ArrayList<AlarmMethod> mQueue, @Nullable ArrayList<AlarmLevel> lQueue, int turnus) {
        this.t = t; //t = time
        this.ID = id;
        this.isActive = isActive;
        this.weekDays = weekDays;
        this.turnus = turnus;

        this.hasLevels = hasLevels;
        if (hasLevels) {
            this.lQueue = lQueue;
            this.selectedLvl = 0;
        }else{
            this.mQueue = mQueue;
            this.label = label;
            this.snoozable = snoozable;
            this.soundPath = soundPath;
            this.extraAwakeCheck = extraAwakeCheck;

        }
    }

    /**
     * @implNote creates Alarm with its id, automatically adding preset informations (isActive=true, weekDays all false, snoozable false, extraAwakeCheck false, hasLevels false, mQueue empty ArrayList<AlarmMethod>, soundpath = "", t="currDateTime")
     * @param id
     */
    public Alarm(int id){
        this.ID = id;
        this.isActive = true;
        this.weekDays = new boolean[]{false,false,false,false,false,false,false};
        this.snoozable = false;
        this.extraAwakeCheck = false;
        this.hasLevels = false;
        this.mQueue = new ArrayList<AlarmMethod>();
        this.soundPath = "android.resource://com.example.alarm/"+ R.raw.weak_1; //should be the standard sound (R.raw.weak_1)

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.t = dtf.format(now);
    }

    public CharSequence getT() {
        return t;
    }

    public void setT(CharSequence t) {
        this.t = t;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public boolean isSnoozable(int position) {
        if(position > -1) return this.lQueue.get(position).isSnoozable();
        return snoozable;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setSnoozable(boolean snoozable, int position) {
        if(position > -1) this.lQueue.get(position).setSnoozable(snoozable);
        else this.snoozable = snoozable;
    }
    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public boolean isExtraAwakeCheck(int position) {
        if(position > -1) return this.lQueue.get(position).isExtraAwakeCheck();
        return extraAwakeCheck;
    }
    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setExtraAwakeCheck(boolean extraAwakeCheck, int position) {
        if(position > -1) this.lQueue.get(position).setExtraAwakeCheck(extraAwakeCheck);
        else this.extraAwakeCheck = extraAwakeCheck;
    }

    public boolean isHasLevels() {
        return hasLevels;
    }

    public void setHasLevels(boolean hasLevels) {
        this.hasLevels = hasLevels;
    }


    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public String getSoundPath(int position) {
        if(position > -1) return this.lQueue.get(position).getLvlSoundPath();
        return soundPath;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setSoundPath(String soundPath, int position) {
        if(position > -1) this.lQueue.get(position).setLvlSoundPath(soundPath);
        else this.soundPath = soundPath;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public String getLabel(int position) {
        if(position > -1) return this.lQueue.get(position).getLabel();
        return label;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */

    public void setLabel(String label, int position) {
        if(position > -1) this.lQueue.get(position).setLabel(label);
        else this.label = label;
    }

    public boolean[] getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(boolean[] weekDays) {
        this.weekDays = weekDays;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public ArrayList<AlarmMethod> getmQueue(int position) {
        if(position > -1) return this.lQueue.get(position).getmQueue();
        return mQueue;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setmQueue(ArrayList<AlarmMethod> mQueue, int position) {
        if(position > -1){ this.lQueue.get(position).setmQueue(mQueue);
            Log.d("currentAlarmParamSetAlarmMQ", lQueue.toString()+position);}
        else this.mQueue = mQueue;
    }

    public ArrayList<AlarmLevel> getlQueue() {
        return lQueue;
    }

    public void setlQueue(ArrayList<AlarmLevel> lQueue, int selectedlvl) {
        this.lQueue = lQueue;
        setSelectedLvl(selectedlvl);
    }

    public int getSelectedLvl() {
        return selectedLvl;
    }

    public void setSelectedLvl(int selectedLvl) {
        this.selectedLvl = selectedLvl;
    }

    public String mainMethod(int position){
        if(position > -1) return "Multiple Lvls, first method is "+ this.lQueue.get(position).getmQueue().get(0).toString();
        else if (this.mQueue.size()>1) return "Multiple Methods, fist is " + this.mQueue.get(0).toString();
        else if (this.mQueue.size() == 1) return this.mQueue.get(0).toString();
        else return "No method set yet, default tap off, if nothing is chosen";
    }

    public int getSnoozeAmount(int pos) {
        if (pos == -1) return snoozeAmount;
        return this.lQueue.get(pos).getSnoozeAmount();
    }

    public void setSnoozeAmount(int snoozeAmount, int pos) {
        if (pos == -1) this.snoozeAmount = snoozeAmount;
        else {
            AlarmLevel lvl = this.lQueue.get(pos);
            lvl.setSnoozeAmount(snoozeAmount);
        }
    }

    public int getSnoozeMinutes(int pos) {
        if (pos == -1) return snoozeMinutes;
        return this.lQueue.get(pos).getSnoozeMinutes();
    }

    public void setSnoozeMinutes(int snoozeMinutes, int pos) {
        if (pos == -1) this.snoozeMinutes = snoozeMinutes;
        else {
            AlarmLevel lvl = this.lQueue.get(pos);
            lvl.setSnoozeMinutes(snoozeMinutes);
        }
    }

    public int getMinutesUntilTurnBackOn(int pos) {
        if (pos == -1) return minutesUntilTurnBackOn;
        AlarmLevel l = this.lQueue.get(pos);
        return l.getMinutesUntilTurnBackOn();
    }

    public void setMinutesUntilTurnBackOn(int minutesUntilTurnBackOn, int pos) {
        if (pos == -1) this.minutesUntilTurnBackOn = minutesUntilTurnBackOn;
        else{
            AlarmLevel lvl = this.lQueue.get(pos);
            lvl.setMinutesUntilTurnBackOn(minutesUntilTurnBackOn);
        }
    }

    public String mainSoundPath(int pos){
        return this.getSoundPath(pos);
    }

    public int getTurnus() {
        return turnus;
    }

    public void setTurnus(int turnus) {
        this.turnus = turnus;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "t=" + t +
                ", ID=" + ID +
                ", selectedLvl=" + selectedLvl +
                ", snoozeAmount=" + snoozeAmount +
                ", snoozeMinutes=" + snoozeMinutes +
                ", minutesUntilTurnBackOn=" + minutesUntilTurnBackOn +
                ", isActive=" + isActive +
                ", snoozable=" + snoozable +
                ", turnus=" + turnus +
                ", extraAwakeCheck=" + extraAwakeCheck +
                ", hasLevels=" + hasLevels +
                ", soundPath='" + soundPath + '\'' +
                ", label='" + label + '\'' +
                ", weekDays=" + Arrays.toString(weekDays) +
                ", mQueue=" + mQueue +
                ", lQueue=" + lQueue +
                '}';
    }
}
