package com.dxa.android;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.BluetoothLeScanner;
import com.dxa.android.ble.BluetoothTool;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.impl.DefaultGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private BluetoothRecyclerAdapter adapter;

    private BluetoothLeScanner scanner;
    private BluetoothGattClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new BluetoothRecyclerAdapter(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.addOnItemClickListener((parentView, device, position) -> {
            // 连接设备
            Log.i("TAG", "连接设备: " + device.getAddress());
            client.connect(getContext(), device, false);
        });

        scanner = new BluetoothLeScanner();
        scanner.setBLeScanListener(listener);

        // 蓝牙4.0连接的客户端
        client = new SimpleGattClient();
        // 设置状态改变的监听
        client.setOnGattChangedListener(changedListener);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("蓝牙扫描需要权限，请允许，谢谢!")
                .setNegativeButton("不给", (dialog, which) -> {
                    dialog.dismiss();
                    MainActivity.this.finish();
                })
                .setPositiveButton("朕,准了", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 666);
                    }
                })
                .show();

        // 可以替换成自己的日志实现
//        DLogger logger = new DLoggerImpl();
//        LoggerManager.getInstance().setLogger(logger);
    }

    private Context getContext() {
        return this;
    }

    @OnClick({
            R.id.btn_scan,
            R.id.btn_stop_scan,
            R.id.btn_disconnect,
            R.id.btn_send
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                // 扫描30秒
                scanner.startScan(30 * 1000);
                break;
            case R.id.btn_stop_scan:
                scanner.stopScan();
                break;
            case R.id.btn_disconnect:
                client.disconnect();
                break;
            case R.id.btn_send:
                onSendSomething();
                break;
        }
    }

    private void onSendSomething() {
        String cmd = etInput.getText().toString();
        if (TextUtils.isEmpty(cmd))
            return;

        BluetoothGattService service = client.getService(UUID_SERVICE);
        BluetoothGattCharacteristic characteristic =
                client.getCharacteristic(service, UUID_CHARACTERISTIC);

        // cmd 需要是16进制数据
        byte[] value = BluetoothTool.hexToBin(cmd);
        // 或者 value = cmd.getBytes();
        client.writeCharacteristic(characteristic, value);
    }


    private final BluetoothLeScanner.BLeScanListener listener = new BluetoothLeScanner.BLeScanListener() {
        /**
         * 开始扫描
         */
        @Override
        public void onScanStart() {
            Log.i("TAG", "开始扫描");
        }

        /**
         * 扫描到设备
         *
         * @param device
         * @param rssi
         * @param scanRecord
         */
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(() -> adapter.add(device));
        }

        /**
         * 扫描完成
         */
        @Override
        public void onScanCompleted() {
            Log.i("TAG", "扫描结束");
        }

        /**
         * 扫描被取消
         */
        @Override
        public void onScanCanceled() {
            Log.i("TAG", "扫描被取消");
        }

        /**
         * 扫描出错了
         *
         * @param e
         */
        @Override
        public void onScanError(Throwable e) {
            e.printStackTrace();
        }
    };


    private static final UUID UUID_SERVICE = UUID.fromString("GattService的UUID");
    private static final UUID UUID_CHARACTERISTIC = UUID.fromString("GattCharacteristic的UUID");

    private OnGattChangedListener changedListener = new DefaultGattChangedListener() {

        @Override
        public void onConnected(BluetoothGatt gatt) {
            Log.i("TAG", "连接设备");
        }

        @Override
        public boolean onServiceDiscover(BluetoothGatt gatt) {
            BluetoothGattService gattService = gatt.getService(UUID_SERVICE);
            BluetoothGattCharacteristic characteristic =
                    BluetoothTool.getCharacteristic(gattService, UUID_CHARACTERISTIC);

            // 判断是否查找到服务和特征

            Log.i("TAG", "查找到服务(BluetoothGattService)和特征(BluetoothGattCharacteristic)");
            return true;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i("TAG", "onCharacteristicRead");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic) {
            Log.i("TAG", "onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            String hex = BluetoothTool.binToHex(value);
            Log.i("TAG", "onCharacteristicChanged @ 数据发生改变: " + hex);
        }

        @Override
        public void onDisconnected(BluetoothGatt gatt) {
            Log.i("TAG", "连开连接");
        }
    };
}
