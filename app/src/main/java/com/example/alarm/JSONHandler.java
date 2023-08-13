/*
package com.example.alarm;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class JSONHandler {

    private Alarm alarm;
    private ArrayList<Alarm> alarmList;

    public JSONHandler() {
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public JSONArray toJAlarm(Alarm alarm) throws JSONException{
        CharSequence t = alarm.getT();
        String method = alarm.getTurnOffMethod();
        String soundPath = alarm.getSoundPath();
        String difficulty = alarm.getDifficulty();
        int ID = alarm.getID();
        JSONArray jArr = new JSONArray();

        //translate [t,method,soundPath,difficulty,ID] to [0,1,2,3,4]

        jArr.put(0,t);
        jArr.put(1,method);
        jArr.put(2,soundPath);
        jArr.put(3,difficulty);
        jArr.put(4,ID);
        jArr.put(5,alarm.isActive());
        return jArr;
    }

    public JSONArray toJAlarmArray(ArrayList<Alarm> alarmList) throws  JSONException{

        JSONArray jArrList = new JSONArray();
        for(Alarm jj : alarmList){
            JSONArray arr = toJAlarm(jj);
            jArrList.put(jArrList.length(),arr);
        }
        return jArrList;
    }

    public ArrayList<Alarm> fromJAlarmArray(JSONArray jArr) throws JSONException {
        ArrayList<Alarm> a = new ArrayList<>();
        for (int i = 0; i < jArr.length(); i++){
            a.add(fromJAlarm(jArr.getJSONArray(i)));
        }

        return a;

    }

    private Alarm fromJAlarm(JSONArray arr) throws JSONException {
        int id = (int) arr.get(4);
        Alarm alarm1 = new Alarm(id);
        CharSequence t = (CharSequence) arr.get(0);
        String method = arr.getString(1);
        String soundPath = arr.getString(2);
        String difficulty = arr.getString(3);

        alarm1.setT(t);
        alarm1.setTurnOffMethod(method);
        alarm1.setSoundPath(soundPath);
        alarm1.setDifficulty(difficulty);
        alarm1.setActive((boolean) arr.get(5));

        return alarm1;
    }

}
*/
//todo i left this here just in case i need it, if it works the next time, the program is runnable, delete this class