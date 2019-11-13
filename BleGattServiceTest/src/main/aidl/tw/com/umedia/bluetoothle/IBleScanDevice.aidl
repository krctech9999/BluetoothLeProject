package tw.com.umedia.bluetoothle;

import tw.com.umedia.bluetoothle.BleScanCallback;

/**
 * IBleScanDevice aidl interface object
 * @author : Chevy Lin
 * @version : 1.0.0
 * @since  : Nov. 10, 2015 
 */
interface IBleScanDevice {

    /**
      * BleScan() function is to enable or disable Bluetooth Le Device Scanning
      * @param : boolean flag for enable or disable scan
      * @return : none
      */
	void BleScan(boolean enabled);
	
	/**
	 * registerCallback() function is to register the Callback object which named BleScanCallback
	 * @param : BleScanCallback object
	 * @return : none
	 */
	void registerCallback(BleScanCallback callback);
	
	/**
	 * unregisterCallback() function is to unregister the Callback object which named BleScanCallback
	 * @param : none
	 * @return : none
	 */
	void unregisterCallback();
}
