package com.app.blegattservicetest;

public interface BleDeviceCallback {
	public boolean Connect(String address, int type);
	public boolean Disconnect(String address); 
}
