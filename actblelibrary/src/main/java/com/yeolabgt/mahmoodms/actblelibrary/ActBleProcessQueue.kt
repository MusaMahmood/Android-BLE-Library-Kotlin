package com.yeolabgt.mahmoodms.actblelibrary

import android.util.Log

import java.util.ArrayList
import java.util.concurrent.Callable

/**
 * Created by mmahmood31 on 11/8/2017.
 *
 */
internal object ActBleProcessQueue {
    private val TAG = ActBleProcessQueue::class.java.simpleName
    val REQUEST_TYPE_READ_CHAR = 1
    val REQUEST_TYPE_WRITE_CHAR = 2
    val REQUEST_TYPE_READ_DESCRIPTOR = 3
    val REQUEST_TYPE_WRITE_DESCRIPTOR = 4
    private val REQUEST_TYPE_NOTIFICATION_ON = 5
    private val REQUEST_TYPE_NOTIFICATION_OFF = 6

    private val actBleCharacteristicList = ArrayList<ActBleCharacteristic>()

    val actBleCharacteristicListSize: Int
        get() = if (!actBleCharacteristicList.isEmpty()) {
            actBleCharacteristicList.size
        } else 0

    var numberOfBluetoothGattCommunications = 0

    fun addCharacteristicRequest(actBleCharacteristic: ActBleCharacteristic) {
        actBleCharacteristicList.add(actBleCharacteristic)
    }

    private fun removeCharacteristicRequest(actBleCharacteristic: ActBleCharacteristic) {
        actBleCharacteristicList.remove(actBleCharacteristic)
    }

    fun removeCharacteristicRequest(index: Int) {
        actBleCharacteristicList.removeAt(index)
    }

    fun getActBleCharacteristicList(): List<ActBleCharacteristic> {
        return actBleCharacteristicList
    }

    private fun executeRequest(actBleCharacteristic: ActBleCharacteristic): Boolean {
        val success: Boolean
        val bluetoothGatt = actBleCharacteristic.bluetoothGatt
        when (actBleCharacteristic.requestCode) {
            REQUEST_TYPE_READ_CHAR -> success = bluetoothGatt!!.readCharacteristic(actBleCharacteristic.bluetoothGattCharacteristic)
            REQUEST_TYPE_WRITE_CHAR -> {
                success = bluetoothGatt!!.writeCharacteristic(actBleCharacteristic.bluetoothGattCharacteristic)
                removeCharacteristicRequest(actBleCharacteristic)
            }
            REQUEST_TYPE_READ_DESCRIPTOR -> success = bluetoothGatt!!.readDescriptor(actBleCharacteristic.bluetoothGattDescriptor)
            REQUEST_TYPE_WRITE_DESCRIPTOR -> success = bluetoothGatt!!.writeDescriptor(actBleCharacteristic.bluetoothGattDescriptor)
            REQUEST_TYPE_NOTIFICATION_ON -> success = bluetoothGatt!!.setCharacteristicNotification(actBleCharacteristic.bluetoothGattCharacteristic, true)
            REQUEST_TYPE_NOTIFICATION_OFF -> {
                success = bluetoothGatt!!.setCharacteristicNotification(actBleCharacteristic.bluetoothGattCharacteristic, false)
                removeCharacteristicRequest(actBleCharacteristic)
            }
            else -> success = false
        }
        return success
    }

    internal class SequentialThread : Callable<Boolean> {
        @Throws(Exception::class)
        override fun call(): Boolean? {
            if (!actBleCharacteristicList.isEmpty()) {
                Log.e(TAG, "Executing Command#" + (++numberOfBluetoothGattCommunications).toString())
                return executeRequest(actBleCharacteristicList[0])
            }
            return null
        }
    }
}
