package com.example.alarm;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{
    
    Context context;

    public DBHelper(Context context, String db) {
        
        super(context, db, null, 1);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create Table Alarmtable (id INTEGER primary key , Alarm TEXT)");

        db.execSQL("create Table QRBarcodedatabase (label TEXT primary key, decoded TEXT)");
        //setupTablesForPreset(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int verOld, int verNew) {

        db.execSQL("drop Table if exists Alarmtable");
        db.execSQL("drop Table if exists QRBarcodedatabase");
        onCreate(db);
    }




    /**
     * @param database: the string of the table in database.db
     * @return the max id in the table, if db empty it returns 0, assumes the table to have id integer as a primary unique key
     */
    public int getMaxTableId(String database){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c1 = db.rawQuery("SELECT max(id) FROM " + database, null);
        if(c1.getCount() >0){
            int id;
            c1.moveToFirst();
            id = c1.getInt(0);
            c1.close();

            return id;
        }
        return 0;
    }


    //TODO: I don't need to handle SQLInjection, at least for now, because sharing of settings is not supported as of now, so this would only harm the person itself, or be a nice niche feature lol
    //(this might change in the future, so i will keep this todo in until publishing, so if i add that feature at any point, i'll know, that i have to take care of that.)







    public void deleteRow(String table, int row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(table, "id=?", new String[]{String.valueOf(row_id)});
        if(res ==0){
            Toast.makeText(context, "Failed deleting item: " + res, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Success deleting item: " +res, Toast.LENGTH_SHORT).show();
        }
    }




        //ouch, really hurts deleting THAT many lines of now useless code D_:


        public void addQRBar(String label, String decoded){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("label", label);
        cv.put("decoded", decoded);
        long res = db.insert("QRBarcodedatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into QRBarcodedatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into QRBarcodedatabase", Toast.LENGTH_SHORT).show();
        }
    }



    public Cursor getData(String database ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;

        if (db != null){
        c = db.rawQuery("Select * from "+database, null);
        }
        return c;
    }

    public Cursor execQuery(String sql, String[] selectionArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(sql, selectionArgs);

    }

    public void saveAlarmToDB(Alarm alarmParameter, boolean edit) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (!edit)  cv.put("id", alarmParameter.getID());
        String alarm = new Gson().toJson(alarmParameter);
        cv.put("Alarm", alarm);
        if(edit){
            db.update("Alarmtable", cv, "id=?", new String[]{Integer.toString(alarmParameter.getID())});
        }else{
            db.insert("Alarmtable", null, cv);
        }
    }



    public Alarm getAlarm(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor c = db.rawQuery("Select Alarm From Alarmtable where id=?", new String[]{Integer.toString(id)});

        if(c.getCount()>0){
            c.moveToFirst();
            String json = c.getString(0);
            return new Gson().fromJson(json, Alarm.class);
        }else{
            return new Alarm(-1);
        }
    }

    public int getMaxAlarmID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("Select max(id) from Alarmtable", new String[]{});
        if(c.getCount() > 0){
            c.moveToFirst();
            int ret = c.getInt(0);
            c.close();
            return ret;
        }else{
            c.close();
            return -1;
        }

    }



    public ArrayList<Alarm> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from Alarmtable", new String[]{});
        ArrayList<Alarm> alarms = new ArrayList<>();

        if(c.getCount()>0){
            while(c.moveToNext()) alarms.add(new Gson().fromJson(c.getString(1), Alarm.class));
        }
        db.close();
        c.close();
        return alarms;



    }
}
