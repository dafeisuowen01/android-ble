package com.dxa.android.ble;

/**
 * 连接状态，定义了6个状态
 */
public enum ConnectState {
    CONNECTING(1, "connecting", "连接中(还未连上)"),
    CONNECTED(2, "connected", "连接成功(已经连上)"),
    SERVICE_DISCOVER(3, "found_service", "发现服务(可以进行通讯)"),
    DISCONNECTEING(4, "disconnecting", "连接断开中(会自动重新)"),
    AUTO_CONNECT(5, "auto_connect", "自动重连中(设备断开了，还没有连接上)"),
    DISCONNECTED(6, "disconnected", "断开连接(彻底断开了连接)"),;

    private final int state;
    private final String name;
    private final String description;

    ConnectState(int state, String name, String description) {
        this.state = state;
        this.name = name;
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
