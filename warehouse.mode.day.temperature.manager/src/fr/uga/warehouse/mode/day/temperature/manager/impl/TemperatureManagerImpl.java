package fr.uga.warehouse.mode.day.temperature.manager.impl;

import fr.uga.warehouse.client.time.MomentOfTheDay;
import fr.uga.warehouse.client.time.MomentOfTheDayListener;
import fr.uga.warehouse.mode.day.temperature.controller.TemperatureController;
import fr.uga.warehouse.mode.day.temperature.manager.TemperatureAdministration;
import fr.uga.warehouse.client.time.TimeService;

/**
 * <u>TemperatureManagerImpl</u> implements the <u>TemperatureAdministration</u>
 * in order to manipulate the temperature of the whole map.
 * 
 * @author mathys
 */
public class TemperatureManagerImpl implements MomentOfTheDayListener, TemperatureAdministration {

	/** Field for controller dependency */
	private TemperatureController controller;
	/** Field for timeService dependency */
	private TimeService timeService;

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[DAY][MANAGER] - Stopping TemperatureManager.");
		this.timeService.unregister(this);
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[DAY][MANAGER] - Starting TemperatureManager.");
		this.timeService.register(this);
	}

	@Override
	public void hourHasChanged(int h) {
		if (!MomentOfTheDay.MORNING.getCorrespondingMoment(h).equals(MomentOfTheDay.MORNING)) {
			this.controller.updateTemperatures();
		}

	}

	@Override
	public void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
		switch (newMomentOfTheDay) {
		case NIGHT:
			this.stopCoolers();
			this.stopHeaters();
			break;
		default:
			break;
		}

	}

	@Override
	public void updateTargetTemperature(String location, Double temperature) {
		System.out.println("[DAY][MANAGER] - Updating Target Temperature of '" + location + "' from "
				+ this.controller.getTargetTemperature(location) + " to " + temperature + ".");
		this.controller.updateTargetTemperature(location, temperature);
	}

	@Override
	public Double getTargetTemperature(String location) {
		return this.controller.getTargetTemperature(location);
	}

	@Override
	public Double getCurrentTemperature(String location) {
		return this.controller.getCurrentTemperature(location);
	}

	@Override
	public void startHeaters() {
		System.out.println("[DAY][MANAGER] - Start all heaters.");
		this.controller.startHeaters();
	}

	@Override
	public void stopHeaters() {
		System.out.println("[DAY][MANAGER] - Stop all heaters.");
		this.controller.stopHeaters();
	}

	@Override
	public void startCoolers() {
		System.out.println("[DAY][MANAGER] - Start all coolers.");
		this.controller.startCoolers();
	}

	@Override
	public void stopCoolers() {
		System.out.println("[DAY][MANAGER] - Stop all coolers.");
		this.controller.stopCoolers();
	}

}
