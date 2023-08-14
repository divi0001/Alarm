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
import android.location.Address;
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
import java.util.Calendar;
import java.util.Objects;

public class EditAlarmActivity extends AppCompatActivity implements AlarmLevelAdapter.EditAlarms {

    private Spinner spHours,spMins,spMethods;
    private CheckBox mo,di,mi,thu,fr,sa,so,checkWeekDays,checkWeekEnds,checkAllowSnooze,checkAwake,checkAlarmLvls,checkRevokePrivilege;
    private ImageView imgAddMethodPlus, imgBtnDownSnooze, imgBtnUpSnooze;
    private TextView txtCurrSoundName,txtSetLabel,txtMinutes,txtAllowSnooze, textLevel;
    private Button btnPickSound,btnAddLevel,btnAddSaveAlarm;
    private EditText editLabel, editMinutesCheckAwake, editAmountSnoozes, editMinutesSnooze;
    private ValContainer[] c = new ValContainer[2];
    public final int CONTAINER_POS_CHECK_WEEK = 0;
    public final int CONTAINER_POS_CHECK_WEEKEND = 1;
    private RecyclerView alarmQueue,alarmLevels;
    private final int REQ_CODE_MATH_METHOD = 1234;

    private final Context context = this;
    private QueueRecViewAdapter adapter1;

    private Enums.Difficulties difficulty = Enums.Difficulties.None;
    private int alarmId, hour, minute;
    private int level_id = 0;
    private RelativeLayout relLayoutHideable, relLayAddLvls;
    private Enums.Method methodToSet = Enums.Method.None;
    private Enums.SubMethod subMethod = Enums.SubMethod.None;
    private Uri curr_uri;
    AlarmLevelAdapter adapter;

    private Alarm alarmParameter = new Alarm(0); //todo change this to get the id from overViewActivity
    //todo soundactivity should not use db

    ArrayList<String> alarmLevel = new ArrayList<>();
    private int lvlID = -1;


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

        adapter = new AlarmLevelAdapter(context, this);
        adapter.setAlarmLevel(alarmParameter.getlQueue());

        alarmLevels.setAdapter(adapter);
        alarmLevels.setLayoutManager(new LinearLayoutManager(context));

        hour = 0;
        minute = 0;

