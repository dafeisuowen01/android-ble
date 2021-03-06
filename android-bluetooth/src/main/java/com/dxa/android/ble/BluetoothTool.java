package com.dxa.android.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙4.0的工具类
 */
public class BluetoothTool {

    private static final String UNKNOWN = "UNKNOWN";
    private static final String EMPTY = "";
    private static final String NULL = null;

    /********************************************************/
    /**
     * 16进制和2进制转换
     */

    private static final String HEX_UPPER_CASE = "0123456789ABCDEF";
    private static final String HEX_LOWER_CASE = "0123456789abcdef";

    private static final String[] _BINARY = {
            "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"
    };

    /**
     * 二进制转换成二进制字符串
     *
     * @param bin 二进制字节数组
     * @return 返回二进制字符串
     */
    public static String binToBin(byte[] bin) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bin) {
            //高四位
            builder.append(_BINARY[(b & 0xF0) >> 4]);
            //低四位
            builder.append(_BINARY[b & 0x0F]);
        }
        return builder.toString();
    }

    /**
     * 二进制转换成16进制字符串
     *
     * @param bin 二进制字节数组
     * @return 返回16进制字符串或空
     */
    public static String byteToHex(byte[] bin) {
        return byteToHex(bin, false);
    }

    /**
     * 二进制转换成16进制字符串
     *
     * @param bin       二进制字节数组
     * @param lowerCase 是否为小写字母
     * @return 返回16进制字符串或空
     */
    public static String byteToHex(byte[] bin, boolean lowerCase) {
        if (isEmpty(bin)) {
            return NULL;
        }

        String hex = lowerCase ? HEX_LOWER_CASE : HEX_UPPER_CASE;
        StringBuilder builder = new StringBuilder(bin.length * 2);
        for (byte b : bin) {
            //字节高4位
            builder.append(hex.charAt((b & 0xF0) >> 4));
            //字节低4位
            builder.append(hex.charAt(b & 0x0F));
        }
        return builder.toString();
    }

    /**
     * 16进制字符串转换成字节数组
     *
     * @param hex 字符串
     * @return 转换的字节数组
     */
    public static byte[] hexToByte(String hex) {
        return hexToByte(hex, null);
    }

    /**
     * 16进制字符串转换成字节数组
     *
     * @param hex          字符串
     * @param defaultValue 默认值
     * @return 转换的字节数组
     */
    public static byte[] hexToByte(String hex, byte[] defaultValue) {
        if (isNotEmpty(hex)) {
            int length = hex.length() / 2;
            char[] ch = hex.toUpperCase().toCharArray();
            byte[] bin = new byte[length];

            char high, low;
            for (int i = 0; i < length; ++i) {
                high = ch[i * 2];
                low = ch[i * 2 + 1];
                bin[i] = (byte) (charToByte(high) << 4 | charToByte(low));
            }
            return bin;
        }
        return defaultValue;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将字节数组转化成10进制整数
     *
     * @param high 高位
     * @param low  低位
     * @return 返回一个转换后的10进制整数
     */
    public static int byteToInt(byte high, byte low) {
        int value = low & 0xFF;
        value |= ((high << 8) & 0xFF00);
        return value;
    }


    /**
     * 整形数值转换成字节数组
     *
     * @param num 整形数值
     * @param bit 位，根据位取几个字节
     * @return 返回转换后的字节数组
     */
    public static byte[] numberToBin(long num, int bit) {
        int size = bit / 8;
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            b[i] = (byte) (num >> ((bit - 8) - i * 8));
        }
        return b;
    }

    public static byte numberForBit(long num, int bit) {
        return (byte) (num >> (bit * 8));
    }

    /**
     * 转换成整数
     *
     * @param num 数值
     * @return 返回一个整数
     */
    public static byte[] shortToByte(short num) {
        return numberToBin(num, 16);
    }

    /**
     * 转换成整数
     *
     * @param num 数值
     * @return 返回一个整数
     */
    public static byte[] shortToByte(int num) {
        return numberToBin(num, 16);
    }

    /**
     * 转换成整数
     *
     * @param num 数值
     * @return 返回一个整数
     */
    public static byte[] intToByte(int num) {
        return numberToBin(num, 32);
    }

    /**
     * 转换成整数
     *
     * @param num 数值
     * @return 返回一个整数
     */
    public static byte[] longToByte(long num) {
        return numberToBin(num, 64);
    }


    /**
     * 整形转换成16进制
     *
     * @param num 数值
     * @return 返回16进制字符串
     */
    public static String intToHex(int num) {
        String hex = Integer.toHexString(num);
        return (hex.length() & 0x01) != 0 ? "0" + hex : hex;
    }

    /**
     * 整形转换成16进制
     *
     * @param num 数值
     * @return 返回16进制字符串
     */
    public static byte[] intToByte2(int num) {
        return hexToByte(intToHex(num));
    }

    /**
     * 转换成整数
     *
     * @param num 数值
     * @return 返回一个整数
     */
    public static byte[] unsignedIntToByte(int num) {
        int size;
        if ((num >> 24) > 0) {
            size = 4;
        } else if ((num >> 16) > 0) {
            size = 3;
        } else if ((num >> 8) > 0) {
            size = 2;
        } else {
            size = 1;
        }
        byte[] b = new byte[size];
        for (int i = size; i > 0; i--) {
            b[i] = (byte) (num >> (24 - i * 8));
        }
        return b;
    }

