package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.bluetooth.gatt.utils.GattByteBuffer;

import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class RSCMeasurement extends DeviceGattCharacteristic {

	private final static int maskInstantaneousStrideLengthPresent = 0x01;
	private final static int maskTotalDistancePresent = 0x02;
	private final static int maskWalkingorRunningStatusbits = 0x04;
		
	private int Flags = 0;
	private boolean flagInstantaneousStrideLengthPresent = false;
	private boolean flagTotalDistancePresent = false;
	private boolean flagWalkingorRunningStatusbits = false;
	private int InstantaneousSpeed = 0;
	private int InstantaneousCadence = 0;
	private int InstantaneousStrideLength = 0;
	private long TotalDistance = 0; 

	public RSCMeasurement(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
	}

	@Override
	public HashMap<String, Object> getData() {
		int startb = 0;
		HashMap<String, Object> data = new HashMap<String, Object>();

		Flags = mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		if( (Flags&maskInstantaneousStrideLengthPresent) > 0 ) {
			flagInstantaneousStrideLengthPresent = true;
			data.put(BluetoothLeCharacteristicKey.KEY_STRIDE_LENGTH_FLAG, "true");
		} else {
			flagInstantaneousStrideLengthPresent = false;
			data.put(BluetoothLeCharacteristicKey.KEY_STRIDE_LENGTH_FLAG, "false");
		}
		
		if( (Flags&maskTotalDistancePresent) > 0 ) {
			flagTotalDistancePresent = true;
			data.put(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE_FLAG, "true");
		} else {
			flagTotalDistancePresent = false;
			data.put(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE_FLAG, "false");
		}
		
		if( (Flags&maskWalkingorRunningStatusbits) > 0 ) {
			flagWalkingorRunningStatusbits = true;
			data.put(BluetoothLeCharacteristicKey.KEY_RUNNING_STATUS_FLAG, "true");
		} else {
			flagWalkingorRunningStatusbits = false;
			data.put(BluetoothLeCharacteristicKey.KEY_RUNNING_STATUS_FLAG, "false");
		}
		
		InstantaneousSpeed = mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
		data.put(BluetoothLeCharacteristicKey.KEY_SPEED, (float)InstantaneousSpeed/256f);
		InstantaneousCadence = mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
		data.put(BluetoothLeCharacteristicKey.KEY_CADENCE, InstantaneousCadence);
		
		startb = 4;		
		if(flagInstantaneousStrideLengthPresent) {
			InstantaneousStrideLength =  mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, startb);
			data.put(BluetoothLeCharacteristicKey.KEY_STRIDE_LENGTH, (float)InstantaneousStrideLength*0.01f);
			startb += 2;
		}
		
		if(flagTotalDistancePresent) {
			TotalDistance  = mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, startb);
			data.put(BluetoothLeCharacteristicKey.KEY_TOTAL_DISTANCE, (float)TotalDistance/10.0f);
		}
		
		return data;
	}

	public int getFlags() {
		return Flags;
	}
	
	public boolean getInstantaneousStrideLengthPresent() {
		return flagInstantaneousStrideLengthPresent;
	}
	
	public boolean getTotalDistancePresent() {
		return flagTotalDistancePresent;
	}
	
	public boolean getWalkingorRunningStatusbits() {
		return flagWalkingorRunningStatusbits;
	}
	
	public int getInstantaneousSpeedInCount() {
		return InstantaneousSpeed;
	}
	
	public float getInstantaneousSpeedInMPS() {
		return (float)(InstantaneousSpeed/256.0f);
	}
	
	public float getInstantaneousSpeedInKMPHR() {
		return (float)(InstantaneousSpeed/256.0f)*3.6f;
	}
	
	public int getInstantaneousCadence() {
		return InstantaneousCadence;
	}
	
	public int getInstantaneousStrideLengthInCM() {
		if(flagInstantaneousStrideLengthPresent) {
			return InstantaneousStrideLength;
		} else {
			return 0;
		}
	}
	
	public float getInstantaneousStrideLengthInMeter() {
		if(flagInstantaneousStrideLengthPresent) {
			return (float)(InstantaneousStrideLength/100.0f);
		} else {
			return 0.0f;
		}
	}
	
	public long getTotalDistanceInDeciMeter() {
		if(flagTotalDistancePresent) {
			return TotalDistance;
		} else {
			return 0L;
		}
	}

	public float getTotalDistanceInMeter() {
		if(flagTotalDistancePresent) {
			return (float)(TotalDistance/10.0f);
		} else {
			return 0.0f;
		}
	}

	public float getTotalDistanceInKiloMeter() {
		if(flagTotalDistancePresent) {
			return (float)(TotalDistance/10000.0f);
		} else {
			return 0.0f;
		}
	}

}
