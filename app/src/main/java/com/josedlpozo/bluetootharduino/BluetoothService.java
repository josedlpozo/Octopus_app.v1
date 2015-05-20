package com.josedlpozo.bluetootharduino;

/**
 * Created by josedlpozo on 11/4/15.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that for connecting with a
 * device and a thread for performing data transmissions when connected.
 */
public class BluetoothService {

    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;

    // Member fields
    private Context context;
    private final BluetoothAdapter mBluetoothAdapter;
    // private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    // Name of the connected device
    private String mConnectedDeviceName;

    private StringBuilder recDataString = new StringBuilder();

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    if (mConnectedDeviceName == null)
                        break;
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            hideLoadingProgress();
                            Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                                    .text("Connected to " + mConnectedDeviceName) // text to display
                                    .show(Variables.INSTANCE.getActivity()); // activity where it is displayed


                            break;
                        case BluetoothService.STATE_CONNECTING:
                            showLoadingProgress();
                            /*Toast.makeText(context,
                                    "Connecting " + mConnectedDeviceName,
                                    Toast.LENGTH_LONG).show();*/

                            break;
                        case BluetoothService.STATE_NONE:
                            Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                                    .text("Disconnect to " + mConnectedDeviceName) // text to display
                                    .show(Variables.INSTANCE.getActivity()); // activity where it is displayed
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(
                            BluetoothService.DEVICE_NAME);
                    break;
                case BluetoothService.MESSAGE_TOAST:
                    hideLoadingProgress();
                    Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                            .text(msg.getData().getString(BluetoothService.TOAST)) // text to display
                            .show(Variables.INSTANCE.getActivity()); // activity where it is displayed

                    break;
                case BluetoothService.MESSAGE_READ:

                    String readMessage = (String) msg.obj;
                    Log.i(TAG,readMessage);
                    recDataString.append(readMessage); //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("*");

                    if(endOfLineIndex>0) {
                        Log.i("JOSE",""+endOfLineIndex);
                        String dataInPrint = recDataString.substring(0, endOfLineIndex+1);
                        Variables.INSTANCE.setRecibido(dataInPrint);
                    }
                    break;
            }
        }
    };

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key name received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    /**
     * Constructor. Prepares a new Bluetooth session.
     *
     * @param //context
     *            The UI Activity Context
     * @param //handler
     *            A Handler to send messages back to the UI Activity
     * @throws Exception
     */
    public BluetoothService(Context cnt) throws Exception {
        mState = STATE_NONE;
        context = cnt;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Snackbar.with(Variables.INSTANCE.getAppContext()) // context
                    .text("Bluetooth no disponible.") // text to display
                    .show(Variables.INSTANCE.getActivity()); // activity where it is displayed
            throw new Exception("Bluetooth is not supported");
        }
    }

    private void hideLoadingProgress() {
        ((CircularProgressDrawable) Variables.INSTANCE.getCircularProgressBar().getIndeterminateDrawable()).progressiveStop();
        Variables.INSTANCE.getCircularProgressBar().setVisibility(View.INVISIBLE);

    }

    private void showLoadingProgress() {

        Variables.INSTANCE.getCircularProgressBar().setVisibility(View.VISIBLE);
        ((CircularProgressDrawable) Variables.INSTANCE.getCircularProgressBar().getIndeterminateDrawable()).start();
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param //device
     *            The BluetoothDevice to connect
     */
    public synchronized void connect(String addr) {
        if (D)
            Log.d(TAG, "connect to: " + addr);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
        mConnectedDeviceName = device.getName();
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket
     *            The BluetoothSocket on which the connection was made
     * @param device
     *            The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        if (D)
            Log.d(TAG, "connected");
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device.");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
     private void connectionLost() {
      setState(STATE_LISTEN);
      // Send a failure message back to the Activity
       Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
       Bundle bundle = new Bundle();
       bundle.putString(TOAST, "Device connection was lost");
       msg.setData(bundle);
       mHandler.sendMessage(msg);
     }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    public boolean isBluetoothAdapterEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state
     *            An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D)
            Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                connectionFailed();
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
             InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                 tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        // @Override
         public void run() {
             Log.i(TAG, "BEGIN mConnectedThread");
             byte[] buffer = new byte[1024];
             int bytes;
             // Keep listening to the InputStream while connected
             while (true) {
                 try {
                     // Read from the InputStream
                     bytes = mmInStream.read(buffer);
                     String readMessage = new String(buffer, 0, bytes);
                     // Send the obtained bytes to the UI Activity
                     //mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
                     //buffer).sendToTarget();

                     mHandler.obtainMessage(MESSAGE_READ, bytes, -1, readMessage).sendToTarget();
                 } catch (IOException e) {
                     Log.e(TAG, "disconnected", e);
                     connectionLost();
                     break;
                 }
             }
         }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            Method m = null;
            try {
                m = device.getClass().getMethod("createInsecureRfcommSocket",
                        new Class[] { int.class });
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                tmp = (BluetoothSocket) m.invoke(device, 1);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log
                            .e(
                                    TAG,
                                    "unable to close() socket during connection failure",
                                    e2);
                }
                return;
            }
            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            // Start the connected thread
            connected(mmSocket, mmDevice);
        }
    }
}
