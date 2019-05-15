package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

/**
 * 蓝牙4.0客户端
 */

public interface BluetoothGattClient extends GattOperator {

    /**
     * 获取 BluetoothAdapter
     */
    default BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 蓝牙是否可用
     */
    boolean isEnabled();

    /**
     * 获取当前的设备
     *
     * @return 返回当前连接的设备或null
     */
    BluetoothDevice getCurrentDevice();

    /**
     * 连接设备
     *
     * @param context     上下文对象
     * @param address     设备的MAC地址
     * @param autoConnect 是否自动重连，当设备休眠或超过距离断开后，系统会自动重新连接
     * @return 是否连接
     */
    boolean connect(Context context, String address, boolean autoConnect);

    /**
     * 连接设备
     *
     * @param context     上下文对象
     * @param device      即将连接的设备
     * @param autoConnect 是否自动重连，设备休眠或超过距离断开后，当设备可以重新连接时，系统会自动重新连接
     * @return 是否连接
     */
    default boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
        return connect(context, device.getAddress(), autoConnect);
    }

    /**
     * 重新连接，如果之前连接过设备
     *
     * @return 是否重新连接(没有连接过设备调用会返回false)
     */
    default boolean reconnect() {
        BluetoothGatt gatt = getBluetoothGatt();
        return gatt != null && gatt.connect();
    }

    /**
     * 获取BluetoothGatt对象
     *
     * @return 返回 BluetoothGatt 或 null
     */
    BluetoothGatt getBluetoothGatt();

    /**
     * 设置BluetoothGatt对象
     *
     * @param bluetoothGatt BluetoothGatt
     */
    void setBluetoothGatt(BluetoothGatt bluetoothGatt);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 断开连接
     *
     * @param close 是否顺便关闭资源
     */
    void disconnect(boolean close);

    /**
     * 使用BLE设备后，app必须调用此方法以确保资源被合理释放
     */
    void close();

    /**
     * 设置OnGattChangedListener监听
     *
     * @param listener OnGattChangedListener实现对象
     */
    void setOnGattChangedListener(OnGattChangedListener listener);

    /**
     * 是否已连接
     */
    boolean isConnected();

    /**
     * 是否发现服务
     */
    boolean isDiscoverService();

    /**
     * 是否自动连接
     *
     * @return 如果在连接时传入的是自动连接，自返回true，否则返回false
     */
    boolean autoConnect();

    /**
     * 连接状态，如果支持直接返回Null
     */
    ConnectState getConnectState();

    /**
     * 设置默认的 BluetoothGattService
     *
     * @param service BluetoothGattService 对象
     */
    void setReadGattService(BluetoothGattService service);

    /**
     * 获取默认的 BluetoothGattService
     */
    BluetoothGattService getReadGattService();

    /**
     * 设置读的 BluetoothGattCharacteristic
     *
     * @param characteristic BluetoothGattCharacteristic 对象
     */
    void setReadGattCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 获取读的 BluetoothGattCharacteristic
     */
    BluetoothGattCharacteristic getReadGattCharacteristic();

    /**
     * 设置写的 BluetoothGattService
     *
     * @param service BluetoothGattService 对象
     */
    void setWriteGattService(BluetoothGattService service);

    /**
     * 获取写的 BluetoothGattService
     */
    BluetoothGattService getWriteGattService();

    /**
     * 设置默认的 BluetoothGattCharacteristic
     *
     * @param characteristic BluetoothGattCharacteristic 对象
     */
    void setWriteGattCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 获取写的 BluetoothGattCharacteristic
     */
    BluetoothGattCharacteristic getWriteGattCharacteristic();

    /**
     * 设置读的 BluetoothGattService 和 BluetoothGattCharacteristic
     *
     * @param service        BluetoothGattService
     * @param characteristic BluetoothGattCharacteristic
     */
    default void setReadServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        // read
        setReadGattService(service);
        setReadGattCharacteristic(characteristic);
    }

    /**
     * 设置写的 BluetoothGattService 和 BluetoothGattCharacteristic
     *
     * @param service        BluetoothGattService
     * @param characteristic BluetoothGattCharacteristic
     */
    default void setWriteServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        // write
        setWriteGattService(service);
        setWriteGattCharacteristic(characteristic);
    }

    /**
     * 设置读和写的 BluetoothGattService 和 BluetoothGattCharacteristic
     *
     * @param service        BluetoothGattService
     * @param characteristic BluetoothGattCharacteristic
     */
    default void setDefaultServiceAndCharacteristic(BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        // read
        setReadGattService(service);
        setReadGattCharacteristic(characteristic);
        // write
        setWriteGattService(service);
        setWriteGattCharacteristic(characteristic);
    }

    /**
     * 往默认的 BluetoothGattCharacteristic 中写入数据
     *
     * @param value 数据
     */
    default boolean write(byte[] value) {
        if (value != null && value.length > 0) {
            return writeCharacteristic(getWriteGattCharacteristic(), value);
        }
        return false;
    }

    /**
     * 往默认的 BluetoothGattCharacteristic 中写入数据
     *
     * @param hex 16进制数据
     */
    default boolean write(String hex) {
        if (hex != null && hex.trim().length() > 0) {
            return writeCharacteristic(getWriteGattCharacteristic(), BluetoothTool.hexToByte(hex));
        }
        return false;
    }
}
