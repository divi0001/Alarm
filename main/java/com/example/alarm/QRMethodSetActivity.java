package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;


public class QRMethodSetActivity extends AppCompatActivity {

    private RadioGroup rgQRBarMethod;
    private RadioButton rbQR, rbBar;
    private Button btnAddNewQRBar, btnAddSelectedQRBar;
    private EditText editLabel;
    private Spinner spSavedQRBars;
    Bitmap bitmap;

    private ArrayList<Bitmap> qrCodes,barCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrmethod_set);



    }
}