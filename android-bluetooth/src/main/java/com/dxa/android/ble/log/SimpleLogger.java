package com.dxa.android.ble.log;

import android.util.Log;

/**
 * DLogger的简单实现
 */
public class SimpleLogger implements DLogger {

    private volatile boolean debug;
    private final StringBuffer buffer;

    private String tag = "TAG@[bluetooth]";

    public SimpleLogger(boolean debug) {
        this.debug = debug;
        this.buffer = new StringBuffer();
    }

    private String append(Object... args) {
        String message = "";
        if (debug) {
            synchronized (buffer) {
                buffer.setLength(0);
                for (Object o : args) {
                    buffer.append(o);
                }
                message = buffer.toString();
            }
        }
        return message;
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 打印日志(debug)
     *
     * @param messages 日志信息
     */
    @Override
    public void d(Object... messages) {
        if (debug) {
            Log.d(tag, append(messages));
        }
    }

    /**
     * 打印日志(info)
     *
     * @param messages 日志信息
     */
    @Override
    public void i(Object... messages) {
        if (debug) {
            Log.i(tag, append(messages));
        }
    }

    /**
     * 打印日志(warn)
     *
     * @param messages 日志信息
     */
    @Override
    public void w(Object... messages) {
        if (debug) {
            Log.w(tag, append(messages));
        }
    }

    /**
     * 打印日志(error)
     *
     * @param messages 日志信息
     */
    @Override
    public void e(Object... messages) {
        if (debug) {
            Log.e(tag, append(messages));
        }
    }

}
