package com.dxa.android.ble.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.dxa.android.ble.BluetoothTool;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.log.LoggerManager;

/**
 * 打印日志
 */
public class LogGattChangedListener implements OnGattChangedListener {

    private final LoggerManager log = LoggerManager.getInstance();

    @Override
    public void onConnected(BluetoothGatt gatt) {
        log.i("onConnected: " + gatt.getDevice());
    }

    @Override
    public boolean onServiceDiscover(BluetoothGatt gatt) {
        log.i("onServiceDiscover: ", gatt.getDevice());
        return false;
    }

    @Override
    public void onDisconnected(BluetoothGatt gatt, boolean auto) {
        log.i("onDisconnected: ", gatt.getDevice(), ", auto: ", auto);
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy) {
        log.i("onPhyUpdate: ", gatt.getDevice(), ", txPhy: ", txPhy, ", rxPhy: ", rxPhy);
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy) {
        log.i("onPhyUpdate: ", gatt.getDevice(), ", txPhy: ", txPhy, ", rxPhy: ", rxPhy);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        log.i("onCharacteristicRead: ", gatt.getDevice(), ", characteristic: ", characteristic, ", status: ", status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        log.i("onCharacteristicWrite: ", gatt.getDevice(), ", characteristic: ", characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        log.i("onCharacteristicChanged: ", gatt.getDevice(), ", characteristic: ",
                characteristic, ", value: ", BluetoothTool.binToBin(characteristic.getValue()));
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
        log.i("onDescriptorRead: ", gatt.getDevice(), ", descriptor: ", descriptor);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
        log.i("onDescriptorWrite: ", gatt.getDevice(), ", descriptor: ", descriptor);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt) {
        log.i("onReliableWriteCompleted: ", gatt.getDevice());
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi) {
        log.i("onReadRemoteRssi: ", gatt.getDevice(), ", rssi: ", rssi);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu) {
        log.i("onMtuChanged: ", gatt.getDevice(), ", mtu: ", mtu);
    }
}
