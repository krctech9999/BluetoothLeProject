package tw.com.umedia.bluetoothle;

import java.util.HashMap;

/**
 * 
 *Bluetooth Le Device Data Callback object.
 * @author Chevy Lin
 * @since Nov 12, 2015
 * @version 1.0.0
 * 
 */
public interface BluetoothLeDataCallback {
	/**
	 * 
	 * DataCallback to implement how to process the data with hash map and bluetooth address
	 * @param data : HashMap<String, Object>
	 * @param address : Bluetooth Device Address
	 * 
	 */
	public void DataCallback(HashMap <String, Object> data, String address);
	
	/**
	 * 
	 * ConnectStatus to implement the device connect with state and bluetooth address
	 * @param state : boolean true or false for the connected status
	 * @param address : Bluetooth Device Address
	 * 
	 */
	public void ConnectStatus(boolean state, String address); 
}
