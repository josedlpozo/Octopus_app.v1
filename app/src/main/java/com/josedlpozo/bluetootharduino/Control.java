package com.josedlpozo.bluetootharduino;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by josedlpozo on 18/4/15.
 */
public class Control extends Fragment {

    private static final String TAG = "CONTROL";

    TextView modos;

    private Button change;

    private Button stop;

    RelativeLayout layout_joystick;

    JoyStickClass js;

    private static final int MODO_LINEAS = 1;
    private static final int MODO_CONTROL = 3;

    private static final String MODO_LINEAS_S = "Sigue lineas";
    private static final String MODO_CONTROL_S = "Control joystick";

    private static int MODO = 0;

    private static final int MOTOR = 0;
    private static final int SENSOR = 1;

    private Bqzum bqzum;
    private float x,y;

    private JoystickView joystick;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.control_fragment,container,false);
        bqzum = Variables.INSTANCE.getBqzum();
        if(v != null){
            change = (Button) v.findViewById(R.id.change_modo);
            modos = (TextView) v.findViewById(R.id.modo);
            stop = (Button) v.findViewById(R.id.stop);
            joystick = (JoystickView) v.findViewById(R.id.joystickView);
            modos.setText("Modo: "+MODO_CONTROL_S);
            //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
            joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

                @Override
                public void onValueChanged(int angle, int power, int direction) {
                    // TODO Auto-generated method stub
                    switch (direction) {
                        case JoystickView.FRONT:
                            Log.i(TAG, "UNO");
                            bqzum.sendData("1", MOTOR);
                            break;
                        case JoystickView.FRONT_RIGHT:
                            Log.i(TAG, "TRES");
                            bqzum.sendData("3", MOTOR);
                            break;
                        case JoystickView.RIGHT:
                            Log.i(TAG, "TRES");
                            bqzum.sendData("3", MOTOR);
                            break;
                        case JoystickView.RIGHT_BOTTOM:
                            Log.i(TAG, "DOS");
                            bqzum.sendData("2", MOTOR);
                            break;
                        case JoystickView.BOTTOM:
                            Log.i(TAG, "CERO");
                            bqzum.sendData("0", MOTOR);
                            break;
                        case JoystickView.BOTTOM_LEFT:
                            Log.i(TAG, "CINCO");
                            bqzum.sendData("5", MOTOR);
                            break;
                        case JoystickView.LEFT:
                            Log.i(TAG, "CUATRO");
                            bqzum.sendData("4", MOTOR);
                            break;
                        case JoystickView.LEFT_FRONT:
                            Log.i(TAG, "CUATRO");
                            bqzum.sendData("4", MOTOR);
                            break;
                        default:
                            Log.i(TAG, "SEIS");
                            bqzum.sendData("6", MOTOR);
                    }
                }
            }, JoystickView.DEFAULT_LOOP_INTERVAL);


            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bqzum.sendData("6",MOTOR);
                }
            });

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // custom dialog
                    final Dialog dialog = new Dialog(Variables.INSTANCE.getActivity());
                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("Selecciona un modo:");


                    Button lineas = (Button) dialog.findViewById(R.id.lineas);
                    // if button is clicked, close the custom dialog
                    lineas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MODO = MODO_LINEAS;
                            Log.i(TAG, "MODO: " + MODO);
                            bqzum.sendData("S", MOTOR);
                            setModo();
                            joystick.setVisibility(View.GONE);
                            stop.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });


                    Button control = (Button) dialog.findViewById(R.id.control);
                    // if button is clicked, close the custom dialog
                    control.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MODO = MODO_CONTROL;
                            Log.i(TAG, "MODO: " + MODO);
                            bqzum.sendData("B", MOTOR);
                            setModo();
                            joystick.setVisibility(View.VISIBLE);
                            stop.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });


        }
        return v;
    }

    private void setModo(){
        String modo;
        switch (MODO){
            case 1: modo = MODO_LINEAS_S;
                break;
            case 3: modo = MODO_CONTROL_S;
                break;
            default:
                modo = "Desconocido";
        }
        modos.setText("Modo: "+modo);
    }

}
