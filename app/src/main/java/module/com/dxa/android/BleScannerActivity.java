package com.dxa.android;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.benefit.base.ui.ActivityPresenter;
import com.benefit.base.ui.BenefitActivity;
import com.dxa.android.ble.BleScanner;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.yanzhenjie.permission.AndPermission;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BleScannerActivity extends BenefitActivity {

    @Nullable
    public static BluetoothDevice getDevice(Intent data) {
        if (data != null) {
            return data.getParcelableExtra(DEVICE);
        }
        return null;
    }

    public static final String DEVICE_NAME = "bleDeviceName";
    public static final String DEVICE_ADDRESS = "bleDeviceAddress";
    public static final String DEVICE = "bleDevice";

    @BindView(R.id.easy_recycler_view)
    EasyRecyclerView recyclerView;

    private RecyclerArrayAdapter<BluetoothDevice> adapter;

    private BleScanner scanner;
    private final Set<BluetoothDevice> deviceSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDebug(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        ButterKnife.bind(this);

        recyclerView.setRefreshingColor(getResources().getColor(R.color.colorPrimary));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加item之间的分割线
        DividerDecoration itemDecoration = new DividerDecoration(
                Color.TRANSPARENT, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        // 下拉刷新
        recyclerView.setRefreshListener(() -> {
            if (scanner.isScanning()){
                return;
            }
            recyclerView.setRefreshing(true);
            deviceSet.clear();
            // 清空之前扫描到的设备
            adapter.clear();
            // 扫描5秒,建议在子线程扫描，并且扫描之前应该先检查权限（这里我偷个懒）
            scanner.startScan(5000);
        });
        recyclerView.setAdapter(adapter = new RecyclerArrayAdapter<BluetoothDevice>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
                return new BleDeviceViewHolder(itemView);
            }
        });
        adapter.setOnItemClickListener(position -> {
            // 连接设备
            logger.i("连接设备: " + adapter.getItem(position).getAddress());

            BluetoothDevice device = adapter.getItem(position);
            Intent intent = new Intent();
            intent.putExtra(DEVICE, device);
            intent.putExtra(DEVICE_NAME, device.getName());
            intent.putExtra(DEVICE_ADDRESS, device.getAddress());
            setResult(RESULT_OK, intent);
            finish();
        });

        scanner = new BleScanner();
        scanner.setBLeScanListener(onScanListener);

        requestBlePermissions();


    }

    private void requestBlePermissions() {
        AndPermission.with(this)
                .runtime()
                .permission(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN)
                .onGranted(data -> scanner.startScan())
                .onDenied(data -> showAlertDialog())
                .start();
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("蓝牙扫描需要权限，请允许，谢谢!")
                .setNegativeButton("不给", (dialog, which) -> {
                    dialog.dismiss();
                    getActivity().finish();
                })
                .setPositiveButton("朕,准了", (dialog, which) -> requestBlePermissions())
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        scanner.stopScan();
        super.onDestroy();
    }


    private final BleScanner.Listener onScanListener = new BleScanner.Listener() {

        /**
         * 开始扫描
         */
        @Override
        public void onScanStart() {
            showToast("开始扫描设备");
            logger.d("开始扫描");
        }

        /**
         * 扫描到设备
         */
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // 主线程更新，防止部分手机添加没有更新UI
            deviceSet.add(device);
            adapter.clear();
            runOnUiThread(() -> adapter.addAll(deviceSet));
            logger.d(String.format("扫描到设备: %s ==>: %s", device.getName(), device.getAddress()));
        }

        /**
         * 扫描完成
         */
        @Override
        public void onScanCompleted() {
            logger.d("扫描结束");
            recyclerView.setRefreshing(false);
        }

        /**
         * 扫描被取消
         */
        @Override
        public void onScanCanceled() {
            logger.d("扫描被取消");
            recyclerView.setRefreshing(false);
        }

        /**
         * 扫描出错了
         *
         * @param e
         */
        @Override
        public void onScanError(Throwable e) {
            recyclerView.setRefreshing(false);
            e.printStackTrace();
        }
    };


    static class BleDeviceViewHolder extends BaseViewHolder<BluetoothDevice> {

        @BindView(R.id.tv_device)
        TextView tvDevice;

        public BleDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setData(BluetoothDevice device) {
            tvDevice.setText(String.format("名称: %s, mac: %s", device.getName(), device.getAddress()));
        }

    }
}
