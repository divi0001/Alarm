package com.example.alarm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


//TODO: Apache License include
public class QRMethodSetActivity extends AppCompatActivity {
    private static final int REQUEST_FROM_CAMERA = 20;
    private Button btnAddNewQRBar, btnAddSelectedQRBar, btnAddFromAbove;
    private EditText editLabel;
    private Spinner spSavedQRBars;
    private TextView txtDecode,txtFormat;

    private ArrayList<Bitmap> qrCodes,barCodes;
    private DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrmethod_set);

        btnAddNewQRBar = (Button) findViewById(R.id.btnReadQRBarCode);
        txtDecode = (TextView) findViewById(R.id.txtDecode);
        txtFormat = (TextView) findViewById(R.id.txtFormat);
        btnAddSelectedQRBar = (Button) findViewById(R.id.btnAddSelected);
        btnAddFromAbove = (Button) findViewById(R.id.btnAddNewQRCode);
        editLabel = (EditText) findViewById(R.id.editLabelMe);

        db = new DBHelper(this, "QRBarcodedatabase");


        //spSavedQRBars.setAdapter();



        btnAddNewQRBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(QRMethodSetActivity.this);
                intentIntegrator.setPrompt("Scan a QR/Bar Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();

            }
        });

        btnAddFromAbove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor c = db.getData("QRBarcodedatabase");
                ArrayList<String> values = new ArrayList<>();

                if(c.getCount()>0){
                    while(c.moveToNext()){
                        values.add(c.getString(0));
                    }
                    System.out.println(values);
                }
                if(values.contains(editLabel.getText().toString())){
                    Toast.makeText(QRMethodSetActivity.this, "Label already used. Please use a unique label", Toast.LENGTH_SHORT).show();
                }else {

                    db.insertAlarmData(new String[]{"label", "decoded"}, new String[]{editLabel.getText().toString(), txtDecode.getText().toString()}, "QRBarcodedatabase");
                    finish();
                }


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {

                txtDecode.setText(intentResult.getContents());
                txtFormat.setText(intentResult.getFormatName());





            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }



    }







}