//    /**
//     * 将字节数组转化成10进制整数
//     *
//     * @param bytes 字节数组
//     * @return 返回一个转换后的10进制整数
//     */
//    public static long byteToLong(byte... bytes) {
//        long value = 0;
//        for (byte b : bytes) {
//            value <<= bytes.length;
//            value |= (b & 0xff);
//        }
//        return value;
//    }


    /**
     * 字节数组转换成长整数
     *
     * @param bytes 字节数组
     * @return 返回长整数值
     */
    public static long byteToLong(byte... bytes) {
        long value = 0;
        for (byte b : bytes) {
            value <<= 8;
            value |= b & 0xff;
        }
        return value;
    }

    /**
     * 字节数组转换成整数
     *
     * @param bytes 字节数组
     * @return 返回整数值
     */
    public static int byteToInt(byte... bytes) {
        return (int) byteToLong(bytes);
    }

    /**
     * 转换成整数
     *
     * @param b 字节
     * @return 返回一个整数
     */
    public static short byteToShort(byte b) {
        return (short) ((b & 0xFF) * 256 + (b & 0xFF));
    }

    /**
     * 取低字节
     */
    public static int byteToIntLow(byte b) {
        return (b & 0xFF);
    }

    /**
     * 取高字节
     */
    public static int byteToIntHigh(byte b) {
        return (b & 0xFF) * 256;
    }


    /******************************************************************/

    @SuppressLint("UseSparseArrays")
    public enum Gatt {
        /**
         * GATT 服务
         */
        SERVICE,

        /**
         * GattCharacteristic的属性和权限
         */
        CHARACTERISTIC,

        /**
         * GattDescriptor的权限
         */
        DESCRIPTOR;

        /********************************************************************************/

        private HashMap<Integer, String> serviceTypes = new HashMap<>();

        {
            serviceTypes.put(BluetoothGattService.SERVICE_TYPE_PRIMARY, "PRIMARY");
            serviceTypes.put(BluetoothGattService.SERVICE_TYPE_SECONDARY, "SECONDARY");
        }

        public String getServiceType(int type) {
            return serviceTypes.get(type);
        }

        /********************************************************************************/

        private HashMap<Integer, String> charPermissions = new HashMap<>();
        private HashMap<Integer, String> charProperties = new HashMap<>();

        {
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST, "BROADCAST");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS, "EXTENDED_PROPS");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE, "INDICATE");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY, "NOTIFY");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE, "SIGNED_WRITE");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
            charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, "WRITE_NO_RESPONSE");

            charPermissions.put(0, UNKNOWN);
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE, "WRITE");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
            charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
        }

        public String getCharactristicProperty(int property) {
            return getHashMapValue(charProperties, property);
        }

        /**
         * 获取权限
         */
        public String getCharactristicPermission(int permission) {
            return getHashMapValue(charPermissions, permission);
        }

        /********************************************************************************/

        private HashMap<Integer, String> descriptorPermissions = new HashMap<>();
        private HashMap<byte[], String> descriptorValueTypes = new HashMap<>();

        {
            descriptorPermissions.put(0, UNKNOWN);
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
            descriptorPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");

            descriptorValueTypes.put(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, "ENABLE_NOTIFICATION_VALUE");
            descriptorValueTypes.put(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, "DISABLE_NOTIFICATION_VALUE");
            descriptorValueTypes.put(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, "ENABLE_INDICATION_VALUE");
        }

        /**
         * 获取权限
         *
         * @param permission 权限
         * @return 权限
         */
        public String getDescriptorPermission(int permission) {
            return getHashMapValue(descriptorPermissions, permission);
        }

        /**
         * 获取值的类型
         */
        public String getDescriptorValueType(byte[] value) {
            String type = descriptorValueTypes.get(value);
            if (isEmpty(type) && isNotEmpty(value)) {
                byte[] temp = {value[0], value[1]};
                type = descriptorValueTypes.get(temp);
            }
            return isEmpty(type) ? UNKNOWN : type;
        }

        private static String getHashMapValue(HashMap<Integer, String> hashMap, int number) {
            String result = hashMap.get(number);
            if (isEmpty(result)) {
                List<Integer> numbers = getElement(number);
                result = "";
                for (int i = 0; i < numbers.size(); i++) {
                    result += hashMap.get(numbers.get(i)) + "|";
                }
            }
            return result;
        }

        /**
         * 位运算结果的反推函数10 -> 2 | 8;
         */
        private static List<Integer> getElement(int number) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                int b = 1 << i;
                if ((number & b) > 0)
                    result.add(b);
            }
            return result;
        }

    }

    /*****************************************************************************/

    /**
     * 获取UUID
     */
    public static UUID getUUID(String uuid) {
        return UUID.fromString(uuid);
    }

    /**
     * 获取BluetoothGattService集合
     */
    public static List<BluetoothGattService> getServices(BluetoothGatt gatt) {
        if (gatt == null)
            return new ArrayList<>();
        return gatt.getServices();
    }

    /**
     * 获取对应UUID的BluetoothGattService
     */
    public static BluetoothGattService getService(
            BluetoothGatt gatt, UUID serviceUUID) {
        return gatt != null ? gatt.getService(serviceUUID) : null;
    }

    /**
     * 获取对应UUID的BluetoothGattService
     */
    public static BluetoothGattService getService(
            List<BluetoothGattService> services, UUID serviceUUID) {
        if (isEmpty(services) || serviceUUID == null)
            return null;

        for (BluetoothGattService service : services) {
            UUID sUUID = service.getUuid();
            if (sUUID.equals(serviceUUID)) {
                return service;
            }
        }
        return null;
    }

    /**
     * 获取对应UUID的BluetoothGattCharacteristic
     */
    public static BluetoothGattCharacteristic getCharacteristic(
            BluetoothGattService service, UUID characteristicUUID) {
        if (!isNonNull(service, characteristicUUID))
            return null;
        return service.getCharacteristic(characteristicUUID);
    }

    /**
     * 获取对应UUID的BluetoothGattCharacteristic
     */
    public static BluetoothGattCharacteristic getCharacteristic(
            BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGattService service = getService(gatt, serviceUUID);
        return getCharacteristic(service, characteristicUUID);
    }

    /**
     * 获取对应UUID的BluetoothGattDescriptor
     */
    public static BluetoothGattDescriptor getDescriptor(
            BluetoothGattService gattService, UUID characteristicUUID, UUID descriptorUUID) {
        BluetoothGattCharacteristic c = getCharacteristic(gattService, characteristicUUID);
        return getDescriptor(c, descriptorUUID);
    }

    /**
     * 获取对应UUID的BluetoothGattDescriptor
     */
    public static BluetoothGattDescriptor getDescriptor(
            BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID) {
        BluetoothGattCharacteristic c = getCharacteristic(gatt, serviceUUID, characteristicUUID);
        return getDescriptor(c, descriptorUUID);
    }

    /**
     * 获取对应UUID的BluetoothGattDescriptor
     */
    public static BluetoothGattDescriptor getDescriptor(
            BluetoothGattCharacteristic characteristic, UUID descriptorUUID) {
        return characteristic != null ? characteristic.getDescriptor(descriptorUUID) : null;
    }

    /**
     * 设置特征值改变时的提醒
     */
    public static boolean setNotification(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          boolean enable) {
        return isNonNull(gatt, characteristic)
                && gatt.setCharacteristicNotification(characteristic, enable);
    }

    /**
     * 设置特征值改变时的提醒
     */
    public static boolean setNotification(BluetoothGatt gatt,
                                          UUID serviceUUID,
                                          UUID characteristicUUID,
                                          boolean enable) {
        BluetoothGattCharacteristic characteristic =
                getCharacteristic(gatt, serviceUUID, characteristicUUID);
        return setNotification(gatt, characteristic, enable);
    }

    /**
     * 设置描述符的值
     * {@link BluetoothGattDescriptor#ENABLE_NOTIFICATION_VALUE}
     * {@link BluetoothGattDescriptor#ENABLE_INDICATION_VALUE}
     * {@link BluetoothGattDescriptor#DISABLE_NOTIFICATION_VALUE}
     */
    public static boolean setDescriptorValue(BluetoothGatt gatt,
                                             BluetoothGattDescriptor descriptor,
                                             byte[] value) {
        return isNonNull(gatt, descriptor) && descriptor.setValue(value);
    }

    /**
     * 设置提醒
     */
    public static boolean notification(
            BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
            return notification(gatt, service, characteristic);
        }
        return false;
    }

    /**
     * 设置提醒
     */
    public static boolean notification(
            BluetoothGatt gatt, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        if (isNonNull(gatt, service, characteristic)) {
            gatt.setCharacteristicNotification(characteristic, true);
            // 适配部分机型(会导致部分手机无法接收到数据)
            // gatt.readCharacteristic(characteristic);
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                BluetoothTool.writeDescriptorValue(
                        gatt, descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
            return true;
        }
        return false;
    }

    public static boolean indication(
            BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic c = service.getCharacteristic(characteristicUUID);
            if (c != null) {
                gatt.setCharacteristicNotification(c, true);
                for (BluetoothGattDescriptor descriptor : c.getDescriptors()) {
                    BluetoothTool.writeDescriptorValue(
                            gatt, descriptor, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 是否写入描述符的值
     */
    public static boolean writeDescriptorValue(BluetoothGatt gatt,
                                               BluetoothGattDescriptor descriptor,
                                               byte[] value) {
        return setDescriptorValue(gatt, descriptor, value)
                && gatt.writeDescriptor(descriptor);
    }

    /**
     * 是否读取描述符的值
     */
    public static boolean readDescriptorValue(BluetoothGatt gatt,
                                              BluetoothGattDescriptor descriptor,
                                              byte[] value) {
        return setDescriptorValue(gatt, descriptor, value)
                && gatt.readDescriptor(descriptor);
    }

    /**
     * 写入特征值
     */
    public static boolean writeCharacteristic(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic,
                                              String value) {
        byte[] bin = hexToByte(value);
        return writeCharacteristic(gatt, characteristic, bin);
    }

    /**
     * 写入特征值
     */
    public static boolean writeCharacteristic(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic,
                                              byte[] value) {
        return characteristic != null
                && characteristic.setValue(value)
                && gatt.writeCharacteristic(characteristic);
    }


    /**
     * 打印BluetoothGattService的信息
     */
    public static void printGattInfo(List<BluetoothGattService> getServices, String tag) {
        StringBuilder builder = new StringBuilder();
        int sIndex = 0;
        for (BluetoothGattService service : getServices) {
            sIndex++;
            Log.e(tag, "\n-------- start -------- " + sIndex + " ------------------------");
            builder.append("Service uuid: ").append(service.getUuid().toString());
            builder.append("; type: ").append(service.getType());
            Log.i(tag, builder.toString());
            builder.setLength(0);

            int cIndex = 0;
            Log.w(tag, "==>: characteristic ----> START");
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                cIndex++;
                builder.append(cIndex).append("、characteristic ");
                builder.append("uuid: ").append(characteristic.getUuid());
                builder.append("; permissions: ").append(characteristic.getPermissions());
                builder.append("; properties: ").append(characteristic.getProperties());
                builder.append("; writeType: ").append(characteristic.getWriteType());
                builder.append("; value: ")
                        .append(BluetoothTool.byteToHex(characteristic.getValue()));
                Log.i(tag, builder.toString());
                builder.setLength(0);

                Log.i(tag, cIndex + " ==>: descriptor ----> START");
                int dIndex = 0;
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    dIndex++;
                    builder.append(cIndex).append("").append(dIndex).append("、descriptor ");
                    builder.append("uuid: ").append(descriptor.getUuid());
                    builder.append("; permissions: ").append(descriptor.getPermissions());
                    builder.append("; value: ")
                            .append(BluetoothTool.byteToHex(characteristic.getValue()));
                    Log.d(tag, builder.toString());
                    builder.setLength(0);
                }
                Log.i(tag, "==>: descriptor ----> END");
            }
            Log.w(tag, "==>: characteristic ----> END");
            Log.e(tag, "\n-------- end -------- " + sIndex + " ------------------------");
        }
        builder.setLength(0);
    }


    /*****************************************************************************/


    private static boolean isNotEmpty(byte[] s) {
        return s != null && s.length > 0;
    }

    private static boolean isNonNull(Object... objects) {
        for (Object o : objects) {
            if (o == null)
                return false;
        }
        return true;
    }

    private static boolean isNotEmpty(String s) {
        return s != null && s.trim().length() > 0;
    }

    private static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length <= 0;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}
