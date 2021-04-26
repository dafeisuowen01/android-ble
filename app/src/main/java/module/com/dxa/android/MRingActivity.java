package com.dxa.android;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

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
import butterknife.OnClick;

public class MRingActivity extends SuperActivity {
  static {
    DLogger.setDebug(true);
  }

  @BindView(R.id.toolbar)
  Toolbar toolbar;
  @BindView(R.id.tv_token)
  TextView tvToken;
  @BindView(R.id.tv_cmd)
  TextView tvCmd;

  private String token;

  private BluetoothGattClient client = new SimpleGattClient();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setDebug(true);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mring);
    ButterKnife.bind(this);

    setSupportActionBar(toolbar);

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

  @Nullable
  @Override
  protected ActivityPresenter buildPresenter() {
    return null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_thermometer, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @SuppressLint("NonConstantResourceId")
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
      client.connect(this, address, false);
    }
  }

  @OnClick({
      R.id.btn_open,
      R.id.btn_close,
  })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_open:
        send("ED004000");
        break;
      case R.id.btn_close:
        send("ED004001");
        break;
    }
  }

  private void send(String cmd) {
    // 发送指令
    boolean write = client.write(cmd + token);
    logger.i("写入指令: " + cmd + ", write: " + write);
    tvCmd.setText("发送指令: " + cmd);
  }

//  private Runnable cmdTask = new Runnable() {
//    @Override
//    public void run() {
//      if (client.isDiscoverService()) {
//        // 发送指令
//        BluetoothGattCharacteristic characteristic = client.getCharacteristic(UUID_SERVICE, UUID_WRITE);
//        client.writeCharacteristic(characteristic, BluetoothTool.hexToByte("FFFE04872261"));
//        logger.d("发送指令");
//      }
//      getSyncHandler().removeCallbacks(cmdTask);
//      getSyncHandler().postDelayed(cmdTask, 1000);
//    }
//  };

  private static final UUID UUID_SERVICE = UUID.fromString("0000FAB1-0000-1000-8000-00805f9b34fb");
  private static final UUID UUID_READ = UUID.fromString("0000FAB6-0000-1000-8000-00805f9b34fb");
  private static final UUID UUID_WRITE = UUID.fromString("0000FAB2-0000-1000-8000-00805f9b34fb");
  private static final UUID UUID_NOTIFICATION = UUID.fromString("0000FAB5-0000-1000-8000-00805f9b34fb");


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
      client.setReadServiceAndCharacteristic(gattService
          , BluetoothTool.getCharacteristic(gattService, UUID_READ));

      client.setWriteServiceAndCharacteristic(gattService
          , BluetoothTool.getCharacteristic(gattService, UUID_WRITE));

      // 查找到服务后读取
      getAsyncHandler().postDelayed(() ->
          client.readCharacteristic(client.getReadGattCharacteristic(), true), 1000);

//      // 设置数据改变时提醒
//      gatt.setCharacteristicNotification(gattService.getCharacteristic(UUID_NOTIFICATION), true);

      logger.e("查找到服务(BluetoothGattService)和特征(BluetoothGattCharacteristic)");
      logger.w("\n\n--------------------------------------------\n\n");
      BluetoothTool.printGattInfo(gatt.getServices(), "蓝牙服务@TAG");
      logger.w("\n\n--------------------------------------------\n\n");

      // 判断是否查找到服务和特征
      return true;
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
      logger.i("onCharacteristicRead: " + BluetoothTool.byteToHex(characteristic.getValue()));

      String encrypt = RingUtils.encryptHex(characteristic.getValue(), "71696e6b657368695f6875616e67687a");
      token = encrypt.substring(0, 32);
      logger.e("加密: " + token);

      getSyncHandler().post(() -> tvToken.setText(token));
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic) {
      logger.i("onCharacteristicWrite: " + BluetoothTool.byteToHex(characteristic.getValue()));
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
      byte[] value = characteristic.getValue();
      logger.i("onCharacteristicChanged @ 数据发生改变: " + BluetoothTool.byteToHex(value));

//      if (value != null && (value[0] == ((byte) 0xAA))) {
//        float thermometer = (Math.round(BluetoothTool.byteToInt(value[2], value[3]) / 10.0f) / 10.0f);
//        logger.e("体温数据: ", BluetoothTool.byteToInt(value[2], value[3]), ", 计算后的体温数据: ", thermometer);
//        runUiThread(() -> tvThermometer.setText(String.valueOf(thermometer)));
//        logger.i("体温: ", thermometer);
//        switch (value[1]) { // 类型
//          case 0x55: // 物温模式
//            break;
//          case 0x22: // 耳温模式
//            break;
//          case 0x33: // 额温模式
//            break;
//        }
//      }
    }

    @Override
    public void onDisconnected(BluetoothGatt gatt, boolean auto) {
      logger.i("连接断开");
    }
  };

}
