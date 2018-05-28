package com.dxa.android.ble.log;

/**
 * 日志管理
 */

public final class LoggerManager implements DLogger {

    public static LoggerManager getInstance() {
        return Holder.INSTANCE;
    }

    private DLogger logger = new SimpleLogger(false);

    private LoggerManager() {
    }

    /**
     * 设置DLogger
     */
    public void setLogger(DLogger logger) {
        this.logger = logger != null ? logger : this.logger;
    }

    /**
     * 设置标签
     */
    @Override
    public void setTag(String tag) {
        logger.setTag(tag);
    }

    /**
     * 打印日志(debug)
     *
     * @param messages 日志信息
     */
    @Override
    public void d(Object... messages) {
        logger.d(messages);
    }

    /**
     * 打印日志(info)
     *
     * @param messages 日志信息
     */
    @Override
    public void i(Object... messages) {
        logger.i(messages);
    }

    /**
     * 打印日志(warn)
     *
     * @param messages 日志信息
     */
    @Override
    public void w(Object... messages) {
        logger.w(messages);
    }

    /**
     * 打印日志(error)
     *
     * @param messages 日志信息
     */
    @Override
    public void e(Object... messages) {
        logger.e(messages);
    }

    private static final class Holder {

        private static volatile LoggerManager INSTANCE;

        static {
            INSTANCE = new LoggerManager();
        }
    }

}
