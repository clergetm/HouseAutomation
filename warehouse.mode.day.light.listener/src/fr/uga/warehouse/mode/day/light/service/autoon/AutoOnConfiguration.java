package fr.uga.warehouse.mode.day.light.service.autoon;

/**
 * <u>AutoOnConfiguration</u> allows a listener to keep the lights on in a place
 * when the presence sensor detects a presence multiple times in a short period
 * of time.
 * 
 * @author mathys
 */
public interface AutoOnConfiguration {

	/** Name of the AutoOnConfiguration's properties. */
	public static final String PROP_AUTO_ON_NAME = "autoOn";

	/** A period is equals to 2 hours. */
	public static final int PROP_AUTO_ON_PERIOD = 2;

	/** 4 passages are possible before leaving the lights on. */
	public static final int PROP_AUTO_ON_NB_PASSAGE = 4;

	/**
	 * Check if the lights should stay on.
	 * 
	 * @param location the location to check.
	 * @return true if the number of passages in the location is greater than or
	 *         equals to NB_PASSAGE property.
	 */
	public boolean leaveTheLightsOn(String location);

	/** Reset all location's lights. */
	public void reset();

}
