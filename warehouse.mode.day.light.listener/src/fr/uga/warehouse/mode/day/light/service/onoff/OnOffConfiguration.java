package fr.uga.warehouse.mode.day.light.service.onoff;

/**
 * <u>OnOffConfiguration</u> allows you to turn On or Off all the lights
 * 
 * @author mathys
 */
public interface OnOffConfiguration {

	/** Name of the OnOffConfiguration's properties */
	public static final String PROP_ON_OFF_NAME = "onOff";

	/**
	 * Turn <b>OFF</b> all the the lights of the map.
	 */
	public void turnOffAllTheLights();

	/**
	 * Turn <b>ON</b> all the the lights of the map.
	 */
	public void turnOnAllTheLights();
}
