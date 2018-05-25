package com.dxa.android.ble;

/**
 * 连接状态，定义了6个状态
 *
 * @author DINGXIUAN
 */
public enum ConnectState {

    /**
     * 连接成功(已经连上)
     */
    CONNECTING(1, false, "connecting", "连接中(不自动)"),
    /**
     * 已连接(自动)
     */
    AUTO_CONNECTING(1, true, "auto_connecting", "正在连接中(自动)"),
    /**
     * 连接成功(已经连上)
     */
    CONNECTED(2, false, "connected", "连接成功(不自动)"),
    /**
     * 已连接(自动)
     */
    AUTO_CONNECTED(2, true, "auto_connected", "已连接(自动)"),
    /**
     * 发现服务(可以进行通讯)
     */
    SERVICE_DISCOVER(3, true, "found_service", "发现服务"),
    /**
     * 断开连接(彻底断开了连接)
     */
    DISCONNECTED(4, false, "disconnected", "已断开(不自动)"),
    /**
     * 已断开(自动)
     */
    AUTO_DISCONNECTED(4, true, "auto_connect", "已断开(自动)"),;

    private final int state;
    private final boolean autoConnect;
    private final String name;
    private final String description;

    ConnectState(int state, boolean autoConnect,
                 String name, String description) {
        this.state = state;
        this.autoConnect = autoConnect;
        this.name = name;
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static boolean tryAgain(ConnectState cs) {
        return cs != null && cs.isAutoConnect();
    }
}
