package com.dxa.android;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.benefit.base.ui.BenefitActivity;
import com.dxa.android.ble.impl.LogGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;
import com.dxa.android.ble.log.LoggerManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BenefitActivity {

    private SimpleGattClient client;

    private final LoggerManager logger = LoggerManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        logger.setTag("BLE");
        logger.setDebug(true);

        client = new SimpleGattClient();
        client.setOnGattChangedListener(new LogGattChangedListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10022 && resultCode == RESULT_OK) {
            BluetoothDevice device = BleScannerActivity.getDevice(data);
            client.connect(this, device, true);
        }
    }

    @OnClick({
            R.id.btn_connect,
            R.id.btn_disconnect1,
            R.id.btn_disconnect2,
            R.id.btn_reconnect,
            R.id.btn_close
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                startActForResult(BleScannerActivity.class, 10022);
                break;
            case R.id.btn_disconnect1:
                client.disconnect();
                break;
            case R.id.btn_disconnect2:
                client.disconnect(true);
                break;
            case R.id.btn_reconnect:
                client.reconnect();
                break;
            case R.id.btn_close:
                client.close();
                break;
        }
    }
}
