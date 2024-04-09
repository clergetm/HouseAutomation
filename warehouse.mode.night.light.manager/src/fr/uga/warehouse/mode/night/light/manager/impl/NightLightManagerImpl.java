package fr.uga.warehouse.mode.night.light.manager.impl;

import fr.uga.warehouse.mode.night.light.manager.NightLightAdministration;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.ColorLight;
import java.util.Map;
import fr.uga.warehouse.mode.night.light.service.onoff.onOffLights;

public class NightLightManagerImpl implements NightLightAdministration {

	/** Field for lightService dependency */
	public onOffLights lightService;

	@Override
	public void nightTurnLightsOn() {
		// TODO Auto-generated method stub
		System.out.println("Night light manager impl turn lights on");

	}

	@Override
	public void nightTurnLightsOff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nightTurnLightsOffSoft() {
		// TODO Auto-generated method stub

	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[NIGHT][LIGHTMANAGER] - Stopping Night Manager");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[NIGHT][LIGHTMANAGER] - Starting Night Manager");
	}

}
