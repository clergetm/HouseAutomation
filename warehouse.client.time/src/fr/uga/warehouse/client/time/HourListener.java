package fr.uga.warehouse.client.time;

/**
 * The listener interface for receiving hourly events.
 * 
 * @author mathys
 */
public interface HourListener {

	/**
	 * Notify the listener that the hour has changed.
	 * 
	 * @param h the new hour.
	 */
	void hourHasChanged(int h);
}
