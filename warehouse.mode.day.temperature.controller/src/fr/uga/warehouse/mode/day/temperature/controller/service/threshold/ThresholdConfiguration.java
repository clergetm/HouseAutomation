package fr.uga.warehouse.mode.day.temperature.controller.service.threshold;

/**
 * <u>ThresholdConfiguration</u> interface determine the actions 
 * on the Threshold.
 * 
 * @author mathys
 */
public interface ThresholdConfiguration {
	/** The updating value to use. */
	public static final Double THRESHOLD_STEP = 0.5;
	/** Minimal threshold value. */
	public static final Double MIN_THRESHOLD = 1.0;
	/** Minimal threshold value. */
	public static final Double MAX_THRESHOLD = 5.0;
	
	/** Number of people in a location to update the threshold. */
	public static final int NB_PEOPLE_IN_LOCATION = 2;

	/**
	 * Change the threshold depending on the number of person in the given location.
	 * 
	 * @param location The given location.
	 */
	public void updateThresholdInLocation(String location);
}
