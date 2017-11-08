package com.yeolabgt.mahmoodms.actblelibrary;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by mmahmood31 on 11/8/2017.
 * Object for processing requests upon (implicitly) readable or writable characteristics
 */

public class ActBleCharacteristic {
    private BluetoothGatt bluetoothGatt;
    private int requestCode;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
    private BluetoothGattDescriptor bluetoothGattDescriptor = null;
    private boolean operationSuccess = false;

    public ActBleCharacteristic(int requestCode, BluetoothGatt bluetoothGatt,
                                BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.bluetoothGatt = bluetoothGatt;
        this.requestCode = requestCode;
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    public ActBleCharacteristic(int requestCode, BluetoothGatt bluetoothGatt,
                                BluetoothGattDescriptor bluetoothGattDescriptor, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        this.bluetoothGatt = bluetoothGatt;
        this.requestCode = requestCode;
        this.bluetoothGattDescriptor = bluetoothGattDescriptor;
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return bluetoothGattCharacteristic;
    }

    public void setBluetoothGattCharacteristic(BluetoothGattCharacteristic
                                                       bluetoothGattCharacteristic) {
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
    }

    public BluetoothGattDescriptor getBluetoothGattDescriptor() {
        return bluetoothGattDescriptor;
    }

    public void setBluetoothGattDescriptor(BluetoothGattDescriptor
                                                   bluetoothGattDescriptor) {
        this.bluetoothGattDescriptor = bluetoothGattDescriptor;
    }

    public boolean isOperationSuccess() {
        return operationSuccess;
    }

    public void setOperationSuccess(boolean operationSuccess) {
        this.operationSuccess = operationSuccess;
    }
}
