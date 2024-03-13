package fr.uga.warehouse.client.time.command.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.uga.warehouse.client.time.TimeService;

/**
 * <u>TimeCommandImpl</u> allows you to turn get informations about time.
 * 
 * @author mathys
 */
@Component
@Instantiate(name = "fr.uga.warehouse.client.time.command")
@CommandProvider(namespace = "timecmd")
public class TimeCommandImpl {

	/** Field for timeService dependency */
	@Requires
	private TimeService timeService;

	/**
	 * Get the current <u>MomentOfTheDay<u>.
	 */
	@Command
	public void getTime() {
		System.out.println("[CMD][TIME] - The current time is : " + this.timeService.getMomentOfTheDay());
	}

	/** Component Lifecycle Method */
	@Invalidate
	public void stop() {
		System.out.println("[CMD] - Time commands are now unusable.");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("[CMD] - Time commands are now usable.");
		System.out.println("[CMD] - The commands are :");
		System.out.println("[CMD] -   - getTime : Get the current MomentOfTheDay.");
	}
}
