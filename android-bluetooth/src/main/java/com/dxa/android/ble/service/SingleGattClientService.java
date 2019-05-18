package com.dxa.android.ble.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * BLE蓝牙服务：单设备
 */
public class SingleGattClientService extends Service {

    private final SingleGattBinder binder = new SingleGattBinder();

    public SingleGattClientService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
