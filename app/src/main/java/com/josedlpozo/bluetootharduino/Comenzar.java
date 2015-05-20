package com.josedlpozo.bluetootharduino;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import android.widget.CheckBox;

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


    private CheckBox checkbox1;
    private CheckBox checkbox2;

    private Handler mHandler;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.comenzar_fragment,container,false);
        bqzum = Variables.INSTANCE.getBqzum();

        mHandler = new Handler();

        if(v != null){

            FloatingActionButton buttonCar = (FloatingActionButton) v.findViewById(R.id.btnJoinCar);
            buttonCar.setSize(FloatingActionButton.SIZE_NORMAL);
            buttonCar.setColorNormalResId(R.color.morado);
            buttonCar.setColorPressedResId(R.color.morado2);
            buttonCar.setStrokeVisible(false);
            buttonCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    motorSensor = MOTOR;
                    if (bqzum.getMotorStatus()) {
                        bqzum.disconnectMotor();
                    } else {
                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("MOTORSENSOR", motorSensor);
                        serverIntent.putExtras(bundle);
                        getActivity().startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        setChecked(motorSensor);
                    }
                }
            });

            FloatingActionButton buttonSensor = (FloatingActionButton) v.findViewById(R.id.btnJoinSensor);
            buttonSensor.setSize(FloatingActionButton.SIZE_NORMAL);
            buttonSensor.setColorNormalResId(R.color.morado);
            buttonSensor.setColorPressedResId(R.color.morado2);
            buttonSensor.setStrokeVisible(false);
            buttonSensor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    motorSensor = SENSOR;
                    Toast.makeText(getActivity(),""+motorSensor,Toast.LENGTH_LONG).show();
                    if (bqzum.getSensorStatus()) {
                        bqzum.disconnectSensor();
                    } else {
                        Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("MOTORSENSOR", motorSensor);
                        serverIntent.putExtras(bundle);
                        getActivity().startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                        setChecked(motorSensor);
                    }
                }
            });
            checkbox1 = (CheckBox) v.findViewById(R.id.check1);
            checkbox2 = (CheckBox) v.findViewById(R.id.check2);
            checkbox1.setClickable(false);
            checkbox2.setClickable(false);
            circularProgressBar = (CircularProgressBar) v.findViewById(R.id.circularProgress);
            Variables.INSTANCE.setCircularProgressBar(circularProgressBar);
            Variables.INSTANCE.setActivity(getActivity());
            mStatusChecker.run();

        }
        return v;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(mStatusChecker, 100);
            setChecked(motorSensor);
        }
    };



    private void setChecked(int motorsensor){
        switch (motorsensor){
            case MOTOR:
                if(bqzum.getMotorStatus()){
                    if(!checkbox1.isChecked()){
                        checkbox1.toggle();
                    }

                }else {
                    if(checkbox1.isChecked()){
                        checkbox1.toggle();
                    }

                }
                break;
            case SENSOR:
                if(bqzum.getSensorStatus()){
                    if(!checkbox2.isChecked()){
                        checkbox2.toggle();
                    }
                }else{
                    if(checkbox2.isChecked()){
                        checkbox2.toggle();
                    }
                }
                break;
            default:
                Log.i(TAG,"ERROR");

        }

    }


}
