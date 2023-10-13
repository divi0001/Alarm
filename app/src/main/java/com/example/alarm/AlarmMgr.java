package com.example.alarm;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.widget.SeekBar;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
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




        Intent bI = new Intent("com.example.alarm.IntentAction.RECEIVE_TURNUS_UPDATE");
        bI.putExtra("id",alarm.getID());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, bI, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //todo waaaay later try to implement this without needing exactAlarm Permission
        am.setExactAndAllowWhileIdle(AlarmManager.RTC, alarmTime.getTimeInMillis(), pi);



        try(DBHelper db = new DBHelper(context, "Database.db")) {
            db.saveAlarmToDB(alarm, true);
        }
        return alarmTime.getTimeInMillis();
    }



    public boolean nextOrAlarmOff(Alarm alarm) {
        int lvlID = context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
        int queueID = context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
        if(alarm.isHasLevels()){
            if(alarm.getlQueue().get(lvlID).getmQueue().size()-1 == queueID){
                if(alarm.getlQueue().size()-1 == lvlID){
                    context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_lvl_id", -1).apply();
                    context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", 0).apply();
                }
                lvlID++;
                context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_lvl_id", lvlID).apply();
                context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", 0).apply();

            }else if(lvlID != -1){
                queueID++;
                context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
            }else{
                if(alarm.getmQueue(-1).size()-1 == queueID){
                    //todo cleanup after alarm is done
                    com.example.alarm.AlarmReceiver.r.stop();
                    return false; // == finish()
                }else{
                    queueID++;
                    context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
                }
            }
        }else{
            if (alarm.getmQueue(-1).size()-1 == queueID){
                //todo cleanup after alarm is done
                com.example.alarm.AlarmReceiver.r.stop();
                return false; // == finish()
            }else{
                queueID++;
                context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
            }
        }
        return true;
    }


    public void resetToUserProgress(SeekBar seekBar, int progress, ValueAnimator anim, long taskTime) {
        seekBar.setProgress(progress);
        anim.cancel();

        anim = ValueAnimator.ofInt(progress, seekBar.getMax());

        anim.setDuration((long) (taskTime-(progress/seekBar.getMax()*1F)));
        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            seekBar.setProgress(animProgress);
        });
        anim.start();
    }


    public Enums.Difficulties getAlarmDifficulty(Alarm alarm){
        Enums.Difficulties difficulty;
        int lvlID = getLvlID();
        int queueID = getQueueID();
        if(alarm.isHasLevels()){
            difficulty = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getDifficulty();
        }else {
            difficulty = alarm.getmQueue(-1).get(queueID).getDifficulty();
        }
        return difficulty;
    }


    public Enums.SubMethod getAlarmMode(Alarm alarm){
        Enums.SubMethod mode;
        int lvlID = getLvlID();
        int queueID = getQueueID();
        if(alarm.isHasLevels()){
            mode = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getSubMethod();
        }else {
            mode = alarm.getmQueue(-1).get(queueID).getSubMethod();
        }
        return mode;
    }

    public int getLvlID(){
        return context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",-1);
    }

    public int getQueueID(){
        return context.getSharedPreferences(context.getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",-1);
    }


    public ValueAnimator makeAnim(int maxSeekBar, SeekBar seekBar) {
        ValueAnimator anim = ValueAnimator.ofInt(0, maxSeekBar);
        anim.setDuration(getTaskTime());
        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            seekBar.setProgress(animProgress);
        });
        anim.start();
        return anim;
    }

    public long getTaskTime() {
        return context.getSharedPreferences(context.getString(R.string.settings_key),MODE_PRIVATE).getInt("time_per_task", 30)*1000L;

    }


    public double radiusToZoom(double radius, double latitude) {

        DisplayMetrics dM = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dM);
        int width = dM.widthPixels;
        double[][] zoomTable;
        zoomTable = new double[][]{
                {78271.484, 39135.742, 19567.871, 9783.936, 4891.968, 2445.984, 1222.992, 611.496, 305.748, 152.874, 76.437, 38.218, 19.109, 9.555, 4.777, 2.389, 1.194, 0.597, 0.299, 0.149, 0.075, 0.037, 0.019},
                {73551.136, 36775.568, 18387.784, 9193.892, 4596.946, 2298.473, 1149.237, 574.618, 287.309, 142.655, 71.827, 35.914, 17.957, 8.978, 4.489, 2.245, 1.122, 0.561, 0.281, 0.140, 0.070, 0.035, 0.018},
                {59959.436, 29979.718, 14989.859, 7494.929, 3747.465, 1873.732, 936.866, 468.433, 234.217, 117.108, 58.554, 29.227, 14.639, 7.319, 3.660, 1.830, 0.915, 0.457, 0.229, 0.114, 0.057, 0.029, 0.014},
                {39135.742, 19567.871, 9783.936, 4891.968, 2445.984, 1222.992, 611.496, 305.748, 152.874, 76.437, 38.218, 19.109, 9.555, 4.777, 2.389, 1.194, 0.597, 0.299, 0.149, 0.075, 0.037, 0.019, 0.009},
                {13591.701, 6795.850, 3397.925, 1698.963, 849.481, 424.741, 212.370, 106.185, 53.093, 26.546, 13.273, 6.637, 3.318, 1.659, 0.830, 0.410, 0.207, 0.104, 0.052, 0.026, 0.013, 0.006, 0.003}
        };

        int latIndex = (int) latitude/20;
        if(latIndex < 0){
            latIndex *= -1;
        }

        double[] meterPerPixel = zoomTable[latIndex];

        for(int i = 0; i < meterPerPixel.length; i++){

            if((double)width*meterPerPixel[i] <= radius){

                if(i>3) return i-4;
                return 0;
            }

        }

        return 22.0;

    }

    public ArrayList<LatLng> polygonCircleForCoordinate(LatLng location, double radius){
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = location.getLatitude() * Math.PI / 180;
        double centerLonRadians = location.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> polygons = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            polygons.add(point);
        }
        return polygons;
    }


}
