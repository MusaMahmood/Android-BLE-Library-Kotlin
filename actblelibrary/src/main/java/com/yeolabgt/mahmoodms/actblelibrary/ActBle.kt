package com.yeolabgt.mahmoodms.actblelibrary

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log

import java.util.HashMap
import java.util.UUID
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * Created by mmahmood31 on 11/8/2017.
 * ActBle Library Object
 */

class ActBle(private val mContext: Context, private val mBluetoothManager: BluetoothManager?,
             private val mActBleListener: ActBleListener)
{
    private val bluetoothGattHashMap = HashMap<String, BluetoothGatt>()

    private val mBleGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            mActBleListener.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            mActBleListener.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            var remove = false
            var position = 0
            for (abc in ActBleProcessQueue.getActBleCharacteristicList()) {
                if (characteristic.uuid == (abc.bluetoothGattCharacteristic!!.uuid)
                        && abc.requestCode == ActBleProcessQueue.REQUEST_TYPE_READ_CHAR) {
                    remove = true
                    position = ActBleProcessQueue.getActBleCharacteristicList().indexOf(abc)
                }
            }
            if (remove) {
                ActBleProcessQueue.removeCharacteristicRequest(position)
                runProcess()
            }
            mActBleListener.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d(TAG, "onCharacteristicWrite: "+characteristic.uuid.toString())
            var remove = false
            var position = 0
            for (abc in ActBleProcessQueue.getActBleCharacteristicList()) {
                if (characteristic.uuid == (abc.bluetoothGattCharacteristic!!.uuid)
                        && abc.requestCode == ActBleProcessQueue.REQUEST_TYPE_WRITE_CHAR) {
                    remove = true
                    position = ActBleProcessQueue.getActBleCharacteristicList().indexOf(abc)
                }
            }
            if (remove) {
                ActBleProcessQueue.removeCharacteristicRequest(position)
                if (ActBleProcessQueue.actBleCharacteristicListSize>0 &&
                        ActBleProcessQueue.getActBleCharacteristicList()[0]
                                .requestCode!=ActBleProcessQueue.REQUEST_TYPE_WRITE_CHAR)  {
                    //If next characteristic request isn't a write, run the process:
                    runProcess()
                }
            }
            mActBleListener.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            var remove = false
            var position = 0
            for (abc in ActBleProcessQueue.getActBleCharacteristicList()) {
                if (characteristic.uuid == (abc.bluetoothGattCharacteristic!!.uuid)
                        && abc.requestCode == ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR) {
                    remove = true
                    position = ActBleProcessQueue.getActBleCharacteristicList().indexOf(abc)
                }
            }
            if (remove) {
                ActBleProcessQueue.removeCharacteristicRequest(position)
                runProcess()
            }
            mActBleListener.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            mActBleListener.onDescriptorRead(gatt, descriptor, status)
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            Log.d(TAG, "onDescriptorWrite: charUUID: "+descriptor.characteristic.uuid.toString())
            var remove = false
            var position = 0
            for (abc in ActBleProcessQueue.getActBleCharacteristicList()) {
                if (descriptor.characteristic.uuid == (abc.bluetoothGattCharacteristic!!.uuid)
                        && abc.requestCode == ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR) {
                    remove = true
                    position = ActBleProcessQueue.getActBleCharacteristicList().indexOf(abc)
                }
            }
            if (remove) {
                ActBleProcessQueue.removeCharacteristicRequest(position)
                runProcess()
            }
            mActBleListener.onDescriptorWrite(gatt, descriptor, status)
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            mActBleListener.onReadRemoteRssi(gatt, rssi, status)
        }

    }

    // BLE Operations:
    fun runProcess() {
        if (ActBleProcessQueue.actBleCharacteristicListSize > 0) {
            val executorService = Executors.newSingleThreadExecutor()
            val operationSuccess = executorService.submit(ActBleProcessQueue.SequentialThread())
            try {
                Log.d(TAG, "runProcess: operationSuccess:"
                        + operationSuccess.get().toString())
            } catch (e: InterruptedException) {
                Log.e(TAG, "runProcess1: ", e)
            } catch (e: ExecutionException) {
                Log.e(TAG, "runProcess2: ", e)
            }

        }
    }

    fun readCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val actBleCharacteristic = ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_READ_CHAR, gatt, characteristic)
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic)
    }

    fun writeCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, bytes: ByteArray) {
        characteristic.value = bytes
        val actBleCharacteristic = ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_CHAR, gatt, characteristic)
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic)
        runProcess()
    }

    fun setCharacteristicNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, enableCharacteristic: Boolean) {
        if (enableCharacteristic) {
            enableNotifications(gatt, characteristic)
        } else {
            disableNotifications(gatt, characteristic)
        }
    }

    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        if (!gatt.setCharacteristicNotification(characteristic, true)) {
            return
        }
        val clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) ?: return
        clientConfig.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val actBleCharacteristic = ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR, gatt, clientConfig, characteristic)
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic)
    }

    private fun disableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        if (!gatt.setCharacteristicNotification(characteristic, false)) return
        val clientConfig = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG) ?: return
        clientConfig.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        val actBleCharacteristic = ActBleCharacteristic(ActBleProcessQueue.REQUEST_TYPE_WRITE_DESCRIPTOR, gatt, clientConfig, characteristic)
        ActBleProcessQueue.addCharacteristicRequest(actBleCharacteristic)
    }

    fun connect(device: BluetoothDevice?, autoConnect: Boolean): BluetoothGatt? {
        if (mBluetoothManager == null || device == null) {
            mActBleListener.onError("Bluetooth Manager is Null")
            return null
        }
        val gatt = bluetoothGattHashMap[device.address]
        if (gatt != null) {
            Log.d(TAG, "Found Device in Hashmap: " + device.address
                    + " :: Attempting to Disconnect")
            //If already in hashmap, disconnect before attempting to reconnect
            gatt.disconnect()
            gatt.close()
        }
        //Check Connection State:
        var bluetoothGatt: BluetoothGatt? = null
        if (mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED) {
            //Attempt to Connect:
            bluetoothGatt = device.connectGatt(mContext, autoConnect, mBleGattCallback)
            if (bluetoothGattHashMap.containsKey(device.address)) {
                bluetoothGattHashMap.remove(device.address)
            }
            bluetoothGattHashMap.put(device.address, bluetoothGatt)
        }
        return bluetoothGatt
    }

    fun disconnect(bluetoothGatt: BluetoothGatt) {
        try {
            bluetoothGatt.disconnect()
            bluetoothGatt.close()
            bluetoothGattHashMap.remove(bluetoothGatt.device.address)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.toString())
            mActBleListener.onError(e.toString())
        }

    }

    interface ActBleListener {
        fun onServicesDiscovered(gatt: BluetoothGatt, status: Int)

        fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int)

        fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int)

        fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic)

        fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int)

        fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int)

        fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int)

        fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int)

        fun onError(errorMessage: String)
    }

    companion object {
        private val TAG = ActBle::class.java.simpleName
        private val CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
