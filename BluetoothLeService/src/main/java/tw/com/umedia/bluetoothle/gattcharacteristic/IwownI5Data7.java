package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.util.HashMap;

import org.bluetooth.gatt.utils.GattByteBuffer;

import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import tw.com.umedia.bluetoothle.utils.BleLog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class IwownI5Data7 extends DeviceGattCharacteristic  {
	private final static String TAG = "IwownI5Data7";

	private long timestamp = 0L;
	private long steps = 0L;
	private float distance = 0.0f;
	private float calorie = 0.0f;
	private long prevTimestamp = 0L;
	private long prevSteps = 0L;
	private float prevDistance = 0.0f;
	private float prevCalorie = 0.0f;
	
	public IwownI5Data7(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, Object> getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		//byte[] values = mGattCharacteristic.getValue();
		long mTimestamp =  mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0) & 0x00000000ffffffffL;;
		long mSteps =  mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 4) & 0x00000000ffffffffL;
		float mDistance = mGattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 8)/ 10.0f;
		float mCalorie = mGattCharacteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 12)/ 10.0f;
		if((mSteps != prevSteps) || (mDistance != prevDistance) || (mCalorie != prevCalorie)) {
			float diffTime = (mTimestamp - timestamp);
			float diffDistance = (mDistance - distance);
			BleLog.d(TAG, "time diff = " + diffTime + " , distance diff = " + diffDistance);
			prevTimestamp = timestamp;
			prevSteps = steps;
			prevDistance = prevDistance;
			prevCalorie = calorie;
		}
		timestamp = mTimestamp;
		steps = mSteps;
		distance = mDistance;
		calorie = mCalorie;
		data.put(BluetoothLeCharacteristicKey.KEY_TIMESTAMP, timestamp);
		data.put(BluetoothLeCharacteristicKey.KEY_STEPS, steps);
		data.put(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE, distance);
		data.put("calorie", calorie);
		return data;
	}

	public long getTimeStamp() {
		return timestamp;
	}
	
	public long getSteps() {
		return steps;
	}
	
	public float getDistanceInMeter() {
		return distance;
	}
	
	public float getDistanceInKiloMeter() {
		return distance/ 1000.0f;
	}
	
	public float getCalorie() {
		return calorie;
	}
	
}
