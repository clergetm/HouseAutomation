package fr.uga.warehouse.client.time;

/**
 * <u>TimeService</u> allows you to retrieve the moment of the day.
 * 
 * @author mathys
 */
public interface TimeService {

	/**
	 * Gets the current hour.
	 * 
	 * @return the current hour.
	 */
	public int getCurrentHour();

	/**
	 * Gets the moment of the day.
	 * 
	 * @return the moment of the day
	 */
	public MomentOfTheDay getMomentOfTheDay();

	/**
	 * Register a listener that will be notified each time the current moment of the
	 * day changed.
	 * 
	 * @param listener the listener
	 */
	public void register(MomentOfTheDayListener listener);

	/**
	 * Unregister a moment of the day listener.
	 * 
	 * @param listener the listener
	 */
	public void unregister(MomentOfTheDayListener listener);
}
