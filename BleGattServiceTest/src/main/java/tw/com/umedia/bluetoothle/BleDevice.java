package tw.com.umedia.bluetoothle;

public class BleDevice  {
	
	private String deviceName;
	private String macAddress;
	private int rssi;
	
	public BleDevice(String _deviceName, String _macAddress, int _rssi) {
		rssi = _rssi;
		deviceName = _deviceName;
		macAddress = _macAddress;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public int getRssi() {
		return rssi;
	}
	
	public void setRssi(int _rssi) {
		rssi = _rssi;
	}
	
 }
