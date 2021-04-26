package com.dxa.android;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dxa.android.ble.impl.LogGattChangedListener;
import com.dxa.android.ble.impl.SimpleGattClient;
import com.dxa.android.ble.log.LoggerManager;
import com.dxa.android.ui.ActivityPresenter;
import com.dxa.android.ui.SuperActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SuperActivity {

  private SimpleGattClient client = new SimpleGattClient();

  private final LoggerManager logger = LoggerManager.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    logger.setTag("BLE");
    logger.setDebug(true);
    client.setOnGattChangedListener(new LogGattChangedListener());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    client.close();
  }

  @Nullable
  @Override
  protected ActivityPresenter buildPresenter() {
    return null;
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
      R.id.btn_close,
      R.id.btn_thermometer,
      R.id.btn_mring
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
      case R.id.btn_thermometer:
        startAct(ThermometerActivity.class);
        break;
      case R.id.btn_mring:
        startAct(MRingActivity.class);
        break;
    }
  }
}
