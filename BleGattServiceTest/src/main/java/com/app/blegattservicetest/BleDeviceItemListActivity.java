package com.app.blegattservicetest;

import com.app.blegattservicetest.BleDeviceItemDetailFragment.ViewContents;

import tw.com.umedia.bluetoothle.BleDevice;
import tw.com.umedia.bluetoothle.BleGattDataCallback;
import tw.com.umedia.bluetoothle.BleScanCallback;
import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import tw.com.umedia.bluetoothle.IBleGattClient;
import tw.com.umedia.bluetoothle.IBleScanDevice;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

/**
 * An activity representing a list of BleDeviceItems. This activity has
 * different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a
 * {@link BleDeviceItemDetailActivity} representing item details. On tablets,
 * the activity presents the list of items and item details side-by-side using
 * two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link BleDeviceItemListFragment} and the item details (if present) is a
 * {@link BleDeviceItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link BleDeviceItemListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class BleDeviceItemListActivity extends Activity implements
		BleDeviceItemListFragment.Callbacks {
	private final static String TAG = "BleDeviceItemListActivity";

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private boolean mScanning = false;
	private Handler mHandler= new Handler();
	private BleScanDeviceConnection mScanDeviceConnection;
	private IBleScanDevice mScanDevice;
	private BleDeviceListAdapter mBleDeviceListAdapter;
	private Intent mIntent1;
    private IBleGattClient mClient;
    private BleGattClientConnection mGattClientConnection;
    private Intent mIntent2;
    private DeviceView[] deviceView = new DeviceView[4];
    private BleDeviceItemDetailFragment mFragment;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BleLog.d(TAG, "onCreate()");
		setContentView(R.layout.activity_bledeviceitem_list);

		if (findViewById(R.id.bledeviceitem_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((BleDeviceItemListFragment) getFragmentManager().findFragmentById(
					R.id.bledeviceitem_list)).setActivateOnItemClick(true);
		}

		mBleDeviceListAdapter = ((BleDeviceItemListFragment) getFragmentManager().findFragmentById(
												R.id.bledeviceitem_list)).getListAdapter();
		
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        initService();
        initService2();
	}
	
	private void initService() {
		BleLog.d(TAG, "initService()");
		mScanDeviceConnection = new BleScanDeviceConnection();
    	mIntent1 = new Intent( );
    	mIntent1.setClassName("tw.com.umedia.bluetoothle", "tw.com.umedia.bluetoothle.BluetoothLeService");
    	mIntent1.setAction("tw.com.umedia.bluetoothle.IBLESCANDEVICE");
    	BleLog.d(TAG, "initService() intent = " + "tw.com.umedia.bluetoothle.BluetoothLeServic");
    	BleLog.d(TAG, "initService() action = " + "tw.com.umedia.bluetoothle.IBLESCANDEVICE");
    	boolean ret;
    	ret = bindService(mIntent1, mScanDeviceConnection, Context.BIND_AUTO_CREATE);
    	BleLog.d(TAG, "initService() bound with IBleScanDevice ret=" + ret);
    	if(!ret) {
    		Toast.makeText(this, "Service can not binded", 3);
    		finish();
    		return;
    	} 
    	startService(mIntent1);
    	showDetails("00:00:00:00:00:00");
	}
	
	private void releaseService() {
		BleLog.d(TAG, "releaseService()");
		unbindService(mScanDeviceConnection);
		mScanDeviceConnection = null;
	}
	
	private void showDetails(String address) {
		BleLog.d(TAG, "showDetails()");
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(BleDeviceItemDetailFragment.ARG_MAC_ADDRESS, address);
			mFragment = new BleDeviceItemDetailFragment();
			mFragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.bledeviceitem_detail_container, mFragment)
					.commit();
			mBleDeviceListAdapter.setDeviceCallback(mDeviceCallback);
			deviceView[0] = new DeviceView();
			deviceView[1] = new DeviceView();
			deviceView[2] = new DeviceView();
			deviceView[3] = new DeviceView();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					BleDeviceItemDetailActivity.class);
			detailIntent.putExtra(BleDeviceItemDetailFragment.ARG_MAC_ADDRESS, address);
			startActivity(detailIntent);
		}
	}
	
	/**
	 * Callback method from {@link BleDeviceItemListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String address) {
		BleLog.d(TAG, "onItemSelected()");
		showDetails(address);
		}

	@Override
	protected void onResume() {
		BleLog.d(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause() {
		BleLog.d(TAG, "onPause()");
		mBleDeviceListAdapter.clear();
		super.onPause();
		finish();
	}

	@Override
	protected void onDestroy() {
		BleLog.d(TAG, "onDestroy()");
		releaseService();
		releaseService2();
		stopService(mIntent1);
		stopService(mIntent2);
		super.onDestroy();
	}


	/**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
	
    private class BleScanDeviceConnection implements ServiceConnection {

      public void onServiceConnected(ComponentName name, IBinder boundService) {
    	  mScanDevice = IBleScanDevice.Stub.asInterface((IBinder) boundService);
        try {
        	mScanDevice.registerCallback(mScanCallback);
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
 
	
    private BleScanCallback.Stub mScanCallback = new BleScanCallback.Stub() {

		@Override
		public void getBleDeviceData(String name, String address, int rssi)
				throws RemoteException {
			BleLog.d(TAG, "getBleDeviceData() name = " + name + " address = " + address + " rssi = " + rssi);
			BleDevice mDevice = new BleDevice(name, address, rssi);
			for(int i=0; i < mBleDeviceListAdapter.getCount(); i++) {
				BleDevice device = mBleDeviceListAdapter.getItem(i);
				if(device.getMacAddress().equals(address)) {
					device.setRssi(rssi);
					return;
				}
			}
			mBleDeviceListAdapter.addDevice(mDevice);
			//mBleDeviceListAdapter.notifyDataSetChanged();
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
			BleLog.d(TAG, "updateList()");
			mBleDeviceListAdapter.notifyDataSetChanged();
		}
    	
    };

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	BleLog.d(TAG, "onCreateOptionsMenu()");
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
    	BleLog.d(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.menu_scan:
            	if(!mBleDeviceListAdapter.isEmpty()) {            		
            		mBleDeviceListAdapter.clear();
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

    
	private void initService2() {
		BleLog.d(TAG, "initService2()");
		mGattClientConnection = new BleGattClientConnection();
    	mIntent2 = new Intent( );
    	mIntent2.setClassName("tw.com.umedia.bluetoothle", "tw.com.umedia.bluetoothle.BluetoothLeService");
    	mIntent2.setAction("tw.com.umedia.bluetoothle.IBLEGATTCLIENT");
    	BleLog.d(TAG, "initService() intent = " + "tw.com.umedia.bluetoothle.BluetoothLeServic");
    	BleLog.d(TAG, "initService() action = " + "tw.com.umedia.bluetoothle.IBLEGATTCLIENT");
    	boolean ret;
    	ret = bindService(mIntent2, mGattClientConnection, Context.BIND_AUTO_CREATE);
    	BleLog.d(TAG, "initService() bound with IBleGattClient ret=" + ret);
    	if(!ret) {
    		Toast.makeText(this, "Service can not binded", 3);
    		finish();
    		return;
    	} 
    	startService(mIntent2);
	}
	
	private void releaseService2() {
		BleLog.d(TAG, "releaseService2()");
		for(int i = 0; i < 4; i++) {
			if(deviceView[i].linked) {
				String address = deviceView[i].address;
				if(address != null) {
					DoDisconnect(address, -1);
				} // if address
			} // if linked
		} // for
		unbindService(mGattClientConnection);
		mGattClientConnection = null;
	}
	
	private boolean DoConnect(String address, int position) {
		BleLog.d(TAG, "DoConnect() address = " + address + ", pos = " + position);
		boolean connected = false;
		try {
			connected =  mClient.connect(address);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connected;
	}
	
	private void DoDisconnect(String address, int position) {
		BleLog.d(TAG, "DoDisconnect() address = " + address );
		try {
			mClient.disconnect(address);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private BleDeviceCallback mDeviceCallback = new BleDeviceCallback() {
		private int mPosition;
		private String mAddress;
		private String mName;
		
		@Override
		public boolean Connect(String address, int type) {
			BleLog.d(TAG, "Connect() address = " + address  + ", type = " + type);
			for(int i=0; i < 4; i++) {
				BleLog.d(TAG, "deviceView[" + i + "]");
				if(!deviceView[i].linked || deviceView[i].address.equals(address)) {
					boolean ret = DoConnect(address, i);					
					if(ret) {
						deviceView[i].address = address;
						deviceView[i].linked = true;
						deviceView[i].view = mFragment.getDeviceView(i, type);
						deviceView[i].viewContents = mFragment.getDeviceViewContents((ViewGroup)deviceView[i].view, type);
						BleLog.d(TAG, "deviceView added Ok");
						mPosition = i;
						mName = mBleDeviceListAdapter.getDeviceName(address);
						mAddress = address;
						mFragment.setViewText(mPosition, "bt_address", mAddress);
						mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_DEVICE_NAME, mName);
						BleLog.d(TAG,"position :" + mPosition + ", Name : " + mName + ", BTAddress :" + mAddress);
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean Disconnect(String address) {
			BleLog.d(TAG, "Disconnect() address = " + address);
			for(int i=0; i < 4; i++) {
				if((deviceView[i].address != null) &&(deviceView[i].address.equals(address))) {
					BleLog.d(TAG, "DoDisconnect() address = " + address);
					DoDisconnect(address, i);
					deviceView[i].address = null;
					deviceView[i].linked = false;
					deviceView[i].view = null;
					deviceView[i].viewType = -1;
					mFragment.clearDeviceView(i);
					//return true;
				}
			}
			return true;
		}
		
	};
	
	
	public BleDeviceCallback getDeviceCallback() {
		return mDeviceCallback;
	}
		
    private BleGattDataCallback.Stub mDataCallback = new BleGattDataCallback.Stub() {
		private String mKey;
		private String mValue;
		private String mAddress;
		private int mPosition;		
		
		@Override
		public void DataCallback(String key, String value, String address)
				throws RemoteException {
			BleLog.d(TAG, "DataCallback key = " + key + ", value = " + value + ", address = " + address);
			if(key == null || address == null) return;
			mKey = key;
			mValue = value;
			mAddress = address;			
			//mPosition = position;
			for(int i=0; i < 4; i++) {
				if((deviceView[i].address != null) &&(deviceView[i].address.equals(address))) {
					mPosition = i;
					mHandler.post(updateView);
					return;
				}
			}
			return;
		}
		
		private Runnable updateView = new Runnable() {
			@Override
			public void run() {
				BleLog.d(TAG, "updateView mPosition = " + mPosition + ", mKey = " + mKey + ", mValue = " +  mValue);
				if(mKey.equals(BluetoothLeCharacteristicKey.KEY_MANUFACTURER_NAME)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_MANUFACTURER_NAME, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_DEVICE_NAME)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_DEVICE_NAME, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_HARDWARE_REVISION)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_HARDWARE_REVISION, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_FIRMWARE_REVISION)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_FIRMWARE_REVISION, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_SOFTWARE_REVISION)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_SOFTWARE_REVISION, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_SPEED)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_SPEED, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_CADENCE)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_CADENCE, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_STEPS)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_STEPS, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_CALORIE)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_CALORIE, mValue);
				} else if(mKey.equals(BluetoothLeCharacteristicKey.KEY_TIMESTAMP)) {
					mFragment.setViewText(mPosition, BluetoothLeCharacteristicKey.KEY_TIMESTAMP, mValue);
				}
				return;
			}
		};
		
		@Override
		public void ConnectStatus(final boolean state, final String address)
				throws RemoteException {
			
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					BleLog.d(TAG, "ConnectStatus state = " + state + ", address = " + address);
					for(int i = 0; i < mBleDeviceListAdapter.getCount(); i++) {
						BleDevice device = mBleDeviceListAdapter.getItem(i);
						if(address.equals(device.getMacAddress())) {
							mBleDeviceListAdapter.setButtonText(i, state);
							return;
						}
					}
					return;
				}
				
			});
		}

    };
	
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
    
    private class DeviceView {
    	public String address = null;
    	public View view = null;
    	public ViewContents viewContents = null;
    	public int viewType = -1;
    	public boolean linked = false;
    }
    
    
}
