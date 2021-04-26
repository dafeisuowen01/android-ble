package com.dxa.android;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dxa.android.ble.BluetoothTool;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RingUtils {

  /**
   * 加密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回加密后的数据
   */
  public static byte[] encrypt(byte[] source, String key) {
    return op(source, key, Cipher.ENCRYPT_MODE);
  }

  /**
   * 加密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回加密后的数据
   */
  public static String encryptHex(byte[] source, String key) {
    byte[] original = op(source, key, Cipher.ENCRYPT_MODE);
    return BluetoothTool.byteToHex(original);
  }

  /**
   * 加密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回加密后的数据
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  public static String encryptBase64(byte[] source, String key) {
    byte[] original = op(source, key, Cipher.ENCRYPT_MODE);
    return Base64.getEncoder().encodeToString(original);
  }

  /**
   * 解密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回揭秘后的数据
   */
  public static byte[] decrypt(byte[] source, String key) {
    return op(source, key, Cipher.DECRYPT_MODE);
  }

  /**
   * 解密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回揭秘后的数据
   */
  public static byte[] decryptHex(String source, String key) {
    return decrypt(BluetoothTool.hexToByte(source), key);
  }

  /**
   * 解密
   *
   * @param source 数据源
   * @param key    密钥
   * @return 返回揭秘后的数据
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  public static byte[] decryptBase64(String source, String key) {
    return decrypt(Base64.getDecoder().decode(source), key);
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  private static byte[] op(byte[] source, String hexKey, int opmode) {
    return op(source, BluetoothTool.hexToByte(hexKey), opmode);
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  private static byte[] op(byte[] source, byte[] key, int opmode) {
    if (key.length != 16) {
      throw new IllegalArgumentException("Key长度不是16位: " + BluetoothTool.byteToHex(key));
    }
    try {
      SecretKeySpec aes = new SecretKeySpec(key, "AES");
      // "算法/模式/补码方式"
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(opmode, aes);
      return cipher.doFinal(source);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}
