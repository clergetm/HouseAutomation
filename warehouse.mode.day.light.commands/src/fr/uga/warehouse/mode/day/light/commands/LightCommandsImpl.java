package fr.uga.warehouse.mode.day.light.commands;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.day.light.manager.LightAdministration;

/**
 * <u>LightCommandsImpl</u> introduce the commands for light configuration.
 * <ul>
 * <li><u><b>reset</b></u> allows you to reset The AutoOn Configuration.</li>
 * <li><u><b>turnlightson</b></u> allows you to turn <b>on</b> all the
 * lights.</li>
 * <li><u><b>turnlightsoff</b></u> allows you to turn <b>off</b> all the
 * lights.</li.
 * </ul>
 * 
 * @author mathys
 */
@Component
@Instantiate(name = "fr.uga.warehouse.mode.day.light.commands")
@CommandProvider(namespace = "lightcmd")
public class LightCommandsImpl {

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

	/** Component Lifecycle Method */
	@Invalidate
	public void stop() {
		System.out.println("[CMD] - Light commands are now unusable.");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("[CMD] - Light commands are now usable.");
		System.out.println("[CMD] - The commands are :");
		System.out.println("[CMD] -   - reset : Reset the AutoOn Configuration.");
		System.out.println("[CMD] -   - turnlightsoff : Turn OFF all the the lights of the map.");
		System.out.println("[CMD] -   - turnlightson : Turn ON all the the lights of the map.");
	}
}
