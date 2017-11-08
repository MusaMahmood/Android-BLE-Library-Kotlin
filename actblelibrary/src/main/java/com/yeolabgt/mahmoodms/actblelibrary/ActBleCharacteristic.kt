package com.yeolabgt.mahmoodms.actblelibrary

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

/**
 * Created by mmahmood31 on 11/8/2017.
 * Object for processing requests upon (implicitly) readable or writable characteristics
 */

class ActBleCharacteristic {
    var bluetoothGatt: BluetoothGatt? = null
        private set
    var requestCode: Int = 0
        private set
    var bluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
        private set
    var bluetoothGattDescriptor: BluetoothGattDescriptor? = null

    constructor(requestCode: Int, bluetoothGatt: BluetoothGatt,
                bluetoothGattCharacteristic: BluetoothGattCharacteristic) {
        this.bluetoothGatt = bluetoothGatt
        this.requestCode = requestCode
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic
    }

    constructor(requestCode: Int, bluetoothGatt: BluetoothGatt,
                bluetoothGattDescriptor: BluetoothGattDescriptor, bluetoothGattCharacteristic: BluetoothGattCharacteristic) {
        this.bluetoothGatt = bluetoothGatt
        this.requestCode = requestCode
        this.bluetoothGattDescriptor = bluetoothGattDescriptor
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic
    }

}
