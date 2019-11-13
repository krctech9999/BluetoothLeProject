package tw.com.umedia.bluetoothle;

/**
 * 
 * The GATT characteristic Key name implements.
 * @author Chevy Lin
 * @since Nov 12, 2015
 * @version 1.0.0 
 *
 */
public class BluetoothLeCharacteristicKey {
	
	// Device Information
	public final static String KEY_DEVICE_NAME = "device_name";
	public final static String KEY_MANUFACTURER_NAME = "manufacturer_name";
	public final static String KEY_MODEL_NAME = "model_name";
	public final static String KEY_SERIAL_NUMBER = "serial_number";
	public final static String KEY_HARDWARE_REVISION = "hardware_revision";
	public final static String KEY_FIRMWARE_REVISION = "firmware_revision";
	public final static String KEY_SOFTWARE_REVISION = "software_revision";
	public final static String KEY_PNPID = "pnp_id";
	
	// RSC Sensor Location
	public final static String KEY_SENSOR_LOCATION = "sensor_location";
	
	// RSC Feature
	public final static String KEY_RSC_FEATURE_STRIDE_LENGTH_SUPPORTED = "stride_length_supported";
	public final static String KEY_RSC_FEATURE_TOTAL_DISTANCE_SUPPORTED = "total_distance_supported";
	public final static String KEY_RSC_FEATURE_RUNNING_STATUS_SUPPORTED = "running_status_supported";
	public final static String KEY_RSC_FEATURE_CALIBRATION_SUPPORTED = "calibration_supported";
	public final static String KEY_RSC_FEATURE_MULTIPLE_SENSOR_SUPPORTED = "multiple_sensor_supported";
	
	// RSC Measurement
	public final static String KEY_STRIDE_LENGTH_FLAG = "stride_length_flag";
	public final static String KEY_TOTAL_DISTANCE_FLAG = "total_distance_flag";
	public final static String KEY_RUNNING_STATUS_FLAG = "running_status_flag";
	public final static String KEY_SPEED = "speed";
	public final static String KEY_CADENCE = "cadence";
	public final static String KEY_STRIDE_LENGTH  = "stride_length";
	public final static String KEY_TOTAL_DISTANCE = "total_distance";
	
	// Bracelet data
	public final static String KEY_TIMESTAMP = "timestamp";
	public final static String KEY_STEPS = "steps";
	public final static String KEY_CALORIE = "calorie";	
	 
	// UMHD Key Event
	public final static String KEY_UMHD_KEY_EVENT = "umhd_key_event";
}
