package com.example.alarm;

import android.net.Uri;

public class Song {

    private long id, albumId;
    private Uri uri, albumArtUri;
    private String name;
    private int duration, size;


    /**
     *
     * @param id the id of the song
     * @param albumId the id of the album
     * @param uri the uri in internal storage to the song
     * @param albumArtUri the uri to the album art
     * @param name the name of the song
     * @param duration the duration of the song
     * @param size the size (byte sized) of the song file
     */
    public Song(long id, long albumId, Uri uri, Uri albumArtUri, String name, int duration, int size) {
        this.id = id;
        this.albumId = albumId;
        this.uri = uri;
        this.albumArtUri = albumArtUri;
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

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(Uri albumArtUri) {
        this.albumArtUri = albumArtUri;
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
                ", albumId=" + albumId +
                ", uri=" + uri +
                ", albumArtUri=" + albumArtUri +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}
