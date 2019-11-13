package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.util.HashMap;

import org.bluetooth.gatt.utils.GattByteBuffer;

import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class M04LData2 extends DeviceGattCharacteristic {
	private final static String TAG = "M04LData2";
	
	private long steps = 0L;
	private float distance = 0.0f;
	private float calorie = 0.0f;

	public M04LData2(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, Object> getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		long timestamp = System.currentTimeMillis();
		byte[] values = mGattCharacteristic.getValue();
		byte cmd = values[0];
		
		int tempv = (values[6] < 0) ? (values[6]&0x0f)+128: values[6];
		tempv *= 256;
		tempv += (values[7] < 0) ? (values[7]&0x0f)+128: values[7];
		
		steps = tempv;
		BleLog.d(TAG, "steps = " + steps);
		distance = steps * 0.45f;
		calorie = steps /3.0f;
		
		data.put(BluetoothLeCharacteristicKey.KEY_TIMESTAMP, timestamp);
		data.put(BluetoothLeCharacteristicKey.KEY_STEPS, steps);
		data.put(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE, distance);
		data.put(BluetoothLeCharacteristicKey.KEY_CALORIE,calorie);
		return data;
	}
	
	public long getSteps() {
		return steps;
	}
	
	public float getDistanceInMeter() {
		return distance;
	}
	
	public float getDistanceInKiloMeter() {
		return distance/1000.0f;
	}
	
	public float getCalorie() {
		return calorie;
	}

}
