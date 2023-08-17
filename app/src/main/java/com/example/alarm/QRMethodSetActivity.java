package com.example.alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.Objects;


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
        spSavedQRBars = (Spinner) findViewById(R.id.spinnerQRBar);

        db = new DBHelper(this, "Database.db");






        btnAddNewQRBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(QRMethodSetActivity.this);
                intentIntegrator.setPrompt("Scan a QR/Bar Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();

            }
        });
        Cursor c = db.getData("QRBarcodedatabase");

        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        if(c.getCount()>0){
            c.moveToFirst();
            while(c.moveToNext()){
                labels.add(c.getString(0)); //TODO: find out, why only 1 item is shown in the qr/barcode spinner when updating
                values.add(c.getString(1));
            }
        }

        SharedPreferences sp = QRMethodSetActivity.this.getSharedPreferences(getString(R.string.queue_key), MODE_PRIVATE);
        int queueId = sp.getInt("queue_id",-1);
        btnAddFromAbove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("label")){

                    //todo if this was based on editing an already set method, this needs another if to filter, if the label is unique, if a new qr was added, but it also shouldn't stop this from working if the label stays the same, because then it might be seen as ununique?

                    int row_id = getIntent().getIntExtra("id",-1);

                    SharedPreferences sp = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key),MODE_PRIVATE);
                    SharedPreferences.Editor se = sp.edit();
                    se.putString("Method", Enums.Method.QRBar.name());
                    se.putString("QRLabel", editLabel.getText().toString());
                    //decoded value at the key of the label in the qr DB
                    se.apply();
                    finish();
                }else {


                    if (labels.contains(editLabel.getText().toString())) {
                        Toast.makeText(QRMethodSetActivity.this, "Label already used. Please use a unique label", Toast.LENGTH_SHORT).show();
                    } else {

                        db.addQRBar(editLabel.getText().toString(), txtDecode.getText().toString());

                        int l = db.getMaxTableId("Methoddatabase")+1;

                        SharedPreferences sp = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key),MODE_PRIVATE);
                        SharedPreferences.Editor se = sp.edit();
                        se.putString("Method", Enums.Method.QRBar.name());
                        se.putString("QRLabel", editLabel.getText().toString());
                        //decoded value at the key of the label in the qr DB
                        se.apply();
                        finish();
                    }

                }
            }
        });

        spSavedQRBars.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels));

        btnAddSelectedQRBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("label") && getIntent().hasExtra("edit_add") && getIntent().hasExtra("id")) {
                    int row_id = getIntent().getIntExtra("id",-1);


                    db.editMethoddatabase(queueId, 2, -1, -1, spSavedQRBars.getSelectedItem().toString(), -1, row_id);
                    finish();

                }else {
                    int l = db.getMaxTableId("Methoddatabase")+1;
                    db.addMethod(l, queueId, 2, -1, -1, spSavedQRBars.getSelectedItem().toString(), -1);
                    finish();

                }

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Cursor c = db.getData("QRBarcodedatabase");

        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();


        if(c.getCount()>0){
            c.moveToFirst();
            while(c.moveToNext()){
                labels.add(c.getString(0));
                values.add(c.getString(1));
            }
        }
        spSavedQRBars.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels));
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