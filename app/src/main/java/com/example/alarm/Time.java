package com.example.alarm;

import android.util.Log;

public class Time implements java.io.Serializable {

    private int weeks;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private int miliseconds;
    private int nanoseconds;

    public Time(int hours, int minutes) {
        if (hours < 24 && hours >= 0){
        this.hours = hours;}
        if(minutes < 60 && minutes >= 0){
        this.minutes = minutes;}
        Log.d("Time","set hours+minutes, prolly alarm");
    }

    public Time(int weeks, int days, int hours, int minutes) {
        if(weeks >= 0) this.weeks = weeks;
        if (days >= 0 && days < 7 ) this.days = days;
        if (hours < 24 && hours >= 0){
            this.hours = hours;}
        if(minutes < 60 && minutes >= 0){
            this.minutes = minutes;}
        Log.d("Time","set weeks-minutes, prolly reminder, or turnus");
    }

    public Time(int weeks, int days, int hours, int minutes, int seconds, int miliseconds, int nanoseconds) {
        if(weeks >= 0) this.weeks = weeks;
        if (days >= 0 && days < 7 ) this.days = days;
        if (hours < 24 && hours >= 0){
            this.hours = hours;}
        if(minutes < 60 && minutes >= 0){
            this.minutes = minutes;}
        if(seconds >= 0 && seconds < 60) this.seconds = seconds;
        if(miliseconds >= 0 && miliseconds < 1000)this.miliseconds = miliseconds;
        if(nanoseconds >= 0 && nanoseconds < 1000000)this.nanoseconds = nanoseconds;
        Log.d("Time","set weeks-nanosecs, prolly stopwatch, or timer");
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
        this.weeks = weeks;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMiliseconds() {
        return miliseconds;
    }

    public void setMiliseconds(int miliseconds) {
        this.miliseconds = miliseconds;
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    public void setNanoseconds(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }

    @Override
    public String toString() {
        return "Time{" +
                "weeks=" + weeks +
                ", days=" + days +
                ", hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                ", miliseconds=" + miliseconds +
                ", nanoseconds=" + nanoseconds +
                '}';
    }
}


