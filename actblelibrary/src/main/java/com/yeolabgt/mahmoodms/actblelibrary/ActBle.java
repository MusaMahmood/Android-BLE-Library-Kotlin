package com.yeolabgt.mahmoodms.actblelibrary;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by mmahmood31 on 11/8/2017.
 * ActBle Library Object
 */

public class ActBle {
    private static final String TAG = ActBle.class.getSimpleName();
    private static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private HashMap<String, BluetoothGatt> bluetoothGattHashMap = new HashMap<>();
    private ActBleListener mActBleListener;
    public ActBleProcessQueue actBleProcessQueue = new ActBleProcessQueue();
    private BluetoothManager mBluetoothManager;
    private Context mContext;

    public ActBle(Context context, BluetoothManager bluetoothManager,
                  ActBleListener actBleListener) {
        mActBleListener = actBleListener;
        this.mContext = context;
        this.mBluetoothManager = bluetoothManager;
        actBleProcessQueue.run();
    }

    private BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            mActBleListener.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            mActBleListener.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            mActBleListener.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            mActBleListener.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mActBleListener.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mActBleListener.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            mActBleListener.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            mActBleListener.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            mActBleListener.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            mActBleListener.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };


    // BLE Operations:
    public void readCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        ActBleCharacteristic actBleCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_READ_CHAR, gatt, characteristic);
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic);
    }

    public void writeCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] bytes) {
        characteristic.setValue(bytes);
        ActBleCharacteristic actBleCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_CHAR, gatt, characteristic);
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic);
    }

    public void readDescriptor(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        ActBleCharacteristic readWriteCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_READ_DESCRIPTOR, gatt, characteristic);
        ActBleProcessQueue.addCharacteristicRequest(readWriteCharacteristic);
    }

    public void writeDescriptor(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] bytes) {
        characteristic.setValue(bytes);
        ActBleCharacteristic readWriteCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_CHAR, gatt, characteristic);
        ActBleProcessQueue.addCharacteristicRequest(readWriteCharacteristic);
    }

    public void setCharacteristicNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enableCharacteristic) {
        if (enableCharacteristic) {
            enableNotifications(gatt, characteristic);
        } else {
            disableNotifications(gatt, characteristic);
        }
    }

    private void enableNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(!gatt.setCharacteristicNotification(characteristic, true)) {
            return;
        }
        final BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if(clientConfig==null) return;
        clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        ActBleCharacteristic actBleCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR, gatt, clientConfig);
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic);
    }

    private void disableNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if(!gatt.setCharacteristicNotification(characteristic, false)) return;
        final BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
        if(clientConfig==null) return;
        clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        ActBleCharacteristic actBleCharacteristic = new ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR, gatt, clientConfig);
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic);
    }

    public BluetoothGatt connect(BluetoothDevice device, boolean autoConnect) {
        if(mBluetoothManager==null || device==null) {
            mActBleListener.onError("Bluetooth Manager is Null");
            return null;
        }
        BluetoothGatt gatt = bluetoothGattHashMap.get(device.getAddress());
        if (gatt!=null) {
            Log.d(TAG, "Found Device in Hashmap: "+device.getAddress()
                    +" :: Attempting to Disconnect");
           //If already in Hashmap, disconnect before attempting to reconnect
           gatt.disconnect();
           gatt.close();
        }
        //Check Connection State:
        BluetoothGatt bluetoothGatt = null;
        if(mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT)
                == BluetoothProfile.STATE_DISCONNECTED) {
            //Attempt to Connect:
            bluetoothGatt = device.connectGatt(mContext, autoConnect, mBleGattCallback);
            if(bluetoothGattHashMap.containsKey(device.getAddress())) {
                bluetoothGattHashMap.remove(device.getAddress());
            }
            bluetoothGattHashMap.put(device.getAddress(), bluetoothGatt);
        }
        return bluetoothGatt;
    }

    public void disconnect(BluetoothGatt bluetoothGatt) {
        try {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGattHashMap.remove(bluetoothGatt.getDevice().getAddress());
            actBleProcessQueue.interrupt();
        } catch (Exception e) {
            Log.e(TAG, "Exception: "+e.toString());
            mActBleListener.onError(e.toString());
        }
    }

    public interface ActBleListener {
        void onServicesDiscovered(BluetoothGatt gatt, int status);

        void onReliableWriteCompleted(BluetoothGatt gatt, int status);

        void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

        void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

        void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

        void onMtuChanged(BluetoothGatt gatt, int mtu, int status);

//        void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status);
//
//        void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status);

        void onError(String errorMessage);
    }
}
