package tw.com.umedia.bluetoothle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bluetooth.service.GattCharacteristic;
import org.bluetooth.service.GattService;

import tw.com.umedia.bluetoothle.gattcharacteristic.FirmwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.GenericGattCharacteristic;
import tw.com.umedia.bluetoothle.gattcharacteristic.HardwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.M04LData2;
import tw.com.umedia.bluetoothle.gattcharacteristic.ManufacturerName;
import tw.com.umedia.bluetoothle.gattcharacteristic.ModelNumber;
import tw.com.umedia.bluetoothle.gattcharacteristic.RSCFeature;
import tw.com.umedia.bluetoothle.gattcharacteristic.RSCMeasurement;
import tw.com.umedia.bluetoothle.gattcharacteristic.SensorLocation;
import tw.com.umedia.bluetoothle.gattcharacteristic.SerialNumber;
import tw.com.umedia.bluetoothle.gattcharacteristic.SoftwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.UmediaKeyEvent;
import tw.com.umedia.bluetoothle.utils.BleLog;
import tw.com.umedia.bluetoothle.utils.HexUtils;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

public class M04LDevice extends BluetoothLeDevice {
	private final static String TAG="M04LDevice";
	
	private BluetoothDevice mDevice;
	private List<GenericGattCharacteristic> mCharacteristics = new ArrayList<GenericGattCharacteristic>();
	private M04LData2 mM04LData2;
	private int type;
	private boolean autorun = false;
	
	@Override
	public HashMap<String, Object> processNext(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int action) {
		BleLog.i(TAG, "processNext()");
		if(action == ACTION_ONWRITE) {
			if(GattCharacteristic.M04L_CHARACTERISTIC2_1.equals(characteristic.getUuid())) {
				BluetoothGattService service = getGattService(GattService.M04L_SERVICE2);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.M04L_CHARACTERISTIC2_2);
					setCharacteristicNotification(gatt, chara, true);
				}				
			}
		} else if(action == ACTION_ONREADCHANGED) {
			if(GattCharacteristic.M04L_CHARACTERISTIC2_2.equals(characteristic.getUuid())) {
				HashMap<String, Object> data = mM04LData2.getData();
				Log.d(TAG, "timestamp = " + data.get(BluetoothLeCharacteristicKey.KEY_TIMESTAMP));
				Log.d(TAG, "steps = " + data.get(BluetoothLeCharacteristicKey.KEY_STEPS));
				Log.d(TAG, "distance = " + data.get(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE));
				Log.d(TAG, "calorie = " + data.get(BluetoothLeCharacteristicKey.KEY_CALORIE));
				return data;
			}
		}
		return null;
	}

	@Override
	public void processDescriptor(BluetoothGatt gatt,
			BluetoothGattDescriptor descriptor, int action) {
		// TODO Auto-generated method stub
		BleLog.i(TAG, "processDescriptor()");
	}

	@Override
	public void processInit(BluetoothGatt gatt, boolean flag) {
		// TODO Auto-generated method stub
		BleLog.i(TAG, "processInit()");
		BluetoothGattService service = getGattService(GattService.M04L_SERVICE2);
		if(service != null) {
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(GattCharacteristic.M04L_CHARACTERISTIC2_1);
			byte[] code = { (byte) 0xA4, (byte) 0xB1, (byte) 0xB2};
			characteristic.setValue(code);
			gatt.writeCharacteristic(characteristic);
		} // if(service != null)
	}

	@Override
	public void processEnd(BluetoothGatt gatt, boolean flag) {
		// TODO Auto-generated method stub
		BleLog.i(TAG, "processEnd()");
	}

	@Override
	public void setGattServices(BluetoothGatt gatt) {
		BleLog.i(TAG, "setGattServices()");
		mServices = gatt.getServices();
		initCharacteristics();
		if(autorun) {
			processInit(gatt, true);
		}
	}
	
	public M04LDevice(BluetoothDevice device) {
		BleLog.i(TAG, "M04LDevice()");
		mDevice = device;
		type = BluetoothLeDevice.DEVICETYPE_M04LDEVICE;
		autorun = true;
	}
	
	private void initCharacteristics() {
		BleLog.i(TAG, "initCharacteristics()");
		for(BluetoothGattService service : mServices) {
			List<BluetoothGattCharacteristic> mCharacteristics = service.getCharacteristics();
			for(BluetoothGattCharacteristic characteristic : mCharacteristics) {
				checkCharacteristic(service, characteristic);
			}
		}
	}
	
	private BluetoothGattService getGattService(UUID uuid) {
		BleLog.i(TAG, "getGattService()");
		for(BluetoothGattService service : mServices) {
			if(uuid.equals(service.getUuid())) {
				return service;
			} // if(uuid.equals(service.getUuid())) 
		} // for(BluetoothGattService service : mServices) 
		return null;
	}

	private void checkCharacteristic(BluetoothGattService gattservice, BluetoothGattCharacteristic gattcharacteristic) {
		BleLog.i(TAG, "checkCharacteristic()");
		if(GattCharacteristic.M04L_CHARACTERISTIC2_1.equals(gattcharacteristic.getUuid())) {
			return;
		} 
		
		if(GattCharacteristic.M04L_CHARACTERISTIC2_2.equals(gattcharacteristic.getUuid())) {
			mM04LData2 = new M04LData2(gattservice, gattcharacteristic);
			return;
		} 
		
		GenericGattCharacteristic tempCharacteristic = new GenericGattCharacteristic(gattservice, gattcharacteristic);
		mCharacteristics.add(tempCharacteristic);		
	}
	
}
