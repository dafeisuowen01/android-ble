package com.dxa.android.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 蓝牙广播（蓝牙开启关闭的状态、蓝牙扫描的状态）
 */
public class BluetoothStateReceiver extends BroadcastReceiver {


    /**
     * 注册广播
     *
     * @param context 上下文
     * @return 返回BluetoothStateReceiver
     */
    public static BluetoothStateReceiver register(Context context) {
        return register(context, (StateChangedListener) null);
    }


    /**
     * 注册广播
     *
     * @param context   上下文
     * @param listeners 监听状态的实现
     * @return 返回BluetoothStateReceiver
     */
    public static BluetoothStateReceiver register(Context context, StateChangedListener... listeners) {
        IntentFilter intentFilter = makeFilter();
        BluetoothStateReceiver receiver = new BluetoothStateReceiver();
        if (listeners != null && listeners.length > 0) {
            for (StateChangedListener l : listeners) {
                receiver.register(l);
            }
        }
        context.registerReceiver(receiver, intentFilter);
        return receiver;
    }

    /**
     * 取消注册广播
     *
     * @param context  上下文
     * @param receiver 广播
     */
    public static void unregister(Context context, BluetoothStateReceiver receiver) {
        context.unregisterReceiver(receiver);
    }


    private final List<StateChangedListener> listeners = new CopyOnWriteArrayList<>();

    public BluetoothStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            // 蓝牙状态改变
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_TURNING_ON) {
                listener.onStateTurnOn();
            } else if (state == BluetoothAdapter.STATE_OFF) {
                listener.onStateOff();
            } else if (state == BluetoothAdapter.STATE_ON) {
                listener.onStateOn();
            } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                listener.onStateTurnOff();
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            // 开始扫描
            listener.onDiscoveryStarted();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            // 扫描结束
            listener.onDiscoveryFinished();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // 扫描到设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothClass clazz = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
            listener.onFoundDevice(device, clazz);
        }
    }


    /**
     * 注册监听
     *
     * @param listener 监听
     */
    public void register(StateChangedListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 取消注册监听
     *
     * @param listener 监听
     * @return 是否取消
     */
    public boolean unregister(StateChangedListener listener) {
        return listeners.remove(listener);
    }

    private final StateChangedListener listener = new StateChangedListener() {
        @Override
        public void onStateTurnOn() {
            for (StateChangedListener l : listeners) {
                l.onStateTurnOn();
            }
        }

        @Override
        public void onStateOn() {
            for (StateChangedListener l : listeners) {
                l.onStateOn();
            }
        }

        @Override
        public void onStateTurnOff() {
            for (StateChangedListener l : listeners) {
                l.onStateTurnOff();
            }
        }

        @Override
        public void onStateOff() {
            for (StateChangedListener l : listeners) {
                l.onStateOff();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            for (StateChangedListener l : listeners) {
                l.onDiscoveryStarted();
            }
        }

        @Override
        public void onFoundDevice(BluetoothDevice device, BluetoothClass clazz) {
            for (StateChangedListener l : listeners) {
                l.onFoundDevice(device, clazz);
            }
        }

        @Override
        public void onDiscoveryFinished() {
            for (StateChangedListener l : listeners) {
                l.onDiscoveryFinished();
            }
        }
    };

    /**
     * 过滤器：蓝牙状态改变、开始扫描，扫描到设备
     */
    public static IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        // 蓝牙状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 开始扫描
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        // 发现设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        // 扫描结束
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return filter;
    }

    /**
     * 蓝牙状态改变的监听
     */
    public interface StateChangedListener {
        /**
         * 蓝牙被打开
         */
        default void onStateTurnOn() {
            // ~
        }

        /**
         * 蓝牙已开启
         */
        default void onStateOn() {
            // ~
        }

        /**
         * 蓝牙关闭
         */
        default void onStateTurnOff() {
            // ~
        }

        /**
         * 蓝牙已关闭
         */
        default void onStateOff() {
            // ~
        }

        /**
         * 开始扫描
         */
        default void onDiscoveryStarted() {
            // ~
        }

        /**
         * 发现设备时
         *
         * @param device 设备
         * @param clazz  设备的class
         */
        default void onFoundDevice(BluetoothDevice device, BluetoothClass clazz) {
            // ~
        }

        /**
         * 扫描结束
         */
        default void onDiscoveryFinished() {
            // ~
        }
    }
}
