package com.example.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{
    
    Context context;

    public DBHelper(Context context, String db) {
        
        super(context, db, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Alarmdatabase (id INTEGER primary key autoincrement, label TEXT, method_queue_id INTEGER, sound_path_id INTEGER, privilege_rights INTEGER, snoozable INTEGER, time_wake_up_hours INTEGER, time_wake_up_minutes INTEGER, days_schedule_id INTEGER, weeks_schedule_id INTEGER, check_awake INTEGER, alarm_level_table_id INTEGER)");
        //every int representing a bool is -1 for false                         this is the id of the table and the item in it, for the corresponding method

        db.execSQL("create Table Methoddatabase (id INTEGER primary key autoincrement, queue_id INTEGER, method_type_id INTEGER, method_id INTEGER, difficulty_id INTEGER, label TEXT, method_database_specific_id INTEGER)"); //multiple rows with same queue_id are part of the same queue
        db.execSQL("create Table Methodtype (id INTEGER primary key autoincrement, method_type TEXT)");
        db.execSQL("create Table Method (id INTEGER primary key autoincrement, method TEXT)");
        db.execSQL("create Table Difficulty (id INTEGER primary key autoincrement, difficulty TEXT)");

        db.execSQL("create Table Alarmlevel (id INTEGER primary key autoincrement, level_id INTEGER, method_databse_queue_id INTEGER)"); //multiple rows with the same level_id make up the different Alarmlevels, might add more attribs later

        db.execSQL("create Table QRBarcodedatabase (label TEXT primary key, decoded TEXT)");
        db.execSQL("create Table Mathdatabase (id INTEGER primary key autoincrement, method TEXT, difficulty TEXT)");
        db.execSQL("create Table Locationdatabase (id INTEGER primary key autoincrement, latitude_int INTEGER, zero_point_latitude INTEGER, longitude_int INTEGER, zero_point_longitude INTEGER, radius_int INTEGER, zero_point_radius INTEGER, street TEXT, radius_mode TEXT)");
        setupTablesForPreset(db);
    }

    private void setupTablesForPreset(SQLiteDatabase db) {


        ContentValues cv = new ContentValues();

        long res;
        for(String type : new String[]{"tap_off","math","qr_barcode","location","sudoku","memory","passphrase"}) {
            cv.put("method_type", type);
            res = db.insert("Methodtype", null, cv);
            if(res == -1){
                Toast.makeText(context, "Error setting up static Database \"Methodtype\"", Toast.LENGTH_SHORT).show();
            }
        }


        cv = new ContentValues();
        for(String type : new String[]{"null","add","sub","mult","div","fac","root","value_fx","extrema_fx","multiple_choice","reach_radius","leave_radius"}) {
            cv.put("method", type);
            res = db.insert("Method", null, cv);
            if(res == -1){
                Toast.makeText(context, "Error setting up static Database \"Method\"", Toast.LENGTH_SHORT).show();
            }
        }


        cv = new ContentValues();
        for(String type : new String[]{"ex_easy","easy","middle","hard","ex_hard"}) {
            cv.put("difficulty", type);
            res = db.insert("Difficulty", null, cv);
            if(res == -1){
                Toast.makeText(context, "Error setting up static Database \"Difficulty\"", Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int verOld, int verNew) {
        db.execSQL("drop Table if exists Alarmdatabase");

        db.execSQL("drop Table if exists Methoddatabase");
        db.execSQL("drop Table if exists Methodtype");
        db.execSQL("drop Table if exists Method");
        db.execSQL("drop Table if exists Difficulty");

        db.execSQL("drop Table if exists Alarmlevel");

        db.execSQL("drop Table if exists QRBarcodedatabase");
        db.execSQL("drop Table if exists Mathdatabase");
        db.execSQL("drop Table if exists Locationdatabase");
        onCreate(db);
    }

    //TODO: I don't need to handle SQLInjection, atleast for now, because sharing of settings is not supported as of now, so this would only harm the person itself, or be a nice niche feature lol
    //(this might change in the future, so i will keep this todo in until publishing, so if i add that feature at any point, i'll know, that i have to take care of that.)

    public void addAlarm(String label, int methodQueueId, int soundPathId, boolean privilegeRights, boolean snoozable, int wakeUpTimeHours, int wakeUpTimeMinutes, int daysScheduleId, int weeksScheduleId, boolean checkAwake, int alarmLevelTableId){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("label", label);
        cv.put("method_queue_id",methodQueueId);
        cv.put("sound_path_id",soundPathId);
        if(privilegeRights){
        cv.put("privilege_rights",1);}
        else{
            cv.put("privilege_rights", 0);}
        if(snoozable){
        cv.put("snoozable",1);}
        else{
            cv.put("snoozable",0);}
        cv.put("time_wake_up_hours", wakeUpTimeHours);
        cv.put("time_wake_up_minutes", wakeUpTimeMinutes);
        cv.put("days_schedule_id",daysScheduleId);
        cv.put("weeks_schedule_id",weeksScheduleId);
        if(checkAwake){
        cv.put("check_awake", 1);}else{
            cv.put("check_awake", 0);}
        cv.put("alarm_level_table_id",alarmLevelTableId);
        long res = db.insert("Alarmdatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Alarmdatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Alarmdatabase", Toast.LENGTH_SHORT).show();
        }

    }


    public void addMethod(int queueID, int methodTypeId, int methodId, int difficultyId, String label, int methodDatabaseSpecificID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("queue_id",queueID);
        cv.put("method_type_id",methodTypeId);
        cv.put("method_id",methodId);
        cv.put("difficulty_id",difficultyId);
        cv.put("label",label);
        cv.put("method_database_specific_id", methodDatabaseSpecificID);    //This is the id, that (if necessary) points to the specific data of the method database defined by method_type_id
                                                                            //In easy words: if you need to look up the decoded string from qrcode method, this points to the id in the specific database, that has this info
        long res = db.insert("Methoddatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Methoddatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Methoddatabase", Toast.LENGTH_SHORT).show();
        }
    }



    public String getMethodById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Method WHERE ?", new String[]{"id =" + id});

        if(c.getCount() == 0){

            return "non existent";
        }else{
            c.moveToNext();
            return c.getString(1);
            }
        }


    public String getMethodTypeById(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM MethodType WHERE ?", new String[]{"id =" + id});

        if(c.getCount() > 0){

            c.moveToFirst();
            return c.getString(1);

        }else{
            return "non existent";


        }
    }

    public String getDifficulty(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Difficulty WHERE ?", new String[]{"id =" + id});

        if(c.getCount() == 0){

            return "non existent";
        }else{
            c.moveToNext();
            return c.getString(1);
        }
    }




    public int findIdByMethodType(String methodType){

        String[] methArr = new String[]{"tap_off","math","qr_barcode","location","sudoku","memory","passphrase"};
        for(int i = 0; i < methArr.length; i++) {
            if(methArr[i].equals(methodType)){
                return i+1;
            }
        }
        return -1;
    }

    public int findIdByMethod(String method){
        String[] meArr = new String[]{"null","Addition","Subtraction","Multiplication","Division","Faculty (x!)","Root","Value for f(x)","Determine extrema of f(x)","Multiple choice questions","reach_radius","leave_radius"};
        for(int i = 0; i < meArr.length; i++){
            if(meArr[i].equals(method)){
                return i+1;
            }
        }
        return -1;
    }

    public int findIdByDifficulty(String difficulty){
        switch (difficulty) {
            case "Extremely easy":
                return 1;
            case "Easy":
                return 2;
            case "Middle":
                return 3;
            case "Hard":
                return 4;
            case "Extremely hard":
                return 5;
            default:
                return -1;
        }
    }


    public void addMath(String method, String difficulty){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("method",method);
        cv.put("difficulty",difficulty);
        long res = db.insert("Mathdatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Mathdatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Mathdatabase", Toast.LENGTH_SHORT).show();
        }
    }

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
    //        Locationdatabase latitude_int INTEGER, zero_point_latitude INTEGER, longitude_int INTEGER, zero_point_longitude INTEGER, radius_int INTEGER, zero_point_radius INTEGER, street TEXT)");

    public void addLocation(int latitudeInt, int zeroPointLatitude, int longitudeInt, int zeroPointLongitude, int radiusInt, int zeroPointRadius, String street, String radiusMode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("latitude_int",latitudeInt);
        cv.put("zero_point_latitude",zeroPointLatitude);
        cv.put("longitude_int",longitudeInt);
        cv.put("zero_point_longitude",zeroPointLongitude);
        cv.put("radius_int",radiusInt);
        cv.put("zero_point_radius",zeroPointRadius);
        cv.put("street",street);
        cv.put("radius_mode", radiusMode);
        long res = db.insert("Locationdatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Locationdatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Locationdatabase", Toast.LENGTH_SHORT).show();
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
