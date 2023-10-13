package com.example.alarm;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.Objects;

public class ActiveLocationActivity extends AppCompatActivity implements LocationListener {

    SeekBar seekBarSoundTurnOn;
    ValueAnimator anim;
    AlarmMgr alarmMgr = new AlarmMgr(this);
    int tempProgress = 0;
    long taskTime = 1000*30L;
    int lvlID, queueID;
    TextView txtSnoozesLeft;
    Button btnSnooze, btnFakeUpdate;
    MapView mapview;
    PolygonOptions currPoly;
    public double longitude, latitude, radius, zoom, userLong, userLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(ActiveLocationActivity.this, getResources().getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_location);


        txtSnoozesLeft = (TextView) findViewById(R.id.locationSnoozeAmount);
        seekBarSoundTurnOn = (SeekBar) findViewById(R.id.seekBarLocation);
        btnSnooze = (Button) findViewById(R.id.btnLocationSnooze);
        mapview = (MapView) findViewById(R.id.mapActiveMap);
        btnFakeUpdate = (Button) findViewById(R.id.btnFakeUpdateLocation); //make this invisible, when publishing, this is purely for testing purposes
        //btnFakeUpdate.setVisibility(View.GONE);

        userLat = -1000;
        userLong = -1000; //some value that aren't achievable

        lvlID = alarmMgr.getLvlID();
        queueID = alarmMgr.getQueueID();

        Alarm alarm = new Alarm(-1);

        int alarmId = getIntent().getIntExtra("id",-1); // defVal 0?
        try(DBHelper db = new DBHelper(this, "Database.db")){
            alarm = db.getAlarm(alarmId);
        }



        if(alarm.isHasLevels() && alarm.isSnoozable(lvlID))txtSnoozesLeft.setText(alarm.getSnoozeAmount(lvlID));
        else if(alarm.isSnoozable(-1)) txtSnoozesLeft.setText(String.valueOf(alarm.getSnoozeAmount(-1)));

        Enums.Difficulties difficulty = alarmMgr.getAlarmDifficulty(alarm);
        Enums.SubMethod mode = alarmMgr.getAlarmMode(alarm);

        if(alarm.isHasLevels()){
            longitude = alarm.getmQueue(lvlID).get(queueID).getLon();
            latitude = alarm.getmQueue(lvlID).get(queueID).getLat();
            radius = alarm.getmQueue(lvlID).get(queueID).getLocationRadius();
        }else{
            longitude = alarm.getmQueue(-1).get(queueID).getLon();
            latitude = alarm.getmQueue(-1).get(queueID).getLat();
            radius = alarm.getmQueue(-1).get(queueID).getLocationRadius();
        }
        zoom = alarmMgr.radiusToZoom(radius, latitude);

        taskTime = alarmMgr.getTaskTime();
        anim = alarmMgr.makeAnim(seekBarSoundTurnOn.getMax(), seekBarSoundTurnOn);

        seekBarSoundTurnOn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    alarmMgr.resetToUserProgress(seekBar, progress, anim, taskTime);
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
                alarmMgr.resetToUserProgress(seekBar, seekBar.getProgress(), anim, taskTime);
            }
        });

        mapview.getMapAsync(mapboxMap -> updateMap());


        btnFakeUpdate.setOnClickListener(view -> {
            userLat = 60;
            userLong = 60;
            updateMap();
        });


    }

    private void updateMap() {

        mapview.getMapAsync(mapboxMap -> {


            if(currPoly != null){
                mapboxMap.removeAnnotation(currPoly.getPolygon());
            }
            currPoly = new PolygonOptions().addAll(alarmMgr.polygonCircleForCoordinate(new LatLng(latitude,longitude), radius)).fillColor(Color.parseColor("#0F000FFF"));
            mapboxMap.addPolygon(currPoly);

            if(userLat != -1000){
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat,userLong), zoom));
            }else{
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),zoom));
            }

            mapboxMap.addOnScaleListener(new MapboxMap.OnScaleListener() {
                @Override
                public void onScaleBegin(@NonNull StandardScaleGestureDetector detector) {}
                @Override
                public void onScale(@NonNull StandardScaleGestureDetector detector) {}
                @Override
                public void onScaleEnd(@NonNull StandardScaleGestureDetector detector) {zoom = mapboxMap.getCameraPosition().zoom;System.out.println(zoom);}
            });
            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    if(userLat == -1000){
                        style.addImage("red-pin-icon-id", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_user_location_icon2))),false);
                        style.addLayer(new SymbolLayer("icon-layer-id","icon-source-id").withProperties(
                                iconImage("red-pin-icon-id"),
                                iconIgnorePlacement(true),
                                iconAllowOverlap(true),
                                iconOffset(new Float[]{0f, -9f})
                        ));
                    }
                    else{
                        style.addImage("user-icon", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_user_location_icon2))),false);
                        style.addLayer(new SymbolLayer("icon-layer-id","icon-source-id").withProperties(
                                iconImage("user-icon"),
                                iconIgnorePlacement(true),
                                iconAllowOverlap(true),
                                iconOffset(new Float[]{0f, -9f})
                        ));
                    }


                    if(userLat != -1000) {
                        GeoJsonSource iconGeoJsonSource = new GeoJsonSource("icon-source-id", Feature.fromGeometry(Point.fromLngLat(userLong, userLat)));
                        style.addSource(iconGeoJsonSource);
                    }else{
                        GeoJsonSource iconGeoJsonSource = new GeoJsonSource("icon-source-id", Feature.fromGeometry(Point.fromLngLat(longitude, latitude)));
                        style.addSource(iconGeoJsonSource);
                    }

                    zoom = alarmMgr.radiusToZoom(radius, latitude);
                }
            });
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        userLong = location.getLongitude();
        userLat = location.getLatitude();
        updateMap();
        Log.d("matt","new Location: long=" + userLong + ", latitude=" + userLat);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
        createAlert(provider);
    }

    private void createAlert(String provider) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ActiveLocationActivity.this);
        alertBuilder.setMessage(getString(R.string.enable_gps));
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton(getString(R.string.gps_is_enabled), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!manager.isProviderEnabled(provider)){
                    dialogInterface.cancel();
                    createAlert(provider);
                }else{
                    updateMap();
                    dialogInterface.cancel();
                }
            }
        });
        alertBuilder.setNegativeButton(getString(R.string.i_did_not_enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ActiveLocationActivity.this, "Why would you do this to me? Q_Q", Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
                createAlert(provider);
            }
        });
        alertBuilder.create().show();

    }
}