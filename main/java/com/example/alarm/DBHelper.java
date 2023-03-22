package com.example.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{


    public DBHelper(Context context, String db) {
        super(context, db, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Alarmdatabase(id INT primary key, label TEXT, method_queue_list TEXT, sound_path TEXT, privilege_rights TEXT, snoozable_list TEXT, time_wake_up TEXT, days_schedule TEXT, weeks_schedule TEXT, check_awake TEXT, alarm_level_table TEXT)");
        //every int representing a bool is -1 for false                         this is the id of the table and the item in it, for the corresponding method
        db.execSQL("create Table QRBarcodedatabase(label TEXT primary key, decoded TEXT)");
        db.execSQL("create Table Mathdatabase(id TEXT primary key, method TEXT, difficulty TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int verOld, int verNew) {
        db.execSQL("drop Table if exists Alarmdatabase");
        db.execSQL("drop Table if exists QRBarcodedatabase");
        db.execSQL("drop Table if exists Mathdatabase");
    }

    public boolean insertAlarmData(String[] key, String[] value, String tableName){


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < key.length; i++) {
            if (value[i] == null) {
                continue;
            }
            //TODO: handle, when to put ints, also don't forget where to put them from where ever this is called
            //TODO: i don't think i need to handle sqlinjection, because users doing this would only affect their own data right?
            contentValues.put(key[i], value[i]);
        }
        long res = db.insert(tableName, null, contentValues);
        return res != -1;


    }

    public Cursor getData(String database){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from "+database, null);
    }

    public Cursor execQuery(String sql, String[] selectionArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(sql, selectionArgs);

    }

    public int getResInt(Cursor c, int columnIndex) {
        c.moveToNext();
        if(c.getCount() > 1){
            System.out.println("Cursor has more than 1 elem");
        }
        if(c.getCount() == 0){
            System.out.println("Error: Cursor is empty");
        }
        return c.getInt(columnIndex);

    }
}
