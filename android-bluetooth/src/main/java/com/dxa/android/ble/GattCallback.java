package com.dxa.android.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import com.dxa.android.ble.impl.SimpleGattChangedListener;
import com.dxa.android.ble.log.LoggerManager;


/**
 * 默认的BluetoothGattCallback类
 */
public final class GattCallback extends BluetoothGattCallback {

    private final LoggerManager logger = LoggerManager.getInstance();
    /**
     * 默认的OnGattChangedListener
     */
    private final OnGattChangedListener defaultListener = new SimpleGattChangedListener();

    /**
     * 监听
     */
    private OnGattChangedListener mListener = defaultListener;
    /**
     * 是否连接
     */
    private boolean connected = false;
    /**
     * 是否发现服务
     */
    private boolean discoverService;

    public GattCallback() {
    }

    public void setOnGattChangedListener(OnGattChangedListener listener) {
        synchronized (this) {
            if (listener != null) {
                this.mListener = listener;
            } else {
                this.mListener = defaultListener;
            }
        }
    }


    public final OnGattChangedListener getListener() {
        if (mListener == null) {
            mListener = defaultListener;
        }
        return mListener;
    }

    /**
     * Callback triggered as result of {@link BluetoothGatt#setPreferredPhy}, or as a result of
     * remote device changing the PHY.
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY update operation.
     *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onPhyUpdate(gatt, txPhy, rxPhy);
        } else {
            logger.d("onPhyUpdate received: ", status);
        }
    }

    /**
     * Callback triggered as result of {@link BluetoothGatt#readPhy}
     *
     * @param gatt   GATT client
     * @param txPhy  the transmitter PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param rxPhy  the receiver PHY in use. One of {@link BluetoothDevice#PHY_LE_1M},
     *               {@link BluetoothDevice#PHY_LE_2M}, and {@link BluetoothDevice#PHY_LE_CODED}.
     * @param status Status of the PHY read operation.
     *               {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onPhyRead(gatt, txPhy, rxPhy);
        } else {
            logger.d("onPhyRead received: ", status);
        }
    }

    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     *
     * @param gatt     GATT client
     * @param status   Status of the connect or disconnect operation.
     *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of
     *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
     *                 {@link BluetoothProfile#STATE_CONNECTED}
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {//2
            logger.d("Connected to GATT server.");
            connected = true;
            getListener().onConnected(gatt);
            // Attempts to discover services after successful connection.
            boolean discoverServices = gatt.discoverServices();
            logger.d("Attempting to start service discovery: ", discoverServices);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//0
            logger.d("Disconnected from GATT server.");
            connected = false;
            getListener().onDisconnected(gatt, true);
        } else {
            logger.d("onConnectionStateChange status: ", status, ", newState: ", newState);
        }
    }

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
     *               has been explored successfully.
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            discoverService = getListener().onServiceDiscover(gatt);
        } else {
            discoverService = false;
            logger.w("onServicesDiscovered received: ", status);
        }
    }

    /**
     * Callback reporting the result of a characteristic read operation.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
     * @param characteristic Characteristic that was read from the associated
     *                       remote device.
     * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
     *                       was completed successfully.
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onCharacteristicRead(gatt, characteristic, status);
        } else {
            logger.d("onCharacteristicRead receive: ", status);
        }
    }

    /**
     * Callback indicating the result of a characteristic write operation.
     * <p>
     * <p>If this callback is invoked while a reliable write transaction is
     * in progress, the value of the characteristic represents the value
     * reported by the remote device. An application should compare this
     * value to the desired value to be written. If the values don't match,
     * the application must abort the reliable write transaction.
     *
     * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
     * @param characteristic Characteristic that was written to the associated remote device.
     * @param status         The result of the write operation {@link BluetoothGatt#GATT_SUCCESS}
     *                       if the operation succeeds.
     */
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onCharacteristicWrite(gatt, characteristic);
        } else {
            logger.d("onCharacteristicWrite receive: ", status);
        }
    }

    /**
     * Callback triggered as a result of a remote characteristic notification.
     * 远程设备特征值改变提醒的回调
     *
     * @param gatt           GATT client the characteristic is associated with
     * @param characteristic Characteristic that has been updated as a result
     *                       of a remote notification event.
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        getListener().onCharacteristicChanged(gatt, characteristic);
    }

    /**
     * Callback reporting the result of a descriptor read operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
     * @param descriptor Descriptor that was read from the associated remote device.
     * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation was completed successfully
     */
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onDescriptorRead(gatt, descriptor);
        } else {
            logger.d("onDescriptorRead receive: ", status);
        }
    }

    /**
     * Callback indicating the result of a descriptor write operation.
     *
     * @param gatt       GATT client invoked {@link BluetoothGatt#writeDescriptor}
     * @param descriptor Descriptor that was writte to the associated
     *                   remote device.
     * @param status     The result of the write operation
     *                   {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     */
    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onDescriptorWrite(gatt, descriptor);
        } else {
            logger.d("onDescriptorWrite receive: ", status);
        }
    }

    /**
     * Callback invoked when a reliable write transaction has been completed.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#executeReliableWrite}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the reliable write
     */
    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onReliableWriteCompleted(gatt);
        } else {
            logger.d("onReliableWriteCompleted receive: ", status);
        }
    }

    /**
     * Callback reporting the RSSI for a remote device connection.
     * <p>
     * This callback is triggered in response to the
     * {@link BluetoothGatt#readRemoteRssi} function.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#readRemoteRssi}
     * @param rssi   The RSSI value for the remote device
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
     */
    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onReadRemoteRssi(gatt, rssi);
        } else {
            logger.d("onReadRemoteRssi receive: ", status);
        }
    }

    /**
     * Callback indicating the MTU for a given device connection has changed.
     * <p>
     * This callback is triggered in response to the
     * {@link BluetoothGatt#requestMtu} function, or in response to a connection
     * event.
     *
     * @param gatt   GATT client invoked {@link BluetoothGatt#requestMtu}
     * @param mtu    The new MTU size
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the MTU has been changed successfully
     */
    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            getListener().onMtuChanged(gatt, mtu);
        } else {
            logger.d("onMtuChanged receive: ", status);
        }
    }

    /**
     * 是否已连接
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * 是否发现服务
     */
    public boolean isDiscoverService() {
        return discoverService;
    }

}
