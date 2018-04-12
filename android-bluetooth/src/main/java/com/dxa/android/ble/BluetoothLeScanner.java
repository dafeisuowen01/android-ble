package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/**
 * 蓝牙扫描类
 */
public final class BluetoothLeScanner {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    private final Handler handler = new Handler();
    private volatile Listener listener;
    private volatile boolean scanning = false;

    private final BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device,
                                     int rssi, byte[] scanRecord) {
                    if (listener != null) {
                        listener.onLeScan(device, rssi, scanRecord);
                    }
                }
            };

    private final Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            BluetoothAdapter adapter = getAdapter();
            if (isEnabled(adapter)) {
                scanStop(adapter);
            } else {
                scanError();
            }
        }
    };

    public BluetoothLeScanner() {
    }

    public BluetoothLeScanner(Listener listener) {
        this.listener = listener;
    }

    public void setBLeScanListener(Listener listener) {
        this.listener = listener;
    }

    private BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private boolean isEnabled(BluetoothAdapter adapter) {
        return adapter != null && adapter.isEnabled();
    }

    private void scanStart(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = true;
            listener.onScanStart();
            adapter.startLeScan(leScanCallback);
        }
    }

    private void scanStop(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = false;
            adapter.stopLeScan(leScanCallback);
            listener.onScanCompleted();
        }
    }

    private void scanCanceled(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = false;
            handler.removeCallbacks(stopScan);
            adapter.stopLeScan(leScanCallback);
            listener.onScanCanceled();
        }
    }

    private void scanError() {
        if (listener != null) {
            handler.removeCallbacks(stopScan);
            listener.onScanError(new BLeScanException());
            scanning = false;
        }
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        if (scanning)
            return;

        BluetoothAdapter adapter = getAdapter();
        if (isEnabled(adapter)) {
            scanStart(adapter);
        } else {
            scanError();
        }
    }

    /**
     * 扫描蓝牙
     *
     * @param duration 执行时长
     */
    public void startScan(long duration) {
        if (scanning)
            return;

        if (duration < 1000) {
            duration = SECOND;
        } else if (duration > HOUR) {
            duration = MINUTE;
        }

        startScan();
        handler.postDelayed(stopScan, duration);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (scanning) {
            BluetoothAdapter adapter = getAdapter();
            if (isEnabled(adapter)) {
                scanCanceled(adapter);
            }
        }
    }

    /**
     * 是否正在扫描
     */
    public boolean isScanning() {
        return scanning;
    }

    /**
     * 扫描监听
     */
    public interface Listener {
        /**
         * 开始扫描
         */
        void onScanStart();

        /**
         * 扫描到设备
         */
        void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);

        /**
         * 扫描完成
         */
        void onScanCompleted();

        /**
         * 扫描被取消
         */
        void onScanCanceled();

        /**
         * 扫描出错了
         */
        void onScanError(Throwable e);
    }

    public static class BLeScanException extends Exception {
        public BLeScanException() {
            super("蓝牙不可用!");
        }
    }
}