package tw.com.umedia.bluetoothle.gattcharacteristic;

import java.util.HashMap;

import org.bluetooth.gatt.utils.GattByteBuffer;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

/* 
 * Bluetooth GATT characteristic Sensor Location
 * Assigned Number:  0x2A5D
 * Properties: Read
 */
public class UmediaKeyEvent extends DeviceGattCharacteristic {
	private final static String TAG = "UmediaKeyEvent";
	private Event event = Event.NONE;

	public enum Event {
		NONE, UP, LEFT, CENTER, RIGHT, DOWN, RESERVED
	}

	public UmediaKeyEvent(BluetoothGattService gattservice,
			BluetoothGattCharacteristic gattcharacteristic) {
		super(gattservice, gattcharacteristic);
	}
	
	@Override
	public HashMap<String, Object> getData() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		int loc = mGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		switch (loc) {
		case 0:
			event = Event.NONE;
			data.put("KeyEvent", "NONE");
			break;
		case 1:
			event = Event.UP;
			data.put("KeyEvent", "UP");
			break;
		case 2:
			event = Event.LEFT;
			data.put("KeyEvent", "LEFT");
			break;
		case 3:
			event = Event.CENTER;
			data.put("KeyEvent", "CENTER");
			break;
		case 4:
			event = Event.RIGHT;
			data.put("KeyEvent", "RIGHT");
			break;
		case 5:
			event = Event.DOWN;
			data.put("KeyEvent", "DOWN");
			break;
		default:
			event = Event.RESERVED;
			data.put("KeyEvent", "RESERVED");
			break;
		}
		return data;
	}

	/**
	 * @return The current location of the sensor
	 */
	public Event getSensorLocation() {
		return event;
	}
	
}
