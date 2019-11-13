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
import tw.com.umedia.bluetoothle.gattcharacteristic.ManufacturerName;
import tw.com.umedia.bluetoothle.gattcharacteristic.ModelNumber;
import tw.com.umedia.bluetoothle.gattcharacteristic.RSCFeature;
import tw.com.umedia.bluetoothle.gattcharacteristic.RSCMeasurement;
import tw.com.umedia.bluetoothle.gattcharacteristic.SensorLocation;
import tw.com.umedia.bluetoothle.gattcharacteristic.SerialNumber;
import tw.com.umedia.bluetoothle.gattcharacteristic.SoftwareRevision;
import tw.com.umedia.bluetoothle.gattcharacteristic.UmediaKeyEvent;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

public class UMHDDevice extends BluetoothLeDevice {
	private static final String TAG="UMHDDevice";

	private BluetoothDevice mDevice;
	private int type;
	
	private ManufacturerName mManufacturerName;
	private ModelNumber mModelNumber;
	private SerialNumber mSerialNumber;
	private HardwareRevision mHardwareRevision;
	private FirmwareRevision mFirmwareRevision;
	private SoftwareRevision mSoftwareRevision;
	private RSCMeasurement mRSCMeasurement;
	private RSCFeature mRSCFeature;
	private SensorLocation mSensorLocation;
	private UmediaKeyEvent mUmediaKeyEvent;
	private List<GenericGattCharacteristic> mCharacteristics = new ArrayList<GenericGattCharacteristic>();
	private boolean autorun = false;
	private boolean stopDevice =  false;
	
