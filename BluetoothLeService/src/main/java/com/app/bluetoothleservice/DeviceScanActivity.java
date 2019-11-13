/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.bluetoothleservice;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import tw.com.umedia.bluetoothle.BleDevice;
import tw.com.umedia.bluetoothle.BleGattDataCallback;
import tw.com.umedia.bluetoothle.BleScanCallback;
import tw.com.umedia.bluetoothle.BluetoothLeClient;
import tw.com.umedia.bluetoothle.BluetoothLeDataCallback;
import tw.com.umedia.bluetoothle.BluetoothLeService;
import tw.com.umedia.bluetoothle.IBleGattClient;
import tw.com.umedia.bluetoothle.IBleScanDevice;
import tw.com.umedia.bluetoothle.utils.BleLog;
import tw.com.umedia.bluetoothle.R;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private final static String TAG = "Main";
	
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private IBleScanDevice mScanDevice;
    private IBleGattClient mClient;
    private Intent mIntent1, mIntent2;
    private BleScanDeviceConnection mScanDeviceConnection;
    private BleGattClientConnection mGattClientConnection;
    private boolean mScanning;
    private Handler mHandler;    
    private ViewHolder mViewholder;
    private String updateText;

    private HashMap<String, Integer> mConnectedDevice = new HashMap<String, Integer>();    
    
    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    private class BleScanDeviceConnection implements ServiceConnection {

      public void onServiceConnected(ComponentName name, IBinder boundService) {
    	  mScanDevice = IBleScanDevice.Stub.asInterface((IBinder) boundService);
        try {
        	mScanDevice.registerCallback(mCallback);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BleLog.d(TAG, "BleScanDeviceConnection onServiceConnected() connected");
      }

      public void onServiceDisconnected(ComponentName name) {
    	  mScanDevice = null;
        BleLog.d(TAG, "BleScanDeviceConnection onServiceDisconnected() disconnected");
      }

    }
    
    private class BleGattClientConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder boundService) {
			mClient =  IBleGattClient.Stub.asInterface((IBinder) boundService);
	        try {
	        	mClient.registerCallback(mDataCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        BleLog.d(TAG, "BleGattClientConnection onServiceConnected() connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mClient = null;
			BleLog.d(TAG, "BleGattClientConnection onServiceDisconnected() disconnected");
		}
    	
    }

    /** Binds this activity to the service. */
    private void initService() {
    	mScanDeviceConnection = new BleScanDeviceConnection();
    	mGattClientConnection = new BleGattClientConnection();
    	mIntent1 = new Intent( );
    	mIntent1.setClassName("com.app.bluetoothleservice", "tw.com.umedia.bluetoothle.BluetoothLeService");
    	mIntent1.setAction("tw.com.umedia.bluetoothle.IBLESCANDEVICE");
    	BleLog.d(TAG, "initService() : " + IBleScanDevice.class.getName());      
    	mIntent2 = new Intent();
    	mIntent2.setClassName("com.app.bluetoothleservice", "tw.com.umedia.bluetoothle.BluetoothLeService");
    	mIntent2.setAction("tw.com.umedia.bluetoothle.IBLEGATTCLIENT");
    	BleLog.d(TAG, "initService() : " + IBleGattClient.class.getName());      
    	boolean ret;
    	ret = bindService(mIntent1, mScanDeviceConnection, Context.BIND_AUTO_CREATE);
    	if(!ret) {
    		finish();
    	} 
    	BleLog.d(TAG, "initService() bound with IBleScanDevice ret=" + ret);
    	ret = bindService(mIntent2, mGattClientConnection, Context.BIND_AUTO_CREATE);
    	if(!ret) {
    		finish();
    	}
    	startService(mIntent1);
    	startService(mIntent2);
    	BleLog.d(TAG, "initService() bound with IBleGattClient ret=" + ret);      
    }

    /** Unbinds this activity from the service. */
    private void releaseService() {    	
      unbindService(mScanDeviceConnection);
      unbindService(mGattClientConnection);
      mScanDeviceConnection = null;
      BleLog.d(TAG, "releaseService() unbound.");
    }
/*
    private BluetoothLeDataCallback callback= new BluetoothLeDataCallback() {

		@Override
		public void DataCallback(HashMap<String, Object> data, int position) {
			// TODO Auto-generated method stub
			BleLog.d(TAG, "DataCallback");
			if(data == null) return;
			mViewholder = mLeDeviceListAdapter.getItemViewHolder(position);
			Set<String> set = data.keySet();
			String str = "";
			for(String item : set) {
				String str1 = data.get(item).toString();
				str += " [ " + item + " : " + str1 + "], ";
			}
			updateText = str;
			mHandler.post(updateView);
		}    	
    };    
 */
    
    private Runnable updateView = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mViewholder.connectState.setText("connected");
			mViewholder.instantData.setText(updateText);
			mLeDeviceListAdapter.notifyDataSetChanged();
		}    	
    };    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        initService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
            	if(!mLeDeviceListAdapter.isEmpty()) {            		
            		mLeDeviceListAdapter.clear();
            	}
            	try {
            		mScanDevice.BleScan(true);
            	} catch (RemoteException e) {
            		// TODO Auto-generated catch block
            		e.printStackTrace();
            	}
            	mScanning = true;
                break;
            case R.id.menu_stop:
            	try {
            		mScanDevice.BleScan(false);
            	} catch (RemoteException e) {
            		// TODO Auto-generated catch block
            		e.printStackTrace();
            	}
            	mScanning = false;
                break;
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    protected void onDestroy() {
    	releaseService();
    	stopService(mIntent1);
    	stopService(mIntent2);
    	super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mLeDeviceListAdapter.isEmpty()) {
        	mLeDeviceListAdapter.clear();
        }
    }    
    
    @Override
    protected void onListItemClick(ListView l, View view, int position, long id) {
        BleDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        if (mScanning) {
            try {
            	mScanDevice.BleScan(false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            mScanning = false;
        }
        String str = device.getMacAddress().toString();
        ViewHolder  viewHolder = (ViewHolder) view.getTag();
        if( mConnectedDevice.containsKey(str) ) {
        	try {
				mClient.disconnect(str);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//comm.close();
        	mConnectedDevice.remove(str);
        	viewHolder.connectState.setText("disconnected");      
        	viewHolder.instantData.setText("");
        } else {
        	boolean connected = false;
			try {
				connected = mClient.connect(str);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};        	
        	if(connected) {
        		mConnectedDevice.put(str, position);
           		viewHolder.connectState.setText("connected");
        	} else {
           		viewHolder.connectState.setText("connect error!");
        	}
        } // if( mConnectedDevice.containsKey(str) ) 
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BleDevice> mLeDevices;
        private ArrayList<ViewHolder> mViewHolder;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BleDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
            mViewHolder = new ArrayList<ViewHolder>();
        }

        public void addDevice(BleDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BleDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        public ViewHolder getItemViewHolder(int i) {
        	return mViewHolder.get(i);
        }
        
        public ViewHolder  getItemViewHolder(String address) {
        	for(int i=0; i < mLeDevices.size(); i++) {
        		if(mLeDevices.get(i).getMacAddress().equals(address)) {
        			return mViewHolder.get(i);        			
        		}
        	}
        	return null;
        }
        
        @Override
        public long getItemId(int i) {
            return i;
        }
        
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.connectState =  (TextView) view.findViewById(R.id.connect_state);
                viewHolder.instantData = (TextView) view.findViewById(R.id.instant_data);
                view.setTag(viewHolder);                
                mViewHolder.add(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BleDevice device = mLeDevices.get(i);
            final String deviceName = device.getDeviceName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getMacAddress());
            //viewHolder.connectState.setText("found");
            return view;
        }
    }

    private BleScanCallback.Stub mCallback = new BleScanCallback.Stub() {
		
		@Override
		public void getBleDeviceData(String name, String address, int rssi)
				throws RemoteException {
			BleLog.d(TAG, "name = " + name + " address = " + address + " rssi = " + rssi);
			BleDevice mDevice = new BleDevice(name, address, rssi);
			for(int i=0; i < mLeDeviceListAdapter.getCount(); i++) {
				BleDevice device = mLeDeviceListAdapter.getDevice(i);
				if(device.getMacAddress().equals(address)) {
					device.setRssi(rssi);
					return;
				}
			}
			mLeDeviceListAdapter.addDevice(mDevice);
			mHandler.post(updateList);
		}

		@Override
		public void scanStatus(boolean state) throws RemoteException {			
			mScanning = state;
			invalidateOptionsMenu();
		}    	
    };
    
    private Runnable updateList = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mLeDeviceListAdapter.notifyDataSetChanged();
		}
    	
    };
    
    private BleGattDataCallback.Stub mDataCallback = new BleGattDataCallback.Stub() {
		
		@Override
		public void DataCallback(String key, String value, String address)
				throws RemoteException {
			BleLog.d(TAG, "key = " + key + ", value = " + value + ", address = " + address);
			// TODO Auto-generated method stub
			if(key == null || address ==  null) return;
			mViewholder = mLeDeviceListAdapter.getItemViewHolder(address);
			if(mViewholder == null) return;
			updateText = "key = " + key + ", value = " + value;
			mHandler.post(updateView);
		}

		@Override
		public void ConnectStatus(boolean state, String address) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView connectState;
        TextView instantData;
    }
}