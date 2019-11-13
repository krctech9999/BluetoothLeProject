package tw.com.umedia.bluetoothle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bluetooth.descriptor.GattDescriptor;

import tw.com.umedia.bluetoothle.utils.BleLog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

/**
 * 
 * BluetoothLeDevice object is an abstract class for physical devices to extend.
 * @author Chevy Lin
 *
 */
public abstract class BluetoothLeDevice {
	private final static String TAG = "BluetoothLeDevice";
	
	/**
	 *  DEVICETYPEs declarations
	 */
	public final static int DEVICETYPE_UMHDDEVICE 				= 0x0001;
	public final static int DEVICETYPE_I5DEVICE 							= 0x0002;
	public final static int DEVICETYPE_M04LDEVICE					= 0x0003;
	
	/**
	 *  ACTION types, onRead, onWrite, onReadChanged, onDescriptorRead, onDescriptorWrite callback indicates
	 */
	public final static int ACTION_ONREAD 										= 0x7011;
	public final static int ACTION_ONWRITE 									= 0x7012;
	public final static int ACTION_ONREADCHANGED 				= 0x7013;
	public final static int ACTION_DESCRIPTOR_ONREAD 		= 0x7021;
	public final static int ACTION_DESCRIPTOR_ONWRITE 	= 0x7022;

	/**
	 *  mServices to store the Bluetooth Gatt Service as a List Array
	 */
	protected static List<BluetoothGattService> mServices;
	
	/**
	 * 
	 * processNext is called by onRead, onWrite, onReadChanged, onDescriptorRead, onDescriptorWrite functions in BluetoothLeClient. 
	 * @param gatt
	 * @param characteristic
	 * @param action
	 * @return data stored in Hash Map
	 * 
	 */
	public abstract HashMap<String, Object>  processNext(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int action);
	
	/**
	 * 
	 * processDescriptor is called by onDescriptorRead, onDescriptorWrite
	 * @param gatt
	 * @param descriptor
	 * @param action
	 * 
	 */
	public abstract void processDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,  int action);
	
	/**
	 * 
	 * processInit is called by BluetoothLeDevice in initializing stage.
	 * @param gatt
	 * @param flag
	 * 
	 */
	public abstract void processInit(BluetoothGatt gatt, boolean flag);
	
	/**
	 * 
	 * processEnd is called by BluetoothLeDevice before disconnect  stage.
	 * @param gatt
	 * @param flag
	 * 
	 */
	public abstract void processEnd(BluetoothGatt gatt, boolean flag);
	
	/**
	 * 
	 * setGattServices is called by onServiceDiscovered
	 * @param gatt
	 * 
	 */
	public abstract void setGattServices(BluetoothGatt gatt);
	
	/**
	 * 
	 * setCharacteristicNotification can set the characteristic notification with enabed flag.
	 * @param gatt : the connection client object
	 * @param characteristic : the characteristic which will be set to notify
	 * @param enabled : to enabled or disabled by boolean value
	 * 
	 */
    protected void setCharacteristicNotification(BluetoothGatt gatt,  BluetoothGattCharacteristic characteristic,
            boolean enabled) {
    	BleLog.i(TAG, "setCharacteristicNotification");
    	gatt.setCharacteristicNotification(characteristic, enabled);
    	BluetoothGattDescriptor descriptor = characteristic.getDescriptor(GattDescriptor.CLIENT_CHARACTERISTIC_CONFIGURATION);
    	descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    	gatt.writeDescriptor(descriptor);
    }
}
