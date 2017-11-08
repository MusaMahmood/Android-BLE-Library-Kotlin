package com.yeolabgt.mahmoodms.actblelibrary;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mmahmood31 on 11/8/2017.
 * ActBle Library Object
 */

public class ActBle {

    private static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
//    private ProcessQueueExecutor processQueueExecutor = new ProcessQueueExecutor();
    private HashMap<String, BluetoothGatt> bluetoothGattHashMap = new HashMap<>();
    private ActBleListener mActBleListener;
    private BluetoothManager mBluetoothManager;
    private Context context;
    


    public interface ActBleListener {
        void onServicesDiscovered(BluetoothGatt gatt, int status);

        void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

        void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

        void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

        void onError(String errorMessage);
    }
}
