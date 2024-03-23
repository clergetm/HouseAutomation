package fr.uga.warehouse.mode.day.light.manager.impl;

import fr.uga.warehouse.mode.day.light.manager.LightAdministration;
import fr.uga.warehouse.mode.day.light.service.onoff.OnOffConfiguration;
import java.util.Map;

public class LightManagerImpl implements LightAdministration {

	/** Field for OnOffConfigurations dependency */
	private OnOffConfiguration[] OnOffConfigurations;

	@Override
	public void turnOffAllTheLights() {
		System.out.println("[DAY][MANAGER] - turning all lights off.");
		for (OnOffConfiguration conf : this.OnOffConfigurations) {
			conf.turnOffAllTheLights();
		}
	}

	@Override
	public void turnOnAllTheLights() {
		System.out.println("[DAY][MANAGER] - turning all lights on.");
		for (OnOffConfiguration conf : this.OnOffConfigurations) {
			conf.turnOnAllTheLights();
		}
	}

	/** Bind Method for OnOffConfigurations dependency */
	public void bindOnOffConfiguration(OnOffConfiguration onOffConfiguration, Map<?, ?> properties) {
		System.out.println("[DAY][MANAGER] - bind new OnOffConfiguration : " 
				+ properties.get(OnOffConfiguration.PROP_ON_OFF_NAME));
	}

	/** Unbind Method for OnOffConfigurations dependency */
	public void unbindOnOffConfiguration(OnOffConfiguration onOffConfiguration, Map<?, ?> properties) {
		System.out.println("[DAY][MANAGER] - unbind OnOffConfiguration : "
				+ properties.get(OnOffConfiguration.PROP_ON_OFF_NAME));
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[DAY][MANAGER] - Stopping LightManager.");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[DAY][MANAGER] - Starting LightManager.");
	}

}
