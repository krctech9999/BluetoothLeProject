package com.app.blegattservicetest;

import tw.com.umedia.bluetoothle.BleGattDataCallback;
import tw.com.umedia.bluetoothle.IBleGattClient;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * An activity representing a single BleDeviceItem detail screen. This activity
 * is only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * {@link BleDeviceItemListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link BleDeviceItemDetailFragment}.
 */
public class BleDeviceItemDetailActivity extends Activity {
	private final static String TAG = "BleDeviceItemDetailActivity";
	
    private IBleGattClient mClient;
    private BleGattClientConnection mGattClientConnection;
    private Intent mIntent;
    private DeviceView[] deviceView = new DeviceView[4];
    private BleDeviceItemDetailFragment mFragment;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bledeviceitem_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(
					BleDeviceItemDetailFragment.ARG_MAC_ADDRESS,
					getIntent().getStringExtra(
							BleDeviceItemDetailFragment.ARG_MAC_ADDRESS));
			mFragment = new BleDeviceItemDetailFragment();
			mFragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.bledeviceitem_detail_container, mFragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			navigateUpTo(new Intent(this, BleDeviceItemListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initService() {
		BleLog.d(TAG, "initService()");
		mGattClientConnection = new BleGattClientConnection();
    	mIntent = new Intent( );
    	mIntent.setClassName("tw.com.umedia.bluetoothle", "tw.com.umedia.bluetoothle.BluetoothLeService");
    	mIntent.setAction("tw.com.umedia.bluetoothle.IBLESCANDEVICE");
    	BleLog.d(TAG, "initService() intent = " + "tw.com.umedia.bluetoothle.BluetoothLeServic");
    	BleLog.d(TAG, "initService() action = " + "tw.com.umedia.bluetoothle.IBLEGATTCLIENT");
    	boolean ret;
    	ret = bindService(mIntent, mGattClientConnection, Context.BIND_AUTO_CREATE);
    	BleLog.d(TAG, "initService() bound with IBleScanDevice ret=" + ret);
    	if(!ret) {
    		Toast.makeText(this, "Service can not binded", 3);
    		finish();
    		return;
    	} 
    	startService(mIntent);
	}
	
	private void releaseService() {
		BleLog.d(TAG, "releaseService()");
		unbindService(mGattClientConnection);
		mGattClientConnection = null;
	}
	
	
	
	private boolean DoConnect(String address) {
		BleLog.d(TAG, "DoConnect() address = " + address );
		boolean connected = false;
		try {
			connected =  mClient.connect(address);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connected;
	}
	
	private void DoDisconnect(String address) {
		BleLog.d(TAG, "DoDisconnect() address = " + address );
		try {
			mClient.disconnect(address);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private BleDeviceCallback mCallback = new BleDeviceCallback() {

		@Override
		public boolean Connect(String address, int type) {
			for(int i=0; i < 4; i++) {
				if(!deviceView[i].linked) {
					boolean ret = DoConnect(address);					
					if(ret) {
						deviceView[i].address = address;
						deviceView[i].linked = true;
						deviceView[i].view = mFragment.getDeviceView(i, type);
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean Disconnect(String address) {
			for(int i=0; i < 4; i++) {
				if(deviceView[i].address.equals(address)) {
					DoConnect(address);
					deviceView[i].address = null;
					deviceView[i].linked = false;
					deviceView[i].view = null;
					deviceView[i].viewType = -1;
					return true;
				}
			}
			return false;
		}
		
	};
	
	public BleDeviceCallback getCallback() {
		return mCallback;
	}
		
    private BleGattDataCallback.Stub mDataCallback = new BleGattDataCallback.Stub() {
		
		@Override
		public void DataCallback(String key, String value, String address)
				throws RemoteException {
			BleLog.d(TAG, "key = " + key + ", value = " + value + ", address = " + address);
			// TODO Auto-generated method stub
			if(key == null || address == null) return;
			//mViewholder = mLeDeviceListAdapter.getItemViewHolder(position);
			//updateText = "key = " + key + ", value = " + value;
			//mHandler.post(updateView);
		}

		@Override
		public void ConnectStatus(boolean state, String address) throws RemoteException {
			
			
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
    	public int viewType = -1;
    	public boolean linked = false;
    }
	
}
