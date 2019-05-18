package com.dxa.android.ble.service;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Binder;

import androidx.annotation.Nullable;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.impl.SimpleGattClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiGattBinder extends Binder {

    private final Map<String, BluetoothGattClient> clientMap = new ConcurrentHashMap<>();

    public MultiGattBinder() {
    }

    protected Map<String, BluetoothGattClient> getClientMap() {
        return clientMap;
    }

    /**
     * 获取设备的客户端
     *
     * @param device 设备
     * @return 返回对应的设备
     */
    public BluetoothGattClient getClient(String device) {
        return getClientMap().get(device);
    }

    /**
     * 连接设备
     *
     * @param context     上下文
     * @param device      设备
     * @param autoConnect 是否自动连接
     * @return 返回是否连接
     */
    public boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
        String address = device.getAddress();
        BluetoothGattClient client = getClient(address);
        if (client == null) {
            getClientMap().put(address, client = new SimpleGattClient());
            return client.connect(context, device, autoConnect);
        }
        return client.reconnect();
    }

    /**
     * 断开设备的连接
     *
     * @param device 设备
     * @return 返回断开连接的客户端
     */
    @Nullable
    public BluetoothGattClient disconnect(BluetoothDevice device) {
        return disconnect(device.getAddress());
    }

    /**
     * 断开设备的连接
     *
     * @param device 设备
     * @return 返回断开连接的客户端
     */
    @Nullable
    public BluetoothGattClient disconnect(String device) {
        return disconnect(device, false);
    }

    /**
     * 断开设备的连接
     *
     * @param device 设备
     * @param remove 是否移除客户端
     * @return 返回断开连接的客户端
     */
    @Nullable
    public BluetoothGattClient disconnect(String device, boolean remove) {
        BluetoothGattClient client;
        if (remove) {
            client = getClientMap().remove(device);
        } else {
            client = getClient(device);
        }
        if (client != null) {
            client.disconnect(true);
        }
        return client;
    }

}