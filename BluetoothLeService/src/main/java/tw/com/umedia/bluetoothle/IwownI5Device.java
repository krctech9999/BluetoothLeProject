package tw.com.umedia.bluetoothle;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bluetooth.gatt.utils.GattByteBuffer;
import org.bluetooth.service.GattCharacteristic;
import org.bluetooth.service.GattService;

import tw.com.umedia.bluetoothle.gattcharacteristic.FirmwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.GenericGattCharacteristic;
import tw.com.umedia.bluetoothle.gattcharacteristic.HardwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.IwownI5Data3;
import tw.com.umedia.bluetoothle.gattcharacteristic.IwownI5Data5;
import tw.com.umedia.bluetoothle.gattcharacteristic.IwownI5Data7;
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

public class IwownI5Device extends BluetoothLeDevice {
	private static final String TAG = "IwownI5Device";

	private BluetoothDevice mDevice;
	private int type;
	private boolean autorun = false;
	private List<GenericGattCharacteristic> mCharacteristics = new ArrayList<GenericGattCharacteristic>();
	private IwownI5Data7 mData7;
	private IwownI5Data5 mData5;
	private IwownI5Data3 mData3;
	private int writepass = 0;
	private int writetimestamp = 0;

	@Override
	public HashMap<String, Object> processNext(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic, int action) {
		// TODO Auto-generated method stub
		BleLog.i(TAG, "processNext() : " + action);
		if(action == ACTION_ONREAD) {
			if(characteristic.getUuid().equals(GattCharacteristic.I5_DATA7)) {
				String str = HexUtils.array(characteristic.getValue());
				BleLog.d(TAG,  "Stepper data : " + str);
				HashMap<String, Object> data = mData7.getData();
				Log.d(TAG, "timestamp = " + data.get(BluetoothLeCharacteristicKey.KEY_TIMESTAMP));
				Log.d(TAG, "steps = " + data.get(BluetoothLeCharacteristicKey.KEY_STEPS));
				Log.d(TAG, "distance = " + data.get(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE));
				Log.d(TAG, "calorie = " + data.get(BluetoothLeCharacteristicKey.KEY_CALORIE));
				// read next data
				BluetoothGattService service = getGattService(GattService.I5_DATA_SERVICE);
				if(writepass < 2) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.I5_DATA7);
					gatt.readCharacteristic(chara);
					//BleLog.d(TAG,  "Time Sync data : " + str);
					writepass ++;
				} else {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.I5_DATA6);
					byte[] code = { 0x65, 0x74, 0x2D, 0x37 };
					chara.setValue(code);
					gatt.writeCharacteristic(chara);
					Log.d(TAG, "Write Passwords = " + HexUtils.array(code));
					writepass = 0;
				}
				return data;
			} else if(characteristic.getUuid().equals(GattCharacteristic.I5_DATA5)) {
				String str = HexUtils.array(characteristic.getValue());
				BleLog.d(TAG,  "Time Sync data : " + str);
				HashMap<String, Object> data = mData5.getData();
				Log.d(TAG, "timestamp = " + data.get("timestamp"));
				BluetoothGattService service = getGattService(GattService.I5_DATA_SERVICE);
				BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.I5_DATA7);
				gatt.readCharacteristic(chara);
			} else if(characteristic.getUuid().equals(GattCharacteristic.I5_DATA3)) {
				String str = HexUtils.array(characteristic.getValue());
				BleLog.d(TAG,  "Time Sync data : " + str);
				HashMap<String, Object> data = mData3.getData();
				Log.d(TAG, "timestamp = " + data.get("timestamp"));
				BluetoothGattService service = getGattService(GattService.I5_DATA_SERVICE);
				BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.I5_DATA7);
				gatt.readCharacteristic(chara);
			}
		} else if(action == ACTION_ONWRITE) {
			BluetoothGattService service = getGattService(GattService.I5_DATA_SERVICE);
			if(service != null) {
				BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.I5_DATA7);
				gatt.readCharacteristic(chara);
			} // if(service != null) 
		} // if(action == ACTION_ONREAD)
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
		BluetoothGattService service = getGattService(GattService.I5_DATA_SERVICE);
		if(service != null) {
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(GattCharacteristic.I5_DATA6);
			byte[] code = { 0x65, 0x74, 0x2D, 0x37 };
			characteristic.setValue(code);
			gatt.writeCharacteristic(characteristic);
		} // if(service != null)
	}

	@Override
	public void processEnd(BluetoothGatt gatt, boolean flag) {
		// TODO Auto-generated method stub
		BleLog.i(TAG, "processEnd()");
	}

	public IwownI5Device(BluetoothDevice device) {
		BleLog.i(TAG, "IwownI5Device()");
		mDevice = device;
		type = BluetoothLeDevice.DEVICETYPE_I5DEVICE;
		autorun = true;
	}
	
	@Override
	public void setGattServices(BluetoothGatt gatt) {	
		BleLog.i(TAG, "setGattServices");
		mServices = gatt.getServices();
		initCharacteristics();
		if(autorun) {
			processInit(gatt, true);
		}
	}
	
	private void initCharacteristics() {
		BleLog.i(TAG, "initCharacteristics");
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
/*		
		if(GattCharacteristic.I5_CONFIG.equals(gattcharacteristic.getUuid())) {
			mManufacturerName = new ManufacturerName(gattservice, gattcharacteristic);
			return;
		} // MANUFACTURER_NAME_STRING
		
		if(GattCharacteristic.I5_DATA1.equals(gattcharacteristic.getUuid())) {
			mModelNumber = new ModelNumber(gattservice, gattcharacteristic);
			return;
		} // MODEL_NUMBER_STRING

		if(GattCharacteristic.I5_DATA2.equals(gattcharacteristic.getUuid())) {
			mSerialNumber = new SerialNumber(gattservice, gattcharacteristic);
			return;
		} // SERIAL_NUMBER_STRING

		if(GattCharacteristic.I5_DATA3.equals(gattcharacteristic.getUuid())) {
			mHardwareRevision = new HardwareRevision(gattservice, gattcharacteristic);
			return;
		} // HARDWARE_REVISION_STRING

		if(GattCharacteristic.I5_DATA4.equals(gattcharacteristic.getUuid())) {
			mFirmwareRevision = new FirmwareRevision(gattservice, gattcharacteristic);
			return;
		} // FIRMWARE_REVISION_STRING

		if(GattCharacteristic.I5_DATA5.equals(gattcharacteristic.getUuid())) {
			mSoftwareRevision = new SoftwareRevision(gattservice, gattcharacteristic);
			return;
		} // SOFTWARE_REVISION_STRING

		if(GattCharacteristic.I5_DATA6.equals(gattcharacteristic.getUuid())) {
			mRSCMeasurement = new RSCMeasurement(gattservice, gattcharacteristic);
			return;
		} // RSC_MEASUREMENT

		if(GattCharacteristic.I5_DATA7.equals(gattcharacteristic.getUuid())) {
			mRSCFeature = new RSCFeature(gattservice, gattcharacteristic);
			return;
		} // RSC_FEATURE

		if(GattCharacteristic.I5_DATA8.equals(gattcharacteristic.getUuid())) {
			mSensorLocation = new SensorLocation(gattservice, gattcharacteristic);
			return;
		} // SENSOR_LOCATION

		if(GattCharacteristic.I5_DATA9.equals(gattcharacteristic.getUuid())) {
			mUmediaKeyEvent = new UmediaKeyEvent(gattservice, gattcharacteristic);
			return;
		} // UMEDIA_KEY_EVENT
*/		
		BleLog.i(TAG, "checkCharacteristic()");
		if(GattCharacteristic.I5_DATA7.equals(gattcharacteristic.getUuid())) {
			mData7 = new IwownI5Data7(gattservice, gattcharacteristic);
			return;
		} // Stepper Data
		
		if(GattCharacteristic.I5_DATA5.equals(gattcharacteristic.getUuid())) {
			mData5 = new IwownI5Data5(gattservice, gattcharacteristic);
			return;
		} // Time Sync Data
		
		if(GattCharacteristic.I5_DATA3.equals(gattcharacteristic.getUuid())) {
			mData3 = new IwownI5Data3(gattservice, gattcharacteristic);
			return;
		} // Time Sync Data
		
		GenericGattCharacteristic tempCharacteristic = new GenericGattCharacteristic(gattservice, gattcharacteristic);
		mCharacteristics.add(tempCharacteristic);		
	}
}
