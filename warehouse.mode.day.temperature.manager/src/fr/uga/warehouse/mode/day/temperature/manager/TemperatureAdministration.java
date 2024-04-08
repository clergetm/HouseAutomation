package fr.uga.warehouse.mode.day.temperature.manager;

public interface TemperatureAdministration {

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

	/** Start all heaters. */
	public void startHeaters();

	/** Stop all heaters. */
	public void stopHeaters();

	/** Start all coolers. */
	public void startCoolers();

	/** Stop all coolers. */
	public void stopCoolers();
}
