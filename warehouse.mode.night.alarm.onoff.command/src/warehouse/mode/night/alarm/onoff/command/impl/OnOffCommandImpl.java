package warehouse.mode.night.alarm.onoff.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.mode.night.manager.AlarmAdministration;

/**
 * <u>OnOffCommandImpl</u> allows you to turn On or Off all the lights from the
 * console.
 * 
 * @author mathys
 */
@Component
@Instantiate(name = "fr.uga.warehouse.mode.night.alarm.onoff.command")
@CommandProvider(namespace = "onoffalarm")
public class OnOffCommandImpl {

	/** Field for LightAdministration dependency */
	@Requires
	private AlarmAdministration alarm;

	/**
	 * Turn <b>OFF</b> all the the lights of the map.
	 */
	@Command
	public void turnAlarmOff() {
		this.alarm.turnAlarmOff();
	}

	/**
	 * Turn <b>ON</b> all the the lights of the map.
	 */
	@Command
	public void turnAlarmOn() {
		this.alarm.turnAlarmOn();
	}

	/** Component Lifecycle Method */
	@Invalidate
	public void stop() {
		System.out.println("[CMD] - OnOff alarm commands are now unusable.");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("[CMD] - OnOff alarm commands are now usable.");
		System.out.println("[CMD] - The commands are :");
		System.out.println("[CMD] -   - turnalarmoff : Turn OFF alarm mode");
		System.out.println("[CMD] -   - turnalarmon : Turn ON alarm mode");
	}
}

