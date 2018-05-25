package com.dxa.android.ble.impl;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dxa.android.ble.ConnectState;
import com.dxa.android.ble.OnGattChangedListener;

/**
 * 带连接状态GattClient类
 */
public class SimpleStateGattClient extends SimpleGattClient {

    private final GattChangedListenerDelegate delegate;

    public SimpleStateGattClient() {
        this.delegate = new GattChangedListenerDelegate();
        super.setOnGattChangedListener(delegate);
    }

    @Override
    public boolean connect(Context context, String address, boolean autoConnect) {
        this.delegate.onConnect(autoConnect);
        return super.connect(context, address, autoConnect);
    }

    @Override
    public boolean connect(Context context, BluetoothDevice device, boolean autoConnect) {
        this.delegate.onConnect(autoConnect);
        return super.connect(context, device, autoConnect);
    }

    @Override
    public void disconnect() {
        this.delegate.setAutoConnect(false);
        super.disconnect();
    }

    @Override
    public void disconnect(boolean close) {
        this.delegate.setAutoConnect(false);
        super.disconnect(close);
    }

    @Override
    public void close() {
        this.delegate.setAutoConnect(false);
        super.close();
    }

    @Override
    public void setOnGattChangedListener(OnGattChangedListener listener) {
        this.delegate.setChangedListener(listener);
    }

    @NonNull
    @Override
    public ConnectState getConnectState() {
        return this.delegate.getState();
    }
}
