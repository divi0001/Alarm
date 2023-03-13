package com.example.alarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.AttributeSet;
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
                SharedPreferences sharedPref = EditAlarmActivity.this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sharedPref.edit();
                switch(position){
                    case 0:

                        ed.apply();

                        method = "Tap_off";
                        difficulty = "None";

                        break;

                    case 1:

                        ed.putString(getString(R.string.method_to_set),"Math");
                        Intent iMath = new Intent(EditAlarmActivity.this, MathMethodSetActivity.class);
                        iMath.putExtra("btnType","set");
                        startActivity(iMath);
                        ed.apply();
                        break;

                    case 2:
                        //TODO: add CardView and new window with settings for scan qr code method, also handle that this method is actually registered
                        break;
                    case 3:
                        //TODO: add CardView and new window with settings for location based method, also handle that this method is actually registered
                        break;
                    case 4:
                        //TODO: add CardView and new window with settings for sudoku method, also handle that this method is actually registered
                        break;
                    case 5:
                        //TODO: add CardView and new window with settings for memory method, also handle that this method is actually registered
                        break;
                    case 6:
                        //TODO: add CardView and new window with settings for passphrase method, also handle that this method is actually registered
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        adapter1 = new QueueRecViewAdapter(this);
        adapter1.setAlarmParameter(alarmParameter);

        alarmQueue.setAdapter(adapter1);
        alarmQueue.setLayoutManager(new LinearLayoutManager(this));


        imgAddMethodPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: this is in onCreate, so only adding Tap off method should be allowed
                if (adapter1.getItemCount() > 5) {
                    Toast.makeText(EditAlarmActivity.this, "Do you really need more than 5 methods for this Level?", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(method.equals("tap_off")){
                    int aId = alarmParameter.size();
                    Alarm a1 = new Alarm(aId);
                    a1.setTurnOffMethod(getString(R.string.method_tap_off));
                    a1.setDifficulty("None");
                    alarmParameter.add(a1);
                    adapter1.setAlarmParameter(alarmParameter);
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


    }














    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key),Context.MODE_PRIVATE);

        imgAddMethodPlus = (ImageView) findViewById(R.id.btnMethodPlus);
        //TODO: put this into an if case depending on where the user came from, since this is bugging out the method queue recView
        if(prefs.contains(getString(R.string.current_math_method)) && prefs.contains(getString(R.string.current_math_method_difficulty))) {
            if(prefs.contains(getString(R.string.math_method_key))){
                String edit = prefs.getString(getString(R.string.math_method_key), "com.example.alarm.MATH_METHOD_KEY");
                if(edit.equals("editAlarm")){
                    int pos = prefs.getInt(getString(R.string.pos_in_alarm_list_key),-1);
                    Alarm aa = alarmParameter.get(pos);
                    alarmParameter.remove(pos);
                    method = prefs.getString(getString(R.string.current_math_method), "Addition");
                    difficulty = prefs.getString(getString(R.string.current_math_method_difficulty), "Easy");
                    aa.setDifficulty(difficulty);
                    aa.setTurnOffMethod(method);
                    alarmParameter.add(pos,aa);
                    adapter1.setAlarmParameter(alarmParameter);
                }
            }else {

                method = prefs.getString(getString(R.string.current_math_method), "Addition");
                difficulty = prefs.getString(getString(R.string.current_math_method_difficulty), "Easy");

                Alarm ala = new Alarm(0);
                ala.setDifficulty(difficulty);
                ala.setTurnOffMethod(method);

                alarmParameter.add(ala);
                adapter1.setAlarmParameter(alarmParameter);
            }
        }
        imgAddMethodPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapter1.getItemCount() > 4){
                    Toast.makeText(EditAlarmActivity.this, "Do you really need more than 5 methods for this Level?", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!alarmParameter.isEmpty()){
                Alarm alar = alarmParameter.get(alarmParameter.size()-1);
                alar.setID(alarmParameter.size());
                alarmParameter.add(alar);
                adapter1.setAlarmParameter(alarmParameter);}else{ //TODO: understand why mathmethodsetactivity is the dominant setter, so all new cardviews follow the last setting from there, or the first one set
                    Alarm alar = new Alarm(0); //TODO: try out math_to_edit_shared_pref_key as means of getting the method + difficulty to the method, that registers the cardviews/adds them. This key is used by the dominant class, so i guess that's the whole reason. Anyways, goodnight
                    alar.setTurnOffMethod(method);
                    alar.setDifficulty(difficulty);
                    alarmParameter.add(alar);
                    adapter1.setAlarmParameter(alarmParameter);
                }
            }
        });
    }
}