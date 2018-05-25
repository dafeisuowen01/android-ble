package com.dxa.android.ble.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.Nullable;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.BluetoothTool;
import com.dxa.android.ble.ConnectState;
import com.dxa.android.ble.GattCallback;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.log.LoggerManager;

import java.util.UUID;

/**
 * 默认的GattClient类
 */
public class SimpleGattClient implements BluetoothGattClient {

    private final LoggerManager logger = LoggerManager.getInstance();
    private GattCallback mCallback;

    /**
     * 连接的BluetoothGatt
     */
    private BluetoothGatt mGatt;
    /**
     * 当前连接的设备
     */
    private volatile BluetoothDevice mDevice;

    /**
     * 默认的 BluetoothGattService
     */
    private volatile BluetoothGattService mService;
    /**
     * 默认的 BluetoothGattCharacteristic
     */
    private volatile BluetoothGattCharacteristic mCharacteristic;

    public SimpleGattClient() {
        this.mCallback = new GattCallback();
    }

    public SimpleGattClient(GattCallback callback) {
        this.mCallback = callback;
    }

    public void setCallback(GattCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("GattCallback对象不能为Null.");
        }
        this.mCallback = callback;
    }

    /**
     * 获取 BluetoothAdapter
     */
    @Override
    public BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 蓝牙是否可用
     */
    @Override
    public boolean isEnabled() {
        BluetoothAdapter adapter = getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * 获取当前的设备
     *
     * @return 返回当前连接的设备或null
     */
    @Override
    public BluetoothDevice getCurrentDevice() {
        return mDevice;
    }

    /**
     * 连接设备
     *
     * @param context     上下文对象
     * @param address     设备的MAC地址
     * @param autoConnect 是否自动重连，设备休眠或超过距离断开后，当设备可以重新连接时，系统会自动重新连接
     * @return 是否连接
     */
    @Override
    public boolean connect(Context context, String address, boolean autoConnect) {
        if (isEnabled()) {
            BluetoothDevice remoteDevice = getAdapter().getRemoteDevice(address);
            return connect(context, remoteDevice, autoConnect);
        }
        return false;
    }

    /**
     * 连接设备
     *
     * @param context     上下文对象
     * @param device      即将连接的设备
     * @param autoConnect 是否自动重连，设备休眠或超过距离断开后，当设备可以重新连接时，系统会自动重新连接
     * @return 是否连接
     */
    @Override
    public boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
        if (isEnabled() && checkDevice(device)) {
            mGatt = device.connectGatt(context, autoConnect, mCallback);
            mDevice = device;
            logger.i("连接蓝牙设备: ", device.getName(), ": ", device.getAddress(), " mGatt ", mGatt != null);
            return mGatt != null;
        }
        return false;
    }

    private boolean checkDevice(BluetoothDevice device) {
        // 设备不为null且当前设备为null时，可以连接
        return (device != null && mDevice == null);
    }

    /**
     * 重新连接，如果之前连接过设备
     *
     * @return 是否重新连接
     */
    @Override
    public boolean reconnect() {
        logger.i("重新连接");
        return mGatt != null && mGatt.connect();
    }

    /**
     * 获取BluetoothGatt对象
     *
     * @return 返回 BluetoothGatt 或 null
     */
    @Override
    public BluetoothGatt getBluetoothGatt() {
        return mGatt;
    }

    /**
     * 断开连接
     */
    @Override
    public void disconnect() {
        if (mGatt != null) {
            if (mDevice != null) {
                logger.i("断开连接，", mDevice.getName(), ": ", mDevice.getAddress());
            }
            mGatt.disconnect();
            mDevice = null;
            mService = null;
            mCharacteristic = null;
        }
    }

    /**
     * 断开连接
     *
     * @param close 是否顺便关闭资源
     */
    @Override
    public void disconnect(boolean close) {
        if (close) {
            close();
        } else {
            disconnect();
        }
    }

    /**
     * 使用BLE设备后，app必须调用此方法以确保资源被合理释放
     */
    @Override
    public void close() {
        if (mGatt != null) {
            mDevice = mDevice == null ? mGatt.getDevice() : mDevice;
            if (mDevice != null) {
                logger.i("断开连接并关闭通道，", mDevice.getName(), ": ", mDevice.getAddress());
            }
            disconnect();
            mGatt.close();
            mGatt = null;
        }
    }

    /**
     * 设置OnGattChangedListener监听
     *
     * @param listener OnGattChangedListener实现对象
     */
    @Override
    public void setOnGattChangedListener(OnGattChangedListener listener) {
        mCallback.setOnGattChangedListener(listener);
    }

    /**
     * 是否已连接
     */
    @Override
    public boolean isConnected() {
        return mCallback.isConnected();
    }

    @Override
    public boolean isDiscoverService() {
        return mCallback.isDiscoverService();
    }

    @Nullable
    @Override
    public ConnectState getConnectState() {
        return null;
    }

    @Override
    public void setGattService(BluetoothGattService service) {
        this.mService = service;
    }

    @Override
    public BluetoothGattService getGattService() {
        return mService;
    }

    @Override
    public void setGattCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.mCharacteristic = characteristic;
    }

    @Override
    public BluetoothGattCharacteristic getGattCharacteristic() {
        return mCharacteristic;
    }

    @Override
    public boolean write(byte[] value) {
        return writeCharacteristic(mCharacteristic, value);
    }

    @Override
    public boolean write(String hex) {
        if (hex != null && hex.trim().length() > 0) {
            byte[] value = BluetoothTool.hexToBin(hex);
            return writeCharacteristic(mCharacteristic, value);
        }
        return false;
    }


    private static String getDeviceAddress(BluetoothDevice device) {
        return device != null ? device.getAddress() : "";
    }

    /**********************************************************/

    /**
     * 获取对应UUID的BluetoothGattService
     *
     * @param serviceUUID BluetoothGattService对应的UUID
     * @return 返回对应UUID的BluetoothGattService或null
     */
    @Override
    public BluetoothGattService getService(UUID serviceUUID) {
        return BluetoothTool.getService(mGatt, serviceUUID);
    }

    /**
     * 获取BluetoothGattCharacteristic
     *
     * @param serviceUUID        BluetoothGattService的UUID
     * @param characteristicUUID BluetoothGattCharacteristic的UUID
     * @return 返回对应UUID的BluetoothGattCharacteristic或null
     */
    @Override
    public BluetoothGattCharacteristic getCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        return BluetoothTool.getCharacteristic(mGatt, serviceUUID, characteristicUUID);
    }

    /**
     * 获取BluetoothGattCharacteristic
     *
     * @param gattService        BluetoothGattService对象
     * @param characteristicUUID BluetoothGattCharacteristic的UUID
     * @return 返回对应UUID的BluetoothGattCharacteristic或null
     */
    @Override
    public BluetoothGattCharacteristic getCharacteristic(BluetoothGattService gattService, UUID characteristicUUID) {
        return BluetoothTool.getCharacteristic(gattService, characteristicUUID);
    }

    /**
     * @param serviceUUID        BluetoothGattService的UUID
     * @param characteristicUUID BluetoothGattCharacteristic的UUID
     * @param enableNotification 可读时是否提醒
     * @return 是否读取
     */
    @Override
    public boolean readCharacteristic(UUID serviceUUID, UUID characteristicUUID, boolean enableNotification) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(serviceUUID, characteristicUUID);
        return readCharacteristic(characteristic, enableNotification);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic     The characteristic to read from.
     * @param enableNotification 可读时是否提醒.
     * @return 是否读取
     */
    @Override
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic, boolean enableNotification) {
        if (!nonNull(characteristic, mGatt))
            return false;

        if (enableNotification) {
            logger.i("readCharacteristic ==>: 设置特征提醒: ", characteristic.getUuid());
            mGatt.setCharacteristicNotification(characteristic, true);
        }
        return mGatt.readCharacteristic(characteristic);
    }

    /**
     * 写入特征值
     */
    @Override
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        return nonNull(characteristic, mGatt)
                && characteristic.setValue(value)
                && mGatt.writeCharacteristic(characteristic);
    }

    @Override
    public boolean writeCharacteristic(BluetoothGattService service, UUID characteristicUUID, byte[] value) {
        if (service == null)
            return false;

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        return writeCharacteristic(characteristic, value);
    }

    @Override
    public boolean writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(serviceUUID, characteristicUUID);
        return writeCharacteristic(characteristic, value);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     * @return 是否设置BluetoothGattCharacteristic提醒
     */
    @Override
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        return mGatt != null && mGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * 获取对应UUID的BluetoothGattDescriptor
     */
    @Override
    public BluetoothGattDescriptor getDescriptor(BluetoothGattCharacteristic characteristic, UUID descriptorUUID) {
        return BluetoothTool.getDescriptor(characteristic, descriptorUUID);
    }

    /**
     * 给BluetoothGattDescriptor设置值
     * {@link BluetoothGattDescriptor#ENABLE_NOTIFICATION_VALUE}
     * {@link BluetoothGattDescriptor#ENABLE_INDICATION_VALUE}
     * {@link BluetoothGattDescriptor#DISABLE_NOTIFICATION_VALUE}
     */
    @Override
    public boolean setDescriptorValue(BluetoothGattCharacteristic characteristic, UUID descriptorUUID, byte[] value) {
        BluetoothGattDescriptor descriptor = getDescriptor(characteristic, descriptorUUID);
        return descriptor != null && descriptor.setValue(value);
    }

    /**
     * 给BluetoothGattDescriptor设置值
     * {@link BluetoothGattDescriptor#ENABLE_NOTIFICATION_VALUE}
     * {@link BluetoothGattDescriptor#ENABLE_INDICATION_VALUE}
     * {@link BluetoothGattDescriptor#DISABLE_NOTIFICATION_VALUE}
     */
    @Override
    public boolean setDescriptorValue(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, byte[] value) {
        BluetoothGattCharacteristic characteristic = getCharacteristic(serviceUUID, characteristicUUID);
        return setDescriptorValue(characteristic, descriptorUUID, value);
    }


    private static boolean nonNull(Object... objects) {
        for (Object o : objects) {
            if (o == null)
                return false;
        }
        return true;
    }
}
