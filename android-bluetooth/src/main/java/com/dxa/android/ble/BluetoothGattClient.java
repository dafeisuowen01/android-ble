package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.Nullable;

/**
 * 蓝牙4.0客户端
 */

public interface BluetoothGattClient extends GattOperator {

    /**
     * 获取 BluetoothAdapter
     */
    BluetoothAdapter getAdapter();

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
    boolean connect(Context context, BluetoothDevice device, boolean autoConnect);

    /**
     * 重新连接，如果之前连接过设备
     *
     * @return 是否重新连接(没有连接过设备调用会返回false)
     */
    boolean reconnect();

    /**
     * 获取BluetoothGatt对象
     *
     * @return 返回 BluetoothGatt 或 null
     */
    BluetoothGatt getBluetoothGatt();

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
     * 连接状态，如果支持直接返回Null
     */
    @Nullable
    ConnectState getConnectState();

    /**
     * 设置默认的 BluetoothGattService
     *
     * @param service BluetoothGattService 对象
     */
    void setGattService(BluetoothGattService service);

    /**
     * 获取默认的 BluetoothGattService
     */
    BluetoothGattService getGattService();

    /**
     * 设置默认的 BluetoothGattCharacteristic
     *
     * @param characteristic BluetoothGattCharacteristic 对象
     */
    void setGattCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 获取默认的 BluetoothGattCharacteristic
     */
    BluetoothGattCharacteristic getGattCharacteristic();

    /**
     * 往默认的 BluetoothGattCharacteristic 中写入数据
     *
     * @param value 数据
     */
    boolean write(byte[] value);

    /**
     * 往默认的 BluetoothGattCharacteristic 中写入数据
     *
     * @param hex 16进制数据
     */
    boolean write(String hex);
}