        //populate Spinners with arrays in strings.xml
        ArrayAdapter<CharSequence> adapt1, adapt2, adapt3;
        adapt1 = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        adapt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHours.setAdapter(adapt1);
        spHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = position;
                String hh;
                if(hour < 10){
                    hh = "0"+ hour;
                }else{
                    hh = Integer.toString(hour);
                }
                CharSequence t = alarmParameter.getT();
                CharSequence newT = "";
                newT+=t.subSequence(0,11).toString();
                newT+= hh;
                newT+=t.subSequence(13,t.length()).toString();
                //todo test this
                alarmParameter.setT(newT);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                String hh;
                if(hour < 10){
                    hh = "0"+ hour;
                }else{
                    hh = Integer.toString(hour);
                }
                CharSequence t = alarmParameter.getT();
                CharSequence newT = "";
                newT+=t.subSequence(0,11).toString();
                newT+= hh;
                newT+=t.subSequence(13,t.length()).toString();
                alarmParameter.setT(newT);
            }
        });

        adapt2 = ArrayAdapter.createFromResource(this, R.array.secmins, android.R.layout.simple_spinner_item);
        adapt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMins.setAdapter(adapt2);
        spMins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = position;
                String hh;
                if(minute < 10){
                    hh = "0"+ minute;
                }else{
                    hh = Integer.toString(minute);
                }
                CharSequence t = alarmParameter.getT();
                CharSequence newT = "";
                newT+=t.subSequence(0,14).toString();
                newT+= hh;
                newT+=t.subSequence(16,t.length()).toString();
                alarmParameter.setT(newT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Calendar c = Calendar.getInstance();
                minute = c.get(Calendar.MINUTE);
                String hh;
                if(minute < 10){
                    hh = "0"+ minute;
                }else{
                    hh = Integer.toString(minute);
                }
                CharSequence t = alarmParameter.getT();
                CharSequence newT = "";
                newT+=t.subSequence(0,14).toString();
                newT+= hh;
                newT+=t.subSequence(16,t.length()).toString();
                alarmParameter.setT(newT);
            }
        });

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

                    boolean[] weekdays = alarmParameter.getWeekDays();
                    for(int i = 0; i < 5; i++){
                        weekdays[i] = true;
                    }
                    alarmParameter.setWeekDays(weekdays);

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

                    boolean[] weekdays = alarmParameter.getWeekDays();
                    for(int i = 0; i < 5; i++){
                        weekdays[i] = false;
                    }
                    alarmParameter.setWeekDays(weekdays);

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

                    boolean[] weekends = alarmParameter.getWeekDays();
                    weekends[5] = true;
                    weekends[6] = true;
                    alarmParameter.setWeekDays(weekends);


                }else{
                    sa.setChecked(false);
                    so.setChecked(false);

                    rep.remove("sa");
                    rep.remove("so");

                    a.setVal(rep);

                    boolean[] weekends = alarmParameter.getWeekDays();
                    weekends[5] = false;
                    weekends[6] = false;
                    alarmParameter.setWeekDays(weekends);

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
                boolean[] b = alarmParameter.getWeekDays();
                b[0] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[1] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[2] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[3] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[4] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[5] = isChecked;
                alarmParameter.setWeekDays(b);
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
                boolean[] b = alarmParameter.getWeekDays();
                b[6] = isChecked;
                alarmParameter.setWeekDays(b);
            }
        });

        spMethods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                methodToSet = Enums.Method.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {methodToSet= Enums.Method.TapOff;}
        });

        adapter1 = new QueueRecViewAdapter(context);
        //todo not entirely sure, if removing mkNewAlarmParms() is right here, but after all, the rest of the code here should update the views right?
        alarmQueue.setAdapter(adapter1);
        alarmQueue.setLayoutManager(new LinearLayoutManager(context));
        adapter1.setAlarmParameter(alarmParameter.getmQueue(lvlID));

        imgAddMethodPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (adapter1.getItemCount() > 5) {
                    Toast.makeText(EditAlarmActivity.this, "Do you really need more than 5 methods for this Level?", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences pref = getSharedPreferences(getString(R.string.queue_shared_pref_key_adapter),Context.MODE_PRIVATE);
                SharedPreferences.Editor edi = pref.edit();
                edi.putString("method", "set_alarm");
                edi.apply();
                if(methodToSet.equals(Enums.Method.TapOff)) {

                    if(alarmParameter.isHasLevels() && alarmParameter.getlQueue().size()>0){

                        //todo while this might be redundant and be removed later, also make sure to update the selected lvl here
                        int sel = getSelectedLvl();
                        alarmParameter.setSelectedLvl(sel);
                        ArrayList<AlarmMethod> aL = alarmParameter.getmQueue(sel);
                        aL.add(new AlarmMethod(getMaxMQueueID(alarmParameter,sel)+1, Enums.Difficulties.None, Enums.Method.TapOff, Enums.SubMethod.None));
                        alarmParameter.setmQueue(aL,-1);

                    }else{
                        ArrayList<AlarmMethod> mQ = alarmParameter.getmQueue(-1);
                        mQ.add(new AlarmMethod(getMaxMQueueID(alarmParameter,-1)+1, Enums.Difficulties.None, Enums.Method.TapOff, Enums.SubMethod.None));
                        alarmParameter.setmQueue(mQ,-1);
                    }

                }else{

                            switch (methodToSet) {
                                case Math:

                                    Intent iMath = new Intent(context, MathMethodSetActivity.class);
                                    startActivity(iMath);

                                    break;

                                case QRBar:

                                    Intent iScan = new Intent(context, QRMethodSetActivity.class);
                                    startActivity(iScan);
                                    break;
                                case Location:

                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        Mapbox.getInstance(EditAlarmActivity.this, getResources().getString(R.string.mapbox_access_token));
                                        Intent iLoc = new Intent(context, LocationMethodSetActivity.class);
                                        startActivity(iLoc);
                                    } else {
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                    }

                                    break;

                                case Sudoku:
                                    Intent iSudoku = new Intent(context, SudokuMethodSetActivity.class);
                                    startActivity(iSudoku);
                                    break;

                                case Memory:
                                    Intent iMemory = new Intent(context, MemoryMethodSetActivity.class);
                                    startActivity(iMemory);
                                    break;

                            }
                }
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
                alarmParameter.setSnoozable(isChecked, lvlID);
            }
        });

        editMinutesSnooze.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    try{
                        editMinutesSnooze.setText(String.valueOf(Integer.parseInt(editMinutesSnooze.getText().toString()))); //multiple parses here to make sure an
                        //invalid input would cause an exception and the text to be rewritten

                        alarmParameter.setSnoozeMinutes(Integer.parseInt(editMinutesSnooze.getText().toString()), lvlID);
                    }catch (Exception e){
                        editMinutesSnooze.setText("1");
                        alarmParameter.setSnoozeMinutes(1, lvlID);
                    }
                }
            }
        });

        editAmountSnoozes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    try{
                        editAmountSnoozes.setText(String.valueOf(Integer.parseInt(editAmountSnoozes.getText().toString())));
                        alarmParameter.setSnoozeAmount(Integer.parseInt(editAmountSnoozes.getText().toString()), lvlID);
                    } catch (Exception e){
                        editAmountSnoozes.setText("-1");
                        alarmParameter.setSnoozeAmount(-1,lvlID);
                    }
                }
            }
        });

        editMinutesCheckAwake.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    try {
                        editMinutesCheckAwake.setText(String.valueOf(Integer.parseInt(editMinutesCheckAwake.getText().toString())));
                        alarmParameter.setMinutesUntilTurnBackOn(Integer.parseInt(editMinutesCheckAwake.getText().toString()), lvlID);
                    }catch (Exception e){
                        editMinutesCheckAwake.setText("1");
                        alarmParameter.setMinutesUntilTurnBackOn(1,lvlID);
                    }
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
                alarmParameter.setExtraAwakeCheck(isChecked, lvlID);
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
                alarmParameter.setHasLevels(isChecked);
            }
        });


        btnPickSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMusic = new Intent(EditAlarmActivity.this, AlarmSoundSetActivity.class);
                startActivity(iMusic);
            }
        });



        btnAddLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initializing current level w.r.t. alarmParameter
                SharedPreferences se = getSharedPreferences(getString(R.string.uri_key), MODE_PRIVATE);

                if (editLabel.getText().toString().equals("") || editAmountSnoozes.getText().toString().equals("")||editMinutesSnooze.getText().toString().equals("")) {
                    Toast.makeText(context, "Data is missing", Toast.LENGTH_SHORT).show();
                } else {

                    ArrayList<AlarmLevel> aL = alarmParameter.getlQueue();
                    aL.add(new AlarmLevel(getMaxLQueueID()));
                    lvlID = aL.size()-1;
                    AlarmLevel newLvl = aL.get(lvlID);

                    String lab = editLabel.getText().toString();

                    newLvl.setLabel(lab);

                    int amountSnoozes = -2;
                    int timeSnooze = -1;
                    newLvl.setSnoozable(checkAllowSnooze.isChecked());
                    if(checkAllowSnooze.isChecked()) {
                        amountSnoozes = Integer.parseInt(editAmountSnoozes.getText().toString());
                        timeSnooze = Integer.parseInt(editMinutesSnooze.getText().toString());
                        newLvl.setSnoozeMinutes(timeSnooze);
                        newLvl.setSnoozeAmount(amountSnoozes);
                    }


                    String uri = ""; //todo make this the standard sound uri & update it
                    String name = "";

                    newLvl.setLvlSoundPath(uri);

                    newLvl.setExtraAwakeCheck(checkAwake.isChecked());
                    if(checkAwake.isChecked()) newLvl.setMinutesUntilTurnBackOn(Integer.parseInt(editMinutesCheckAwake.getText().toString()));
                    aL.add(lvlID, newLvl);

                     //todo before this, isn't checkAwake settings missing?

                    alarmParameter.setlQueue(aL, lvlID);


                    adapter.setAlarmLevel(alarmParameter.getlQueue());


                    alarmLevels.setAdapter(adapter);
                    alarmLevels.setLayoutManager(new LinearLayoutManager(context));

                    adapter1.setAlarmParameter(alarmParameter.getmQueue(lvlID));



                }
            }
        });



    btnAddSaveAlarm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                    //first condition might be redundant
            if((alarmParameter.getlQueue().size() > 0 || alarmParameter.getmQueue(-1).size() > 0)&& !Objects.equals(null, curr_uri)){

                DBHelper db = new DBHelper(context, "Database.db");

                SharedPreferences sp = getSharedPreferences(getString(R.string.alarm_id_key), MODE_PRIVATE);
                alarmId = sp.getInt("id", 1);

                //todo if set do x if edit do y
                //if set:
                // missing:int soundPathId, int daysScheduleId, int weeksScheduleAmount
                //db.addAlarm(alarmId, editLabel.getText().toString(), ,
                //        checkRevokePrivilege.isChecked(), checkAllowSnooze.isChecked(), hour, minute
                //        , , , checkAwake.isChecked());


                db.syncFinalWithTempDB(alarmId);
                finish();

            }else{
                Toast.makeText(context, "You need to set at least 1 method to the queue and a sound to play", Toast.LENGTH_SHORT).show();
            }

        }
    });



    updateViews();




    }

    private int getMaxLQueueID() {
        ArrayList<AlarmLevel> aL = alarmParameter.getlQueue();
        int maxID = 0;
        for(AlarmLevel m: aL){
            if(m.getID() > maxID) maxID = m.getID();
        }
        return maxID;
    }

    private int getSelectedLvl() {
        return this.lvlID;
    }

    private int getMaxMQueueID(Alarm alarmParameter, int pos) {

        ArrayList<AlarmMethod> me = alarmParameter.getmQueue(pos);
        int maxID = 0;
        for(AlarmMethod m: me){
            if(m.getId() > maxID){
                maxID = m.getId();
            }
        }
        return maxID;
    }

    /**
     * @implNote Specifies what happens, if you click one of the alarm lvl texts
     * @param label
     */
    @Override
    public void setIfClicked(String label) {

        for (AlarmLevel lvl: alarmParameter.getlQueue()){
            if(lvl.getLabel().equals(label)) this.lvlID = lvl.getID();
        }
        AlarmLevel currLvl = alarmParameter.getlQueue().get(this.lvlID);

        adapter1.setAlarmParameter(currLvl.getmQueue()); //todo check if this updates the mQueue to the lvl's one

        int snoozeAmount = -1;
        int snoozeTime = -1;

        if(currLvl.isSnoozable()){
            checkAllowSnooze.setChecked(true);
            snoozeAmount = currLvl.getSnoozeAmount();
            snoozeTime = currLvl.getSnoozeMinutes();
            editAmountSnoozes.setText(Integer.toString(snoozeAmount));
            editMinutesSnooze.setText(Integer.toString(snoozeTime));
        }else{
            checkAllowSnooze.callOnClick();
        }

        String sound_path = "";

        if(!Objects.equals(currLvl.getLvlSoundPath(), null)){
            sound_path = currLvl.getLvlSoundPath();
        }
        txtCurrSoundName.setText(sound_path); //todo this might be cause of a bug, there is sound_name apparently, dont remember where that comes from, even though i think it was the same as the path, or depended on something in the setactivity for sounds


        int minsUntil = -1;

        if(currLvl.isExtraAwakeCheck()){
            minsUntil = currLvl.getMinutesUntilTurnBackOn();
            checkAwake.setChecked(true);
            editMinutesCheckAwake.setText(Integer.toString(minsUntil));
        }else{
            checkAwake.callOnClick();
        }
    }





    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {

        super.onResume();


        SharedPreferences shared = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE); //todo after the code is done, reset this shardedpref to default value, so if it fails, it wont put in the same val twice
        if(shared.contains("Method")){ //todo this is the if that should be changed (see todo above)
            methodToSet = Enums.Method.valueOf(shared.getString("Method", (Enums.Method.None).toString()));
        }
        if(shared.contains("Difficulty")){
            difficulty = Enums.Difficulties.valueOf(shared.getString("Difficulty", (Enums.Difficulties.None).toString()));
        }
        if(shared.contains("SubMethod")){
            subMethod = Enums.SubMethod.valueOf(shared.getString("SubMethod", Enums.SubMethod.None.toString()));
        }
        SharedPreferences se = getSharedPreferences(getString(R.string.uri_key),MODE_PRIVATE);
        if(se.contains("name")){

            String name = se.getString("name", "Not yet set");
            if(name.length() < 20) txtCurrSoundName.setText(name);
            if(name.length() >= 20) txtCurrSoundName.setText(name.substring(0,20) + "...");
            curr_uri = Uri.parse(se.getString("uri", ""));

        }
        ArrayList<AlarmMethod> currMethod;
        if(lvlID > -1){
            AlarmLevel currLevel = alarmParameter.getlQueue().get(lvlID); //todo looks if any of the 3 following values is none, if not, adds/edits the alarm accordingly?
            currMethod = currLevel.getmQueue();
        }else{
            currMethod = alarmParameter.getmQueue(-1);
        }





        if(!methodToSet.equals(Enums.Method.None)){ //set it to None now, so the same value wont get added twice todo add the same for the shared prefs

            currMethod.add(new AlarmMethod(getMaxMQueueID(alarmParameter, lvlID), difficulty, methodToSet, subMethod));

            if(!subMethod.equals(Enums.SubMethod.None)){
                subMethod = Enums.SubMethod.None;
                //... (yes the above doesn't have to be in the if, but does the shared pref?)
            }

            if(!difficulty.equals(Enums.Difficulties.None)){
                difficulty = Enums.Difficulties.None;
            }

            methodToSet = Enums.Method.None;
            //...
        } //todo check if the value differs, when setting this to none, since this is the same reference (is enum callbyreference?) --> tested in online compiler, no need to worry, if you change the var, the arraylists values dont change aswell






        adapter1 = new QueueRecViewAdapter(context);
        adapter1.setAlarmParameter(alarmParameter.getmQueue(lvlID));

        alarmQueue.setAdapter(adapter1);
        alarmQueue.setLayoutManager(new LinearLayoutManager(context));


        imgAddMethodPlus = (ImageView) findViewById(R.id.btnMethodPlus);




        updateViews();
    }

    private void updateViews() {
        adapter1.setAlarmParameter(alarmParameter.getmQueue(lvlID));
        CharSequence prevT = alarmParameter.getT();
        int hh = Integer.parseInt(prevT.subSequence(11,13).toString());
        int mm = Integer.parseInt(prevT.subSequence(14,16).toString()); //todo make sure the indexes are right with the yyyy-mm-dd hh:mm:ss format
        spHours.setSelection(hh);
        spMins.setSelection(mm);
        boolean[] b = alarmParameter.getWeekDays();
        CheckBox[] checks = new CheckBox[]{mo,di,mi,thu,fr,sa,so};
        int g = 0;
        for (boolean b1: b){
            checks[g].setChecked(b1);
            g++;
        }

        txtCurrSoundName.setText(alarmParameter.getSoundPath(lvlID));
        if (checkAllowSnooze.isChecked() != alarmParameter.isSnoozable(lvlID)) checkAllowSnooze.callOnClick();
        if(alarmParameter.isSnoozable(lvlID)){ //todo stricter conditions to have coherent lvldata
            editAmountSnoozes.setText(alarmParameter.getSnoozeAmount(lvlID));
            editMinutesSnooze.setText(alarmParameter.getSnoozeMinutes(lvlID));
        }

        editLabel.setText(alarmParameter.getLabel(lvlID));

        if(checkAwake.isChecked() != alarmParameter.isExtraAwakeCheck(lvlID)) checkAwake.callOnClick();
        if(alarmParameter.isExtraAwakeCheck(lvlID)) editMinutesCheckAwake.setText(alarmParameter.getMinutesUntilTurnBackOn(lvlID));

        if (checkAlarmLvls.isChecked() != alarmParameter.isHasLevels()){
            checkAlarmLvls.callOnClick(); //todo make sure none of the callOnClick ends in an endless recursion or resetting values wrong
        }
        //todo what is with privilege rights? did i remove it, so should i delete the view?


    }



