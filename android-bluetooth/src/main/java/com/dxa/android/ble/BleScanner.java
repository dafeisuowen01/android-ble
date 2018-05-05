package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;

import java.util.List;

/**
 * 蓝牙扫描类
 */
public final class BleScanner {

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    private final Handler handler = new Handler();
    private volatile Listener listener;
    private volatile boolean scanning = false;

    private BluetoothLeScanner leScanner;

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

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (listener != null && checkVersion()) {
                listener.onLeScan(result.getDevice(),
                        result.getRssi(), result.getScanRecord().getBytes());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (listener != null && checkVersion()) {
                for (ScanResult result : results) {
                    listener.onLeScan(result.getDevice(),
                            result.getRssi(), result.getScanRecord().getBytes());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (listener != null) {
                listener.onScanError(new BLeScanException());
            }
        }
    };

    private final Runnable stopScan = () -> {
        BluetoothAdapter adapter = getAdapter();
        if (isEnabled(adapter)) {
            scanStop(adapter);
        } else {
            scanError();
        }
    };

    public BleScanner() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            leScanner = getAdapter().getBluetoothLeScanner();
        }
    }

    public BleScanner(Listener listener) {
        this();
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

    private boolean checkVersion() {
        return leScanner != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private void scanStart(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = true;
            listener.onScanStart();

            if (checkVersion()) {
                leScanner.startScan(scanCallback);
            } else {
                adapter.startLeScan(leScanCallback);
            }
        }
    }

    private void scanStop(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = false;
            if (checkVersion()) {
                leScanner.stopScan(scanCallback);
            } else {
                adapter.stopLeScan(leScanCallback);
            }
            listener.onScanCompleted();
        }
    }

    private void scanCanceled(BluetoothAdapter adapter) {
        if (listener != null) {
            scanning = false;
            handler.removeCallbacks(stopScan);
            if (checkVersion()) {
                leScanner.stopScan(scanCallback);
            } else {
                adapter.stopLeScan(leScanCallback);
            }
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
        } else if (duration > 5 * MINUTE) {
            duration = 5 * MINUTE;
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
