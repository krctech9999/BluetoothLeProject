package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.util.HashMap;

import org.bluetooth.gatt.utils.GattByteBuffer;

import tw.com.umedia.bluetoothle.BluetoothLeCharacteristicKey;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

public class IwownI5Data5 extends DeviceGattCharacteristic {

	private long mTimestamp = 0L;
	
	public IwownI5Data5(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, Object> getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		byte[] values = mGattCharacteristic.getValue();
		mTimestamp = GattByteBuffer.wrap(values).getUint32();
		data.put(BluetoothLeCharacteristicKey.KEY_TIMESTAMP, Long.toString(mTimestamp));
		return data;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	
}
