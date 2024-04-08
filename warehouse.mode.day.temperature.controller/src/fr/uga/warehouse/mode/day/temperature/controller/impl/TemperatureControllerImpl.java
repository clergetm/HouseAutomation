package fr.uga.warehouse.mode.day.temperature.controller.impl;

import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.uga.warehouse.mode.day.temperature.controller.TemperatureController;
import fr.uga.warehouse.mode.day.temperature.controller.service.threshold.ThresholdConfiguration;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.liglab.adele.icasa.service.location.PersonLocationService;

/**
 * <u>TemperatureControllerImpl</u> allows you to:
 * <ul>
 * <li>Manage the preferred temperature of a location.</li>
 * <li>Start the heater or the cooler if needed.</li>
 * </ul>
 * 
 * @author mathys
 */
public class TemperatureControllerImpl
		implements TemperatureController, DeviceListener<GenericDevice>, ThresholdConfiguration {

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;
	/** Target temperature for each location. */
	private Map<String, Double> targetTemperatureMap = new HashMap<String, Double>() {
		private static final long serialVersionUID = 1L;
		{
			LOCATIONS.forEach(location -> put(location, DEFAULT_TEMPERATURE));
		}
	};

	/** Threshold used to avoid to many updates. */
	private Map<String, Double> thresholdMap = new HashMap<String, Double>() {
		private static final long serialVersionUID = 1L;
		{
			LOCATIONS.forEach(location -> put(location, MIN_THRESHOLD));
		}
	};
	/** Field for personLocationService dependency */
	private PersonLocationService personLocationService;

	/** Bind Method for coolers dependency */
	public synchronized void bindCooler(Cooler cooler, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Bind cooler.");
		cooler.addListener(this);
		cooler.setPropertyValue(PROPERTY_OBJECT_NAME, Cooler.class.getName());
	}

	/** Unbind Method for coolers dependency */
	public synchronized void unbindCooler(Cooler cooler, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Unbind cooler.");
		cooler.removeListener(this);
		cooler.removeProperty(PROPERTY_OBJECT_NAME);
	}

	/** Bind Method for thermometers dependency */
	public synchronized void bindThermometer(Thermometer thermometer, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Bind thermometer.");
		thermometer.addListener(this);
		thermometer.setPropertyValue(PROPERTY_OBJECT_NAME, Thermometer.class.getName());
	}

	/** Unbind Method for thermometers dependency */
	public synchronized void unbindThermometer(Thermometer thermometer, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Unbind thermometer.");
		thermometer.removeListener(this);
		thermometer.removeProperty(PROPERTY_OBJECT_NAME);
	}

	/** Bind Method for heaters dependency */
	public synchronized void bindHeater(Heater heater, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Bind heater.");
		heater.addListener(this);
		heater.setPropertyValue(PROPERTY_OBJECT_NAME, Heater.class.getName());
	}

	/** Unbind Method for heaters dependency */
	public synchronized void unbindHeater(Heater heater, Map<?, ?> properties) {
		System.out.println("[DAY][TEMP] - Unbind heater.");
		heater.removeListener(this);
		heater.removeProperty(PROPERTY_OBJECT_NAME);
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("[DAY] - Stopping TemperatureController.");

		for (Cooler cooler : this.coolers) {
			cooler.removeListener(this);
			cooler.removeProperty(PROPERTY_OBJECT_NAME);
		}

		for (Thermometer thermometer : this.thermometers) {
			thermometer.removeListener(this);
			thermometer.removeProperty(PROPERTY_OBJECT_NAME);
		}

		for (Heater heater : this.heaters) {
			heater.removeListener(this);
			heater.removeProperty(PROPERTY_OBJECT_NAME);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[DAY] - Starting TemperatureController.");
	}

	@Override
	public void updateTargetTemperature(String location, Double temperature) {
		this.targetTemperatureMap.replace(location, temperature);

	}

	@Override
	public Double getTargetTemperature(String location) {
		return this.targetTemperatureMap.getOrDefault(location, NO_TEMPERATURE);
	}

	@Override
	public Double getCurrentTemperature(String location) {
		List<Thermometer> thermometersList = this.getThermometersFromLocation(location);
		if (!thermometersList.isEmpty()) {
			return thermometersList.get(0).getTemperature() - 273.15; // Kelvin to Celsius.
		}
		return NO_TEMPERATURE;
	}

	@Override
	public void updateTemperatures() {
		System.out.println("===================================================================");
		System.out.println("[DAY][TEMP] - Location    - Coolers   - Heaters   - Temperature ");
		for (String location : LOCATIONS) {
			List<Heater> heatersList = this.getHeatersFromLocation(location);
			List<Cooler> coolersList = this.getCoolersFromLocation(location);
			Double currentTemperature = this.getCurrentTemperature(location);
			Double targetTemperature = this.getTargetTemperature(location);
			this.updateThresholdInLocation(location);
			Double threshold = this.thresholdMap.get(location);
			System.out.print("[DAY][TEMP] - '" + location + "' ");
			// Too hot
			if (currentTemperature > targetTemperature + threshold) {
				System.out.print("- STARTING   - STOPPING   ");
				coolersList.forEach(c -> c.setPowerLevel(0.05));
				heatersList.forEach(h -> h.setPowerLevel(0));
			} else if (currentTemperature > targetTemperature + threshold / 2) {
				System.out.print("- STARTING   - STOPPING   ");
				coolersList.forEach(c -> c.setPowerLevel(0.01));
				heatersList.forEach(h -> h.setPowerLevel(0));
			}
			// Too cold
			else if (currentTemperature < targetTemperature - threshold) {
				System.out.print("- STOPPING   - STARTING   ");
				coolersList.forEach(c -> c.setPowerLevel(0));
				heatersList.forEach(h -> h.setPowerLevel(0.05));
			} else if (currentTemperature < targetTemperature - threshold / 2) {
				System.out.print("- STOPPING   - STARTING   ");
				coolersList.forEach(c -> c.setPowerLevel(0));
				heatersList.forEach(h -> h.setPowerLevel(0.01));
			} else {
				System.out.print("- STOPPING   - STOPPING   ");
				coolersList.forEach(c -> c.setPowerLevel(0));
				heatersList.forEach(h -> h.setPowerLevel(0));
			}
			System.out.println("- " + currentTemperature);
		}

		System.out.println("===================================================================");

	}

	@Override
	public void startHeaters() {
		this.stopCoolers();
		for (Heater heater : this.heaters) {
			heater.setPowerLevel(1);
		}
	}

	@Override
	public void stopHeaters() {
		for (Heater heater : this.heaters) {
			heater.setPowerLevel(0);
		}
	}

	@Override
	public void startCoolers() {
		this.stopHeaters();
		for (Cooler cooler : this.coolers) {
			cooler.setPowerLevel(1);
		}
	}

	@Override
	public void stopCoolers() {
		for (Cooler cooler : this.coolers) {
			cooler.setPowerLevel(0);
		}
	}

	/**
	 * Get the heaters from a specific location.
	 * 
	 * @param location The given location.
	 * @return a list of heaters placed in this location.
	 */
	public synchronized List<Heater> getHeatersFromLocation(String location) {
		List<Heater> heaterList = new ArrayList<Heater>();
		for (Heater h : this.heaters) {
			if (h.getPropertyValue(PROPERTY_LOCATION_NAME).equals(location)) {
				heaterList.add(h);
			}
		}
		return heaterList;
	}

	/**
	 * Get the coolers from a specific location.
	 * 
	 * @param location The given location.
	 * @return a list of coolers placed in this location.
	 */
	public synchronized List<Cooler> getCoolersFromLocation(String location) {
		List<Cooler> coolerList = new ArrayList<Cooler>();
		for (Cooler c : this.coolers) {
			if (c.getPropertyValue(PROPERTY_LOCATION_NAME).equals(location)) {
				coolerList.add(c);
			}
		}
		return coolerList;
	}

	/**
	 * Get the thermometers from a specific location.
	 * 
	 * @param location The given location.
	 * @return a list of thermometers placed in this location.
	 */
	public synchronized List<Thermometer> getThermometersFromLocation(String location) {
		List<Thermometer> thermometherList = new ArrayList<Thermometer>();
		for (Thermometer t : this.thermometers) {
			if (t.getPropertyValue(PROPERTY_LOCATION_NAME).equals(location)) {
				thermometherList.add(t);
			}
		}
		return thermometherList;
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		/** Not used in this case. */
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		/** Not used in this case. */
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		/** Not used in this case. */
	}

	@Override
	public void devicePropertyModified(GenericDevice arg0, String arg1, Object arg2, Object arg3) {
		/** Not used in this case. */
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		/** Not used in this case. */
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		/** Not used in this case. */
	}

	@Override
	public void updateThresholdInLocation(String location) {
		Double threshold = this.thresholdMap.get(location);

		// Increase the threshold if there are more people than NB_PEOPLE_IN_LOCATION.
		threshold += this.personLocationService.getPersonInZone(location).size() >= NB_PEOPLE_IN_LOCATION
				? THRESHOLD_STEP
				: -THRESHOLD_STEP;
		if (threshold > MIN_THRESHOLD & threshold < MAX_THRESHOLD) {
			System.out.println("[DAY][TEMP] - Updating Threshold in '" + location + "', now : " + threshold);
			this.thresholdMap.replace(location, threshold);
		}
	}

}
