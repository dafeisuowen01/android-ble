package com.dxa.android;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxa.android.recycler.RecyclerAdapter;

/**
 * 蓝牙设备
 */
public class BluetoothRecyclerAdapter extends RecyclerAdapter<
        BluetoothDevice, BluetoothRecyclerAdapter.ViewHolder> {

    public BluetoothRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getView(R.layout.item_bluetooth_device, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = getItem(position);
        holder.deviceName.setText(getText(device.getName()));
        holder.deviceAddress.setText(getText(device.getAddress()));
    }

    private String getText(String text) {
        return text != null ? text : "";
    }

    static class ViewHolder extends RecyclerAdapter.ViewHolder {

        TextView deviceName;
        TextView deviceAddress;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}