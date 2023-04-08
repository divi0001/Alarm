package com.example.alarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmSoundSetActivity extends AppCompatActivity {

    Button btnAddSound, btnSetSound, btnSetUrl;
    ArrayList<Song> soundNames;
    RecyclerView recViewSound;
    ActivityResultLauncher<String> storagePermissionLauncher;
    SoundRecViewAdapter adapter = new SoundRecViewAdapter(this);
    ScrollView scView;
    CardView cvAddUrl;
    EditText editUrl;
    ArrayList<Song> songs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound_set);

        btnSetSound = (Button) findViewById(R.id.btnSetSound);
        btnAddSound = (Button) findViewById(R.id.btnAddSoundFile);

        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if(result){
                //perms granted
                fetchSongs();
                
            }else{
                respondOnUserPermissionActs();
            }
        });

        //launch storage permission launcher
        storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);


        recViewSound = (RecyclerView) findViewById(R.id.recViewSounds);

        btnSetSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getChosen() != null){

                    SharedPreferences sp = AlarmSoundSetActivity.this.getSharedPreferences(getString(R.string.uri_key),MODE_PRIVATE);
                    SharedPreferences.Editor se = sp.edit();

                    se.putString("uri", adapter.getChosen().toString());
                    se.putString("name", adapter.getName());
                    se.apply();

                    finish();

                }else{
                    Toast.makeText(AlarmSoundSetActivity.this, "No sound selected, though, as you wish, you go back to previous activity", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnSetUrl = (Button) findViewById(R.id.btnAddUrl);
        scView = (ScrollView) findViewById(R.id.scrollSounds);
        cvAddUrl = (CardView) findViewById(R.id.cvSetUrl);
        editUrl = (EditText) findViewById(R.id.editUrl);

        btnAddSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scView.setVisibility(View.INVISIBLE);
                cvAddUrl.setVisibility(View.VISIBLE);

            }
        });


        btnSetUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /* todo

                if(checkAndSetValidUrl(editUrl.getText().toString())) {
                    String name = editUrl.getText().toString();
                    songs.add(new Song(songs.get(songs.size()-1).getId()+1, null, Uri.parse(name), getTitleFromWeb(name), getDuration(name), getSize(name)));
                    showSongs(songs);
                }
                scView.setVisibility(View.VISIBLE);
                cvAddUrl.setVisibility(View.INVISIBLE);

                */
            }

        });



    }



    private void respondOnUserPermissionActs() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            fetchSongs();
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //show explanation, why ext write access to storage is needed
            new AlertDialog.Builder(this).setTitle("Requesting Permission").setMessage("The app needs permission to write in the external storage in order to fetch" +
                    "songs from your devices storage").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }).setNegativeButton("I do not accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AlarmSoundSetActivity.this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).show();
        }else{
            Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchSongs() {


        Uri songLibraryUri;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            songLibraryUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            songLibraryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(songLibraryUri, projection, null, null,sortOrder)){

            //cursor indices:
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            //get vals
            while (cursor.moveToNext()){

                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumColumn);

                //song uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                //album art uri
                Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                name = name.substring(0, name.lastIndexOf("."));
                Song song = new Song(id, albumId, uri, albumArtUri, name, duration, size);
                songs.add(song);

            }

            showSongs(songs);
            Toast.makeText(this, "Number of songs: " + songs.size(), Toast.LENGTH_SHORT).show();

        }

    }

    private void showSongs(ArrayList<Song> songs) {

        adapter = new SoundRecViewAdapter(this);
        adapter.setSounds(songs);

        recViewSound.setAdapter(adapter);
        recViewSound.setLayoutManager(new LinearLayoutManager(this));



    }
}