package fr.uga.warehouse.client.time.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import fr.liglab.adele.icasa.clockservice.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.uga.warehouse.client.time.MomentOfTheDay;
import fr.uga.warehouse.client.time.MomentOfTheDayListener;
import fr.uga.warehouse.client.time.TimeService;

public class TimeImpl implements TimeService, PeriodicRunnable {

	/** Field for clock dependency */
	private Clock clock;

	/** Default moment is the Morning */
	private MomentOfTheDay moment = MomentOfTheDay.MORNING;

	private List<MomentOfTheDayListener> listeners = new ArrayList<MomentOfTheDayListener>();

	/** Default moment is the Morning */
	private int hour = (new DateTime(clock.currentTimeMillis())).getHourOfDay();

	@Override
	public MomentOfTheDay getMomentOfTheDay() {
		return this.moment;
	}

	@Override
	public int getCurrentHour() {
		return this.hour;
	}

	@Override
	public void run() {
		// Get the current time from currentTimeMillis and convert it in
		// getCorrespondingMoment method.
		int currentHour = (new DateTime(clock.currentTimeMillis())).getHourOfDay();

		// Manage MomentOfTheDay
		MomentOfTheDay currentMoment = this.moment.getCorrespondingMoment(currentHour);
		// If the moment changed, transfer the information.
		if (currentMoment != this.moment) {
			for (MomentOfTheDayListener listener : listeners) {
				listener.momentOfTheDayHasChanged(currentMoment);
			}
			this.moment = currentMoment;
		}

		// Manage hour
		if (currentHour != this.hour) {
			for (MomentOfTheDayListener listener : listeners) {
				listener.hourHasChanged(currentHour);
			}

			this.hour = currentHour;
		}
	}

	@Override
	public long getPeriod() {
		return 1;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.HOURS;
	}

	@Override
	public void register(MomentOfTheDayListener listener) {
		System.out.println("[TIME] - Add a MomentOfTheDayListener.");
		this.listeners.add(listener);
	}

	@Override
	public void unregister(MomentOfTheDayListener listener) {
		System.out.println("[TIME] - Remove a MomentOfTheDayListener.");
		this.listeners.remove(listener);

	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[TIME] - Time Component is stopping.");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[TIME] - Time Component is starting.");
	}

}
