package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class Octopus extends Activity {

    private static final String TAG = "Octopus";
    private static final int MODO_LINEAS = 1;
    private static final int MODO_AUTONOMO = 2;
    private static final int MODO_CONTROL = 3;

    private static final String MODO_LINEAS_S = "Sigue lineas";
    private static final String MODO_AUTONOMO_S = "Control autonomo";
    private static final String MODO_CONTROL_S = "Control joystick";

    private Bqzum bqzum;
    private float x,y;
    private int motorSensor = 0;
    private static int MODO = 0;
    private Button change;
    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5, textView6;

    JoyStickClass js;

    private static final int MOTOR = 0;
    private static final int SENSOR = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_octopus);

        bqzum = (Bqzum) Variables.INSTANCE.getBqzum();
        change = (Button) findViewById(R.id.change_modo);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(Octopus.this);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("Selecciona un modo:");


                Button lineas = (Button) dialog.findViewById(R.id.lineas);
                // if button is clicked, close the custom dialog
                lineas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MODO = MODO_LINEAS;
                        Log.i(TAG,"MODO: " +MODO);
                        bqzum.sendData(""+MODO,MOTOR);
                        setModo();
                        layout_joystick.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });

                Button autonomo = (Button) dialog.findViewById(R.id.autonomo);
                // if button is clicked, close the custom dialog
                autonomo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MODO = MODO_AUTONOMO;
                        Log.i(TAG,"MODO: " +MODO);
                        bqzum.sendData(""+MODO,MOTOR);
                        setModo();
                        layout_joystick.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });

                Button control = (Button) dialog.findViewById(R.id.control);
                // if button is clicked, close the custom dialog
                control.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MODO = MODO_CONTROL;
                        Log.i(TAG,"MODO: " +MODO);
                        bqzum.sendData(""+MODO,MOTOR);
                        setModo();
                        layout_joystick.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        textView6 = (TextView)findViewById(R.id.textView6);

        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button);
        js.setStickSize(70, 70);
        js.setLayoutSize(250, 250);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(9);
        js.setMinimumDistance(5);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {

                    x = js.getX();
                    y = js.getY();
                }
                return true;
            }
        });
    }

    private void setModo(){
        String modo;
        switch (MODO){
            case 1: modo = MODO_LINEAS_S;
                    break;
            case 2: modo = MODO_AUTONOMO_S;
                    break;
            case 3: modo = MODO_CONTROL_S;
                    break;
            default:
                    modo = "Desconocido";
        }
        textView6.setText("Modo: "+modo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.octopus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
