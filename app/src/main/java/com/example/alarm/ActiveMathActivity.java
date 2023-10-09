package com.example.alarm;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.LongPredicate;

public class ActiveMathActivity extends AppCompatActivity {

    private TextView txtSnoozesLeft, txtMathTask;
    private Button btnSubmit, btnSnooze;
    private SeekBar seekBarSoundTurnOn;
    private EditText editMathAnswer;
    private ValueAnimator anim;
    int tempProgress = 0;
    long taskTime = 30*1000;
    int lvlID, queueID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_math);


        seekBarSoundTurnOn = (SeekBar) findViewById(R.id.seekProgressBarMath);
        anim = ValueAnimator.ofInt(0, seekBarSoundTurnOn.getMax());
        txtMathTask = (TextView) findViewById(R.id.txtMathTask);
        txtSnoozesLeft = (TextView) findViewById(R.id.txtMathSnoozesLeft);
        btnSubmit = (Button) findViewById(R.id.btnSubmitMathAnswer);
        btnSnooze = (Button) findViewById(R.id.btnMathSnooze);
        editMathAnswer = (EditText) findViewById(R.id.editMathAnswer);

        taskTime = getSharedPreferences(getString(R.string.settings_key),MODE_PRIVATE).getInt("time_per_task", 30)*1000L;


        lvlID = -1;
        queueID = -1;
        Alarm alarm = new Alarm(-1);

        int alarmId = getIntent().getIntExtra("id",-1); // defVal 0?
        try(DBHelper db = new DBHelper(this, "Database.db")){
            alarm = db.getAlarm(alarmId);
        }
        //com.example.alarm.AlarmReceiver.r.stop(); pausing the alarm sound
        //com.example.alarm.AlarmReceiver.r.play(); repeating alarm sound from start
        //todo code to check if its a new level, therefore if a new sound uri should be set, if that's a thing, but i want to have that implemented

        Enums.Difficulties difficulty = Enums.Difficulties.ExEasy;
        Enums.SubMethod mode = Enums.SubMethod.Add;

        if(alarm.isHasLevels()){
            lvlID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
            queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            difficulty = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getDifficulty();
            mode = alarm.getlQueue().get(lvlID).getmQueue().get(queueID).getSubMethod();
            txtSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID));

        }else {
            queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
            difficulty = alarm.getmQueue(-1).get(queueID).getDifficulty();
            mode = alarm.getmQueue(-1).get(queueID).getSubMethod();
            txtSnoozesLeft.setText(String.valueOf(alarm.getSnoozeAmount(-1)));

        }




        long[] task = makeMathTask(difficulty, mode);

        String displayedText = mode != Enums.SubMethod.FuncVal && mode != Enums.SubMethod.Extrema && mode != Enums.SubMethod.Fac && Enums.SubMethod.Root != mode
                ? task[0] + " " + toOperator(mode) + " " + task[1] + " = ?" : makeComplikatedDisplayText(task, mode, difficulty);

        txtMathTask.setText(displayedText);

        Alarm finalAlarm1 = alarm;
        btnSnooze.setOnClickListener(v -> {
            if(Integer.parseInt(txtSnoozesLeft.getText().toString())>0){
                AlarmReceiver.r.stop();
                getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("snooze_amount", Integer.parseInt(txtSnoozesLeft.getText().toString())-1).apply();
                txtSnoozesLeft.setText(Integer.parseInt(txtSnoozesLeft.getText().toString())-1);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    AlarmReceiver.r.play();
                    Intent i = new Intent(ActiveMathActivity.this, ActiveMathActivity.class);
                    i.putExtra("id", alarmId);
                    startActivity(i);
                }, finalAlarm1.getSnoozeMinutes(lvlID)*60*1000L);



            }else{
                Toast.makeText(ActiveMathActivity.this, "No snoozes left, get up!", Toast.LENGTH_SHORT).show();
            }
        });

        Alarm finalAlarm = alarm;
        Enums.SubMethod finalMode = mode;
        Enums.Difficulties finalDifficulty = difficulty;
        btnSubmit.setOnClickListener(v -> {
            if(isCorrect(task, editMathAnswer.getText().toString(), finalMode, finalDifficulty)){
                nextOrAlarmOff(finalAlarm);
            }else{
                editMathAnswer.setText("");
                Toast.makeText(ActiveMathActivity.this, "Your answer didn't match the internally calculated one", Toast.LENGTH_LONG).show();
            }
        });


        anim.setDuration(taskTime);
        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            seekBarSoundTurnOn.setProgress(animProgress);
        });
        anim.start();

        seekBarSoundTurnOn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    resetToUserProgress(seekBar, progress);
                    tempProgress = progress;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    anim.cancel();
                    tempProgress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                resetToUserProgress(seekBar, seekBar.getProgress());
            }
        });










    }

    private void nextOrAlarmOff(Alarm alarm) {
        //lvlID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_lvl_id",0);
        //queueID = getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).getInt("curr_queue_id",0);
        if(alarm.isHasLevels()){
            if(alarm.getlQueue().get(lvlID).getmQueue().size()-1 == queueID){
                if(alarm.getlQueue().size()-1 == lvlID){
                    getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_lvl_id", -1).apply();
                    getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", 0).apply();
                }
                lvlID++;
                getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_lvl_id", lvlID).apply();
                getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", 0).apply();

            }else if(lvlID != -1){
                queueID++;
                getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
            }else{
                if(alarm.getmQueue(-1).size()-1 == queueID){
                    //todo cleanup after alarm is done
                    com.example.alarm.AlarmReceiver.r.stop();
                    finish();
                }else{
                    queueID++;
                    getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
                }
            }
        }else{
            if (alarm.getmQueue(-1).size()-1 == queueID){
                //todo cleanup after alarm is done
                com.example.alarm.AlarmReceiver.r.stop();
                finish();
            }else{
                queueID++;
                getSharedPreferences(getString(R.string.active_alarm_progress_key), MODE_PRIVATE).edit().putInt("curr_queue_id", queueID).apply();
            }
        }
    }

    private boolean isCorrect(long[] task, String answer, Enums.SubMethod sub, Enums.Difficulties diff) {
        switch(sub){
            case None:
            case Mult:
            case Sub:
            case Div:
            case Add:
                return Long.parseLong(answer) == task[2];
            case Root:
            case Fac:
                return Long.parseLong(answer) == task[1];
            case Extrema:
                long[] lAns = toLongArray(answer);
                switch(diff){
                    case None:
                    case ExEasy:
                    case Easy:
                    case Middle:
                        return containsLongs(lAns, new long[]{task[0], task[1]});
                    case Hard:
                        return containsLongs(lAns,new long[]{task[0], task[1],task[2], task[3]});
                    case ExHard:
                        return containsLongs(lAns,new long[]{task[0], task[1],task[2], task[3],task[4]});
                    default:
                        return false;
                }
            case FuncVal:
                long[] lVals = toLongArray(answer);
                return containsLongs(lVals, new long[]{task[task.length-1]});
            default:
                return false;
        }
    }

    private boolean containsLongs(long[] toLongArray, long[] longs) {
        if(toLongArray.length != longs.length){
            return false;
        }else{
            for(long l: toLongArray){
                if(!Arrays.asList(longs).contains(l)) return false; //todo make sure this actually looks, if l is contained in the longs array
            }
            return true;
        }
    }

    private long[] toLongArray(String answer) {
        long[] lArr;
        ArrayList<Long> answers = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        int index=0;
        for (char c: answer.toCharArray()) {

            if(c == ',' && index != 0){
                answers.add(Long.parseLong(buf.toString()));
                buf = new StringBuilder();
            }else if(Character.isDigit(c)){
                buf.append(c);
            }else if(c == '-' && buf.length() == 0){
                buf.append("-");
            }else{
                Toast.makeText(this, "Wrong format of answer", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Give the answer as a comma seperated list like val1,val2,val3,... if there are double answers, naming 1 of them is enough", Toast.LENGTH_LONG).show();
                break;
            }
            index++;
        }
        lArr = new long[answer.length()];
        index = 0;
        for(long l: answers){
            lArr[index] = l;
            index++;
        }

        return lArr;
    }

    private String toOperator(Enums.SubMethod sub) {
        switch(sub){
            case None:
            case Add:
                return "+";
            case Root:
                return "√";
            case Fac:
                return "!";
            case Div:
                return "÷";
            case Sub:
                return "-";
            case Mult:
                return "·";
            default:
                return "not a defined operator";
        }
    }

    private String makeComplikatedDisplayText(long[] taskArr, Enums.SubMethod sub, Enums.Difficulties diff) {

        String stringedTask = "";  //0th element set to "0" so the index of the pws array is also the power
        String[] pws = new String[]{"x⁵","x⁴","x³","x²","x"};
        stringedTask += sub == Enums.SubMethod.FuncVal ? "Value for f(x) = " : "Extrema for f(x) = ";
        if(sub == Enums.SubMethod.FuncVal){
            switch(diff){
                case None:
                case ExEasy:
                    stringedTask+= taskArr[0] + pws[4] + " , x is " + taskArr[1]; //this one is in reverse order because too lazy to change (it is right this way dont worry)
                    break;
                case Easy:
                    stringedTask+= taskArr[1] + pws[4] + " + " + taskArr[2] + pws[3] + " , x is " +taskArr[0]; //this should be the structure for every func value from now on
                    break;
                case Middle:
                    stringedTask+= taskArr[1] + pws[4] + " + " + taskArr[2] + pws[3] + " + " + taskArr[3] + pws[2] + " , x is " + taskArr[0];
                    break;
                case Hard:
                    stringedTask+= taskArr[1] + pws[4] + " + " + taskArr[2] + pws[3] + " + " + taskArr[3] + pws[2] + " + " + taskArr[4] + pws[1] + " , x is " + taskArr[0];
                    break;
                case ExHard:
                    stringedTask+= taskArr[1] + pws[4] + " + " + taskArr[2] + pws[3] + " + " + taskArr[3] + pws[2] + " + " + taskArr[4] + pws[1] + " + " + taskArr[5] + pws[0] + " , x is " + taskArr[0];
                    break;
                default:
                    stringedTask+= taskArr[0] + pws[4] + " , x is " + taskArr[1]; //this one is in reverse order because too lazy to change (it is right this way dont worry)
                    break;
            }
        }else if (sub == Enums.SubMethod.Extrema){
             switch (diff){
                 case None:
                 case ExEasy:
                     stringedTask+= pws[3] + (taskArr[0] < 0 ? " - ": " + ") + abs(taskArr[3]) + "x + " + taskArr[4];
                     break;
                 case Easy:
                     stringedTask+= pws[3] + (taskArr[3] < 0 ? " - ": " + ") + abs(taskArr[3]) + "x" + (taskArr[4] < 0 ? " - ": " + ") + abs(taskArr[4]);
                     break;
                 case Middle:
                     stringedTask+= pws[1] + (taskArr[1] < 0 ? " - ": " + ") + abs(taskArr[1]) + pws[3];
                     break;
                 case Hard:
                     stringedTask+= pws[1] + (taskArr[5] < 0 ? " - ": " + ") + abs(taskArr[5]) + pws[2] + (taskArr[6] < 0 ? " - ": " + ") + abs(taskArr[6]) + pws[3]
                             + (taskArr[7] < 0 ? " - ": " + ") + abs(taskArr[7]) + pws[4] + (taskArr[8] < 0 ? " - ": " + ") + abs(taskArr[8]);
                     break;
                 case ExHard:
                     stringedTask+= pws[0] + (taskArr[6] < 0 ? " - ": " + ") + abs(taskArr[6]) + pws[1] + (taskArr[7] < 0 ? " - ": " + ") + abs(taskArr[7]) + pws[2] +
                             (taskArr[8] < 0 ? " - ": " + ") + abs(taskArr[8]) + pws[3] + (taskArr[9] < 0 ? " - ": " + ") + abs(taskArr[9]) + pws[4] +
                             (taskArr[10] < 0 ? " - ": " + ") + abs(taskArr[10]);
                     break;
                 default:
                     stringedTask+= "Something went wrong lol Knecht";
                     break;

             }

        }else if(sub == Enums.SubMethod.Root){
            stringedTask = toOperator(Enums.SubMethod.Root) + taskArr[0] + " = ?";
        }else{
            stringedTask = taskArr[0]+ toOperator(Enums.SubMethod.Fac) + " = ?";
        }

        return stringedTask;
    }

    private long[] makeMathTask(Enums.Difficulties diff, Enums.SubMethod sub) {

        Random rand = new Random();
        int bound = new int[]{10,100,1000,10000,100000}[diff.ordinal()];
        int[] randarray = new int[10];
        for(int i = 0; i < randarray.length; i++) randarray[i] = rand.nextInt(bound);

        long[] taskArray = new long[5];
        taskArray[0] = randarray[0];
        taskArray[1] = randarray[1];
        switch(sub){
            case Add:
                taskArray[2] = taskArray[0]+taskArray[1];
                break;
            case Sub:
                taskArray[2] = taskArray[0]-taskArray[1];
                break;
            case Mult:
                taskArray[2] = taskArray[0]*taskArray[1];
                break;
            case Div:
                taskArray[2] = taskArray[0]/taskArray[1]; //todo maybe make this, so only even divisions (without rest) come outta here
                break;
            case Fac:
                taskArray[0] = rand.nextInt(10);
                taskArray[1] = faculty((int)taskArray[0]);
                break;
            case Root:
                taskArray[1] = taskArray[1] > 100 ? rand.nextInt(100) : taskArray[1]; //this is an if expression in one line, in the form of condition ? true execution : false execution
                taskArray[0] = taskArray[1]*taskArray[1];
                break;
            case Extrema:
            case FuncVal:
                taskArray = mkTaskArrExtrFuncVal(diff, randarray, sub);
                break;
            default:
                taskArray[2] = taskArray[0]+taskArray[1];
                break; //prolly useless, for the style points xd

        }

        return taskArray;
    }

    private long[] mkTaskArrExtrFuncVal(Enums.Difficulties diff, int[] randarray, Enums.SubMethod sub) {

        long[] retArr;
        Random rand = new Random();
        long[] smallRandArr = new long[]{rand.nextInt(10),rand.nextInt(10),rand.nextInt(10),rand.nextInt(10),rand.nextInt(10)};

        if(sub == Enums.SubMethod.FuncVal){
            switch(diff){
                case None:
                case ExEasy:
                    retArr = new long[]{randarray[0],smallRandArr[0], (long) randarray[0] *smallRandArr[0]};
                    break;
                case Easy:
                    retArr = new long[]{smallRandArr[0],randarray[0],randarray[1], (long) randarray[0] *smallRandArr[0] + (long) randarray[1] * smallRandArr[0]*smallRandArr[0]};
                    break;
                case Middle:
                    retArr = new long[]{smallRandArr[0],randarray[0],randarray[1],randarray[2], (long) ((long) randarray[0] *smallRandArr[0] +
                            (long) randarray[1] *smallRandArr[0]*smallRandArr[0] + (long) randarray[2]* Math.pow(smallRandArr[0],3))};
                    break;
                case Hard:
                    retArr = new long[]{smallRandArr[0],randarray[0],randarray[1],randarray[2], randarray[3],
                            (long) ((long) randarray[0] *smallRandArr[0] + (long) randarray[1] *smallRandArr[0]*smallRandArr[0] + (long) randarray[2]*
                                    Math.pow(smallRandArr[0],3) + + (long) randarray[3]* Math.pow(smallRandArr[0],4))};
                    break;
                case ExHard:
                    retArr = new long[]{smallRandArr[0],randarray[0],randarray[1],randarray[2], randarray[3],
                            randarray[4], (long) ((long) randarray[0] *smallRandArr[0] + (long) randarray[1] *smallRandArr[0]*smallRandArr[0]
                            + (long) randarray[2]*Math.pow(smallRandArr[0],3) + (long) randarray[3]* Math.pow(smallRandArr[0],4) + (long) randarray[4]* Math.pow(smallRandArr[0],5))};
                    break;
                default:
                    retArr = new long[]{randarray[0],smallRandArr[0], (long) randarray[0] *smallRandArr[0]};
                    break; //style point yk :P
            }
        }else{

            for(int i = 0; i < smallRandArr.length; i++){
                if(rand.nextInt()%2 == 0){
                    smallRandArr[i] = -smallRandArr[i];
                }
            }

            switch(diff){

                case None:
                case ExEasy:            // args  (x+smallRandArr[0])^2  ,x^2, (?-)2const*x                                               , const (last args are the function multiplied from minoms)
                    retArr = new long[]{smallRandArr[0], smallRandArr[0], 1, smallRandArr[0] < 0 ? -2*smallRandArr[0] : 2*smallRandArr[0], smallRandArr[0]*smallRandArr[0]};
                    break;
                case Easy:              // (x+s[0])*(x+s[1])            ,x^2, s[0]*x + s[1]*x               , s[0]*s[1] (remember, minus is included :D)
                    retArr = new long[]{smallRandArr[0], smallRandArr[1], 1, smallRandArr[0]+smallRandArr[1], smallRandArr[0]*smallRandArr[1]};
                    break;
                case Middle://x^4 + s[1]x^2 type: clean biquadratics, will generate int extrema after root substituting
                    retArr = new long[]{1, //must be 1 instead of smallRandArr[0] < 0 ? -(long) Math.pow(smallRandArr[0],2)*2: (long) Math.pow(smallRandArr[0],2)*2
                             smallRandArr[1] < 0 ? -(long) Math.pow(smallRandArr[1],2)*2:(long) Math.pow(smallRandArr[1],2)*2,
                            -(long) smallRandArr[1]*2, 0};
                    break;
                case Hard: //(x+a)(x+b)(x+c)(x+d) = (x^2+(a+b)x+ab)(x^2+(c+d)x+cd) = x^4 + (c+d)x^3 + cdx^2 + (a+b)x^3 + (a+b)*(c+d)x^2 + (a+b)cdx + abx^2 + (c+d)abx + abcd
                    // = x^4 + (a+b+c+d)x^3 + (a+b+c+d+ac+ad+bc+bd)x^2 + (i lost interest in calculating this because there are online tools) (a to d may be negative)
                    long a,b,c,d;
                    a= smallRandArr[0];
                    b= smallRandArr[1];
                    c= smallRandArr[2];
                    d= smallRandArr[3];      //^4, x^3   , x^2                               , x^1                           , x^0
                    retArr = new long[]{a,b,c,d,1,a+b+c+d,(a*b)+(a*c)+(a*d)+(b*c)+(b*d)+(c*d),(a*b*c)+(a*b*d)+(a*c*d)+(b*c*d), a*b*c*d}; //(yes i know that the brackets are sometimes redundant because first * then +)
                    break;
                case ExHard:
                    long j,k,l,m,n;
                    j= smallRandArr[0]; //a
                    k= smallRandArr[1]; //b
                    l= smallRandArr[2]; //c
                    m= smallRandArr[3]; //g
                    n= smallRandArr[4]; //h                         bruh to anyone solving this as their normal wakeup routine -> 2 thoughts: are you insane?! and wtf this is way too hard to solve when waking
                    retArr = new long[]{j,k,l,m,n, 1, j+k+l+m+n, (m*n)+(l*n)+(m*l)+(k*n)+(k*m)+(k*l)+(j*n)+(j*m)+(j*l)+(j*k),
                             (m*n*l)+(k*m*n)+(l*k*n)+(k*m*l)+(j*m*n)+(j*l*n)+(j*m*l)+(j*k*n)+(j*k*m)+(j*k*l), //x^2
                            (k*m*n*l)+(j*m*n*l)+(j*k*m*n)+(j*k*n*l)+(j*k*m*l), j*k*l*m*n};
                    break; //go touch some grass xP jk jk i love you
                default:
                    retArr = new long[]{smallRandArr[0], smallRandArr[0], 1, smallRandArr[0] < 0 ? -2*smallRandArr[0] : 2*smallRandArr[0], smallRandArr[0]*smallRandArr[0]};
                    //exeasy/none case
                    break;

            }

        }
        return retArr;
    }

    private void resetToUserProgress(SeekBar seekBar, int progress) {
        seekBar.setProgress(progress);
        anim.cancel();

        anim = ValueAnimator.ofInt(progress, seekBarSoundTurnOn.getMax());

        anim.setDuration((long) (taskTime-(progress/seekBarSoundTurnOn.getMax()*1F)));
        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            seekBarSoundTurnOn.setProgress(animProgress);
        });
        anim.start();
    }



    private int faculty(int exeasynum1) {
        if (exeasynum1 == 0) {
            return 1;
        }else{
            return exeasynum1 * faculty(exeasynum1-1);
        }
    }
}