package com.example.alarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;


public class QRMethodSetActivity extends AppCompatActivity {

    private static final int REQUEST_FROM_CAMERA = 20;
    private RadioGroup rgQRBarMethod;
    private RadioButton rbQR, rbBar;
    private Button btnAddNewQRBar, btnAddSelectedQRBar;
    private EditText editLabel;
    private Spinner spSavedQRBars;
    private Bitmap bitmap;
    private String path;

    private ArrayList<Bitmap> qrCodes,barCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrmethod_set);

        btnAddNewQRBar = (Button) findViewById(R.id.btnAddQRBarCode);

        btnAddNewQRBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("foto",android.provider.MediaStore.EXTRA_OUTPUT);

                startActivityForResult(intent,REQUEST_FROM_CAMERA);
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_FROM_CAMERA && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            DetectQRCode det = new DetectQRCode();
            if (det.detect(photo)){
                editLabel.setHint((CharSequence) photo.toString()); //TODO: read qr from foto
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}