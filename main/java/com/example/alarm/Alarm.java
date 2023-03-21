package com.example.alarm;

public class Alarm implements java.io.Serializable {

    private CharSequence t;
    private String turnOffMethod;
    int ID;
    private String soundPath;
    private boolean extendedPrivileges;
    private boolean isActive;
    private String difficulty;
    private String type;


    public Alarm(CharSequence t, String turnOffMethod, int id, String soundPath, boolean extendedPrivileges, boolean isActive, String difficulty, String type) {
        this.t = t;
        this.turnOffMethod = turnOffMethod;
        this.ID = id;
        this.soundPath = soundPath;
        this.extendedPrivileges = extendedPrivileges;
        this.isActive = isActive;
        this.difficulty = difficulty;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                ", ID=" + ID +
                ", soundPath='" + soundPath + '\'' +
                ", extendedPrivileges=" + extendedPrivileges +
                ", isActive=" + isActive +
                ", difficulty='" + difficulty + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
