package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;


public class Main extends ActionBarActivity {



    private static final String TAG = "MAIN";



    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int REQUEST_LOGIN = 3;

    private Bqzum bqzum;

    private Button joinCar;
    private Button joinSensor;
    private Button comenzar;

    private int motorSensor = 0;

    private static final int MOTOR = 0;
    private static final int SENSOR = 1;

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"CONEXION","SENSORES","CONTROL"};
    int Numboftabs =3;

    private static final int SEND = 1;
    private static final int NOT_SEND = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Variables.INSTANCE.init(getApplication());
        } catch (Exception e) {
            e.printStackTrace();
        }
        bqzum = (Bqzum) Variables.INSTANCE.getBqzum();



        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                switch (position) {
                    case 0:
                        tabs.setBackgroundColor(getResources().getColor(R.color.green_octopus));
                        toolbar.setBackgroundColor(getBaseContext().getResources().getColor(R.color.ColorPrimaryDark));
                        pager.setBackgroundColor(getBaseContext().getResources().getColor(R.color.ColorPrimaryDark));
                        break;
                    case 1:
                        tabs.setBackgroundColor(getResources().getColor(R.color.azul));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.azul2));
                        pager.setBackgroundColor(getBaseContext().getResources().getColor(R.color.azul2));
                        break;
                    case 2:
                        tabs.setBackgroundColor(getResources().getColor(R.color.rojo));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.rojo2));
                        pager.setBackgroundColor(getBaseContext().getResources().getColor(R.color.rojo2));
                        break;
                    default:
                        tabs.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                }

                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


    }

    @Override
    public void onStart() {
        super.onStart();
            Log.e(TAG, "++ ON START ++");
        if (!bqzum.btEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.about){
            new MaterialDialog.Builder(this)
                    .title("OCTOPUS")
                    .content("App diseñada por equipo 3P para el control del robot OCTOPUS.")
                    .positiveText("OK")
                    .show();
        }
        if(id == R.id.login){
            Intent serverIntent = new Intent(this, Login.class);
            startActivityForResult(serverIntent, REQUEST_LOGIN);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    Bundle extras = data.getExtras();
                    motorSensor = extras.getInt("MOTORSENSOR");
                    if (motorSensor == MOTOR) {
                        Variables.INSTANCE.setMotorAddress(address);
                        try {
                            bqzum.connectMotor();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                                    .text(e.getMessage()) // text to display
                                    .show(Variables.INSTANCE.getActivity()); // activity where it is displayed
                        }
                    } else {
                        Variables.INSTANCE.setSensorAddress(address);
                        try {
                            bqzum.connectSensor();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                                    .text(e.getMessage()) // text to display
                                    .show(Variables.INSTANCE.getActivity()); // activity where it is displayed

                        }
                    }

                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                            .text("Bluetooth activado.") // text to display
                            .show(Variables.INSTANCE.getActivity()); // activity where it is displayed
                } else {
                    Log.d(TAG, "BT not enabled");
                    Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                            .text("Bluetooth no activado.") // text to display
                            .show(Variables.INSTANCE.getActivity()); // activity where it is displayed
                    finish();
                }
                break;
            case REQUEST_LOGIN:
                if(resultCode == Activity.RESULT_OK){
                    String user = data.getExtras().getString(Login.USER);
                    Variables.INSTANCE.setUser(user);
                    Variables.INSTANCE.setSend(SEND);
                    Log.i(TAG,Variables.INSTANCE.getUser());
                }else{
                    Variables.INSTANCE.setSend(NOT_SEND);
                }
        }
    }




}
