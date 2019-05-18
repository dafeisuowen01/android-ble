package com.dxa.android.ble.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Binder;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.IDefaultBluetoothGattClient;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SingleGattBinder extends Binder implements IDefaultBluetoothGattClient {

    private final BluetoothGattClient client = new SimpleGattClient();
    /**
     * 监听
     */
    private final List<OnGattChangedListener> listeners = new CopyOnWriteArrayList<>();

    public SingleGattBinder() {
        client.setOnGattChangedListener(listener);
    }

    @Override
    public BluetoothGattClient getGattClient() {
        return client;
    }

    /**
     * 已过时，请调用 {@link #register(OnGattChangedListener)} 和 {@link #unregister(OnGattChangedListener)}
     *
     * @param listener 监听
     * @throws IllegalStateException 如果调用就会抛出此异常
     */
    @Deprecated
    @Override
    public void setOnGattChangedListener(OnGattChangedListener listener) throws IllegalStateException {
        throw new IllegalStateException("Forbidden: please call register(listener) and unregister(listener) method.");
    }

    /**
     * 注册监听
     *
     * @param listener 监听
     * @return 是否注册成功
     */
    public boolean register(OnGattChangedListener listener) {
        boolean isAdded = (listener != null && !listeners.contains(listener));
        if (isAdded) {
            listeners.add(listener);
        }
        return isAdded;
    }

    /**
     * 取消注册
     *
     * @param listener 监听
     * @return 是否注册成功
     */
    public boolean unregister(OnGattChangedListener listener) {
        return (!listeners.contains(listener)) || listeners.remove(listener);
    }

    private final OnGattChangedListener listener = new OnGattChangedListener() {
        @Override
        public void onConnected(BluetoothGatt gatt) {
            for (OnGattChangedListener l : listeners) {
                l.onConnected(gatt);
            }
        }

        @Override
        public boolean onServiceDiscover(BluetoothGatt gatt) {
            for (OnGattChangedListener l : listeners) {
                l.onServiceDiscover(gatt);
            }
            return true;
        }

        @Override
        public void onDisconnected(BluetoothGatt gatt, boolean auto) {
            for (OnGattChangedListener l : listeners) {
                l.onDisconnected(gatt, auto);
            }
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy) {
            for (OnGattChangedListener l : listeners) {
                l.onPhyRead(gatt, txPhy, rxPhy);
            }
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy) {
            for (OnGattChangedListener l : listeners) {
                l.onPhyUpdate(gatt, txPhy, rxPhy);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            for (OnGattChangedListener l : listeners) {
                l.onCharacteristicRead(gatt, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            for (OnGattChangedListener l : listeners) {
                l.onCharacteristicWrite(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            for (OnGattChangedListener l : listeners) {
                l.onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
            for (OnGattChangedListener l : listeners) {
                l.onDescriptorRead(gatt, descriptor);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
            for (OnGattChangedListener l : listeners) {
                l.onDescriptorWrite(gatt, descriptor);
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt) {
            for (OnGattChangedListener l : listeners) {
                l.onReliableWriteCompleted(gatt);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi) {
            for (OnGattChangedListener l : listeners) {
                l.onReadRemoteRssi(gatt, rssi);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu) {
            for (OnGattChangedListener l : listeners) {
                l.onMtuChanged(gatt, mtu);
            }
        }
    };
}