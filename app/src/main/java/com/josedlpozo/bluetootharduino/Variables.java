package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.transition.CircularPropagation;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by josedlpozo on 11/4/15.
 */
public enum Variables {


    INSTANCE;
    private Context appContext = null;
    private Application application = null;
    private Bqzum bqzum;
    private String motorAddress = "00:00:00:00:00:00";
    private String sensorAddress = "00:00:00:00:00:00";

    public Button joinCar;
    public Button joinSensor;

    private Activity activity;

    private String recibido;


    private CircularProgressBar circularProgressBar;

    private String user;

    private int send;


    private Variables(){

    }

    public void init(Application application) throws Exception {
        this.application = application;
        this.appContext = this.application.getApplicationContext();
        this.bqzum = new Bqzum();

    }

    public Bqzum getBqzum(){
        return bqzum;
    }

    public String getMotorAddress(){
        return motorAddress;
    }

    public String getSensorAddress(){
        return sensorAddress;
    }

    public void setMotorAddress(String motorAddress){
        this.motorAddress = motorAddress;
    }

    public void setSensorAddress(String sensorAddress){
        this.sensorAddress = sensorAddress;
    }

    public Context getAppContext() {
        return appContext;
    }

    public Application getApplication() {
        return application;
    }

    public Button getJoinCar() {
        return joinCar;
    }

    public Button getJoinSensor() {
        return joinSensor;
    }

    public CircularProgressBar getCircularProgressBar() {
        return circularProgressBar;
    }

    public void setCircularProgressBar(CircularProgressBar circularProgressBar) {
        this.circularProgressBar = circularProgressBar;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getRecibido() {
        return recibido;
    }

    public void setRecibido(String recibido) {
        this.recibido = recibido;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }
}
