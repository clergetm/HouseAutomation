package fr.uga.warehouse.mode.day.light.autoon.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.day.light.manager.LightAdministration;

/**
 * <u>AutoOnCommandImpl</u> allows you to reset The AutoOn Configuration.
 * 
 * @author mathys
 */
@Component
@Instantiate(name = "fr.uga.warehouse.mode.day.light.autoon.command")
@CommandProvider(namespace = "autoonlight")
public class AutoOnCommandImpl {

	/** Field for LightAdministration dependency */
	@Requires
	private LightAdministration lightAdministrationService;

	/**
	 * Reset the <u>AutoOn Configuration<u>.
	 */
	@Command
	public void reset() {
		this.lightAdministrationService.resetAutoOn();
	}

	/** Component Lifecycle Method */
	@Invalidate
	public void stop() {
		System.out.println("[CMD] - AutoOn commands are now unusable.");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("[CMD] - AutoOn commands are now usable.");
		System.out.println("[CMD] - The commands are :");
		System.out.println("[CMD] -   - reset : Reset the AutoOn Configuration.");
	}
}
