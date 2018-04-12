package com.dxa.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dxa.android.ble.BluetoothGattClient;
import com.dxa.android.ble.BluetoothLeScanner;
import com.dxa.android.ble.BluetoothTool;
import com.dxa.android.ble.OnGattChangedListener;
import com.dxa.android.ble.impl.DefaultGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        /*implements EasyPermissions.PermissionCallbacks */ {


    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private BluetoothRecyclerAdapter adapter;

    private BluetoothLeScanner scanner;
    private BluetoothGattClient client;

    private AlertDialog requestPermissionDialog;

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
            client.connect(getActivity(), device, false);
        });

        scanner = new BluetoothLeScanner();
        scanner.setBLeScanListener(onScanListener);

        // 蓝牙4.0连接的客户端
        client = new SimpleGattClient();
        // 设置状态改变的监听
        client.setOnGattChangedListener(changedListener);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        if (!hasLocationAndBluetoothPermissions()) {
            // 做的比较简陋，需要更完善的权限请求逻辑
            if (requestPermissionDialog == null) {
                requestPermissionDialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("蓝牙扫描需要权限，请允许，谢谢!")
                        .setNegativeButton("不给", (dialog, which) -> {
                            dialog.dismiss();
                            getActivity().finish();
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
                        .create();
            }
            requestPermissionDialog.show();
        }

        // 可以替换成自己的日志实现
//        DLogger logger = new MyLoggerImpl();
//        LoggerManager.getInstance().setLogger(logger);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestPermissionDialog != null) {
            requestPermissionDialog.dismiss();
        }
        handler.getLooper().quitSafely();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private Activity getActivity() {
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
                // 清空之前扫描到的设备
                adapter.clear();
                // 扫描20秒,建议在子线程扫描，并且扫描之前应该先检查权限（这里我偷个懒）
                scanner.startScan(20 * 1000);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void onSendSomething() {
        String cmd = etInput.getText().toString();
        if (TextUtils.isEmpty(cmd)) {
            showToast("需要输入发送的数据");
            return;
        }

        // 使用默认的Characteristic
        BluetoothGattCharacteristic characteristic = gattCharacteristic;

        // 获取BluetoothGattCharacteristic的方式
//        BluetoothGattService service = client.getService(UUID_SERVICE);
//        characteristic = client.getCharacteristic(service, UUID_CHARACTERISTIC);

        // 获取BluetoothGattCharacteristic也可以使用下面的方式
//        BluetoothGatt gatt = client.getBluetoothGatt();
//        characteristic = BluetoothTool.getCharacteristic(gatt, UUID_SERVICE, UUID_CHARACTERISTIC);

        // cmd 需要是16进制数据
        byte[] value = BluetoothTool.hexToBin(cmd);
        // 如果想发送其他非16进制内容，可以使用：value = cmd.getBytes();
        client.writeCharacteristic(characteristic, value);
    }

    public static final int WHAT_ENABLE = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ENABLE:
                    // 设置是否
                    BluetoothGatt gatt = client.getBluetoothGatt();
//                    gatt.setCharacteristicNotification(gattCharacteristic, true);
                    // 或者使用此方法直接
                    BluetoothTool.notification(gatt, gattService, gattCharacteristic);
                    break;
            }
        }
    };

    private final BluetoothLeScanner.Listener onScanListener = new BluetoothLeScanner.Listener() {
        /**
         * 开始扫描
         */
        @Override
        public void onScanStart() {
            showToast("开始扫描设备");
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
            // 主线程更新，防止部分手机添加没有更新UI
            runOnUiThread(() -> adapter.add(device));
            Log.i("TAG", String.format("扫描到设备: %s ==>: %s", device.getName(), device.getAddress()));
        }

        /**
         * 扫描完成
         */
        @Override
        public void onScanCompleted() {
            showToast("扫描完成");
            Log.i("TAG", "扫描结束");
        }

        /**
         * 扫描被取消
         */
        @Override
        public void onScanCanceled() {
            showToast("取消扫描设备");
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


//    private static final UUID UUID_SERVICE = UUID.fromString("GattService的UUID");
//    private static final UUID UUID_CHARACTERISTIC = UUID.fromString("GattCharacteristic的UUID");

    // 如果Service和Characteristic不变的话，可以将其缓存起来
    private BluetoothGattService gattService;
    private BluetoothGattCharacteristic gattCharacteristic;

    // 如果不想全部实现，可以继承自DefaultGattChangedListener，只复写部分方法
    private final OnGattChangedListener changedListener = new DefaultGattChangedListener() {

        @Override
        public void onConnected(BluetoothGatt gatt) {
            Log.i("TAG", "连接设备");
        }

        @Override
        public boolean onServiceDiscover(BluetoothGatt gatt) {
            // 搜索Service和Characteristic
//            gattService = gatt.getService(UUID_SERVICE);
//            gattCharacteristic =
//                    BluetoothTool.getCharacteristic(gattService, UUID_CHARACTERISTIC);

            Log.i("TAG", "查找到服务(BluetoothGattService)和特征(BluetoothGattCharacteristic)");

            BluetoothTool.printGattInfo(gatt.getServices(), "蓝牙服务@TAG");

            // 设置数据改变时提醒
//            handler.sendEmptyMessage(WHAT_ENABLE);

            // 判断是否查找到服务和特征
//            return gattService != null && gattCharacteristic != null

            // 返回true，表示查找到Service,否则连接会被主动断开
            // 断开设备的代码是我主动加的，因为没有找到对应UUID的Service，
            // 也就无法通信，所以不应该维持连接
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

    /***********************************************************************************/

    private static final String[] LOCATION_AND_BLUETOOTH = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    private boolean hasLocationAndBluetoothPermissions() {
        return EasyPermissions.hasPermissions(this, LOCATION_AND_BLUETOOTH);
    }


}
