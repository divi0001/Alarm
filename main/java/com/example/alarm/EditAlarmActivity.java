package com.example.alarm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.mapbox.mapboxsdk.Mapbox;

import org.json.JSONArray;

import java.util.ArrayList;

public class EditAlarmActivity extends AppCompatActivity {

    private Spinner spHours,spMins,spMethods;
    private CheckBox mo,di,mi,thu,fr,sa,so,checkWeekDays,checkWeekEnds,checkAllowSnooze,checkAwake,checkAlarmLvls,checkRevokePrivilege;
    private ImageView imgAddMethodPlus, imgBtnDownSnooze, imgBtnUpSnooze;
    private TextView txtCurrSoundName,txtSetLabel,txtMinutes,txtAllowSnooze;
    private Button btnPickSound,btnAddLevel,btnAddSaveAlarm;
    private EditText editLabel, editMinutesCheckAwake, editAmountSnoozes, editMinutesSnooze;
    private ValContainer[] c = new ValContainer[2];
    public final int CONTAINER_POS_CHECK_WEEK = 0;
    public final int CONTAINER_POS_CHECK_WEEKEND = 1;
    private RecyclerView alarmQueue,alarmLevels;
    private final int REQ_CODE_MATH_METHOD = 1234;
    private ArrayList<Alarm> alarmParameter = new ArrayList<>();
    private final Context context = this;
    private QueueRecViewAdapter adapter1;
    private String method,difficulty;
    private int id;
    private RelativeLayout relLayoutHideable,relLayAddLvls;
    private String methodToSet;
    private Uri curr_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);



        spHours = (Spinner) findViewById(R.id.spinnerHours);
        spMins = (Spinner) findViewById(R.id.spinnerMins);
        spMethods = (Spinner) findViewById(R.id.spinnerMethods);
        mo = (CheckBox) findViewById(R.id.rbMonday);
        di = (CheckBox) findViewById(R.id.rbTuesday);
        mi = (CheckBox) findViewById(R.id.rbWednesday);
        thu = (CheckBox) findViewById(R.id.rbThursday);
        fr = (CheckBox) findViewById(R.id.rbFriday);
        sa = (CheckBox) findViewById(R.id.rbSaturday);
        so = (CheckBox) findViewById(R.id.rbSunday);
        checkWeekDays = (CheckBox) findViewById(R.id.rbWeekdays);
        checkWeekEnds = (CheckBox) findViewById(R.id.rbWeekend);
        checkAllowSnooze = (CheckBox) findViewById(R.id.checkSnooze);
        checkAwake = (CheckBox) findViewById(R.id.checkAwake);
        checkAlarmLvls = (CheckBox) findViewById(R.id.checkAlarmLvls);
        checkRevokePrivilege = (CheckBox) findViewById(R.id.checkRevokePrivileges);
        imgAddMethodPlus = (ImageView) findViewById(R.id.btnMethodPlus);
        imgBtnDownSnooze = (ImageView) findViewById(R.id.btnDownSnooze);
        imgBtnUpSnooze = (ImageView) findViewById(R.id.imgBtnUp);
        txtCurrSoundName = (TextView) findViewById(R.id.txtSound);
        txtSetLabel = (TextView) findViewById(R.id.txtSetLabel);
        txtAllowSnooze = (TextView) findViewById(R.id.txtSnoozeAllow);
        txtMinutes = (TextView) findViewById(R.id.txtMinutes);
        btnPickSound = (Button) findViewById(R.id.btnPickSound);
        btnAddLevel = (Button) findViewById(R.id.btnAddLvl);
        btnAddSaveAlarm = (Button) findViewById(R.id.btnAdd);
        editLabel = (EditText) findViewById(R.id.editLabel);
        editMinutesCheckAwake = (EditText) findViewById(R.id.editTurnBackOn);
        editAmountSnoozes = (EditText) findViewById(R.id.editSnoozeNum);
        editMinutesSnooze = (EditText) findViewById(R.id.editMinutesSnooze);
        alarmQueue = (RecyclerView) findViewById(R.id.cvQueue);
        alarmLevels = (RecyclerView) findViewById(R.id.cvLevels);
        relLayoutHideable = (RelativeLayout) findViewById(R.id.relLayoutHideable);
        relLayAddLvls = (RelativeLayout) findViewById(R.id.relLayAddLevels);

        ArrayList<String> repeatAlarmDays = new ArrayList<>();
        ArrayList<String> repeatOnWeekends = new ArrayList<>();
        ValContainer<ArrayList<String>> a = new ValContainer<>();
        ValContainer<ArrayList<String>> b = new ValContainer<>();
        a.setVal(repeatAlarmDays);
        b.setVal(repeatOnWeekends);

        c[CONTAINER_POS_CHECK_WEEK] = a;
        c[CONTAINER_POS_CHECK_WEEKEND] = b;

        //TODO: in xml + java, add a possiblility to edit the turnus (weekly alarms/every 2 weeks, on a specific date,...)

        //populate Spinners with arrays in strings.xml
        ArrayAdapter<CharSequence> adapt1, adapt2, adapt3;
        adapt1 = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        adapt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHours.setAdapter(adapt1);

        adapt2 = ArrayAdapter.createFromResource(this, R.array.secmins, android.R.layout.simple_spinner_item);
        adapt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMins.setAdapter(adapt2);

        adapt3 = ArrayAdapter.createFromResource(this, R.array.spinner_method, android.R.layout.simple_spinner_item);
        adapt3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMethods.setAdapter(adapt3);

        //if checkWeekDays is checked, turn on all weekdays checkboxes and notify editalarmactivity, also else handled
        checkWeekDays.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    mo.setChecked(true);
                    di.setChecked(true);
                    mi.setChecked(true);
                    thu.setChecked(true);
                    fr.setChecked(true);

                    if (!rep.contains("mo")) rep.add("mo");
                    if (!rep.contains("di")) rep.add("di");
                    if (!rep.contains("mi")) rep.add("mi");
                    if (!rep.contains("thu")) rep.add("thu");
                    if (!rep.contains("fr")) rep.add("fr");

                    a.setVal(rep);
                }else{
                    mo.setChecked(false);
                    di.setChecked(false);
                    mi.setChecked(false);
                    thu.setChecked(false);
                    fr.setChecked(false);

                    rep.remove("mo");
                    rep.remove("di");
                    rep.remove("mi");
                    rep.remove("thu");
                    rep.remove("fr");

                    a.setVal(rep);
                }
            }
        });

        //same as checkWeekDays
        checkWeekEnds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEKEND];
                rep = a.getVal();

                if (isChecked){
                    sa.setChecked(true);
                    so.setChecked(true);

                    if(!rep.contains("sa")) rep.add("sa");
                    if(!rep.contains("so")) rep.add("so");

                    a.setVal(rep);

                }else{
                    sa.setChecked(false);
                    so.setChecked(false);

                    rep.remove("sa");
                    rep.remove("so");

                    a.setVal(rep);

                }
            }
        });


        mo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("di") && rep.contains("mi") && rep.contains("thu") && rep.contains("fr")) checkWeekDays.setChecked(true);
                    rep.add("mo");
                }else{
                    checkWeekDays.setChecked(false);
                    rep.remove("mo");
                }
                a.setVal(rep);
            }
        });

        di.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("mo") && rep.contains("mi") && rep.contains("thu") && rep.contains("fr")) checkWeekDays.setChecked(true);
                    rep.add("di");
                }else{
                    checkWeekDays.setChecked(false);
                    rep.remove("di");
                }
                a.setVal(rep);
            }
        });

        mi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("mo") && rep.contains("di") && rep.contains("thu") && rep.contains("fr")) checkWeekDays.setChecked(true);
                    rep.add("mi");
                }else{
                    checkWeekDays.setChecked(false);
                    rep.remove("mi");
                }
                a.setVal(rep);
            }
        });

        thu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("mo") && rep.contains("di") && rep.contains("mi") && rep.contains("fr")) checkWeekDays.setChecked(true);
                    rep.add("thu");
                }else{
                    checkWeekDays.setChecked(false);
                    rep.remove("thu");
                }
                a.setVal(rep);
            }
        });

        fr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEK];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("mo") && rep.contains("mi") && rep.contains("thu") && rep.contains("di")) checkWeekDays.setChecked(true);
                    rep.add("fr");
                }else{
                    checkWeekDays.setChecked(false);
                    rep.remove("fr");
                }
                a.setVal(rep);

            }
        });

        sa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEKEND];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("so")) checkWeekEnds.setChecked(true);
                    rep.add("sa");
                }else{
                    checkWeekEnds.setChecked(false);
                    rep.remove("sa");
                }
                a.setVal(rep);
            }
        });

        so.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ArrayList<String> rep;
                ValContainer<ArrayList<String>> a = c[CONTAINER_POS_CHECK_WEEKEND];
                rep = a.getVal();

                if(isChecked){
                    if(rep.contains("sa")) checkWeekEnds.setChecked(true);
                    rep.add("so");
                }else{
                    checkWeekEnds.setChecked(false);
                    rep.remove("so");
                }
                a.setVal(rep);
            }
        });

        spMethods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:
                        methodToSet = "tap_off";
                        break;

                    case 1:
                        methodToSet = "math";
                        break;

                    case 2:
                        methodToSet = "scan_qr_barcode";
                        break;
                    case 3:
                        methodToSet = "location_based";
                        break;
                    case 4:
                        methodToSet = "sudoku";
                        break;
                    case 5:
                        methodToSet = "memory";
                        break;
                    case 6:
                        methodToSet = "type_passphrase";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapter1 = new QueueRecViewAdapter(context);
        mkNewAlarmParam();

        alarmQueue.setAdapter(adapter1);
        alarmQueue.setLayoutManager(new LinearLayoutManager(context));


        imgAddMethodPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {




                //TODO: this is in onCreate, so only adding Tap off method should be allowed
                if (adapter1.getItemCount() > 5) {
                    Toast.makeText(EditAlarmActivity.this, "Do you really need more than 5 methods for this Level?", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences pref = getSharedPreferences(getString(R.string.queue_shared_pref_key_adapter),Context.MODE_PRIVATE);
                SharedPreferences.Editor edi = pref.edit();
                edi.putString("method", "set_alarm");
                edi.apply();
                if(methodToSet.equals("tap_off")) {

                    alarmParameter.add(new Alarm(alarmParameter.size()));
                    alarmParameter.get(alarmParameter.size() - 1).setTurnOffMethod(getString(R.string.method_tap_off));
                    alarmParameter.get(alarmParameter.size() - 1).setDifficulty("None");
                    alarmParameter.get(alarmParameter.size() - 1).setType("Tap off");


                }else{
                        Toast.makeText(context, "The selected spMethod did not register", Toast.LENGTH_SHORT).show();


                }

                mkNewAlarmParam();



            }
        });


        checkAllowSnooze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.relLayoutHideable);
                    txtSetLabel.setLayoutParams(params);
                    if (imgBtnDownSnooze.getVisibility() == View.INVISIBLE && imgBtnUpSnooze.getVisibility() == View.INVISIBLE){
                        relLayoutHideable.setVisibility(View.VISIBLE);
                        imgBtnUpSnooze.setVisibility(View.VISIBLE);
                    }
                    imgBtnDownSnooze.setVisibility(View.INVISIBLE);
                    relLayoutHideable.setVisibility(View.VISIBLE);
                }else{

                    RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.txtSnoozeAllow);
                    txtSetLabel.setLayoutParams(params);
                    imgBtnDownSnooze.setVisibility(View.INVISIBLE);
                    relLayoutHideable.setVisibility(View.INVISIBLE);
                }
            }
        });
        imgBtnDownSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relLayoutHideable.setVisibility(View.VISIBLE);
                imgBtnUpSnooze.setVisibility(View.VISIBLE);
                imgBtnDownSnooze.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.relLayoutHideable);
                txtSetLabel.setLayoutParams(params);

                }
        });


        imgBtnUpSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayoutHideable.setVisibility(View.INVISIBLE);
                imgBtnUpSnooze.setVisibility(View.INVISIBLE);
                imgBtnDownSnooze.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.txtSnoozeAllow);
                txtSetLabel.setLayoutParams(params);
            }
        });


        checkAwake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editMinutesCheckAwake.setVisibility(View.VISIBLE);
                }else{
                    editMinutesCheckAwake.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkAlarmLvls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    relLayAddLvls.setVisibility(View.VISIBLE);
                }else{
                    relLayAddLvls.setVisibility(View.INVISIBLE);
                }
            }
        });


        btnPickSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMusic = new Intent(EditAlarmActivity.this, AlarmSoundSetActivity.class);
                startActivity(iMusic);
            }
        });


    }






    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {

        super.onResume();
        DBHelper db = new DBHelper(EditAlarmActivity.this, "Database.db");

        Cursor c = db.getData("Alarmdatabase");
        ArrayList<String> arrMeth = new ArrayList<>();

        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                arrMeth.add(c.getString(2));
            }
        }


        SharedPreferences se = getSharedPreferences(getString(R.string.uri_key),MODE_PRIVATE);
        if(se.contains("name")){
            String name = se.getString("name", "Not yet set");
            if(name.length() < 20) txtCurrSoundName.setText(name);
            if(name.length() >= 20) txtCurrSoundName.setText(name.substring(0,20) + "...");
            curr_uri = Uri.parse(se.getString("uri", ""));
        }


        adapter1 = new QueueRecViewAdapter(context);
        mkNewAlarmParam();

        alarmQueue.setAdapter(adapter1);
        alarmQueue.setLayoutManager(new LinearLayoutManager(context));


        imgAddMethodPlus = (ImageView) findViewById(R.id.btnMethodPlus);
        imgAddMethodPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter1.getItemCount() > 4) {
                    Toast.makeText(EditAlarmActivity.this, "Do you really need more than 5 methods for this Level?", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sP = EditAlarmActivity.this.getSharedPreferences(getString(R.string.queue_key), MODE_PRIVATE);
                SharedPreferences.Editor sE = sP.edit();
                sE.putString("queue_id", "1"); //todo update when doing alarm levels (Knecht)
                sE.apply();

                switch (methodToSet) {
                    case "math":

                        Intent iMath = new Intent(context, MathMethodSetActivity.class);
                        startActivity(iMath);

                        break;
                    case "tap_off":

                        DBHelper dbHelper = new DBHelper(EditAlarmActivity.this, "Database.db");
                        dbHelper.addMethod(1, 1, -1, -1, "-1", -1);

                        mkNewAlarmParam();

                        adapter1.setAlarmParameter(alarmParameter);

                        break;
                    case "scan_qr_barcode":

                        Intent iScan = new Intent(context, QRMethodSetActivity.class);
                        startActivity(iScan);
                        break;
                    case "location_based":

                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Mapbox.getInstance(EditAlarmActivity.this, getResources().getString(R.string.mapbox_access_token));
                            Intent iLoc = new Intent(context, LocationMethodSetActivity.class);
                            startActivity(iLoc);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        }

                        break;
                    case "sudoku":
                        Intent iSudoku = new Intent(context, SudokuMethodSetActivity.class);
                        startActivity(iSudoku);
                        break;
                }


            }

        });


        RecyclerView recViewMethod = (RecyclerView) findViewById(R.id.cvQueue);

    }

    public String translateIdToMethodType(int id){
        String[] tra =  new String[]{"Tap off","Math: ","QR/Barcode","Location: ","Sudoku","Memory","Passphrase"};
        return tra[id-1];
    }

    public String translateIdToMethod(int id){
        String[] tra = new String[]{"null","Addition","Subtraction","Multiplication","Division","Faculty","Root","Value of f(x)","Extrema of f(x)","Multiple Choice","Enter radius","Leave radius"};
        return tra[id-1];
    }

    public String translateIdToDifficulty(int id){
        String[] tra = new String[]{"extremely easy","easy","middle","hard","extremely hard"};
        return tra[id-1];
    }

    private void mkNewAlarmParam() {

        DBHelper db;
        ArrayList<Integer> queueIds = new ArrayList<>();
        ArrayList<Integer> methodTypeIds = new ArrayList<>();
        ArrayList<Integer> methodIds = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> methodDatabaseSpecificIds = new ArrayList<>();
        ArrayList<Integer> difficulties = new ArrayList<>();




        db = new DBHelper(EditAlarmActivity.this, "Database.db"); //TODO: exception handling (try with ressources)
        Cursor c = db.getData("Methoddatabase");

        int maxQueueId = -1;

        if (c.getCount() > 0){
            while(c.moveToNext()){
                queueIds.add(c.getInt(0));

                if(queueIds.get(queueIds.size()-1) > maxQueueId )
                    maxQueueId = queueIds.get(queueIds.size()-1);

                methodTypeIds.add(c.getInt(2));
                methodIds.add(c.getInt(3));
                labels.add(c.getString(5)); // --> if null, wont throw any exception yay :D
                difficulties.add(c.getInt(4));
                methodDatabaseSpecificIds.add(c.getInt(6));
            }
        }else{
            Toast.makeText(this, "No Methods are inside of the queue yet", Toast.LENGTH_SHORT).show();
            return;
        }
        String TAG = "TAGAlarmParameter";
        Log.d(TAG, "mkNewAlarmParam: id-yfied:"+ " \nMethodTypeid "+ methodTypeIds+ " \nMethodid "+methodIds+ " \nLabel "+labels+ " \nspecific to db "+methodDatabaseSpecificIds);

        alarmParameter = new ArrayList<>();


        for(int i = 0; i < queueIds.size(); i++) {

            String metho;
            String methodType = translateIdToMethodType(methodTypeIds.get(i));;
            String difficult;



            int queueId = queueIds.get(i);

            alarmParameter.add(new Alarm(alarmParameter.size()));
            alarmParameter.get(alarmParameter.size() - 1).setType(methodType);

            switch (methodType) {
                case "Tap Off":

                    alarmParameter.get(alarmParameter.size() - 1).setDifficulty("None");
                    Log.d(TAG, "mkNewAlarmParam: " + "in tap off");
                    break;

                case "Math: ":

                    Log.d(TAG, "mkNewAlarmParam: in math");
                    metho = translateIdToMethod(methodIds.get(i)+1);
                    difficult = translateIdToDifficulty(difficulties.get(i)+1);
                    alarmParameter.get(alarmParameter.size() - 1).setDifficulty(difficult);
                    alarmParameter.get(alarmParameter.size() - 1).setTurnOffMethod(metho);
                    alarmParameter.get(alarmParameter.size() - 1).setDifficulty(difficult);


                    break;

                case "QR/Barcode":

                    Log.d(TAG, "mkNewAlarmParam: in qrbar");



                    difficult = "";
                                difficult = labels.get(i);

                    alarmParameter.get(alarmParameter.size() - 1).setDifficulty(difficult);

                    break;

                case "Location: ":

                    Log.d(TAG, "mkNewAlarmParam: in location");
                    db = new DBHelper(EditAlarmActivity.this, "Database.db");


                    Cursor resLoc = db.getData("Locationdatabase");
                    if (resLoc.getCount() > 0) {
                        while (resLoc.moveToNext()) {
                            if (resLoc.getInt(0) == methodDatabaseSpecificIds.get(i)) {
                                alarmParameter.get(alarmParameter.size() - 1).setType("Location: " + resLoc.getString(7));
                                alarmParameter.get(alarmParameter.size() - 1).setDifficulty(resLoc.getInt(5) + " meter: To " + resLoc.getString(8) +" radius");
                            }
                        }
                    }


                    break;
            }
        }
        Log.d("TAGAlarmParameter", "mkNewAlarmParam: "+alarmParameter);
        adapter1.setAlarmParameter(alarmParameter);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent iLoc = new Intent(context, LocationMethodSetActivity.class);
            startActivity(iLoc);
        }
    }
}