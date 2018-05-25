package com.dxa.android.ble.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.dxa.android.ble.ConnectState;
import com.dxa.android.ble.OnGattChangedListener;

/**
 * 状态监听的代理
 *
 * @author DINGXIUAN
 */
public class GattChangedListenerDelegate implements OnGattChangedListener {

    private volatile OnGattChangedListener changedListener;
    /**
     * 蓝牙连接的状态
     */
    private volatile ConnectState state = ConnectState.DISCONNECTED;
    private volatile boolean autoConnect = false;

    public GattChangedListenerDelegate() {
    }

    public GattChangedListenerDelegate(OnGattChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public void onConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
        this.state = ConnectState.CONNECTING;
    }

    public OnGattChangedListener getChangedListener() {
        return changedListener;
    }

    public void setChangedListener(OnGattChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public ConnectState getState() {
        return state;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    @Override
    public void onConnected(BluetoothGatt gatt) {
        this.state = ConnectState.CONNECTED;
        if (changedListener != null) {
            this.changedListener.onConnected(gatt);
        }
    }

    @Override
    public boolean onServiceDiscover(BluetoothGatt gatt) {
        this.state = ConnectState.SERVICE_DISCOVER;
        return changedListener != null && changedListener.onServiceDiscover(gatt);
    }

    @Override
    public void onDisconnected(BluetoothGatt gatt) {
        // 如果还自动重连，则会
        if (autoConnect) {
            this.state = ConnectState.AUTO_CONNECT;
        } else {
            this.state = ConnectState.DISCONNECTED;
        }
        if (changedListener != null) {
            this.changedListener.onDisconnected(gatt);
        }
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy) {
        if (changedListener != null) {
            changedListener.onPhyRead(gatt, txPhy, rxPhy);
        }
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy) {
        if (changedListener != null) {
            changedListener.onPhyUpdate(gatt, txPhy, rxPhy);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (changedListener != null) {
            changedListener.onCharacteristicRead(gatt, characteristic, status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (changedListener != null) {
            changedListener.onCharacteristicWrite(gatt, characteristic);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (changedListener != null) {
            changedListener.onCharacteristicChanged(gatt, characteristic);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
        if (changedListener != null) {
            changedListener.onDescriptorRead(gatt, descriptor);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
        if (changedListener != null) {
            changedListener.onDescriptorWrite(gatt, descriptor);
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt) {
        if (changedListener != null) {
            changedListener.onReliableWriteCompleted(gatt);
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi) {
        if (changedListener != null) {
            changedListener.onReadRemoteRssi(gatt, rssi);
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu) {
        if (changedListener != null) {
            changedListener.onMtuChanged(gatt, mtu);
        }
    }
}
