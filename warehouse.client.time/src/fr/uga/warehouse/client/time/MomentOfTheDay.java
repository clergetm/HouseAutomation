package fr.uga.warehouse.client.time;

/**
 * <u>MomentOfTheDay</u> enum to differentiate different times of the day
 * 
 * @author icasa
 * @implNote https://self-star.imag.fr/?post_type=exercise&p=116
 */
public enum MomentOfTheDay {
	MORNING(6), AFTERNOON(12), EVENING(18), NIGHT(24);

	/**
	 * Gets the moment of the day corresponding to the hour.
	 * 
	 * @param hour the given hour
	 * @return the corresponding moment of the day
	 */
	public MomentOfTheDay getCorrespondingMoment(int hour) {
		assert ((0 <= startHour) && (startHour <= 24));

		if (MomentOfTheDay.NIGHT.startHour <= hour) {
			return MomentOfTheDay.NIGHT;
		} else if (MomentOfTheDay.EVENING.startHour <= hour) {
			return MomentOfTheDay.EVENING;
		} else if (MomentOfTheDay.AFTERNOON.startHour <= hour) {
			return MomentOfTheDay.AFTERNOON;
		} else if (MomentOfTheDay.MORNING.startHour <= hour) {
			return MomentOfTheDay.MORNING;
		} else {
			return MomentOfTheDay.NIGHT;
		}
	}

	/** The hour when the moment start. */
	private final int startHour;

	/**
	 * Build a new moment of the day :
	 * 
	 * @param startHour when the moment start.
	 */
	MomentOfTheDay(int startHour) {
		assert ((0 <= startHour) && (startHour <= 24));
		this.startHour = startHour;
	}
}
