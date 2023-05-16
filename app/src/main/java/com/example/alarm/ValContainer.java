package com.example.alarm;

public class ValContainer <T>{
    private T val;

    private static ValContainer instance = null;
    private void ValContainer(){

    }
    public static ValContainer getInstance(){
        if(instance == null){
            instance = new ValContainer();
        }
        return instance;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
