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
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    String currSoundName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound_set);

        btnSetSound = (Button) findViewById(R.id.btnSetSound);
        btnAddSound = (Button) findViewById(R.id.btnAddSoundFile);
        SharedPreferences sh = getSharedPreferences(getString(R.string.sound_key),MODE_PRIVATE);
        currSoundName = sh.getString("sound_name", "");


        fetchSongs();

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


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION};

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, projection, selection,null, null);

        List<String> s = new ArrayList<String>();

        while(cursor.moveToNext()){
            s.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +
                    cursor.getString(2) + "||" + cursor.getString(3) + "||" +
                    cursor.getString(4) + "||" + cursor.getString(5));
        }

//        Log.d("mett", s.toString());

        Uri u = Uri.parse("android.resource://com.example.alarm/"+R.raw.weak_1);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, u);

        Log.d("mett", mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)); //make sure metadata of that type is not null before retreiving it? wth how?!????!?!?!?!?!?!? ohhhh try catch might help :D
        try {
            mmr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        recViewSound = (RecyclerView) findViewById(R.id.recViewSounds);

        btnSetSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getChosen() != null){

                    SharedPreferences sp = AlarmSoundSetActivity.this.getSharedPreferences(getString(R.string.sound_key),MODE_PRIVATE);
                    SharedPreferences.Editor se = sp.edit();

                    se.putString("uri", adapter.getChosen().toString());
                    se.putString("sound_name", adapter.getName());
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

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION};

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, projection, selection,null, null);

        List<String> s = new ArrayList<String>();

        while(cursor.moveToNext()){
            s.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +
                    cursor.getString(2) + "||" + cursor.getString(3) + "||" +
                    cursor.getString(4) + "||" + cursor.getString(5));
        }
        //Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show();




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            songLibraryUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            songLibraryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection1 = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        try (Cursor curso = getContentResolver().query(songLibraryUri, projection1, null, null,sortOrder)){

            //cursor indices:
            int idColumn = curso.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = curso.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = curso.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = curso.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumColumn = curso.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            //get vals
            while (curso.moveToNext()){

                long id = curso.getLong(idColumn);
                String name = curso.getString(nameColumn);
                int duration = curso.getInt(durationColumn);
                int size = curso.getInt(sizeColumn);
                long albumId = curso.getLong(albumColumn);

                //song uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                //album art uri
                Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

                name = name.substring(0, name.lastIndexOf("."));
                Song song = new Song(id, albumId, uri, albumArtUri, name, duration, size);
                songs.add(song);

            }

            //showSongs(songs);
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