package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MemoryMethodSetActivity extends AppCompatActivity {

    private SeekBar seekSize;
    private TextView txtMemSize;
    private Button btnAddSelected;
    private GridView gridViewMem;
    private int level = 1;
    private int countPair = 0;
    final int[] draw = new int[]{R.drawable.sample1,R.drawable.sample2,R.drawable.sample3,R.drawable.sample4,R.drawable.sample5,R.drawable.sample6,R.drawable.sample7
            ,R.drawable.sample8,R.drawable.sample9,R.drawable.sample10,R.drawable.sample11,R.drawable.sample12,R.drawable.sample13,R.drawable.sample14,
            R.drawable.sample15,R.drawable.sample16};
    ImageView currImgView;
    int[] pos = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    int currPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_method_set);

        seekSize = (SeekBar) findViewById(R.id.seekMemorySize);
        txtMemSize = (TextView) findViewById(R.id.txtMemSize);
        gridViewMem = (GridView) findViewById(R.id.gridMemoryExample);
        btnAddSelected = (Button) findViewById(R.id.btnAddMemory);
        
        
        seekSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 25 && progress >= 0){
                    txtMemSize.setText("2x2");
                    level = 0;
                } else if (progress > 24 && progress < 50) {
                    txtMemSize.setText("4x4");
                    level = 1;
                } else if (progress > 49 && progress < 75) {
                    txtMemSize.setText("6x6");
                    level = 2;
                }else{
                    txtMemSize.setText("8x8");
                    level = 3;
                }
            }


            //Multithreading like this:
            //Handler handler = new Handler();
            //handler.postDelayed(new Runnable(){
            //@Override
            //public void run(){
            //...(async task execution)}},numArg); //numArg prolly number of milliseconds until task execution?

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        GridViewImageAdapter adapter = new GridViewImageAdapter(this, 8);
        gridViewMem.setAdapter(adapter);

        gridViewMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currPos < 0){
                    currPos = position;
                    currImgView = (ImageView) view;
                    ((ImageView) view).setImageResource(draw[pos[position]]);
                }else {
                    if (currPos == position ){
                        ((ImageView)view).setImageResource(R.drawable.hidden);
                    } else if (pos[currPos] != pos[position]) {
                        currImgView.setImageResource(R.drawable.hidden);
                        Toast.makeText(MemoryMethodSetActivity.this, "Not matched", Toast.LENGTH_SHORT).show();
                    }else {
                        ((ImageView)view).setImageResource(draw[pos[position]]);
                        countPair++;

                        if(countPair == 0){
                            Toast.makeText(MemoryMethodSetActivity.this, "You win yay ^^", Toast.LENGTH_SHORT).show();
                        }
                    }
                    currPos = -1;
                }
            }
        });

    }
}