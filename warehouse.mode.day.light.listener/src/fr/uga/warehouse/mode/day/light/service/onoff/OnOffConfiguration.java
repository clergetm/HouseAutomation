package fr.uga.warehouse.mode.day.light.service.onoff;

/**
 * <u>OnOffConfiguration</u> allows you to turn On or Off
 * all the lights
 * 
 * @author mathys
 */
public interface OnOffConfiguration {

	public static final String PROP_ON_OFF_NAME = "onOff";
	
	public void turnOffAllTheLights();
	
	public void turnOnAllTheLights();
}
