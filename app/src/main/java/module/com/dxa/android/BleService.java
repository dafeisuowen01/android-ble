package com.dxa.android;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.benefit.base.log.DLogger;
import com.benefit.base.log.LogLevel;
import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.ConnectState;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;

import java.util.UUID;


public class BleService extends Service {
    private final DLogger logger = DLogger.getLogger(getClass(), LogLevel.DEBUG);

    private final BleBinder bleBinder = new BleBinder();

    public BleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        logger.i("onBind: ", intent);
        return bleBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logger.i("onUnbind: ", intent);
        return super.onUnbind(intent);
    }

    public class BleBinder extends Binder implements BluetoothGattClient {
        private final SimpleGattClient client = new SimpleGattClient();

        @Override
        public BluetoothAdapter getAdapter() {
            return client.getAdapter();
        }

        @Override
        public boolean isEnabled() {
            return client.isEnabled();
        }

        @Override
        public BluetoothDevice getCurrentDevice() {
            return client.getCurrentDevice();
        }

        @Override
        public boolean connect(Context context, String address, boolean autoConnect) {
            return client.connect(context, address, autoConnect);
        }

        @Override
        public boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
            return client.connect(context, device, autoConnect);
        }

        @Override
        public boolean reconnect() {
            return client.reconnect();
        }

        @Override
        public BluetoothGatt getBluetoothGatt() {
            return client.getBluetoothGatt();
        }

        @Override
        public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
            client.setBluetoothGatt(bluetoothGatt);
        }

        @Override
        public void disconnect() {
            client.disconnect();
        }

        @Override
        public void disconnect(boolean close) {
            client.disconnect(close);
        }

        @Override
        public void close() {
            client.close();
        }

        @Override
        public void setOnGattChangedListener(OnGattChangedListener listener) {
            client.setOnGattChangedListener(listener);
        }

        @Override
        public boolean isConnected() {
            return client.isConnected();
        }

        @Override
        public boolean isDiscoverService() {
            return client.isDiscoverService();
        }

        @Override
        public boolean autoConnect() {
            return client.autoConnect();
        }

        @Override
        public ConnectState getConnectState() {
            return client.getConnectState();
        }

        @Override
        public void setReadGattService(BluetoothGattService service) {
            client.setReadGattService(service);
        }

        @Override
        public BluetoothGattService getReadGattService() {
            return client.getReadGattService();
        }

        @Override
        public void setReadGattCharacteristic(BluetoothGattCharacteristic characteristic) {
            client.setReadGattCharacteristic(characteristic);
        }

        @Override
        public BluetoothGattCharacteristic getReadGattCharacteristic() {
            return client.getReadGattCharacteristic();
        }

        @Override
        public void setWriteGattService(BluetoothGattService service) {
            client.setWriteGattService(service);
        }

        @Override
        public BluetoothGattService getWriteGattService() {
            return client.getWriteGattService();
        }

        @Override
        public void setWriteGattCharacteristic(BluetoothGattCharacteristic characteristic) {
            client.setWriteGattCharacteristic(characteristic);
        }

        @Override
        public BluetoothGattCharacteristic getWriteGattCharacteristic() {
            return client.getWriteGattCharacteristic();
        }

        @Override
        public boolean write(byte[] value) {
            return client.write(value);
        }

        @Override
        public boolean write(String hex) {
            return client.write(hex);
        }

        @Override
        public BluetoothGattService getService(UUID serviceUUID) {
            return client.getService(serviceUUID);
        }

        @Override
        public BluetoothGattCharacteristic getCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
            return client.getCharacteristic(serviceUUID, characteristicUUID);
        }

        @Override
        public BluetoothGattCharacteristic getCharacteristic(BluetoothGattService gattService, UUID characteristicUUID) {
            return client.getCharacteristic(gattService, characteristicUUID);
        }

        @Override
        public boolean readCharacteristic(UUID serviceUUID, UUID characteristicUUID, boolean enableNotification) {
            return client.readCharacteristic(serviceUUID, characteristicUUID, enableNotification);
        }

        @Override
        public boolean readCharacteristic(BluetoothGattCharacteristic characteristic, boolean enableNotification) {
            return client.readCharacteristic(characteristic, enableNotification);
        }

        @Override
        public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
            return client.writeCharacteristic(characteristic, value);
        }

        @Override
        public boolean writeCharacteristic(BluetoothGattService service, UUID characteristicUUID, byte[] value) {
            return client.writeCharacteristic(service, characteristicUUID, value);
        }

        @Override
        public boolean writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
            return client.writeCharacteristic(serviceUUID, characteristicUUID, value);
        }

        @Override
        public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
            return client.setCharacteristicNotification(characteristic, enabled);
        }

        @Override
        public BluetoothGattDescriptor getDescriptor(BluetoothGattCharacteristic characteristic, UUID descriptorUUID) {
            return client.getDescriptor(characteristic, descriptorUUID);
        }

        @Override
        public boolean setDescriptorValue(BluetoothGattCharacteristic characteristic, UUID descriptorUUID, byte[] value) {
            return client.setDescriptorValue(characteristic, descriptorUUID, value);
        }

        @Override
        public boolean setDescriptorValue(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] value) {
            return client.setDescriptorValue(serviceUUID, characteristicUUID, descriptorUUID, value);
        }
    }
}
