package com.yeolabgt.mahmoodms.actblelibrary;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by mmahmood31 on 11/8/2017.
 *
 */
 class ActBleProcessQueue {
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

    static void removeCharacteristicRequest(int index) {
        actBleCharacteristicList.remove(index);
    }

    static List<ActBleCharacteristic> getActBleCharacteristicList() {
        return actBleCharacteristicList;
    }

    static int getActBleCharacteristicListSize() {
        if(!actBleCharacteristicList.isEmpty() || actBleCharacteristicList!=null) {
            return actBleCharacteristicList.size();
        }
        return 0;
    }

    private static boolean executeRequest(ActBleCharacteristic actBleCharacteristic) {
        boolean success;
        BluetoothGatt bluetoothGatt = actBleCharacteristic.getBluetoothGatt();
        switch (actBleCharacteristic.getRequestCode()) {
            case REQUEST_TYPE_READ_CHAR:
                success = bluetoothGatt.readCharacteristic(actBleCharacteristic.getBluetoothGattCharacteristic());
                break;
            case REQUEST_TYPE_WRITE_CHAR:
                success = bluetoothGatt.writeCharacteristic(actBleCharacteristic.getBluetoothGattCharacteristic());
                removeCharacteristicRequest(actBleCharacteristic);
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
                removeCharacteristicRequest(actBleCharacteristic);
                break;
            default:
                success = false;
                break;
        }
        return success;
    }

    static int numberOfBlueoothGattCommunications = 0;

    static class SequentialThread implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            if(!actBleCharacteristicList.isEmpty()) {
                Log.e(TAG, "Executing Command#"+String.valueOf(++numberOfBlueoothGattCommunications));
                return executeRequest(actBleCharacteristicList.get(0));
            }
            return null;
        }
    }
}
