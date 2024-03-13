package fr.uga.warehouse.mode.day.light.manager.impl;

import fr.uga.warehouse.mode.day.light.manager.LightAdministration;
import fr.uga.warehouse.mode.day.light.service.onoff.OnOffConfiguration;
import java.util.Map;

import fr.uga.warehouse.client.time.MomentOfTheDay;
import fr.uga.warehouse.client.time.MomentOfTheDayListener;
import fr.uga.warehouse.client.time.TimeService;
import fr.uga.warehouse.mode.day.light.service.autoon.AutoOnConfiguration;

/**
 * <u>LightManagerImpl</u> implements the <u>LightAdministration</u> and bind to
 * <u>OnOffConfiguration</u>. In order to manipulate the lights of the whole
 * map.
 * 
 * @author mathys
 */
public class LightManagerImpl implements LightAdministration, MomentOfTheDayListener {

	/** Field for timeService dependency */
	private TimeService timeService;
	/** Field for onOffConfiguration dependency */
	private OnOffConfiguration onOffConfiguration;
	/** Field for autoOnConfiguration dependency */
	private AutoOnConfiguration autoOnConfiguration;

	/** Timer of periods used for autoOnConfiguration. */
	private int timer = 0;

	@Override
	public void turnOffAllTheLights() {
		System.out.println("[DAY][MANAGER] - Turning all lights off.");
		this.onOffConfiguration.turnOffAllTheLights();
	}

	@Override
	public void turnOnAllTheLights() {
		System.out.println("[DAY][MANAGER] - Turning all lights on.");
		this.onOffConfiguration.turnOnAllTheLights();
	}

	/** Bind Method for OnOffConfigurations dependency */
	public void bindOnOffConfiguration(OnOffConfiguration onOffConfiguration, Map<?, ?> properties) {
		System.out.println("[DAY][MANAGER] - Bind new OnOffConfiguration.");
	}

	/** Unbind Method for OnOffConfigurations dependency */
	public void unbindOnOffConfiguration(OnOffConfiguration onOffConfiguration, Map<?, ?> properties) {
		System.out.println("[DAY][MANAGER] - Unbind OnOffConfiguration.");
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[DAY][MANAGER] - Stopping LightManager.");
		this.timeService.unregister(this);
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[DAY][MANAGER] - Starting LightManager.");
		this.timeService.register(this);
	}

	@Override
	public void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
		switch (newMomentOfTheDay) {
		case MORNING:
			this.turnOnAllTheLights();
			break;
		case AFTERNOON:
		case NIGHT:
			this.turnOffAllTheLights();
			break;
		case EVENING:
			// do nothing, like default.
		default:
			break;
		}

	}

	@Override
	public void hourHasChanged(int h) {
		if (this.timer <= AutoOnConfiguration.PROP_AUTO_ON_PERIOD) {
			this.timer++;
		} else {
			System.out.println("[DAY][MANAGER] - Resetting Auto On Configuration");
			this.timer = 0;
			this.resetAutoOn();
		}

	}

	@Override
	public void resetAutoOn() {
		this.autoOnConfiguration.reset();
	}

}
