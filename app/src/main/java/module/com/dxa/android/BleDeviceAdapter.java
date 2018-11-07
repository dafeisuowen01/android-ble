package com.dxa.android;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxa.android.recycler.RecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 蓝牙设备
 */
public class BleDeviceAdapter extends RecyclerAdapter<BluetoothDevice, BleDeviceAdapter.ViewHolder> {

    public BleDeviceAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View itemView = getView(R.layout.item_bluetooth_device, parent);
        return new ViewHolder(itemView);
    }

    static class ViewHolder extends RecyclerAdapter.ViewHolder<BluetoothDevice> {

        @BindView(R.id.tv_device)
        TextView tvDevice;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setData(BluetoothDevice device, int position) {
            tvDevice.setText(String.format("名称: %s, mac: %s", device.getName(), device.getAddress()));
        }
    }

}
