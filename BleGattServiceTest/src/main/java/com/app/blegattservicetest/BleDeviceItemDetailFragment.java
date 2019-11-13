package com.app.blegattservicetest;

import tw.com.umedia.bluetoothle.BleDevice;
import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * A fragment representing a single BleDeviceItem detail screen. This fragment
 * is either contained in a {@link BleDeviceItemListActivity} in two-pane mode
 * (on tablets) or a {@link BleDeviceItemDetailActivity} on handsets.
 */
public class BleDeviceItemDetailFragment extends Fragment {
	private static final String TAG = "BleDeviceItemDetailFragment";
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_MAC_ADDRESS = "mac_address";

	private BleDeviceCallback mCallback;
	private LayoutInflater mInflater;
	private View[] childView = new View[4];
	private FrameLayout[] mFrame = new FrameLayout[4];
	private ViewContents[] viewContents = new ViewContents[4];

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BleDeviceItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BleLog.i(TAG, "onCreate()");;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		BleLog.i(TAG, "onCreateView()");;
		View rootView = inflater.inflate(
				R.layout.device_data, container, false);
		mInflater = inflater;
		childView[0] = (View) inflater.inflate(R.layout.umhd_data, null);
		childView[1] = (View) inflater.inflate(R.layout.bracelet_data, null);
		childView[2] = (View) inflater.inflate(R.layout.umhd_data, null);
		childView[3] = (View) inflater.inflate(R.layout.bracelet_data, null);
		mFrame[0]= (FrameLayout) rootView.findViewById(R.id.data_layout_1);
		mFrame[1]= (FrameLayout) rootView.findViewById(R.id.data_layout_2);
		mFrame[2]= (FrameLayout) rootView.findViewById(R.id.data_layout_3);
		mFrame[3]= (FrameLayout) rootView.findViewById(R.id.data_layout_4);
		mFrame[0].addView(childView[0]);
		mFrame[1].addView(childView[1]);
		mFrame[2].addView(childView[2]);
		mFrame[3].addView(childView[3]);
		return rootView;
	}
	
	public View getDeviceView(int position, int type) {
		BleLog.i(TAG, "getDeviceView()");;
		FrameLayout f = mFrame[position];
		View v = childView[position];
		if(v != null && f != null) {
			f.removeView(v);
			if(type == 0) {
				v = (View) mInflater.inflate(R.layout.umhd_data, null);
				viewContents[position] = getDeviceViewContents((ViewGroup)v, type);
			} else if(type == 1) {
				v = (View) mInflater.inflate(R.layout.bracelet_data, null);
				viewContents[position] = getDeviceViewContents((ViewGroup)v, type);
			}
			f.addView(v);
		}
		return v;
	}
	
	public void clearDeviceView(int position) {
		BleLog.i(TAG, "clearDeviceView() position = " + position);
		if(position < 0 || position > 3) return;
		viewContents[position].mDeviceName.setText("");
		viewContents[position].mBTAddress.setText("");
		viewContents[position].mManufacturer.setText("");
		viewContents[position].mHardwareRevision.setText("");
		viewContents[position].mFirmwareRevision.setText("");
		viewContents[position].mSoftwareRevision.setText("");
		
		//FrameLayout f = mFrame[position];
		//View v = childView[position];
		//f.removeView(v);
		return;
	}
	
	
	public void setViewText(int position, String key, String text) {
		BleLog.d(TAG, "setViewText(position, key, string) = (" + position + ", " + key + ". " + text + ")" );
		if(position <0 || position > 3) return;
		if(key.equals(BluetoothLeCharacteristicKey.KEY_DEVICE_NAME)) {
			viewContents[position].mDeviceName.setText(text);
		} else if(key.equals("bt_address")) {
			viewContents[position].mBTAddress.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_MANUFACTURER_NAME)) {
			viewContents[position].mManufacturer.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_HARDWARE_REVISION)) {
			viewContents[position].mHardwareRevision.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_FIRMWARE_REVISION)) {
			viewContents[position].mFirmwareRevision.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_SOFTWARE_REVISION)) {
			viewContents[position].mSoftwareRevision.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_SPEED)) {
			viewContents[position].mSpeed.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_CADENCE)) {
			viewContents[position].mCadence.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE)) {
			viewContents[position].mDistance.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_STEPS)) {
			viewContents[position].mSteps.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_CALORIE)) {
			viewContents[position].mCalorie.setText(text);
		} else if(key.equals(BluetoothLeCharacteristicKey.KEY_TIMESTAMP)) {
			viewContents[position].mTimeStamp.setText(text);
		}
	}
	
	public ViewContents getDeviceViewContents(ViewGroup vg, int type) {
		BleLog.d(TAG, "getDeviceViewContents()");
		ViewContents contents = new ViewContents();
		contents.type = type;		
		contents.mDeviceName= (TextView) vg.findViewById(R.id.data_device_name);		
		contents.mManufacturer= (TextView) vg.findViewById(R.id.data_manufacturer);
		contents.mBTAddress= (TextView) vg.findViewById(R.id.data_btaddress);
		contents.mHardwareRevision= (TextView) vg.findViewById(R.id.data_hardware_revision);
		contents.mFirmwareRevision= (TextView) vg.findViewById(R.id.data_firmware_revision);
		contents.mSoftwareRevision= (TextView) vg.findViewById(R.id.data_software_revision);
		contents.mDistance= (TextView) vg.findViewById(R.id.data_distance);
		if(type == 0) {
			contents.mSpeed= (TextView) vg.findViewById(R.id.data_speed);
			contents.mCadence= (TextView) vg.findViewById(R.id.data_cadence);			
		} else if(type == 1) {
			contents.mSteps= (TextView) vg.findViewById(R.id.data_steps);
			contents.mCalorie= (TextView) vg.findViewById(R.id.data_calorie);
			contents.mTimeStamp = (TextView) vg.findViewById(R.id.data_timestamp);
		}
		contents.mGroup = vg;
		return contents;
	}
	
	public static class ViewContents {
		int type;
		ViewGroup mGroup;
		TextView mDeviceName;
		TextView mManufacturer;
		TextView mBTAddress;
		TextView mHardwareRevision;
		TextView mFirmwareRevision;
		TextView mSoftwareRevision;
		TextView mSpeed;
		TextView mCadence;
		TextView mDistance;
		TextView mSteps;
		TextView mCalorie;
		TextView mTimeStamp;
	}
	
}
