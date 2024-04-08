package fr.uga.warehouse.mode.day.light.listener.impl;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.uga.warehouse.mode.day.light.service.autoon.AutoOnConfiguration;
import fr.uga.warehouse.mode.day.light.service.onoff.OnOffConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class LightListenerImpl implements DeviceListener<GenericDevice>, OnOffConfiguration, AutoOnConfiguration {

	/** The property location as a const */
	public static final String PROPERTY_LOCATION_NAME = "Location";
	/** The name of the location for unknown value */
	public static final String PROPERTY_LOCATION_UNKNOWN_VALUE = "unknown";
	/** The custom property to check the device's type */
	public static final String PROPERTY_OBJECT_NAME = "Type";
	/** The locations to use. */
	public static final List<String> LOCATIONS = new ArrayList<String>(
			Arrays.asList("Stockage 1", "Stockage 2", "Stockage 3", "Accueil", "Contrôle"));
	// public static final List<String> LOCATIONS = new
	// ArrayList<String>(Arrays.asList("bathroom", "livingroom", "bedroom",
	// "kitchen"));
	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	private boolean isAutoMode = false;

	private Map<String, Integer> passagesPerLocation = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			LOCATIONS.forEach(location -> put(location, 0));
		}
	};

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map<?, ?> properties) {
		System.out.println("[DAY][LIGHT] - bind presence sensor.");
		presenceSensor.addListener(this);
		presenceSensor.setPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME, PresenceSensor.class.getName());
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map<?, ?> properties) {
		System.out.println("[DAY][LIGHT] - unbind presence sensor.");
		presenceSensor.removeProperty(LightListenerImpl.PROPERTY_OBJECT_NAME);
		presenceSensor.removeListener(this);
	}

	/** Bind Method for binaryLights dependency */
	public synchronized void bindBinaryLight(BinaryLight binaryLight, Map<?, ?> properties) {
		System.out.println("[DAY][LIGHT] - bind binary light.");
		binaryLight.setPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME, BinaryLight.class.getName());
		binaryLight.addListener(this);
	}

	/** Unbind Method for binaryLights dependency */
	public synchronized void unbindBinaryLight(BinaryLight binaryLight, Map<?, ?> properties) {
		System.out.println("[DAY][LIGHT] - unbind binary light.");
		binaryLight.removeProperty(LightListenerImpl.PROPERTY_OBJECT_NAME);
		binaryLight.removeListener(this);
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("[DAY] - Stopping LightListener.");

		for (BinaryLight binaryLight : this.binaryLights) {
			binaryLight.removeProperty(LightListenerImpl.PROPERTY_OBJECT_NAME);
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
		// Stop if automode is activated.
		if (this.isAutoMode) {
			return;
		}

		String location = (String) device.getPropertyValue(LightListenerImpl.PROPERTY_LOCATION_NAME);
		String objectType = (String) device.getPropertyValue(LightListenerImpl.PROPERTY_OBJECT_NAME);

		switch (propertyName) {
		// The sensor detects a new presence or no longer detects a presence
		case PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE:
			if (PresenceSensor.class.getName().equals(objectType)) {
				PresenceSensor sensor = (PresenceSensor) device;

				if (LOCATIONS.contains(location)) {
					boolean powerStatus = sensor.getSensedPresence();
					// if no one is in this location.
					if (!powerStatus) {
						// increment the number of passage at this location.
						int value = this.passagesPerLocation.get(location);
						this.passagesPerLocation.put(location, ++value);

						// check if the lights should stays on.
						powerStatus = this.leaveTheLightsOn(location);
					}

					// Change the power status of the lights
					for (BinaryLight light : this.getBinaryLightFromLocation(location)) {
						light.setPowerStatus(powerStatus);
					}
				}

				System.out.println("===================================================================");
				System.out.println("[DAY][LIGHT] - Location    - Number of passage ");
				for (Entry<String, Integer> entry : this.passagesPerLocation.entrySet()) {
					System.out.println("[DAY][LIGHT] - " + entry.getKey() + " - " + entry.getValue());
				}
				System.out.println("===================================================================");
			}
			break;
		// We are moving a device
		case LightListenerImpl.PROPERTY_LOCATION_NAME:
			// We are moving a Binary Light
			if (BinaryLight.class.getName().equals(objectType)) {
				BinaryLight light = (BinaryLight) device;

				if (LOCATIONS.contains(location)) {
					// If a sensor is in the location, change the powerstatus according to the
					// sensor or the AutoOnConfiguration state.
					List<PresenceSensor> sensors = this.getPresenceSensorFromLocation(location);
					if (!sensors.isEmpty()) {
						light.setPowerStatus(sensors.get(0).getSensedPresence() || this.leaveTheLightsOn(location));
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
					lights.forEach((light) -> light.setPowerStatus(sensors.get(0).getSensedPresence()));
				} else {
					lights.forEach((light) -> light.turnOff());
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
		for (String location : LOCATIONS) {
			for (BinaryLight light : this.getBinaryLightFromLocation(location)) {
				light.turnOff();
			}
		}

		this.isAutoMode = false;
	}

	@Override
	public void turnOnAllTheLights() {
		for (String location : LOCATIONS) {
			for (BinaryLight light : this.getBinaryLightFromLocation(location)) {
				light.turnOn();
			}
		}
		this.isAutoMode = true;
	}

	@Override
	public boolean leaveTheLightsOn(String location) {
		System.out.println("[DAY][LIGHT] - The lights in the location : " + location
				+ " stays on until the reset of the configuration.");
		return this.passagesPerLocation.get(location) >= AutoOnConfiguration.PROP_AUTO_ON_NB_PASSAGE;
	}

	@Override
	public void reset() {
		if (!this.isAutoMode) {
			System.out.println("[DAY][LIGHT] - Reset Auto On Configuration.");
			for (Entry<String, Integer> entry : this.passagesPerLocation.entrySet()) {
				// Reset value.
				entry.setValue(0);
				// Reset lights.
				List<PresenceSensor> sensors = this.getPresenceSensorFromLocation(entry.getKey());
				if (!sensors.isEmpty()) {
					for (BinaryLight light : this.getBinaryLightFromLocation(entry.getKey())) {
						light.setPowerStatus(sensors.get(0).getSensedPresence());
					}

				}

			}
		} else {
			System.out
					.println("[DAY][LIGHT] - Reset Auto On Configuration cancelled by current activated auto mode On.");
			System.out.println("[DAY][LIGHT] - Please run 'turnlightsoff' command before resetting.");
		}
	}

}