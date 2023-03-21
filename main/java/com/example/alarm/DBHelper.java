package com.example.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper{


    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Alarmdatabase(id INT primary key, label TEXT, method_queue_list TEXT, sound_path TEXT, privilege_rights INT, snoozable_list TEXT, time_wake_up DATETIME, days_schedule TEXT, weeks_schedule TEXT, check_awake INT, alarm_level_table TEXT)");
        //every int representing a bool is -1 for false
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int verOld, int verNew) {
        db.execSQL("drop Table if exists Alarmdatabase");
    }

    public boolean insertAlarmData(String[] key, String[] value){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i < key.length; i++) {
            if(value[i] == null){
                continue;
            }
            //TODO: handle, when to put ints, also don't forget where to put them from where ever this is called
            contentValues.put(key[i], value[i]);
        }
        long res = db.insert("Alarmdatabase",null, contentValues);
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Alarmdatabase", null);
        return cursor;
    }

}