	@Override
	public HashMap<String, Object> processNext(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
			int action) {
		BleLog.i(TAG, "processNext()");
		HashMap<String, Object> ret = null;
		if(stopDevice) return null;
		if(action == ACTION_ONREAD) {
			if(characteristic.getUuid().equals(GattCharacteristic.MANUFACTURER_NAME_STRING)) {
				 ret = mManufacturerName.getData();
				 BleLog.d(TAG, "M = " +characteristic.getStringValue(0));
				BleLog.d(TAG, "Manufacturer Name : " +ret.get(BluetoothLeCharacteristicKey.KEY_MANUFACTURER_NAME));
				// read next data
				BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.MODEL_NUMBER_STRING);
					gatt.readCharacteristic(chara);
				} // if(service != null) 
			} else if(characteristic.getUuid().equals(GattCharacteristic.MODEL_NUMBER_STRING)) {
				 ret = mModelNumber.getData();
				BleLog.d(TAG, "Model Number : " +ret.get(BluetoothLeCharacteristicKey.KEY_MODEL_NAME));
				// read next data
				BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.SERIAL_NUMBER_STRING);
					gatt.readCharacteristic(chara);
				} // if(service != null)
			} else if(characteristic.getUuid().equals(GattCharacteristic.SERIAL_NUMBER_STRING)) {
				 ret = mSerialNumber.getData();
				BleLog.d(TAG, "Serial Number : " +ret.get(BluetoothLeCharacteristicKey.KEY_SERIAL_NUMBER));
				// read next data
				BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.HARDWARE_REVISION_STRING);
					gatt.readCharacteristic(chara);
				} // if(service != null)
			} else if(characteristic.getUuid().equals(GattCharacteristic.HARDWARE_REVISION_STRING)) {
				ret = mHardwareRevision.getData();
				BleLog.d(TAG, "Hardware Revision : " +ret.get(BluetoothLeCharacteristicKey.KEY_HARDWARE_REVISION));
				// read next data
				BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.FIRMWARE_REVISION_STRING);
					gatt.readCharacteristic(chara);
				} // if(service != null)
			} else if(characteristic.getUuid().equals(GattCharacteristic.FIRMWARE_REVISION_STRING)) {
				ret = mFirmwareRevision.getData();
				BleLog.d(TAG, "Firmware Revision : " +ret.get(BluetoothLeCharacteristicKey.KEY_FIRMWARE_REVISION));
				// read next data
				BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.SOFTWARE_REVISION_STRING);
					gatt.readCharacteristic(chara);
				} // if(service != null)
			} else if(characteristic.getUuid().equals(GattCharacteristic.SOFTWARE_REVISION_STRING)) {
				ret = mSoftwareRevision.getData();
				BleLog.d(TAG, "Software Revision : " +ret.get(BluetoothLeCharacteristicKey.KEY_SOFTWARE_REVISION));
				// read next data
				BluetoothGattService service = getGattService(GattService.RUNNING_SPEED_AND_CADENCE);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.RSC_FEATURE);
					gatt.readCharacteristic(chara);
				} // if(service != null)
				return ret;
			} else if(characteristic.getUuid().equals(GattCharacteristic.RSC_FEATURE)) {
				ret = mRSCFeature.getData();
				BleLog.d(TAG, "Stride Length Supported : " +ret.get(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_STRIDE_LENGTH_SUPPORTED));
				BleLog.d(TAG, "Total Distance Supported : " +ret.get(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_TOTAL_DISTANCE_SUPPORTED));
				BleLog.d(TAG, "Walking or Running Status Supported : " +ret.get(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_RUNNING_STATUS_SUPPORTED));
				BleLog.d(TAG, "Calibration Procedure Supported : " +ret.get(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_CALIBRATION_SUPPORTED));
				BleLog.d(TAG, "Multiple Sensor Locations Supported : " +ret.get(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_MULTIPLE_SENSOR_SUPPORTED));
				// read next data
				BluetoothGattService service = getGattService(GattService.RUNNING_SPEED_AND_CADENCE);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.SENSOR_LOCATION);
					gatt.readCharacteristic(chara);
				} // if(service != null)
			} else if(characteristic.getUuid().equals(GattCharacteristic.SENSOR_LOCATION)) {
				 ret = mSensorLocation.getData();
				BleLog.d(TAG, "Sensor Location : " +ret.get(BluetoothLeCharacteristicKey.KEY_SENSOR_LOCATION));
				// send as notification for RSC Measurement 
				BluetoothGattService service = getGattService(GattService.RUNNING_SPEED_AND_CADENCE);
				if(service != null) {
					BluetoothGattCharacteristic chara = service.getCharacteristic(GattCharacteristic.RSC_MEASUREMENT);
					setCharacteristicNotification(gatt, chara, true);
				} // if(service != null)
			} // if(characteristic.getUuid().equals(GattCharacteristic.SENSOR_LOCATION))
			return ret;
		} // if(action == ACTION_ONREAD) 
		if(action == ACTION_ONREADCHANGED) {
			if(characteristic.getUuid().equals(GattCharacteristic.RSC_MEASUREMENT)) {
				 ret = mRSCMeasurement.getData();
				BleLog.d(TAG, "StrideLengthFlag : " + ret.get(BluetoothLeCharacteristicKey.KEY_STRIDE_LENGTH_FLAG));
				BleLog.d(TAG, "TotalDistanceFlag : " + ret.get(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE_FLAG));
				BleLog.d(TAG, "RunningStatusFlag : " + ret.get(BluetoothLeCharacteristicKey.KEY_RUNNING_STATUS_FLAG));
				BleLog.d(TAG, "Speed : " + ret.get(BluetoothLeCharacteristicKey.KEY_SPEED));
				BleLog.d(TAG, "Cadence : " + ret.get(BluetoothLeCharacteristicKey.KEY_CADENCE));
				BleLog.d(TAG, "StrideLength : " + ret.get(BluetoothLeCharacteristicKey.KEY_STRIDE_LENGTH));
				BleLog.d(TAG, "TotalDistance : " + ret.get(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE));
				return ret;
			} else if(characteristic.getUuid().equals(GattCharacteristic.UMEDIA_KEY_EVENT)) {
				ret = mUmediaKeyEvent.getData();
				BleLog.d(TAG, "KeyEvent : " + ret.get(BluetoothLeCharacteristicKey.KEY_UMHD_KEY_EVENT));
				return ret;
			} // if(characteristic.getUuid().equals(GattCharacteristic.RSC_MEASUREMENT))
		}  // if(action == ACTION_ONREADCHANGED) 
		return null;
	}

	@Override
	public void processDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int action) {
		BleLog.i(TAG, "processDescriptor()");
		if(action == ACTION_DESCRIPTOR_ONWRITE) {
			BluetoothGattCharacteristic chara = descriptor.getCharacteristic();
			if(chara.getUuid().equals(GattCharacteristic.RSC_MEASUREMENT)) {
				// send as notification forUMEDIA KEY EVENT
				BluetoothGattService service = getGattService(GattService.UMEDIA_KEY_EVENT);
				if(service != null) {
					BluetoothGattCharacteristic chara2 = service.getCharacteristic(GattCharacteristic.UMEDIA_KEY_EVENT);
					setCharacteristicNotification(gatt, chara2, true);
				} // 	if(service != null)
			} // if(chara.getUuid().equals(GattCharacteristic.RSC_MEASUREMENT))
		} // if(action == ACTION_DESCRIPTOR_ONWRITE)
		return;
	}

	@Override
	public void processInit(BluetoothGatt gatt, boolean flag) {
		BleLog.i(TAG, "processInit()");
		BluetoothGattService service = getGattService(GattService.DEVICE_INFORMATION);
		if(service != null) {
			BluetoothGattCharacteristic characteristic = service.getCharacteristic(GattCharacteristic.MANUFACTURER_NAME_STRING);
			gatt.readCharacteristic(characteristic);
		} // if(service != null)
		return;
	}

	@Override
	public void processEnd(BluetoothGatt gatt, boolean flag) {
		BleLog.i(TAG, "processEnd()");
		stopDevice = true;
		return;		
	}
	
	public UMHDDevice(BluetoothDevice device) {
		BleLog.i(TAG, "UMHDDevice()");
		mDevice = device;
		type = BluetoothLeDevice.DEVICETYPE_UMHDDEVICE;
		autorun = true;
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
		if(GattCharacteristic.MANUFACTURER_NAME_STRING.equals(gattcharacteristic.getUuid())) {
			mManufacturerName = new ManufacturerName(gattservice, gattcharacteristic);
			return;
		} // MANUFACTURER_NAME_STRING
		
		if(GattCharacteristic.MODEL_NUMBER_STRING.equals(gattcharacteristic.getUuid())) {
			mModelNumber = new ModelNumber(gattservice, gattcharacteristic);
			return;
		} // MODEL_NUMBER_STRING

		if(GattCharacteristic.SERIAL_NUMBER_STRING.equals(gattcharacteristic.getUuid())) {
			mSerialNumber = new SerialNumber(gattservice, gattcharacteristic);
			return;
		} // SERIAL_NUMBER_STRING

		if(GattCharacteristic.HARDWARE_REVISION_STRING.equals(gattcharacteristic.getUuid())) {
			mHardwareRevision = new HardwareRevision(gattservice, gattcharacteristic);
			return;
		} // HARDWARE_REVISION_STRING

		if(GattCharacteristic.FIRMWARE_REVISION_STRING.equals(gattcharacteristic.getUuid())) {
			mFirmwareRevision = new FirmwareRevision(gattservice, gattcharacteristic);
			return;
		} // FIRMWARE_REVISION_STRING

		if(GattCharacteristic.SOFTWARE_REVISION_STRING.equals(gattcharacteristic.getUuid())) {
			mSoftwareRevision = new SoftwareRevision(gattservice, gattcharacteristic);
			return;
		} // SOFTWARE_REVISION_STRING

		if(GattCharacteristic.RSC_MEASUREMENT.equals(gattcharacteristic.getUuid())) {
			mRSCMeasurement = new RSCMeasurement(gattservice, gattcharacteristic);
			return;
		} // RSC_MEASUREMENT

		if(GattCharacteristic.RSC_FEATURE.equals(gattcharacteristic.getUuid())) {
			mRSCFeature = new RSCFeature(gattservice, gattcharacteristic);
			return;
		} // RSC_FEATURE

		if(GattCharacteristic.SENSOR_LOCATION.equals(gattcharacteristic.getUuid())) {
			mSensorLocation = new SensorLocation(gattservice, gattcharacteristic);
			return;
		} // SENSOR_LOCATION

		if(GattCharacteristic.UMEDIA_KEY_EVENT.equals(gattcharacteristic.getUuid())) {
			mUmediaKeyEvent = new UmediaKeyEvent(gattservice, gattcharacteristic);
			return;
		} // UMEDIA_KEY_EVENT
		
		GenericGattCharacteristic tempCharacteristic = new GenericGattCharacteristic(gattservice, gattcharacteristic);
		mCharacteristics.add(tempCharacteristic);		
	}
	
}
