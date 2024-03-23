package fr.uga.warehouse.mode.day.light.manager;

/**
 * <u>LightAdministration</u> interface allows you to
 * implements methods related to turning the 
 * light <b>ON</b> or <b>OFF</b>.
 * 
 * @author mathys
 */
public interface LightAdministration {

	/**
	 * Turn <b>OFF</b> all the the lights of the map.
	 */
	public void turnOffAllTheLights();
	
	/**
	 * Turn <b>ON</b> all the the lights of the map.
	 */
	public void turnOnAllTheLights();
}
