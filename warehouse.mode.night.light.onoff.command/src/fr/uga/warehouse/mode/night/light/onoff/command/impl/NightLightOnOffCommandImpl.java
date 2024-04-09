package fr.uga.warehouse.mode.night.light.onoff.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.night.light.manager.NightLightAdministration;

/**
 * <u>OnOffCommandImpl</u> allows you to turn On or Off all the lights from the
 * console.
 */
@Component
@Instantiate(name = "fr.uga.warehouse.mode.night.light.onoff.command")
@CommandProvider(namespace = "onoffnightlight")
public class NightLightOnOffCommandImpl {
	@Requires
	private NightLightAdministration nightLightAdministration;
	/**
	 * Turn all lights on
	 */
	@Command
	public void nightTurnLightsOn() {
		this.nightLightAdministration.nightTurnLightsOn();
	}
	/**
	 * Turn all forcefully lightened lights off 
	 */
	@Command
	public void nightTurnLightsOffSoft() {
		this.nightLightAdministration.nightTurnLightsOffSoft();
	}
	/**
	 * Turn all lights off 
	 */
	@Command
	public void nightTurnLightsOff() {
		this.nightLightAdministration.nightTurnLightsOff();
	}
	
	/** Component Lifecycle Method */
	@Invalidate
	public void stop() {
		System.out.println("[CMD] - OnOff night light commands are now unusable.");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("[CMD] - OnOff night light commands are now usable.");
		System.out.println("[CMD] - The commands are :");
		System.out.println("[CMD] -   - turnnightlightsoff : Turn OFF all lights mode");
		System.out.println("[CMD] -   - turnnightlightsoffsoft : Turn OFF all lights mode turned on by other command");
		System.out.println("[CMD] -   - turnnightlightson : Turn all lights on");

	}
}
