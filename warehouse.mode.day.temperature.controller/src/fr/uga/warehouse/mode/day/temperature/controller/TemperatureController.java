package fr.uga.warehouse.mode.day.temperature.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <u>TemperatureController</u> interface determine the actions for the main
 * Controller implementation.
 * 
 * @author mathys
 */
public interface TemperatureController {

	/** Default temperature of an unregistered location. */
	public static final Double NO_TEMPERATURE = -1.0;
	/** Default temperature of a registered location. */
	public static final Double DEFAULT_TEMPERATURE = 22.0;
	/** The custom property to check the device's type */
	public static final String PROPERTY_OBJECT_NAME = "Type";
	/** The property location as a const */
	public static final String PROPERTY_LOCATION_NAME = "Location";
	/** The locations to use. */
	public static final List<String> LOCATIONS = new ArrayList<>(Arrays.asList("Stockage 1", "Stockage 2", "Stockage 3", "Accueil", "Contrôle"));
//	public static final List<String> LOCATIONS = new ArrayList<String>(Arrays.asList("bathroom", "livingroom", "bedroom", "kitchen"));

	/**
	 * Setter of the temperature we want in a location.
	 * 
	 * @param location    The location we want to modify.
	 * @param temperature The new temperature.
	 */
	public void updateTargetTemperature(String location, Double temperature);

	/**
	 * Getter of the <u><b>wanted</b></u> temperature in a location.
	 * 
	 * @param location The targeted location.
	 * @return The wanted temperature of the location or a negative value if the
	 *         location is unknown.
	 */
	public Double getTargetTemperature(String location);

	/**
	 * Getter of the <u><b>current</b></u> temperature in a location.
	 * 
	 * @param location The targeted location.
	 * @return The current temperature of the location or a negative value if the
	 *         location is unknown.
	 */
	public Double getCurrentTemperature(String location);

	/**
	 * Update all location's temperature according to <u><b>current and
	 * wanted</b></u> temperatures.
	 */
	public void updateTemperatures();

	/** Start all heaters. */
	public void startHeaters();

	/** Stop all heaters. */
	public void stopHeaters();

	/** Start all coolers. */
	public void startCoolers();

	/** Stop all coolers. */
	public void stopCoolers();
}
