package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.util.HashMap;

import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class RSCFeature extends DeviceGattCharacteristic {
	
	private final static int InstantaneousStrideLengthMeasurementSupported=0x0001;
	private final static int TotalDistanceMeasurementSupported=0x0002;
	private final static int WalkingorRunningStatusSupported=0x0004;
	private final static int CalibrationProcedureSupported=0x0008;
	private final static int MultipleSensorLocationsSupported=0x0010;

	private int feature;

	public RSCFeature(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
	}

	@Override
	public HashMap<String, Object> getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		feature= mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
		
		if(getInstantaneousStrideLengthMeasurementSupported()) {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_STRIDE_LENGTH_SUPPORTED, "true");
		} else {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_STRIDE_LENGTH_SUPPORTED, "false");
		}
		
		if(getTotalDistanceMeasurementSupported()) {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_TOTAL_DISTANCE_SUPPORTED, "true");
		} else {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_TOTAL_DISTANCE_SUPPORTED, "false");
		}
		
		if(getWalkingorRunningStatusSupported()) {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_RUNNING_STATUS_SUPPORTED, "true");
		} else {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_RUNNING_STATUS_SUPPORTED, "false");
		}
		
		if(getCalibrationProcedureSupported()) {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_CALIBRATION_SUPPORTED, "true");
		} else {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_CALIBRATION_SUPPORTED, "false");
		}
		
		if(getMultipleSensorLocationsSupported()) {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_MULTIPLE_SENSOR_SUPPORTED, "true");
		} else {
			data.put(BluetoothLeCharacteristicKey.KEY_RSC_FEATURE_MULTIPLE_SENSOR_SUPPORTED, "false");
		}
		return data;
	}
	
	public boolean getInstantaneousStrideLengthMeasurementSupported() {
		return (feature&InstantaneousStrideLengthMeasurementSupported) > 0;
	}

	public boolean getTotalDistanceMeasurementSupported() {
		return (feature&TotalDistanceMeasurementSupported) > 0;
	}
	
	public boolean getWalkingorRunningStatusSupported() {
		return (feature&WalkingorRunningStatusSupported) > 0;
	}

	public boolean getCalibrationProcedureSupported() {
		return (feature&CalibrationProcedureSupported) > 0;
	}

	public boolean getMultipleSensorLocationsSupported() {
		return (feature&MultipleSensorLocationsSupported) > 0;
	}
	
}
