# Android 蓝牙4.0的Demo

通过接口BluetoothGattClient定义蓝牙的操作：

 - 连接设备
 - 重新连接
 - 获取BluetoothGatt对象
 - 断开连接
 - 当前连接的设备
 - 是否已连接/发现服务
 - 等...操作
 
 提供了默认的实现类SimpleGattClient，可直接创建SimpleGattClient对象来管理蓝牙4.0设备的连接和状态的监听、数据的传输；
 
 提供了OnGattChangedListener接口，监听连接的状态，如连接成功、连接断开、发现服务、接收到数据等；有默认实现DefaultGattChangedListener，如不想实现全部的OnGattChangedListener方法，可直接继承自DefaultGattChangedListener；

关于更加具体的使用，可以看https://github.com/dingxiuan/android-ble/blob/master/app/src/main/java/module/com/dxa/android/MainActivity.java
