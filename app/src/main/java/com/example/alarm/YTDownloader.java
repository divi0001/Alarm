package com.example.alarm;

public class YTDownloader {

    private String url, path;
    private int progress;

    public YTDownloader(String url, String path, int progress) {
        this.url = url;
        this.path = path;
        this.progress = progress;







    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "YTDownloader{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", progress=" + progress +
                '}';
    }



}
