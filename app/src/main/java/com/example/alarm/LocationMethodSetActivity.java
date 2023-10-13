package com.example.alarm;

import static com.example.alarm.Enums.SubMethod.Enter;
import static com.example.alarm.Enums.SubMethod.Leave;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LocationMethodSetActivity extends AppCompatActivity {

    private MapView mapView;
    private SeekBar seekBar;
    private ImageView imgGetLocation, imgBtnUp, imgSatellite, imgStreetSatellite, imgStreet, imgSatelliteExpanded, imgSatelliteMap;
    private Button btnGo;
    private AutoCompleteTextView autoCompleteTextView;
    private RelativeLayout relLayoutRetractedCircleMenu;
    private ConstraintLayout constLayoutExpandedCircleMenu;
    private TextView txtKm;
    private String currMapStyle = Style.SATELLITE;
    private double latitude= 60;
    private double longitude = 60;
    private double zoom = 15.7;
    private double radius = 0.0;
    private ImageView currentImg;
    private int km,id;
    private PolygonOptions currPoly;
    private RecyclerView recViewAutoComplete;
    private String text, radiusMode;
    private List<Address> lAddr;
    private Address addr;
    private RadioGroup rgRadiusMode;
    private RadioButton rbEnter, rbLeave;
    private boolean isLeaveMode = true;
    private boolean edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(LocationMethodSetActivity.this, getResources().getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_method_set);
        
        mapView = (MapView) findViewById(R.id.mapView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        imgGetLocation = (ImageView) findViewById(R.id.imgGetLocation);
        imgBtnUp = (ImageView) findViewById(R.id.imgBtnUpCircleMenu);
        imgStreetSatellite = (ImageView) findViewById(R.id.imgSatelliteStreetMapExtended);
        imgStreet = (ImageView) findViewById(R.id.imgStreetMapExtended);
        imgSatelliteExpanded = (ImageView) findViewById(R.id.imgSatelliteMapExtended);
        btnGo = (Button) findViewById(R.id.btnGoAddLocation);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteEditTextLocation);
        constLayoutExpandedCircleMenu = (ConstraintLayout) findViewById(R.id.relLayoutCircleMenuExpanded);
        relLayoutRetractedCircleMenu = (RelativeLayout) findViewById(R.id.relLayoutCircleMenuRetracted);
        txtKm = (TextView) findViewById(R.id.txtRadius);
        imgSatelliteMap = (ImageView) findViewById(R.id.imgSatelliteMap);
        recViewAutoComplete = (RecyclerView) findViewById(R.id.recViewAutoComplete);
        rgRadiusMode = (RadioGroup) findViewById(R.id.rgRadiusMode);
        rbEnter = (RadioButton) findViewById(R.id.rbEnterRadius);
        rbLeave = (RadioButton) findViewById(R.id.rbLeaveRadius);


        currentImg = imgSatelliteExpanded;

        if(getIntent().hasExtra("edit_add") && getIntent().hasExtra("id")) {

            edit = Objects.equals(getIntent().getStringExtra("edit_add"), "edit");
            id = getIntent().getIntExtra("id", -1);

            txtKm.setText(String.valueOf((int)(getIntent().getDoubleExtra("radius", 600.0))));
            if(getIntent().hasExtra("enter_leave") && Objects.equals(getIntent().getStringExtra("enter_leave"), "enter")){
                rbEnter.setChecked(true);
            }else{
                rbLeave.setChecked(true);
            }



        }


        AdressAutoCompleteAdapter adapt = new AdressAutoCompleteAdapter();
        recViewAutoComplete.setAdapter(adapt);
        recViewAutoComplete.setLayoutManager(new LinearLayoutManager(LocationMethodSetActivity.this));
        ValContainer<Address> vC = new ValContainer<>();
        adapt.setvC(vC);

        //this method might cause trouble...to be seen in testing if it does
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                text = autoCompleteTextView.getText().toString();
                Log.d("textToAutocomplete",text);
                Geocoder geocoder = new Geocoder(LocationMethodSetActivity.this);
                try {
                    lAddr = geocoder.getFromLocationName(text,5);
                } catch (IOException e) {
                    Toast.makeText(LocationMethodSetActivity.this, "This field is empty already", Toast.LENGTH_SHORT).show();
                }
                adapt.setlAddr(lAddr);

                if (adapt.getvC().getVal() != null){
                    Address addr = adapt.getvC().getVal();
                    autoCompleteTextView.setText(addr.getThoroughfare() + " " + addr.getSubThoroughfare() + ", " + addr.getPostalCode() + " " +addr.getLocality() + ", " + addr.getCountryName());
                }


                return true;
            }
        });

        recViewAutoComplete.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (adapt.getvC().getVal() != null){
                    addr = adapt.getvC().getVal();
                    autoCompleteTextView.setText(addr.getThoroughfare() + " " + addr.getSubThoroughfare() + ", " + addr.getPostalCode() + " " +addr.getLocality() + ", " + addr.getCountryName());
                    Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                    try {
                        addr = g.getFromLocation(addr.getLatitude(), addr.getLongitude(), 1).get(0);
                    } catch (IOException e1) {
                        Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (adapt.getvC().getVal() != null){
                    addr = adapt.getvC().getVal();
                    autoCompleteTextView.setText(addr.getThoroughfare() + " " + addr.getSubThoroughfare() + ", " + addr.getPostalCode() + " " +addr.getLocality() + ", " + addr.getCountryName());
                    Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                    try {
                        addr = g.getFromLocation(addr.getLatitude(), addr.getLongitude(), 1).get(0);
                    } catch (IOException e2) {
                        Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });



        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                updateMap();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if(progress >= 0 && progress < 3){
                    km = 5;
                    txtKm.setText("5 M");
                } else if(progress >= 3  && progress < 33){
                    km = (progress/3) * 10;
                    txtKm.setText(km +" M");
                } else if (progress > 32 && progress < 36) {
                    km = 150;
                    txtKm.setText("150 M");
                } else if (progress > 35 && progress < 60) {
                    km = (((progress-1)/3)*100)-1000;
                    txtKm.setText(km + " M");
                } else if (progress > 59 && progress < 90) {
                    km = ((progress -60) /3)+1;
                    txtKm.setText(km + " Km");
                } else if (progress > 89 && progress < 96) {
                    km = 10 + (((progress-90)/3)+1)*5;
                    txtKm.setText(km + " Km");
                }else{
                    km = 30000;
                    txtKm.setText("Please make the radius smaller");
                }

                if(progress <60){
                    radius = km;
                }else{
                    radius = km*1000;
                }

                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                @SuppressLint("MissingPermission")
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (addr == null) {
                    LocationMethodSetActivity.this.longitude = location.getLongitude();
                    LocationMethodSetActivity.this.latitude = location.getLatitude();
                }

                try{
                    Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                    addr = g.getFromLocation(latitude,longitude,1).get(0);
                }catch (IOException e){
                    Toast.makeText(LocationMethodSetActivity.this, "Updating Location didn't reveal any address", Toast.LENGTH_SHORT).show();
                }

                zoom = radiusToZoom(radius);



                updateMap();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        imgBtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayoutRetractedCircleMenu.setVisibility(View.INVISIBLE);
                constLayoutExpandedCircleMenu.setVisibility(View.VISIBLE);

                imgStreet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) imgStreet.getLayoutParams();
                        int zeroseventyonethreeseven = lp.bottomMargin;
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin+5, 10);

                        lp = (ConstraintLayout.LayoutParams) currentImg.getLayoutParams();
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, zeroseventyonethreeseven);
                        relLayoutRetractedCircleMenu.setVisibility(View.VISIBLE);
                        constLayoutExpandedCircleMenu.setVisibility(View.INVISIBLE);
                        imgSatelliteMap.setImageResource(R.drawable.baseline_terrain_24);
                        currentImg = imgStreet;
                        currMapStyle = Style.MAPBOX_STREETS;
                        updateMap();
                    }
                });

                imgSatelliteExpanded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) imgSatelliteExpanded.getLayoutParams();
                        int zeroseventyonethreeseven = lp.bottomMargin;
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, 0);

                        lp = (ConstraintLayout.LayoutParams) currentImg.getLayoutParams();
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, zeroseventyonethreeseven);
                        relLayoutRetractedCircleMenu.setVisibility(View.VISIBLE);
                        constLayoutExpandedCircleMenu.setVisibility(View.INVISIBLE);
                        imgSatelliteExpanded.setImageResource(R.drawable.baseline_satellite_alt_24);
                        currentImg = imgSatelliteExpanded;
                        currMapStyle = Style.SATELLITE;
                        updateMap();
                    }
                });

                imgStreetSatellite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) imgStreetSatellite.getLayoutParams();
                        int zeroseventyonethreeseven = lp.bottomMargin;
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin+5, 10);

                        lp = (ConstraintLayout.LayoutParams) currentImg.getLayoutParams();
                        lp.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, zeroseventyonethreeseven);
                        relLayoutRetractedCircleMenu.setVisibility(View.VISIBLE);
                        constLayoutExpandedCircleMenu.setVisibility(View.INVISIBLE);
                        imgSatelliteMap.setImageResource(R.drawable.baseline_satellite_24_street);
                        currentImg = imgStreetSatellite;
                        currMapStyle = Style.SATELLITE_STREETS;
                        updateMap();
                    }
                });

            }
        });




        imgGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                @SuppressLint("MissingPermission")
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null){
                    Toast.makeText(LocationMethodSetActivity.this, "Could not fetch current location", Toast.LENGTH_SHORT).show();   
                }else {
                    LocationMethodSetActivity.this.longitude = location.getLongitude();
                    LocationMethodSetActivity.this.latitude = location.getLatitude();
                }
                Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                try {
                    addr = g.getFromLocation(latitude, longitude, 1).get(0);
                } catch (IOException e) {
                    Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                }
                updateMap();
            }
        });


        btnGo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isLeaveMode) {
                    radiusMode = Leave.name();
                }else{
                    radiusMode = Enter.name();
                }

                SharedPreferences sp = LocationMethodSetActivity.this.getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                int queueId = sp.getInt("queue_id",-1);

                if(edit){
                    int pos = getIntent().getIntExtra("pos",-1);
                    if(pos == -1) finish(); //??
                    SharedPreferences ssp = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                    SharedPreferences.Editor se = ssp.edit();

                    if(addr != null) {

                        se.putString("Method",Enums.Method.Location.name());
                        se.putString("edit_add", "edit");
                        se.putInt("long", (int)addr.getLongitude());
                        se.putInt("lat", (int) addr.getLatitude());
                        se.putInt("longitude",(int) ((addr.getLongitude()- (int) addr.getLongitude())*1000000));
                        se.putInt("latitude",(int) ((addr.getLatitude()- (int) addr.getLatitude())*1000000));

                        se.putInt("radius", (int) radius);
                        se.putString("SubMethod", radiusMode);
                        se.putInt("queue_id",queueId);
                        se.apply();
                        Log.d("mett", "Im here "+ latitude  + " " + longitude);
                        finish();
                    }else{
                        Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                        try {
                            Log.d("mett", "debug: long + lat ="+ longitude + " + " + latitude );
                            addr = g.getFromLocation(latitude, longitude, 1).get(0);
                            se.putString("Method",Enums.Method.Location.name());
                            se.putString("edit_add", "edit");
                            se.putInt("long", (int)longitude);
                            se.putInt("lat", (int) latitude);
                            se.putInt("longitude",(int) ((addr.getLongitude()- (int) addr.getLongitude())*1000000));
                            se.putInt("latitude",(int) ((addr.getLatitude()- (int) addr.getLatitude())*1000000));
                            se.putInt("queue_id",queueId);
                            se.putInt("radius", (int) radius);
                            se.putString("SubMethod", radiusMode);
                            Log.d("mett", "Im queer "+ latitude  + " " + longitude);
                            se.apply();
                            finish();

                        } catch (IOException e) {
                            Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    SharedPreferences ssp = getSharedPreferences(getString(R.string.math_to_edit_alarm_pref_key), MODE_PRIVATE);
                    SharedPreferences.Editor se = ssp.edit();
                    if (addr == null) {
                        Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                        try {
                            Log.d("mett", "debug: long + lat ="+ longitude + " + " + latitude );
                            addr = g.getFromLocation(latitude, longitude, 1).get(0);
                            se.putInt("queue_id", ssp.getInt("queue_id", -1));
                            se.putString("Method", Enums.Method.Location.name());

                            se.putInt("radius", (int) radius);
                            se.putInt("long", (int)longitude);
                            se.putInt("lat", (int) latitude);
                            se.putInt("longitude",(int) ((addr.getLongitude()- (int) addr.getLongitude())*1000000));
                            se.putInt("latitude",(int) ((addr.getLatitude()- (int) addr.getLatitude())*1000000));

                            //se.putInt("longitude", Integer.parseInt(String.valueOf(longitude).split("\\.")[1]));
                            //se.putInt("latitude", Integer.parseInt(String.valueOf(latitude).split("\\.")[1]));
                            se.putInt("radius", (int) radius);
                            se.putString("SubMethod", radiusMode);
                            se.apply();
                            finish();

                        } catch (IOException e) {
                            Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        se.putInt("queue_id", ssp.getInt("queue_id", -1));
                        se.putString("Method", Enums.Method.Location.name());
                        if (isLeaveMode){
                            se.putString("SubMethod", Leave.name());
                        }else{
                            se.putString("SubMethod", Enter.name());
                        }
                        se.putInt("radius", (int) radius);
                        se.putInt("long", (int)addr.getLongitude());
                        se.putInt("lat", (int) addr.getLatitude());
                        se.putInt("longitude",(int) ((addr.getLongitude()- (int) addr.getLongitude())*1000000));
                        se.putInt("latitude",(int) ((addr.getLatitude()- (int) addr.getLatitude())*1000000));
                        Log.d("mett", "newLong: " + (int) ((addr.getLongitude()- (int) addr.getLongitude())*1000000));
                        se.putInt("radius", (int) radius);
                        se.putString("SubMethod", radiusMode);
                        se.apply();
                        finish();
                    }

                }
            }
        });




    }


    public int getDecFromDouble(double d){
        String dStr = Double.toString(d);
        for(int i = 0; i < dStr.length(); i++){
            if (dStr.charAt(i) == '.'){
                dStr = dStr.substring(i+1); //substring is inclusive of beginIndex, so +1 is used
                break;
            }
        }
        return Integer.parseInt(dStr);
    }


    public void updateMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),zoom));

                if(currPoly != null){
                    mapboxMap.removeAnnotation(currPoly.getPolygon());
                }
                currPoly = new PolygonOptions().addAll(polygonCircleForCoordinate(new LatLng(latitude,longitude), radius)).fillColor(Color.parseColor("#0F000FFF"));
                mapboxMap.addPolygon(currPoly);

                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),zoom));
                mapboxMap.addOnScaleListener(new MapboxMap.OnScaleListener() {
                    @Override
                    public void onScaleBegin(@NonNull StandardScaleGestureDetector detector) {}
                    @Override
                    public void onScale(@NonNull StandardScaleGestureDetector detector) {}
                    @Override
                    public void onScaleEnd(@NonNull StandardScaleGestureDetector detector) {zoom = mapboxMap.getCameraPosition().zoom;System.out.println(zoom);}
                });


                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {



                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        latitude = point.getLatitude();
                        longitude = point.getLongitude();
                        if(currPoly != null){
                            mapboxMap.removeAnnotation(currPoly.getPolygon());
                        }
                        currPoly = new PolygonOptions().addAll(polygonCircleForCoordinate(new LatLng(latitude,longitude), radius)).fillColor(Color.parseColor("#0F000FFF"));
                        mapboxMap.addPolygon(currPoly);

                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),zoom));
                        Geocoder g = new Geocoder(LocationMethodSetActivity.this);
                        try {
                            addr = g.getFromLocation(latitude, longitude, 1).get(0);
                        } catch (IOException e) {
                            Toast.makeText(LocationMethodSetActivity.this, "Couldn't find a valid location at your click", Toast.LENGTH_SHORT).show();
                        }
                        mapboxMap.setStyle(LocationMethodSetActivity.this.currMapStyle, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                style.addImage("red-pin-icon-id", BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.baseline_place_24)),false);


                                style.addLayer(new SymbolLayer("icon-layer-id","icon-source-id").withProperties(
                                        iconImage("red-pin-icon-id"),
                                        iconIgnorePlacement(true),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, -9f})
                                ));

                                GeoJsonSource iconGeoJsonSource = new GeoJsonSource("icon-source-id", Feature.fromGeometry(Point.fromLngLat(longitude,latitude)));
                                style.addSource(iconGeoJsonSource);

                                zoom = radiusToZoom(radius);


                            }
                        });

                        return true;
                    }
                });
                mapboxMap.setStyle(LocationMethodSetActivity.this.currMapStyle, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        style.addImage("red-pin-icon-id", BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.baseline_place_24)),false);


                        style.addLayer(new SymbolLayer("icon-layer-id","icon-source-id").withProperties(
                                iconImage("red-pin-icon-id"),
                                iconIgnorePlacement(true),
                                iconAllowOverlap(true),
                                iconOffset(new Float[]{0f, -9f})
                        ));

                        GeoJsonSource iconGeoJsonSource = new GeoJsonSource("icon-source-id", Feature.fromGeometry(Point.fromLngLat(longitude,latitude)));
                        style.addSource(iconGeoJsonSource);

                        zoom = radiusToZoom(radius);


                    }
                });


            }
        });
    }

    private double radiusToZoom(double radius) {

        DisplayMetrics dM = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dM);
        int width = dM.widthPixels;
        double[][] zoomTable;
        zoomTable = new double[][]{
                {78271.484, 39135.742, 19567.871, 9783.936, 4891.968, 2445.984, 1222.992, 611.496, 305.748, 152.874, 76.437, 38.218, 19.109, 9.555, 4.777, 2.389, 1.194, 0.597, 0.299, 0.149, 0.075, 0.037, 0.019},
                {73551.136, 36775.568, 18387.784, 9193.892, 4596.946, 2298.473, 1149.237, 574.618, 287.309, 142.655, 71.827, 35.914, 17.957, 8.978, 4.489, 2.245, 1.122, 0.561, 0.281, 0.140, 0.070, 0.035, 0.018},
                {59959.436, 29979.718, 14989.859, 7494.929, 3747.465, 1873.732, 936.866, 468.433, 234.217, 117.108, 58.554, 29.227, 14.639, 7.319, 3.660, 1.830, 0.915, 0.457, 0.229, 0.114, 0.057, 0.029, 0.014},
                {39135.742, 19567.871, 9783.936, 4891.968, 2445.984, 1222.992, 611.496, 305.748, 152.874, 76.437, 38.218, 19.109, 9.555, 4.777, 2.389, 1.194, 0.597, 0.299, 0.149, 0.075, 0.037, 0.019, 0.009},
                {13591.701, 6795.850, 3397.925, 1698.963, 849.481, 424.741, 212.370, 106.185, 53.093, 26.546, 13.273, 6.637, 3.318, 1.659, 0.830, 0.410, 0.207, 0.104, 0.052, 0.026, 0.013, 0.006, 0.003}
        };

        int latIndex = (int) latitude/20;
        if(latIndex < 0){
            latIndex *= -1;
        }

        double[] meterPerPixel = zoomTable[latIndex];

        for(int i = 0; i < meterPerPixel.length; i++){

            if((double)width*meterPerPixel[i] <= radius){

                if(i>3) return i-4;
                return 0;
            }

        }

        return 22.0;

    }

    private ArrayList<LatLng> polygonCircleForCoordinate(LatLng location, double radius){
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = location.getLatitude() * Math.PI / 180;
        double centerLonRadians = location.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> polygons = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            polygons.add(point);
        }
        return polygons;
    }



}