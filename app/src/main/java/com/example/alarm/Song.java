package com.example.alarm;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

public class Song {

    private long id;
    private Uri uri;
    private Bitmap albumArt;
    private String name,album;
    private int duration, size;


    /**
     *
     * @param id the id of the song
     * @param album the name of the album
     * @param uri the uri in internal storage to the song
     * @param albumArt the Bitmap of the album art
     * @param name the name of the song
     * @param duration the duration of the song
     * @param size the size (byte sized) of the song file
     */
    public Song(long id, String album, Uri uri, Bitmap albumArt, String name, int duration, int size) {
        this.id = id;
        this.album = album;
        this.uri = uri;
        this.albumArt = albumArt;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public Song(long id, String album, Uri uri, String name, int duration, int size) {
        this.id = id;
        this.album = album;
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public long getId() {
        return id;
    }



    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String albumId) {
        this.album = albumId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", albumId=" + album +
                ", uri=" + uri +
                ", albumArtUri=" + albumArt +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}
