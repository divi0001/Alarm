package com.example.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper{
    
    Context context;

    public DBHelper(Context context, String db) {
        
        super(context, db, null, 1);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create Table Alarmdatabase (id INTEGER primary key autoincrement, label TEXT, privilege_rights INTEGER," +
                " time_wake_up_hours INTEGER, time_wake_up_minutes INTEGER, days_schedule_id INTEGER, weeks_schedule_amount INTEGER, check_awake INTEGER)");

        //need final tables for adding: Methoddatabase, Alarmlevel, QRBarcode, Location, dayschedule, i think i don't use the Mathdatabase for functional uses, todo check this, and delete if the data is never used

        db.execSQL("create Table finalMethods(id integer primary key autoincrement, queue_id integer, method_type_id integer, method_id integer, difficulty_id integer," +
                    " label text, method_database_specific_id integer)");
        db.execSQL("create Table finalLevels(id integer primary key autoincrement, label text, snooze_count integer, snooze_time integer, sound_path text," +
                "sound_name text, alarm_id integer)");
        db.execSQL("create Table finalQRBar (label text primary key, decoded text)");
        db.execSQL("create Table finalLocation (id integer primary key autoincrement, lat_int integer, point_lat integer, lon_int integer, point_lon integer, " +
                "radius integer, street text, radius_mode text)");
        db.execSQL("create Table inbetweenDayschedule (id integer primary key autoincrement, schedule_id integer, foreign key (schedule_id) references finalDayschedule(id))"); //todo implement syncing here
        db.execSQL("create Table finalDayschedule (id integer primary key autoincrement, mon integer, tue integer, wed integer, thu integer, fri integer, sat integer, sun integer)");





        db.execSQL("create Table Dayschedule (id INTEGER primary key autoincrement, mon INTEGER, tue INTEGER, wed INTEGER, thu INTEGER, fri INTEGER, sat INTEGER, sun INTEGER)");
        //id, mon, tue, wed, thu, fri, sat, sun

        db.execSQL("create Table Methoddatabase (id INTEGER primary key autoincrement, queue_id INTEGER, method_type_id INTEGER, method_id INTEGER, difficulty_id INTEGER," +
                " label TEXT, method_database_specific_id INTEGER, foreign KEY(method_type_id) references Methodtype(id), foreign key (method_id) references Method(id)," +
                " foreign key (difficulty_id) references Difficulty(id), foreign key(label) references QRBarcodedatabase(label)," +
                " foreign KEY(queue_id) references Alarmlevel(id))");

        db.execSQL("create Table Methodtype (id INTEGER primary key autoincrement, method_type TEXT)");
        db.execSQL("create Table Method (id INTEGER primary key autoincrement, method TEXT)");
        db.execSQL("create Table Difficulty (id INTEGER primary key autoincrement, difficulty TEXT)");

        db.execSQL("create Table Alarmlevel (id INTEGER primary key autoincrement, label TEXT, snooze_count INTEGER, snooze_time INTEGER, sound_path TEXT, " +
                "sound_name TEXT, alarm_id INTEGER, foreign key (alarm_id) references Alarmdatabase(id) )");
        //multiple rows with the same level_id make up the different Alarmlevels

        db.execSQL("create Table QRBarcodedatabase (label TEXT primary key, decoded TEXT)");
        db.execSQL("create Table Mathdatabase (id INTEGER primary key autoincrement, method TEXT, difficulty TEXT)");
        db.execSQL("create Table Locationdatabase (id INTEGER primary key autoincrement, latitude_int INTEGER, zero_point_latitude INTEGER, longitude_int INTEGER," +
                " zero_point_longitude INTEGER, radius_int INTEGER, street TEXT, radius_mode TEXT)");




        setupTablesForPreset(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int verOld, int verNew) {

        db.execSQL("drop Table if exists Alarmdatabase");

        db.execSQL("drop Table if exists finalLevels");

        db.execSQL("drop Table if exists finalMethods");

        db.execSQL("drop Table if exists finalQRBar");
        db.execSQL("drop Table if exists finalLocation");
        db.execSQL("drop Table if exists finalDayschedule");


        db.execSQL("drop Table if exists Dayschedule");

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

    /**
     * @param database: the string of the table in database.db
     * @return the max id in the table, if db empty it returns 0
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




    //syncing is not necessary the other way (temp with final) i think
    /**
     * Syncs the persistent database up with the temporary one after each alarm insert or on editing an alarm
     * @param id - the id of the to change row of alarmdatabase
     * @return true if successfull, else false
     */
    public boolean syncFinalWithTempDB(int id){


        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor levelData = dbRead.rawQuery("SELECT * FROM Alarmlevel", null);
        if(levelData.getCount()>0){

            ArrayList<Integer> levelIds = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<Integer> snoozeCounts = new ArrayList<>();
            ArrayList<Integer> snoozeTimes = new ArrayList<>();
            ArrayList<String> sound_paths = new ArrayList<>();
            ArrayList<String> sound_names = new ArrayList<>();

            while(levelData.moveToNext()){

                if(levelData.getInt(6) == id){
                    levelIds.add(levelData.getInt(0));
                    labels.add(levelData.getString(1));
                    if(levelData.getInt(2) == -1){
                        snoozeCounts.add(-1);
                        snoozeTimes.add(-2);
                    }else{
                        snoozeCounts.add(levelData.getInt(2));
                        snoozeTimes.add(levelData.getInt(3));
                    }
                    sound_paths.add(levelData.getString(4));
                    sound_names.add(levelData.getString(5));
                }

            }


            Cursor queueData = dbRead.rawQuery("SELECT * FROM Methoddatabase", null);

            if(queueData.getCount()>0) {

                ArrayList<Integer> ids = new ArrayList<>();
                ArrayList<Integer> queueIds = new ArrayList<>();
                ArrayList<Integer> methodTypeIds = new ArrayList<>();
                ArrayList<Integer> methods = new ArrayList<>();
                ArrayList<Integer> difficulties = new ArrayList<>();
                ArrayList<String> labelsQueue = new ArrayList<>();
                ArrayList<Integer> specificIds = new ArrayList<>();

                while (queueData.moveToNext()){

                    ids.add(queueData.getInt(0));
                    queueIds.add(queueData.getInt(1));
                    methodTypeIds.add(queueData.getInt(2));
                    methods.add(queueData.getInt(3));
                    difficulties.add(queueData.getInt(4));
                    labelsQueue.add(queueData.getString(5));
                    specificIds.add(queueData.getInt(6));
                }



                Cursor cursor = dbRead.rawQuery("SELECT alarm_id FROM finalLevels WHERE alarm_id=?", new String[]{String.valueOf(id)});

                for (int i = 0; i < levelIds.size(); i++) {

                    ContentValues cv = new ContentValues();
                    cv.put("id", levelIds.get(i));
                    cv.put("label", labels.get(i));
                    cv.put("snooze_count", snoozeCounts.get(i));
                    cv.put("snooze_time", snoozeTimes.get(i));
                    cv.put("sound_path", sound_paths.get(i));
                    cv.put("sound_name", sound_names.get(i));
                    cv.put("alarm_id", id);

                    if (cursor.getCount() > 0) {
                        dbWrite.update("finalLevels", cv, "alarm_id=?", new String[]{String.valueOf(id)});
                    } else {
                        dbWrite.insert("finalLevels", null, cv);
                    }
                }


                for(int i = 0; i < queueIds.size(); i++) {

                    ContentValues cv = new ContentValues();
                    cv.put("id", ids.get(i));
                    cv.put("queue_id", queueIds.get(i));
                    if (methodTypeIds.get(i) != 3 || methodTypeIds.get(i) != 2) {
                        cv.put("method_type_id", methodTypeIds.get(i));
                    }else if (methodTypeIds.get(i) == 3){
                        cv.put("method_type_id", methodTypeIds.get(i));

                        Cursor locationData = dbRead.rawQuery("SELECT * FROM Locationdatabase WHERE id=?", new String[]{String.valueOf(specificIds.get(i))});
                        locationData.moveToFirst();
                        ContentValues vals = new ContentValues();
                        vals.put("id", locationData.getInt(0));
                        vals.put("lat_int", locationData.getInt(1));
                        vals.put("point_lat", locationData.getInt(2));
                        vals.put("lon_int", locationData.getInt(3));
                        vals.put("point_lon", locationData.getInt(4));
                        vals.put("radius", locationData.getInt(5));
                        vals.put("street", locationData.getString(6));
                        vals.put("radius_mode", locationData.getString(7));


                        if(cursor.getCount()>0){
                            dbWrite.update("finalLocation", vals, "id=?", new String[]{String.valueOf(specificIds.get(i))});
                        }else{
                            dbWrite.insert("finalLocation", null, cv);
                        }
                        locationData.close();
                    } else if (methodTypeIds.get(i) == 2) {
                        cv.put("method_type_id", methodTypeIds.get(i));

                        Cursor qrData = dbRead.rawQuery("SELECT * FROM QRBarcodedatabase WHERE label=?", new String[]{labelsQueue.get(i)});
                        qrData.moveToFirst();
                        ContentValues vals = new ContentValues();

                        vals.put("label", qrData.getString(0));
                        vals.put("decoded", qrData.getString(1));

                        if(cursor.getCount()>0){
                            dbWrite.update("finalQRBar", vals, "label=?", new String[]{labelsQueue.get(i)});
                        }else{
                            dbWrite.insert("finalQRBar", null, cv);
                        }
                        qrData.close();
                    }
                    cv.put("method_id", methods.get(i));
                    cv.put("difficulty", difficulties.get(i));
                    cv.put("label", labelsQueue.get(i));
                    cv.put("method_database_specific_id", specificIds.get(i));


                    if (cursor.getCount() > 0) {
                        dbWrite.update("finalMethods", cv, "id=?", new String[]{String.valueOf(ids.get(i))});
                    } else {
                        dbWrite.insert("finalMethods", null, cv);
                    }
                }

                cursor.close();


            }else{
                System.out.println("How?!");
                dbRead.close();
                dbWrite.close();
                return false;
            }



            queueData.close();
            levelData.close();
            dbRead.close();
            dbWrite.close();
            return true;

        }else{
            levelData.close();
            dbRead.close();
            dbWrite.close();
            return false;
        }


    }




    public void deleteRow(String table, int row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(table, "id=?", new String[]{String.valueOf(row_id)});
        if(res ==0){
            Toast.makeText(context, "Failed deleting item: " + res, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Success deleting item: " +res, Toast.LENGTH_SHORT).show();
        }
    }


    public void addAlarm(int id, String label, int soundPathId, boolean privilegeRights, boolean snoozable, int wakeUpTimeHours, int wakeUpTimeMinutes, int daysScheduleId, int weeksScheduleAmount, boolean checkAwake){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("label", label);
        cv.put("id", id);
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
        cv.put("weeks_schedule_amount",weeksScheduleAmount);
        if(checkAwake){
        cv.put("check_awake", 1);}else{
            cv.put("check_awake", 0);}
        long res = db.insert("Alarmdatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Alarmdatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Alarmdatabase", Toast.LENGTH_SHORT).show();
        }

    }


    public void addMethod(int id, int queueID, int methodTypeId, int methodId, int difficultyId, String label, int methodDatabaseSpecificID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("queue_id",queueID);
        cv.put("method_type_id",methodTypeId);
        if(methodId != -1) cv.put("method_id",methodId);
        if(difficultyId != -1) cv.put("difficulty_id",difficultyId);
        if(!Objects.equals(label, "-1")) cv.put("label",label);
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


    public long addLocation(int latitudeInt, int zeroPointLatitude, int longitudeInt, int zeroPointLongitude, int radiusInt, String street, String radiusMode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("latitude_int",latitudeInt);
        cv.put("zero_point_latitude",zeroPointLatitude);
        cv.put("longitude_int",longitudeInt);
        cv.put("zero_point_longitude",zeroPointLongitude);
        cv.put("radius_int",radiusInt);
        cv.put("street",street);
        cv.put("radius_mode", radiusMode);
        long res = db.insert("Locationdatabase", null, cv);
        if(res == -1){
            Toast.makeText(context, "Inserting into Locationdatabase failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Locationdatabase", Toast.LENGTH_SHORT).show();
        }
        return res;
    }


    public long addLevel(int id, String label, int snooze_count, int snooze_time, String sound_path, String soundName, int alarm_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(snooze_count != -2) cv.put("snooze_count", snooze_count);
        if(snooze_time != -1) cv.put("snooze_time", snooze_time);
        if(!Objects.equals(label, "")) cv.put("label", label);
        if(!Objects.equals(sound_path, "")) cv.put("sound_path", sound_path);
        if(id != -1) cv.put("id", id);
        if(!Objects.equals(soundName, "")) cv.put("sound_name", soundName);
        cv.put("alarm_id", alarm_id);

        long result = db.insert("Alarmlevel", null, cv);
        if(result == -1){
            Toast.makeText(context, "Inserting into Alarmlevel failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully added into Alarmlevel", Toast.LENGTH_SHORT).show();
        }
        return result;

    }



    public void editLevel(int id, int alarm_id, String label, int snooze_count, int snooze_time, String sound_path, String sound_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(snooze_count != -2) cv.put("snooze_count", snooze_count);
        if(snooze_time != -1) cv.put("snooze_time", snooze_time);
        if(!Objects.equals(label, "")) cv.put("label", label);
        if(!Objects.equals(sound_path, "")) cv.put("sound_path", sound_path);
        if(!Objects.equals(sound_name, "")) cv.put("sound_name", sound_name);

        long result = db.update("Alarmlevel", cv, "id=?&&alarm_id=?", new String[]{String.valueOf(id), String.valueOf(alarm_id)});
        if(result == -1){
            Toast.makeText(context, "Failed to update alarm level", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Success updating alarm level", Toast.LENGTH_SHORT).show();
        }
    }





    public void editMethoddatabase(int queue_id, int method_type_id, int method_id, int difficulty_id, String label, int method_database_spec_id, int row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if(queue_id != -1) cv.put("queue_id", queue_id);
        if(method_type_id != -1) cv.put("method_type_id", method_type_id);
        if(method_id != -1) cv.put("method_id", method_id);
        if(difficulty_id != -1) cv.put("difficulty_id", difficulty_id);
        if(!Objects.equals(label,"-1")) cv.put("label", label);
        if(method_database_spec_id != -1) cv.put("method_database_specific_id", method_database_spec_id);

        if (method_id == 1){
            editMathdatabase(getMethodById(method_id), getDifficulty(difficulty_id), method_database_spec_id);
        }

        long result = db.update("Methoddatabase", cv, "id=?", new String[]{String.valueOf(row_id)});
        if(result == -1){
            Toast.makeText(context, "Failed to update Methoddatabase", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Success updating Methoddatabase", Toast.LENGTH_SHORT).show();
        }
    }



    public void editMathdatabase(String method, String difficulty, int row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        if (!Objects.equals(method, "")) c.put("method", method);
        if (!Objects.equals(difficulty, "")) c.put("difficulty", difficulty);

        long r = db.update("Mathdatabase",c, "id=?", new String[]{String.valueOf(row_id)});
        if(r == -1){
            Toast.makeText(context, "Failed to update Mathdatabase", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Success updating Mathdatabase", Toast.LENGTH_SHORT).show();
        }

    }


    public void editLocation(int lat, int zerolat, int lon, int zerolon, int radius, String street, String enter_leave, int row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int currlat, currzerolat, currlon, currzerolon, currradius;
        String currstreet, currenterleave;

        Cursor c = getData("Locationdatabase");
        if(c.getCount() > 0) {
            while(c.moveToNext()){
                if(c.getInt(0) == row_id){

                    currlat = c.getInt(1);
                    currzerolat = c.getInt(2);
                    currlon = c.getInt(3);
                    currzerolon = c.getInt(4);
                    currradius = c.getInt(5);
                    currstreet = c.getString(7);
                    currenterleave = c.getString(8);

                    if (lat != currlat) cv.put("latitude_int", lat);
                    if (zerolat != currzerolat) cv.put("zero_point_latitude", zerolat);
                    if (lon != currlon) cv.put("longitude_int", lon);
                    if (zerolon != currzerolon) cv.put("zero_point_longitude", zerolon);
                    if (radius != currradius) cv.put("radius_int", radius);
                    if (!Objects.equals(street, currstreet)) cv.put("street", street);
                    if (!Objects.equals(enter_leave,currenterleave)) cv.put("radius_mode", enter_leave);
                }
            }



            long res = db.update("Locationdatabase", cv, "id=?", new String[]{String.valueOf(row_id)});
            if (res == -1) {
                Toast.makeText(context, "Failed to update Locationdatabase", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Success updating Locationdatabase", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "how did you manage to even get here wtf", Toast.LENGTH_SHORT).show();
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

}
