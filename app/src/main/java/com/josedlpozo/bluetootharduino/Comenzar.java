package com.josedlpozo.bluetootharduino;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by josedlpozo on 16/4/15.
 */
public class Comenzar extends Fragment {

    private final static String TAG = "COMENZAR";
    private Button btnJoinCar ;
    private Button btnJoinSensor;
    private int motorSensor = 0;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int MOTOR = 0;
    private static final int SENSOR = 1;
    private Bqzum bqzum;

    CircularProgressBar circularProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.comenzar_fragment,container,false);
        bqzum = Variables.INSTANCE.getBqzum();

        if(v != null){
            btnJoinCar = (Button) v.findViewById(R.id.btnJoinCar);
            btnJoinSensor = (Button) v.findViewById(R.id.btnJoinSensor);
            circularProgressBar = (CircularProgressBar) v.findViewById(R.id.circularProgress);
            Variables.INSTANCE.setCircularProgressBar(circularProgressBar);
            Variables.INSTANCE.setActivity(getActivity());
            setupCar();
            setupSensor();
        }
        return v;
    }

    private void setupCar() {
        btnJoinCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorSensor = MOTOR;
                if (bqzum.getMotorStatus()) {
                    bqzum.disconnectMotor();
                } else {
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    getActivity().startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    private void setupSensor() {
        btnJoinSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motorSensor = SENSOR;
                if (bqzum.getSensorStatus()) {
                    bqzum.disconnectSensor();
                } else {
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    getActivity().startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }
}
