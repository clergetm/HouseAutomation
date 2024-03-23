package fr.uga.warehouse.mode.day.light.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.day.light.manager.LightAdministration;

/**
 * <u>OnOffCommandImpl</u> allows you to turn On or Off
 * all the lights from the console.
 * 
 * @author mathys
 */
@Component
@Instantiate(name = "fr.uga.warehouse.mode.day.light.command")
@CommandProvider(namespace = "onofflight")
public class OnOffCommandImpl {

	/** Field for LightAdministration dependency */
	@Requires
	private LightAdministration lightAdministrationService;
	
	/**
	 * Turn <b>OFF</b> all the the lights of the map.
	 */
	@Command
	public void turnLightsOff() {
		this.lightAdministrationService.turnOffAllTheLights();
	}
	
	/**
	 * Turn <b>ON</b> all the the lights of the map.
	 */
	@Command
	public void turnLightsOn() {
		this.lightAdministrationService.turnOnAllTheLights();
	}
}
