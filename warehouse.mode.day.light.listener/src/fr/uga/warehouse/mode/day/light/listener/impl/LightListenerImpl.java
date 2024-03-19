package fr.uga.warehouse.mode.day.light.listener.impl;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.uga.warehouse.mode.day.light.service.onoff.OnOffConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <u>LightListenerImpl</u> allows you to:
 * <ul>
 * <li>Manage the presence of a person in a Location.</li>
 * <li>Manage the movement of a presence sensor.</li>
 * <li>Manage the movement of a binary light.</li>
 * </ul>
 * 
 * @author mathys
 */
public class LightListenerImpl implements DeviceListener<GenericDevice>, OnOffConfiguration {

	/** The property location as a const */
	public static final String PROPERTY_LOCATION_NAME = "Location";
	/** The name of the location for unknown value */
	public static final String PROPERTY_LOCATION_UNKNOWN_VALUE = "unknown";
	/** The custom property to check the device's type */
	public static final String PROPERTY_OBJECT_NAME = "Type";

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map<?, ?> properties) {
		System.out.println("[DAY] - bind presence sensor.");
		presenceSensor.addListener(this);
		presenceSensor.setPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME, PresenceSensor.class.getName());
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map<?, ?> properties) {
		System.out.println("[DAY] - unbind presence sensor.");
		presenceSensor.removeProperty(LightListenerImpl.PROPERTY_OBJECT_NAME);
		presenceSensor.removeListener(this);
	}

	/** Bind Method for binaryLights dependency */
	public synchronized void bindBinaryLight(BinaryLight binaryLight, Map<?, ?> properties) {
		System.out.println("[DAY] - bind binary light.");
		binaryLight.setPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME, BinaryLight.class.getName());
		binaryLight.addListener(this);
	}

	/** Unbind Method for binaryLights dependency */
	public synchronized void unbindBinaryLight(BinaryLight binaryLight, Map<?, ?> properties) {
		System.out.println("[DAY] - unbind binary light.");
		binaryLight.removeProperty(LightListenerImpl.PROPERTY_OBJECT_NAME);
		binaryLight.removeListener(this);
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("[DAY] - Stopping LightListener.");

		for (BinaryLight binaryLight : this.binaryLights) {
			binaryLight.removeListener(this);
		}

		for (PresenceSensor sensor : this.presenceSensors) {
			sensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[DAY] - Starting LightListener.");
	}

	@Override
	public void deviceAdded(GenericDevice device) {
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
	}

	@Override
	public void devicePropertyAdded(GenericDevice device, String arg1) {
	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		String location = (String) device.getPropertyValue(LightListenerImpl.PROPERTY_LOCATION_NAME);
		String objectType = (String) device.getPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME);

		switch (propertyName) {
		// The sensor detects a new presence or no longer detects a presence
		case PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE:
			if (PresenceSensor.class.getName().equals(objectType)) {
				PresenceSensor sensor = (PresenceSensor) device;

				if (!location.equals(PROPERTY_LOCATION_UNKNOWN_VALUE)) {
					for (BinaryLight binaryLight : getBinaryLightFromLocation(location)) {
						// and switch them on/off depending on the sensed presence
						binaryLight.setPowerStatus(sensor.getSensedPresence());
					}
				}
			}
			break;
		// We are moving a device
		case LightListenerImpl.PROPERTY_LOCATION_NAME:
			// We are moving a Binary Light
			if (BinaryLight.class.getName().equals(objectType)) {
				BinaryLight light = (BinaryLight) device;

				// If a sensor is in the location, change the powerstatus according to the
				// sensor
				if (!location.equals(PROPERTY_LOCATION_UNKNOWN_VALUE)) {
					List<PresenceSensor> sensors = this.getPresenceSensorFromLocation(location);
					if (!sensors.isEmpty()) {
						light.setPowerStatus(sensors.get(0).getSensedPresence());
					} else {
						light.turnOff();
					}
				} else {
					light.turnOff();
				}
				// We are moving a PresenceSensor
			} else if (PresenceSensor.class.getName().equals(objectType)) {
				// Get the devices of the previous location
				List<PresenceSensor> sensors = this.getPresenceSensorFromLocation((String) oldValue);
				List<BinaryLight> lights = this.getBinaryLightFromLocation((String) oldValue);
				if (!sensors.isEmpty()) {
					for (BinaryLight light : lights) {
						light.setPowerStatus(sensors.get(0).getSensedPresence());
					}
				} else {
					for (BinaryLight light : lights) {
						light.turnOff();
					}
				}
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
	}

	@Override
	public void deviceRemoved(GenericDevice device) {
	}

	/**
	 * Return all <u>BinaryLight</u> from the given location.
	 *
	 * @param location the given location.
	 * @return the list of matching BinaryLight.
	 */
	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
		List<BinaryLight> lights = new ArrayList<>();
		for (BinaryLight binLight : this.binaryLights) {
			if (binLight.getPropertyValue(LightListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				lights.add(binLight);
			}
		}
		return lights;
	}

	/**
	 * Return all <u>PresenceSensor</u> from the given location.
	 *
	 * @param location the given location.
	 * @return the list of matching PresenceSensor.
	 */
	private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
		List<PresenceSensor> sensors = new ArrayList<>();
		for (PresenceSensor sensor : this.presenceSensors) {
			if (sensor.getPropertyValue(LightListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				sensors.add(sensor);
			}
		}
		return sensors;
	}

	@Override
	public void turnOffAllTheLights() {
		for (BinaryLight light : this.binaryLights) {
			light.turnOff();
		}

	}

	@Override
	public void turnOnAllTheLights() {
		for (BinaryLight light : this.binaryLights) {
			light.turnOn();
		}

	}
}