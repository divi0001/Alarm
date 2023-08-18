package com.example.alarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MathMethodSetActivity extends AppCompatActivity {

    private TextView txtExample;
    private RadioGroup rgDifficulty,rgKindOfMath;
    private Button btnSave;
    private boolean edit;
    private int posAlarmParam;
    private int spec_id;
    private int id;
    private Enums.SubMethod methodID;
    private Enums.Difficulties difficulty;
    private Cursor alarm;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_method_set);

        txtExample = (TextView) findViewById(R.id.txtExample);
        rgDifficulty = (RadioGroup) findViewById(R.id.rgDifficulty);
        rgKindOfMath = (RadioGroup) findViewById(R.id.rgMathMethod);
        btnSave = (Button) findViewById(R.id.btnSaveMathMethod);

        RadioButton rbSelectedDiff = findViewById(rgDifficulty.getCheckedRadioButtonId());
        int methId = rgKindOfMath.getCheckedRadioButtonId();



        getIntentData(); //gets the bool for edit which is used below here and more
        txtExample.setText("generating example, as soon \nas you select a different math task");

        SharedPreferences s = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
        String e = s.getString("edit_add","add");

        if(e.equals("edit")){
            btnSave.setText(R.string.save);
            edit = true;
        }else{
            btnSave.setText(R.string.add);
            edit = false;
        }

        rgDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                int kindOfMath;
                CharSequence text = radioButton.getText();
                if ("ExEasy".contentEquals(text)) {
                    kindOfMath = rgKindOfMath.getCheckedRadioButtonId();
                    txtExample.setText(generateExample("exEasy", kindOfMath));
                    difficulty = Enums.Difficulties.ExEasy;
                } else if ("Easy".contentEquals(text)) {
                    difficulty = Enums.Difficulties.Easy;
                    kindOfMath = rgKindOfMath.getCheckedRadioButtonId();
                    txtExample.setText(generateExample("easy", kindOfMath));
                } else if ("Middle".contentEquals(text)) {
                    difficulty = Enums.Difficulties.Middle;
                    kindOfMath = rgKindOfMath.getCheckedRadioButtonId();
                    txtExample.setText(generateExample("middle", kindOfMath));
                } else if ("Hard".contentEquals(text)) {
                    difficulty = Enums.Difficulties.Hard;
                    kindOfMath = rgKindOfMath.getCheckedRadioButtonId();
                    txtExample.setText(generateExample("hard", kindOfMath));
                } else if ("ExHard".contentEquals(text)) {
                    difficulty = Enums.Difficulties.ExHard;
                    kindOfMath = rgKindOfMath.getCheckedRadioButtonId();
                    txtExample.setText(generateExample("exHard", kindOfMath));
                }
                Log.d("method", "Selected Diff:"+ difficulty);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MathMethodSetActivity.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage("Are you sure, you want to add this as the math method?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences sp = MathMethodSetActivity.this.getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                                int queueId = sp.getInt("queue_id",-1); //todo (threeDots in EditAlarm)


                                if(edit){

                                    //Parcelable p = new AlarmMethod(queueId, difficulty, Enums.Method.Math, methodID);
                                    //getIntent().putExtra("MathMethod", p);

                                    Log.d("method",new AlarmMethod(difficulty, Enums.Method.Math, methodID).toString());
                                    SharedPreferences sh = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                                    SharedPreferences.Editor se = sh.edit();
                                    se.putString("edit_add", "edit");
                                    se.putString("Method", Enums.Method.Math.toString());
                                    se.putString("Difficulty", difficulty.toString());
                                    se.putString("SubMethod", methodID.toString());
                                    se.putInt("queue_id", queueId);
                                    se.apply();

                                    finish();

                                }else {


                                    Log.d("method",new AlarmMethod(difficulty, Enums.Method.Math, methodID).toString());
                                    SharedPreferences sh = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                                    SharedPreferences.Editor se = sh.edit();
                                    se.putString("edit_add", "add");
                                    se.putString("Method", Enums.Method.Math.toString());
                                    se.putString("Difficulty", difficulty.toString());
                                    se.putString("SubMethod", methodID.toString());
                                    se.apply();
                                    finish();

                                }

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
            }
        });

        rgKindOfMath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = new RadioButton(MathMethodSetActivity.this);
                rb = findViewById(checkedId);
                int i = 0;
                for (String s: new String[]{"Add", "Sub", "Mult", "Division", "Fac", "Root", "Value", "extrema", "Multiple"}){
                    if (rb.getText().toString().contains(s)) break;
                    i++;
                }
                methodID = Enums.SubMethod.values()[i];
                Log.d("method","subMethod is " + methodID);
            }
        });


    }


    private void getIntentData(){

        if(getIntent().hasExtra("edit_add") && getIntent().hasExtra("difficulty") && getIntent().hasExtra("method") && getIntent().hasExtra("id")){


            String edit_add = getIntent().getStringExtra("edit_add");
            edit = edit_add.equals("edit");

            String diff = getIntent().getStringExtra("difficulty");
            String meth = getIntent().getStringExtra("method");
            id = getIntent().getIntExtra("id",-1);



            RadioButton rbCurrDiff, rbCurrMeth;

            switch (diff){
                case "extremely easy":
                    difficulty = Enums.Difficulties.ExEasy;
                    rbCurrDiff = (RadioButton) findViewById(R.id.rbExEasy);
                    break;
                case "easy":
                    difficulty = Enums.Difficulties.Easy;
                    rbCurrDiff = (RadioButton) findViewById(R.id.rbEasy);
                    break;
                case "hard":
                    difficulty = Enums.Difficulties.Hard;
                    rbCurrDiff = (RadioButton) findViewById(R.id.rbHard);
                    break;
                case "extremely hard":
                    difficulty = Enums.Difficulties.ExHard;
                    rbCurrDiff = (RadioButton) findViewById(R.id.rbExHard);
                    break;
                default:
                    difficulty = Enums.Difficulties.Middle;
                    rbCurrDiff = (RadioButton) findViewById(R.id.rbMiddle);
                    break;
            }

            switch (meth){
                case "Subtraction":
                    this.methodID = Enums.SubMethod.Sub;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathSub);
                    break;
                case "Division":
                    this.methodID = Enums.SubMethod.Div;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathDiv);
                    break;
                case "Multiplication":
                    this.methodID = Enums.SubMethod.Mult;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathMult);
                    break;
                case "Root":
                    this.methodID = Enums.SubMethod.Root;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathRoot);
                    break;
                case "Faculty (x!)":
                    this.methodID = Enums.SubMethod.Fac;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathFac);
                    break;
                case "Value for f(x)":
                    this.methodID = Enums.SubMethod.FuncVal;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathFuncValue);
                    break;
                case "Determine extrema of f(x)":
                    this.methodID = Enums.SubMethod.Extrema;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathExtrema);
                    break;
                case "Multiple choice":
                    this.methodID = Enums.SubMethod.MultipleChoice;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathMultipleChoice);
                    break;
                default:
                    this.methodID = Enums.SubMethod.None;
                    rbCurrMeth = (RadioButton) findViewById(R.id.rbMethodMathAdd);
                    break;
            }

            rbCurrDiff.setChecked(true);
            rbCurrMeth.setChecked(true);


        }else{
            edit = false;
        }

    }


    public String generateExample(String difficulty, int methodID) {
        Random rd = new Random();
        int exeasynum1 = rd.nextInt(10);
        int exeasynum2 = rd.nextInt(10);
        int ergexeasy;
        int easynum1 = rd.nextInt(100);
        int easynum2 = rd.nextInt(100);
        int middlenum1 = rd.nextInt(1000);
        int middlenum2 = rd.nextInt(1000);
        int hardnum1 = rd.nextInt(10000);
        int hardnum2 = rd.nextInt(10000);
        int exhardnum1 = rd.nextInt(100000);
        int exhardnum2 = rd.nextInt(100000);

        if (methodID == 4){
            ergexeasy = faculty(exeasynum1);
            return "(This has no difficulty setting) \n" + exeasynum1 + "! = " + ergexeasy;
        }


        switch (difficulty){
            case "exEasy":
                if(methodID != 5 && methodID != 6){
                return multiplexKind(exeasynum1, exeasynum2,methodID);}
                switch (methodID){
                    case 5:
                        ergexeasy = exeasynum1;
                        return "For function f(x) = x is f(" + exeasynum1 + ") = " + ergexeasy;
                    case 6:
                        return "For function f(x) = x is no extremum out of infinity calculable";
                    default:
                        return "Something went terribly wrong >.<";
                }

            case "easy":
                if(methodID != 5 && methodID != 6){
                    return multiplexKind(easynum1, easynum2,methodID);}
                switch (methodID){
                    case 5:
                        ergexeasy = exeasynum1*exeasynum1 + exeasynum1;
                        return "For function f(x) = x²+x is f(" + exeasynum1 + ") = " + ergexeasy;
                    case 6:
                        String erg = "{-0.5}";
                        return "For function f(x) = x²+x is the extremum at " + erg;
                    default:
                        return "Something went terribly wrong >.<";
                }
            case "middle":
                if(methodID != 5 && methodID != 6){
                    return multiplexKind(middlenum1, middlenum2,methodID);}
                switch (methodID){
                    case 5:
                        ergexeasy = exeasynum1*exeasynum1*exeasynum1*middlenum2 + exeasynum1*exeasynum1 * (middlenum1+middlenum2) + middlenum1*middlenum2;
                        return "For function f(x) = " +middlenum2 + "x³+" + middlenum1+middlenum2 + "x²+" + middlenum1*middlenum2 + "x is f(" + exeasynum1 + ") = " + ergexeasy;
                    case 6:
                        String erg = findExtrema((float) exeasynum1, (float) middlenum2, (float) middlenum1);
                        return "For function f(x) = " +middlenum2 + "x³+" + middlenum1+middlenum2 + "x²+" + middlenum1*middlenum2 + "x  are the extrema at " + erg;
                    default:
                        return "Something went terribly wrong >.<";
                }
            case "hard":
                if(methodID != 5 && methodID != 6){
                    return multiplexKind(hardnum1, hardnum2,methodID);}
                int rand = rd.nextInt(10);
                while (rand == 0) rand = rd.nextInt(10);
                switch (methodID){
                    case 5:
                        ergexeasy = exeasynum1*exeasynum1*exeasynum1*exeasynum1*hardnum2 + exeasynum1*exeasynum1*exeasynum1*(hardnum1+hardnum2) + exeasynum1*exeasynum1*hardnum1*rand + exeasynum1*hardnum1/rand;
                        return "For function f(x) = " +hardnum2 + "x⁴+" + hardnum1+hardnum2 + "x³+" + hardnum1*rand + "x²+" + hardnum1/rand + "x is f(" + exeasynum1 + ") = " + ergexeasy;
                    case 6:
                        String erg = findExtremaBig((float) exeasynum1, (float) hardnum1, (float) hardnum2, (float) rand, difficulty);
                        return "For function f(x) = " +hardnum2 + "x⁴+" + hardnum1+hardnum2 + "x³+" + hardnum1*rand + "x²+" + hardnum1/rand + "x are the extrema at " + erg;
                    default:
                        return "Something went terribly wrong >.<";
                }
            case "exHard":
                if(methodID != 5 && methodID != 6){
                    return multiplexKind(exhardnum1, exhardnum2, methodID);}
                rand = rd.nextInt(10);
                while (rand == 0) rand = rd.nextInt(10);
                switch (methodID){
                    case 5:
                        ergexeasy = exeasynum1*exeasynum1*exeasynum1*exeasynum1*exeasynum1*exhardnum2 + exeasynum1*exeasynum1*exeasynum1*exeasynum1*(exhardnum1+exhardnum2) + exeasynum1*exeasynum1*exeasynum1*exhardnum1*rand + exeasynum1*exeasynum1*exhardnum1/rand + exeasynum1*rand;
                        return "For function f(x) = " +exhardnum2 + "x⁵+" + exhardnum1+exhardnum2 + "x⁴+" + exhardnum1*rand + "x³+" + exhardnum1/rand + "x²+" + rand + "x is f(" + exeasynum1 + ") = " + ergexeasy;
                    case 6:
                        String erg = findExtremaBig((float) exeasynum1, (float) exhardnum1, (float) exhardnum2, (float) rand, difficulty);
                        return "For function f(x) = " +exhardnum2 + "x⁵+" + exhardnum1+exhardnum2 + "x⁴+" + exhardnum1*rand + "x³+" + exhardnum1/rand + "x²+" + rand + "x are the extrema at " + erg;
                    default:
                        return "Something went terribly wrong >.<";

                }
            default:
                return "Something went horribly wrong >.<";


        }
    }

    private String findExtremaBig(float x, float y, float z, float a, String difficulty) {

        switch(difficulty){
            case "hard":
                //zx^4 + yzx^3 + yax^2 + (y/a)x
                //f'=4zx^3 + 3yzx^2 +2yax + (y/a) != 0
                float h = a*x*(4*x+3*y);
                float erg1 = 0;
                float erg2 = 0;
                float erg3 = 0;
                boolean e1 = false;
                boolean e2 = false;
                boolean e3 = false;
                float v = a * x * x * (4 * x + 3 * y);
                if(h != 0 && (z* v) != 0){
                    erg1 = (((-2*a*a*x*y)*(-y))*x)/(z*v);
                    e1 = true;
                }
                if(a != 0){
                    if(y == 0){
                        erg3 = 0;
                        e3 = true;
                    }
                    if(y == (float) 2.0/(3.0*a*a)){
                        erg2 = -1/(2*a*a);
                        e2 = true;
                    }
                }
                if (e1) {
                    if(e2){
                        if(e3){
                            return "{" + erg1 + ", " + erg2 + ", " + erg3 + "}";
                        }else{
                            return "{" + erg1 + ", " + erg2 + "}";
                        }
                    }else{
                        if(e3){
                            return "{" + erg1 + ", " + erg3 + "}";
                        }else{
                            return "{" + erg1 + "}";
                        }
                    }
                }else{
                    if(e2){
                        if(e3){
                            return "{" + erg2 + ", " + erg3 + "}";
                        }else{
                            return "{" +  erg2 + "}";
                        }
                    }else{
                        return "{" + erg3 + "}";
                    }
                }
            case "exHard":
                //zx^5+yzx^4+yax^3+(y/a)x^2+ax
                //f'=5zx^4 + 4yzx^3 + 3yax^2 + (2y/a)x + a != 0
                // 1. x = 0
                // 2. 5zx^3 + 4yzx^2 + 3yax + 2y/a + a/x = 0
                //--> 1. x = 0
                return "Nope, maybe coming some other time, anyway, who would even be able to solve something that hard on wakeup lol";
            default:
                return "Something went terribly wrong >.<";
        }

    }

    private String findExtrema(float exeasynum1, float z, float y) {
        //z*x^3 + (y + z) * x^2 + y*z*x
        //f' = 3*z * x^2 + 2*(y + z)x + y * z = 0
        // div 3z
        // = x^2 + (2/3)*yx +y/3
        //pq
        //((2/3)y)^2 +- sqroot(((2/3)y)^2 - y/3)
        // = (4/9)y^2 +- sqroot((4/9)y^2 - y/3)
        double root = Math.sqrt(4.0/9.0*(y*y)- (y/3.0));
        double erg1 = 4.0/9.0*(y*y) + root;
        double erg2 = 4.0/9.0*(y*y) - root;
        return "{" + erg1 + ", " + erg2 + "}";
    }



    private String multiplexKind(int num1, int num2, int methodID) {

        int erg;
        String example = "Something went pretty wrong";

        switch(methodID){
            case 8:
                erg = num1 + num2;
                example = num1 + " + " + num2 + " = " + erg;
                break;
            case 0:
                erg = num1 - num2;
                example = num1 + " - " + num2 + " = " + erg;
                break;
            case 1:
                erg = num1 * num2;
                example = num1 + " * " + num2 + " = " + erg;
                break;
            case 2:
                erg = num1 / num2;
                example = "(Only integerdivision, so round the number down)\n" + num1 + " / " + num2 + " = " + erg;
                break;
            case 3:
                erg = num1 * num1;
                example = "√" + erg + " = " + num1 + "warning, setting this on extremely hard might generate numbers bigger than allowed, use at own risk";
                if (erg < 0){
                    example = "The number was so big, it overflowed the range of int (~2,1 trillion)"; //todo this is an extremely unsafe implementation, change asap
                }
                break;
        }
        return example;
    }


    private int faculty(int exeasynum1) {
        if (exeasynum1 == 0) {
            return 1;
        }else{
            return exeasynum1 * faculty(exeasynum1-1);
        }
    }
}