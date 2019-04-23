package com.dxa.android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.BluetoothTool;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;
import com.dxa.android.logger.DLogger;
import com.dxa.android.ui.ActivityPresenter;
import com.dxa.android.ui.SuperActivity;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThermometerActivity extends SuperActivity {
    static {
        DLogger.setDebug(true);
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_thermometer)
    TextView tvThermometer;

    private BluetoothGattClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDebug(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // 蓝牙4.0连接的客户端
        client = new SimpleGattClient();
        // 设置状态改变的监听
        client.setOnGattChangedListener(changedListener);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }

        // 可以替换成自己的日志实现
//        DLogger logger = new MyLoggerImpl();
//        LoggerManager.getInstance().setLogger(logger);
    }

    @Override
    protected void onDestroy() {
        client.disconnect(true);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thermometer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_ble_connected:
                startActForResult(BleScannerActivity.class, 1021);
                break;
            case R.id.action_ble_reconnect:
                client.reconnect();
                break;
            case R.id.action_ble_disconnected:
                client.disconnect(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    protected ActivityPresenter buildPresenter() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String address = data.getStringExtra(BleScannerActivity.DEVICE_ADDRESS);
            if (TextUtils.isEmpty(address)) {
                return;
            }
            logger.e("连接到设备: " + address);
            client.disconnect(true);
            client.connect(this, address, true);
        }
    }

    private Runnable cmdTask = new Runnable() {
        @Override
        public void run() {
            if (client.isDiscoverService()){
                // 发送指令
                BluetoothGattCharacteristic characteristic = client.getCharacteristic(UUID_SERVICE, UUID_CHARACTERISTIC_WRITE);
                client.writeCharacteristic(characteristic, BluetoothTool.hexToBin("FFFE04872261"));
                logger.d("发送指令");
            }
            getSyncHandler().removeCallbacks(cmdTask);
            getSyncHandler().postDelayed(cmdTask, 1000);
        }
    };

    private static final UUID UUID_SERVICE = UUID.fromString("cdeacb80-5235-4c07-8846-93a37ee6b86d");
    private static final UUID UUID_CHARACTERISTIC_READ = UUID.fromString("cdeacb81-5235-4c07-8846-93a37ee6b86d");
    private static final UUID UUID_CHARACTERISTIC_WRITE = UUID.fromString("6c1cef07-3377-410e-b231-47f76c5a39e1");


    // 如果不想全部实现，可以继承自DefaultGattChangedListener，只复写部分方法
    private final OnGattChangedListener changedListener = new SimpleGattChangedListener() {

        @Override
        public void onConnected(BluetoothGatt gatt) {
            logger.i("连接设备");
        }

        @Override
        public boolean onServiceDiscover(BluetoothGatt gatt) {
            // 搜索Service和Characteristic
            BluetoothGattService gattService = gatt.getService(UUID_SERVICE);
            BluetoothGattCharacteristic gattCharacteristic = BluetoothTool.getCharacteristic(gattService, UUID_CHARACTERISTIC_READ);
            if (gattCharacteristic != null) {
                // 设置数据改变时提醒
                gatt.setCharacteristicNotification(gattCharacteristic, true);

                client.setGattService(gattService);
                client.setGattCharacteristic(gattCharacteristic);
                // 发送指令
                getSyncHandler().post(cmdTask);

                logger.e("查找到服务(BluetoothGattService)和特征(BluetoothGattCharacteristic)");
//            } else {
                logger.w("\n\n--------------------------------------------\n\n");
                BluetoothTool.printGattInfo(gatt.getServices(), "蓝牙服务@TAG");
                logger.w("\n\n--------------------------------------------\n\n");
            }

            // 判断是否查找到服务和特征
            return gattService != null && gattCharacteristic != null;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            logger.i("onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            logger.i("onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            logger.i("onCharacteristicChanged @ 数据发生改变: " + BluetoothTool.binToHex(value));

            if (value != null && (value[0] == ((byte) 0xAA))) {
                float thermometer = (Math.round(BluetoothTool.byteToInt(value[2], value[3]) / 10.0f) / 10.0f);
                logger.e("体温数据: ", BluetoothTool.byteToInt(value[2], value[3]), ", 计算后的体温数据: ", thermometer);
                runUiThread(() -> tvThermometer.setText(String.valueOf(thermometer)));
                logger.i("体温: ", thermometer);
                switch (value[1]) { // 类型
                    case 0x55: // 物温模式
                        break;
                    case 0x22: // 耳温模式
                        break;
                    case 0x33: // 额温模式
                        break;
                }
            }
        }

        @Override
        public void onDisconnected(BluetoothGatt gatt) {
            logger.i("连接断开");
        }
    };
}
