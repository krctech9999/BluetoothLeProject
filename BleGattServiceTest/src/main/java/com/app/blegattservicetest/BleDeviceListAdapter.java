package com.app.blegattservicetest;

import java.util.ArrayList;
import java.util.List;

import tw.com.umedia.bluetoothle.BleDevice;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BleDeviceListAdapter extends ArrayAdapter {
	private final static String TAG = "BleDeviceListAdapter";

	private List<BleDevice> mBleDevices;
	private List<ViewHolder> mViewHolder = new ArrayList<ViewHolder>();
	private BleDeviceCallback mCallback;
	private LayoutInflater mInflator;
	private Context mContext;
	private int mResource;
	
	public BleDeviceListAdapter(Context context, int resource) {
		super(context, resource);
		BleLog.d(TAG, "BleDeviceListAdapter()");
		mContext = context;
		mResource = resource;
		mBleDevices = new ArrayList<BleDevice>();
		mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {		
		BleLog.d(TAG, "getCount() = " + mBleDevices.size());
		return mBleDevices.size();
	}

	@Override
	public BleDevice getItem(int position) {
		BleLog.d(TAG, "getItem(" + position + ")");
		if(mBleDevices == null || mBleDevices.isEmpty()) {
			return null;
		}
		return mBleDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		BleLog.d(TAG, "getItemId()");
		if(mBleDevices == null || mBleDevices.isEmpty()) { 
			return -1;
		}
		return super.getItemId(position);
	}

	public String getDeviceName(String address) {
		String devicename = null;
		for(BleDevice device: mBleDevices) {
			if(device.getMacAddress().equals(address)) {
				devicename = device.getDeviceName();
				break;
			}
		}
		return devicename;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder viewHolder;
		BleLog.d(TAG, "getView()");
         if (convertView == null) {
        	 convertView = mInflator.inflate(mResource, null);
             viewHolder = new ViewHolder();
             viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
             viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
             viewHolder.deviceRssi = (TextView) convertView.findViewById(R.id.device_rssi);
             viewHolder.connectState =  (Button) convertView.findViewById(R.id.connect_state);
             viewHolder.connectState.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					Button button = (Button) v;
					int pos = (Integer) button.getTag();
					if(mBleDevices.size() < pos || mBleDevices.isEmpty()) {
						clear();
						return;
					}
					 String address = mBleDevices.get(pos).getMacAddress();					 
					 String name = mBleDevices.get(pos).getDeviceName();
					 BleLog.d(TAG, "onClick()  address = " + address + ", name = " + name);
					 int type = -1;;
					 if(name.contains("UMHD")) {
						 type = 0;
					 } else if(name.contains("Bracelet") || name.contains("M04_L")) {
						 type = 1;
					 }
					 if(button.getText().equals("Connect")) {
						button.setText("Disconnect");						
						if(mCallback != null) mCallback.Connect(address, type);
					} else {
						button.setText("Connect");
						if(mCallback != null) mCallback.Disconnect(address);
					}
				}
            	 
             });
             convertView.setTag(viewHolder);                
             mViewHolder.add(viewHolder);
         } else {
             viewHolder = (ViewHolder) convertView.getTag();
         }

         BleDevice device = mBleDevices.get(position);
         final String deviceName = device.getDeviceName();
         if (deviceName != null && deviceName.length() > 0)
             viewHolder.deviceName.setText(deviceName);
         else
             viewHolder.deviceName.setText(R.string.unknown_device);
         viewHolder.deviceAddress.setText(device.getMacAddress());
         viewHolder.connectState.setTag(position);
         viewHolder.position = position;
         int rssi = device.getRssi();
         viewHolder.deviceRssi.setText(Integer.toString(rssi));
         //viewHolder.connectState.setText("found");
         return convertView;		
	}

	public void addDevice(BleDevice device) {
		BleLog.d(TAG, "addDevice() name = " + device.getDeviceName() + ",  address = " + device.getMacAddress() + ", rssi = " + device.getRssi());
		if(!mBleDevices.contains(device)) {
			mBleDevices.add(device);
		}
	}

	@Override
	public void clear() {
		BleLog.d(TAG, "clear()");
		mBleDevices.clear();
		super.clear();
		return;
	}

	public void setDeviceCallback(BleDeviceCallback callback) {
		mCallback = callback;
	}
	
	@Override
	public boolean equals(Object address) {
		if(mBleDevices == null || mBleDevices.size() == 0) { 
			return false;
		}
		for(int i = 0; i < mBleDevices.size(); i++) {
			if(address.toString().equals(mBleDevices.get(i).getMacAddress())) {
				return true;
			}
		}
		return false;
	}

	public void setButtonText(int position, boolean state) {
		if(mBleDevices.isEmpty()) return;
		for(ViewHolder view :mViewHolder) {
			int pos = (Integer) view.position;
			if(pos == position) {
				if(!state) {
					view.connectState.setText("Connect");
				} else {
					view.connectState.setText("Disconnect");
				}
			}
		}
	}
	
	private  static class ViewHolder {
		int position;
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        Button connectState;
    }

}