/*
    private void mkNewAlarmParam() {

        DBHelper db;
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> queueIds = new ArrayList<>();
        ArrayList<Integer> methodTypeIds = new ArrayList<>();
        ArrayList<Integer> methodIds = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> methodDatabaseSpecificIds = new ArrayList<>();
        ArrayList<Integer> difficulties = new ArrayList<>();



        db = new DBHelper(EditAlarmActivity.this, "Database.db");
        Cursor c = db.getData("Methoddatabase");



        if (c.getCount() > 0){

            while(c.moveToNext()){
                queueIds.add(c.getInt(0));

                if(level_id == c.getInt(1)) {
                    ids.add(c.getInt(0));
                    methodTypeIds.add(c.getInt(2));
                    methodIds.add(c.getInt(3));
                    labels.add(c.getString(5)); // --> if null, wont throw any exception yay :D
                    difficulties.add(c.getInt(4));
                    methodDatabaseSpecificIds.add(c.getInt(6));
                }
            }

        }else{
            //todo make a better text explaining how to add methods instead of just stating it empty
            Toast.makeText(this, "No Methods are inside of the queue yet", Toast.LENGTH_SHORT).show();
            return;
        }
        String TAG = "TAGAlarmParameter";
        Log.d(TAG, "mkNewAlarmParam: id-yfied:"+ "\n ids" + ids + " \nMethodTypeid "+ methodTypeIds+ " \nMethodid "+methodIds+ " \nLabel "+labels+ " \nspecific to db "+methodDatabaseSpecificIds);

        alarmParameter = new ArrayList<>();


        for(int i = 0; i < ids.size(); i++) {

            String metho;
            String methodType = translateIdToMethodType(methodTypeIds.get(i));
            String difficult;


            alarmParameter.add(new Alarm(ids.get(i)));
            alarmParameter.get(alarmParameter.size()-1).setType(methodType);

            switch (methodType) {
                case "Tap Off":

                     alarmParameter.get(alarmParameter.size()-1).setDifficulty("None");
                    break;

                case "Math: ":

                    metho = translateIdToMethod(methodIds.get(i)+1);
                    difficult = translateIdToDifficulty(difficulties.get(i)+1);

                     alarmParameter.get(alarmParameter.size()-1).setTurnOffMethod(metho);
                     alarmParameter.get(alarmParameter.size()-1).setDifficulty(difficult);


                    break;

                case "QR/Barcode":


                     difficult = labels.get(i);
                     alarmParameter.get(alarmParameter.size()-1).setDifficulty(difficult);

                    break;

                case "Location: ":

                    db = new DBHelper(EditAlarmActivity.this, "Database.db");


                    Cursor resLoc = db.getData("Locationdatabase");
                    if (resLoc.getCount() > 0) {
                        while (resLoc.moveToNext()) {
                            if (resLoc.getInt(0) == methodDatabaseSpecificIds.get(i)) {
                                 alarmParameter.get(alarmParameter.size()-1).setType("Location: " + resLoc.getString(7));
                                 alarmParameter.get(alarmParameter.size()-1).setDifficulty(resLoc.getInt(5)
                                         + " meter: To " + resLoc.getString(8) + " radius");
                            }
                        }
                    }


                    break;
                case "Sudoku":

                case "Memory":

                    difficult = translateIdToDifficulty(difficulties.get(i));
                     alarmParameter.get(alarmParameter.size()-1).setDifficulty(difficult);

                    break;

            }
        }
        Log.d("TAGAlarmParameter", "mkNewAlarmParam: "+alarmParameter);
        adapter1.setAlarmParameter(alarmParameter);

    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent iLoc = new Intent(context, LocationMethodSetActivity.class);
            startActivity(iLoc);
        }
    }





}