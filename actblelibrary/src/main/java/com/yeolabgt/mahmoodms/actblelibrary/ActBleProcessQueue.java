package com.yeolabgt.mahmoodms.actblelibrary;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mmahmood31 on 11/8/2017.
 *
 */
 public class ActBleProcessQueue extends Thread {
    private static final String TAG = ActBleProcessQueue.class.getSimpleName();
    final static int REQUEST_TYPE_READ_CHAR = 1;
    final static int REQUEST_TYPE_WRITE_CHAR = 2;
    final static int REQUEST_TYPE_READ_DESCRIPTOR = 3;
    final static int REQUEST_TYPE_WRITE_DESCRIPTOR = 4;
    private final static int REQUEST_TYPE_NOTIFICATION_ON = 5;
    private final static int REQUEST_TYPE_NOTIFICATION_OFF = 6;

    private static List<ActBleCharacteristic> actBleCharacteristicList = new ArrayList<>();

    static void addCharacteristicRequest(ActBleCharacteristic actBleCharacteristic) {
        actBleCharacteristicList.add(actBleCharacteristic);
    }

    private static void removeCharacteristicRequest(ActBleCharacteristic actBleCharacteristic) {
        actBleCharacteristicList.remove(actBleCharacteristic);
    }

    static int getActBleCharacteristicListSize() {
        if(!actBleCharacteristicList.isEmpty() || actBleCharacteristicList!=null) {
            return actBleCharacteristicList.size();
        }
        return 0;
    }

    private static boolean executeRequest() {
        boolean success = false;
        if(!actBleCharacteristicList.isEmpty()) {
            ActBleCharacteristic actBleCharacteristic = actBleCharacteristicList.get(0); //FIFO
            BluetoothGatt bluetoothGatt = actBleCharacteristic.getBluetoothGatt();
            switch (actBleCharacteristic.getRequestCode()) {
                case REQUEST_TYPE_READ_CHAR:
                    success = bluetoothGatt.readCharacteristic(actBleCharacteristic.getBluetoothGattCharacteristic());
                    break;
                case REQUEST_TYPE_WRITE_CHAR:
                    success = bluetoothGatt.writeCharacteristic(actBleCharacteristic.getBluetoothGattCharacteristic());
                    break;
                case REQUEST_TYPE_READ_DESCRIPTOR:
                    success = bluetoothGatt.readDescriptor(actBleCharacteristic.getBluetoothGattDescriptor());
                    break;
                case REQUEST_TYPE_WRITE_DESCRIPTOR:
                    success = bluetoothGatt.writeDescriptor(actBleCharacteristic.getBluetoothGattDescriptor());
                    break;
                case REQUEST_TYPE_NOTIFICATION_ON:
                    success = bluetoothGatt.setCharacteristicNotification(actBleCharacteristic.getBluetoothGattCharacteristic(), true);
                    break;
                case REQUEST_TYPE_NOTIFICATION_OFF:
                    success = bluetoothGatt.setCharacteristicNotification(actBleCharacteristic.getBluetoothGattCharacteristic(), false);
                    break;
                default:
                    success = false;
                    break;
            }
            actBleCharacteristic.setOperationSuccess(success);
            removeCharacteristicRequest(actBleCharacteristic);
        }
        return success;
    }

    private long delay = 1000;

    public void setDelay(long delay) {
        this.delay = delay;
    }

    private Timer mTimer = new Timer();

    @Override
    public void interrupt() {
        super.interrupt();
        if (mTimer!=null) {
            mTimer.cancel();
        }
    }
    //TODO: Need to change this method for writes:: There's too much delay.
    @Override
    public void run() {
        super.run();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean b = executeRequest();
                Log.d(TAG, "run: execute BLE Request: "+String.valueOf(b));
            }
        },0,delay);
    }
}
