package fr.uga.warehouse.mode.day.light.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.day.light.manager.LightAdministration;

@Component
@Instantiate(name = "fr.uga.warehouse.mode.day.light.command")
@CommandProvider(namespace = "onofflight")
public class OnOffCommandImpl {

	@Requires
	private LightAdministration lightAdministrationService;
	
	@Command
	public void turnLightsOff() {
		this.lightAdministrationService.turnOffAllTheLights();
	}
	
	@Command
	public void turnLightsOn() {
		this.lightAdministrationService.turnOnAllTheLights();
	}
}
