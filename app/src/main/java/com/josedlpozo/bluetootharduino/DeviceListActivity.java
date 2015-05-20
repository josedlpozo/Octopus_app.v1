package com.josedlpozo.bluetootharduino;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class DeviceListActivity extends ActionBarActivity implements ReciclerViewAdapter.ViewHolder.ClickListener{

    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private RecyclerView mRecyclerView;
    private ReciclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    Toolbar toolbar;


    @Override
    public void onClick(View view, int position) {
        // Cancel discovery because it's costly and we're about to connect
        mBtAdapter.cancelDiscovery();
        // Get the device MAC address, which is the last 17 chars in the
        // View
        Bluetooth bt = mAdapter.getBluetoothList().get(position);
        String info = bt.getName().toString();
        String address = info.substring(info.length() - 17);


        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        Bundle b = new Bundle();
        b.putInt("MOTORSENSOR",motorSensor);
        intent.putExtras(b);
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothClass bt = device.getBluetoothClass();
                int clase = bt.getDeviceClass();
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                    if(clase == 7936) {
                        mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.bq),
                                device.getName() + "\n" + device.getAddress()));
                    }else if(clase == 524){
                        mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.mobile_icon),
                                device.getName() + "\n" + device.getAddress()));
                    }else{
                        mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.ic_launcher),
                                device.getName() + "\n" + device.getAddress()));
                    }
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("Seleccione dispositivo");
                if (mPairedDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "No hay dispositivos encontrados.";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D)
            Log.d(TAG, "doDiscovery()");
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("Buscando..");
        // Turn on sub-title for new devices
        //findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    int motorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        setContentView(R.layout.activity_device_list);

        motorSensor = getIntent().getExtras().getInt("MOTORSENSOR");
        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);
        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getBaseContext().getResources().getColor(R.color.green_octopus));

        ArrayList<BluetoothDevice> item = new ArrayList<BluetoothDevice>();
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);




        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();





        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ReciclerViewAdapter(this,getBaseContext());
        mRecyclerView.setAdapter(mAdapter);
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                BluetoothClass bt = device.getBluetoothClass();
                int clase = bt.getDeviceClass();
                if(clase == 7936) {
                    mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.bq),
                            device.getName() + "\n" + device.getAddress()));
                }else if(clase == 524){
                    mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.mobile_icon),
                            device.getName() + "\n" + device.getAddress()));
                }else{
                    mAdapter.add(new Bluetooth(getResources().getDrawable(R.drawable.ic_launcher),
                            device.getName() + "\n" + device.getAddress()));
                }
                mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }

        } else {
            String noDevices = "No hay dispositivos emparejados.";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
}
