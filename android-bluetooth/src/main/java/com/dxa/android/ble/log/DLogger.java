package com.dxa.android.ble.log;

/**
 * 日志
 */
public interface DLogger {
    /**
     * 是否打印日志
     */
    void setDebug(boolean debug);

    /**
     * 设置标签
     */
    void setTag(String tag);

    /**
     * 打印日志(debug)
     *
     * @param messages 日志信息
     */
    void d(Object... messages);

    /**
     * 打印日志(info)
     *
     * @param messages 日志信息
     */
    void i(Object... messages);

    /**
     * 打印日志(warn)
     *
     * @param messages 日志信息
     */
    void w(Object... messages);

    /**
     * 打印日志(error)
     *
     * @param messages 日志信息
     */
    void e(Object... messages);
}
