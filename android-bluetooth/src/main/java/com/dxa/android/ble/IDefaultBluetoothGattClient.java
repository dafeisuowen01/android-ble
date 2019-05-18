package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

/**
 * 有默认实现的 {@link BluetoothGattClient} 接口
 */
public interface IDefaultBluetoothGattClient extends BluetoothGattClient {


    BluetoothGattClient getGattClient();

    @Override
    default BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    default boolean isEnabled() {
        return getAdapter().isEnabled();
    }

    @Override
    default BluetoothDevice getCurrentDevice() {
        return getGattClient().getCurrentDevice();
    }

    @Override
    default boolean connect(Context context, String address, boolean autoConnect) {
        return getGattClient().connect(context, address, autoConnect);
    }

    @Override
    default boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
        return getGattClient().connect(context, device, autoConnect);
    }

    @Override
    default boolean reconnect() {
        return getGattClient().reconnect();
    }

    @Override
    default BluetoothGatt getBluetoothGatt() {
        return getGattClient().getBluetoothGatt();
    }

    @Override
    default void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        getGattClient().setBluetoothGatt(bluetoothGatt);
    }

    @Override
    default void disconnect() {
        getGattClient().disconnect();
    }

    @Override
    default void disconnect(boolean close) {
        getGattClient().disconnect(close);
    }

    @Override
    default void close() {
        getGattClient().close();
    }

    @Override
    default void setOnGattChangedListener(OnGattChangedListener listener) {
        getGattClient().setOnGattChangedListener(listener);
    }

    @Override
    default boolean isConnected() {
        return getGattClient().isConnected();
    }

    @Override
    default boolean isDiscoverService() {
        return getGattClient().isDiscoverService();
    }

    @Override
    default boolean autoConnect() {
        return getGattClient().autoConnect();
    }

    @Override
    default ConnectState getConnectState() {
        return getGattClient().getConnectState();
    }

    @Override
    default void setReadGattService(BluetoothGattService service) {
        getGattClient().setReadGattService(service);
    }

    @Override
    default BluetoothGattService getReadGattService() {
        return getGattClient().getReadGattService();
    }

    @Override
    default void setReadGattCharacteristic(BluetoothGattCharacteristic characteristic) {
        getGattClient().setReadGattCharacteristic(characteristic);
    }

    @Override
    default BluetoothGattCharacteristic getReadGattCharacteristic() {
        return getGattClient().getReadGattCharacteristic();
    }

    @Override
    default void setWriteGattService(BluetoothGattService service) {
        getGattClient().setWriteGattService(service);
    }

    @Override
    default BluetoothGattService getWriteGattService() {
        return getGattClient().getWriteGattService();
    }

    @Override
    default void setWriteGattCharacteristic(BluetoothGattCharacteristic characteristic) {
        getGattClient().setWriteGattCharacteristic(characteristic);
    }

    @Override
    default BluetoothGattCharacteristic getWriteGattCharacteristic() {
        return getGattClient().getWriteGattCharacteristic();
    }

    @Override
    default void setReadServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        getGattClient().setReadServiceAndCharacteristic(service, characteristic);
    }

    @Override
    default void setWriteServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        getGattClient().setWriteServiceAndCharacteristic(service, characteristic);
    }

    @Override
    default void setDefaultServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        getGattClient().setDefaultServiceAndCharacteristic(service, characteristic);
    }

    @Override
    default boolean write(byte[] value) {
        return getGattClient().write(value);
    }

    @Override
    default boolean write(String hex) {
        return getGattClient().write(hex);
    }

    @Override
    default BluetoothGattService getService(UUID serviceUUID) {
        return getGattClient().getService(serviceUUID);
    }

    @Override
    default BluetoothGattCharacteristic getCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        return getGattClient().getCharacteristic(serviceUUID, characteristicUUID);
    }

    @Override
    default BluetoothGattCharacteristic getCharacteristic(BluetoothGattService gattService, UUID characteristicUUID) {
        return getGattClient().getCharacteristic(gattService, characteristicUUID);
    }

    @Override
    default boolean readCharacteristic(UUID serviceUUID, UUID characteristicUUID, boolean enableNotification) {
        return getGattClient().readCharacteristic(serviceUUID, characteristicUUID, enableNotification);
    }

    @Override
    default boolean readCharacteristic(BluetoothGattCharacteristic characteristic, boolean enableNotification) {
        return getGattClient().readCharacteristic(characteristic, enableNotification);
    }

    @Override
    default boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        return getGattClient().writeCharacteristic(characteristic, value);
    }

    @Override
    default boolean writeCharacteristic(BluetoothGattService service, UUID characteristicUUID, byte[] value) {
        return getGattClient().writeCharacteristic(service, characteristicUUID, value);
    }

    @Override
    default boolean writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        return getGattClient().writeCharacteristic(serviceUUID, characteristicUUID, value);
    }

    @Override
    default boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        return getGattClient().setCharacteristicNotification(characteristic, enabled);
    }

    @Override
    default BluetoothGattDescriptor getDescriptor(BluetoothGattCharacteristic characteristic, UUID descriptorUUID) {
        return getGattClient().getDescriptor(characteristic, descriptorUUID);
    }

    @Override
    default boolean setDescriptorValue(BluetoothGattCharacteristic characteristic, UUID descriptorUUID, byte[] value) {
        return getGattClient().setDescriptorValue(characteristic, descriptorUUID, value);
    }

    @Override
    default boolean setDescriptorValue(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] value) {
        return getGattClient().setDescriptorValue(serviceUUID, characteristicUUID, descriptorUUID, value);
    }
}
