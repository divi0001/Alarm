package com.example.alarm;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Alarm implements java.io.Serializable {

    private CharSequence t;
    int ID;

    //todo extendedPriveleges as a global setting, not per Alarm
    private boolean isActive, snoozable, extraAwakeCheck, hasLevels;
    private String soundPath, label;
    private boolean[] weekDays;
    private ArrayList<AlarmMethod> mQueue;
    private ArrayList<AlarmLevel> lQueue;


    public Alarm(CharSequence t, int id, String soundPath, boolean isActive, boolean[] weekDays, boolean snoozable, String label,
                 boolean extraAwakeCheck, boolean hasLevels, @Nullable ArrayList<AlarmMethod> mQueue, @Nullable ArrayList<AlarmLevel> lQueue) {
        this.t = t; //t = time?
        this.ID = id;
        this.isActive = isActive;
        this.weekDays = weekDays;


        this.hasLevels = hasLevels;
        if (hasLevels) {
            this.lQueue = lQueue;
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
        this.soundPath = ""; //todo change this to the location of the standard alarm

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
        if(this.hasLevels) return this.lQueue.get(position).isSnoozable();
        return snoozable;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setSnoozable(boolean snoozable, int position) {
        if(this.hasLevels) this.lQueue.get(position).setSnoozable(snoozable);
        else this.snoozable = snoozable;
    }
    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public boolean isExtraAwakeCheck(int position) {
        if(this.hasLevels) return this.lQueue.get(position).isExtraAwakeCheck();
        return extraAwakeCheck;
    }
    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setExtraAwakeCheck(boolean extraAwakeCheck, int position) {
        if(this.hasLevels) this.lQueue.get(position).setExtraAwakeCheck(extraAwakeCheck);
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
        if(this.hasLevels) return this.lQueue.get(position).getLvlSoundPath();
        return soundPath;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setSoundPath(String soundPath, int position) {
        if(this.hasLevels) this.lQueue.get(position).setLvlSoundPath(soundPath);
        else this.soundPath = soundPath;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public String getLabel(int position) {
        if(this.hasLevels) return this.lQueue.get(position).getLabel();
        return label;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */

    public void setLabel(String label, int position) {
        if(this.hasLevels) this.lQueue.get(position).setLabel(label);
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
        if(this.hasLevels) return this.lQueue.get(position).getmQueue();
        return mQueue;
    }

    /**
     *
     * @param position, -1 if no position
     * @return
     */
    public void setmQueue(ArrayList<AlarmMethod> mQueue, int position) {
        if(this.hasLevels) this.lQueue.get(position).setmQueue(mQueue);
        else this.mQueue = mQueue;
    }

    public ArrayList<AlarmLevel> getlQueue() {
        return lQueue;
    }

    public void setlQueue(ArrayList<AlarmLevel> lQueue) {
        this.lQueue = lQueue;
    }

    public String mainMethod(int position){
        if(this.hasLevels) return "Multiple Lvls, first method is "+ this.lQueue.get(position).getmQueue().get(0).toString();
        else if (this.mQueue.size()>1) return "Multiple Methods, fist is " + this.mQueue.get(0).toString();
        else if (this.mQueue.size() == 1) return this.mQueue.get(0).toString();
        else return "No method set yet, default tap off, if nothing is chosen";
    }

    public String mainSoundPath(int pos){
        return this.getSoundPath(pos);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "t=" + t +
                ", ID=" + ID +
                ", isActive=" + isActive +
                ", snoozable=" + snoozable +
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
