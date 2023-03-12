package com.example.alarm;

public class Alarm implements java.io.Serializable {

    private CharSequence t;
    private String turnOffMethod;
    int ID;
    private String soundPath;
    private boolean extendedPrivileges;
    private boolean isActive;
    private String imgBtnDownSrc;
    private String imgBtnUpSrc;
    private String imgBtnThreeDotsSrc;
    private String difficulty;


    public Alarm(CharSequence t, String turnOffMethod, int id, String soundPath, boolean extendedPrivileges, boolean isActive, String imgBtnDownSrc, String imgBtnUpSrc, String imgBtnThreeDots, String difficulty) {
        this.t = t;
        this.turnOffMethod = turnOffMethod;
        this.ID = id;
        this.soundPath = soundPath;
        this.extendedPrivileges = extendedPrivileges;
        this.isActive = isActive;
        this.imgBtnDownSrc = imgBtnDownSrc;
        this.imgBtnUpSrc = imgBtnUpSrc;
        this.imgBtnThreeDotsSrc = imgBtnThreeDotsSrc;
        this.difficulty = difficulty;
    }

    public Alarm(int id){

        this.ID = id;
        setActive(false);
        setSoundPath("");
        setTurnOffMethod("");
        setDifficulty("");
        setT("");
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getImgBtnDownSrc() {
        return imgBtnDownSrc;
    }

    public void setImgBtnDownSrc(String imgBtnDownSrc) {
        this.imgBtnDownSrc = imgBtnDownSrc;
    }

    public String getImgBtnUpSrc() {
        return imgBtnUpSrc;
    }

    public void setImgBtnUpSrc(String imgBtnUpSrc) {
        this.imgBtnUpSrc = imgBtnUpSrc;
    }

    public String getImgBtnThreeDotsSrc() {
        return imgBtnThreeDotsSrc;
    }

    public void setImgBtnThreeDotsSrc(String imgBtnThreeDots) {
        this.imgBtnThreeDotsSrc = imgBtnThreeDots;
    }

    public CharSequence getT() {
        return t;
    }

    public void setT(CharSequence t) {
        this.t = t;
    }

    public String getTurnOffMethod() {
        return turnOffMethod;
    }

    public void setTurnOffMethod(String turnOffMethod) {
        this.turnOffMethod = turnOffMethod;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    public boolean isExtendedPrivileges() {
        return extendedPrivileges;
    }

    public void setExtendedPrivileges(boolean extendedPrivileges) {
        this.extendedPrivileges = extendedPrivileges;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "t=" + t +
                ", turnOffMethod='" + turnOffMethod + '\'' +
                ", soundPath='" + soundPath + '\'' +
                ", extendedPrivileges=" + extendedPrivileges +
                ", isActive=" + isActive +
                ", imgBtnDownSrc='" + imgBtnDownSrc + '\'' +
                ", imgBtnUpSrc='" + imgBtnUpSrc + '\'' +
                ", imgBtnThreeDotsSrc='" + imgBtnThreeDotsSrc + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
