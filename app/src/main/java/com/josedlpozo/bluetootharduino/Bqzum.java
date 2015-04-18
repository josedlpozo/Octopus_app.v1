package com.josedlpozo.bluetootharduino;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by josedlpozo on 11/4/15.
 */
public class Bqzum {


    // Debugging
    private static final String TAG = "BQZUM";
    private static final boolean D = true;

    // Member object for the bt services
    private BluetoothService mBtServiceMotor;
    private BluetoothService mBtServiceSensor;

    private String startCmd = "CMD=1";
    private String endCmd = "CMD=2";

    private static final int timeBlock = 150;
    private int intensity = 100;

    private static final int MOTOR = 0;
    private static final int SENSOR = 1;

    public Bqzum() throws Exception {
        mBtServiceMotor = new BluetoothService(Variables.INSTANCE.getAppContext());
        mBtServiceSensor = new BluetoothService(Variables.INSTANCE.getAppContext());
    }

    public void connect() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        String sensorAddress = Variables.INSTANCE.getSensorAddress();
        // Attempt to connect to the device
        mBtServiceMotor.connect(motorAddress);
        mBtServiceSensor.connect(sensorAddress);
        // Wait till connected
        start(SENSOR);
        start(MOTOR);
    }

    private void sameMacAddr() throws Exception {
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        String sensorAddress = Variables.INSTANCE.getSensorAddress();
        if (motorAddress.equals(sensorAddress)
                && !(motorAddress.equals("00:00:00:00:00:00") && sensorAddress
                .equals("00:00:00:00:00:00")))
            throw new Exception(
                    "Left and right devices has the same mac address.");
    }

    public void connectSensor() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String rightAddress = Variables.INSTANCE.getSensorAddress();
        // Attempt to connect to the device
        mBtServiceSensor.connect(rightAddress);
        start(SENSOR);
    }

    public void connectMotor() throws Exception {
        sameMacAddr();
        // Get the devices MAC addresses
        String motorAddress = Variables.INSTANCE.getMotorAddress();
        // Attempt to connect to the device
        mBtServiceMotor.connect(motorAddress);
        start(MOTOR);
    }

    public void start(final int lr) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    switch (lr) {
                        case MOTOR:
                            while (!getMotorStatus())
                                ;
                            break;
                        case SENSOR:
                            while (!getSensorStatus())
                                ;
                            break;
                    }
                    SystemClock.sleep(timeBlock);
                    sendData(startCmd, lr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void disconnect() {
        if (getStatus()) {
            sendData(endCmd, MOTOR);
            sendData(endCmd, SENSOR);
        }
        SystemClock.sleep(timeBlock);
        stop();
    }

    public void stop() {
        if (mBtServiceSensor != null)
            mBtServiceSensor.stop();
        if (mBtServiceMotor != null)
            mBtServiceMotor.stop();
    }

    public void disconnectMotor() {
        sendData(endCmd, MOTOR);
        SystemClock.sleep(timeBlock);
        if (mBtServiceMotor != null)
            mBtServiceMotor.stop();
    }

    public void disconnectSensor() {
        sendData(endCmd, SENSOR);
        SystemClock.sleep(timeBlock);
        if (mBtServiceSensor != null)
            mBtServiceSensor.stop();
    }

    public boolean btEnabled() {
        return mBtServiceMotor.isBluetoothAdapterEnabled()
                && mBtServiceSensor.isBluetoothAdapterEnabled();
    }


    public boolean getStatus() {
        return (mBtServiceSensor.getState() == BluetoothService.STATE_CONNECTED && mBtServiceMotor
                .getState() == BluetoothService.STATE_CONNECTED);
    }

    public boolean getSensorStatus() {
        return (mBtServiceSensor.getState() == BluetoothService.STATE_CONNECTED);
    }

    public boolean getMotorStatus() {
        return (mBtServiceMotor.getState() == BluetoothService.STATE_CONNECTED);
    }


    /*
     * Sends an instruction.
     *
     * @param message A string of text to send.
     *
     * @param leftRight Left(0) or right(1) sensor.
     */
    public void sendData(String message, int motorSensor) {
        if (D) Log.d(TAG, "Send message: " + message + " to: " + motorSensor);
        SendThread t = new SendThread(message, motorSensor);
        t.start();
        //SystemClock.sleep(timeBlock); //TODO
    }

    class SendThread extends Thread {
        String message;
        int motorSensor;

        public SendThread(String message, int motorSensor) {
            this.message = message;
            this.motorSensor = motorSensor;
        }

        @Override
        public void run() {
            if (motorSensor == MOTOR) {
                // Check that there's actually something to send
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to
                    // write
                    byte[] send = message.getBytes();
                    mBtServiceMotor.write(send);
                }
            } else {
                // Check that there's actually something to send
                if (message.length() > 0) {
                    // Get the message bytes and tell the BluetoothService to
                    // write
                    byte[] send = message.getBytes();
                    mBtServiceSensor.write(send);
                }
            }
        }
    }
}

