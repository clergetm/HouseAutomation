package fr.uga.warehouse.mode.night.manager.impl;

import fr.uga.warehouse.client.time.MomentOfTheDay;
import fr.uga.warehouse.mode.night.manager.AlarmAdministration;
import fr.uga.warehouse.client.time.MomentOfTheDayListener;
import fr.uga.warehouse.client.time.TimeService;
import fr.uga.warehouse.mode.night.alarm.service.onoff.onOffAlarm;

public class NightManagerImpl implements MomentOfTheDayListener, AlarmAdministration {

	/** Field for timeService dependency */
	private TimeService timeService;
	
	private onOffAlarm alarmService;

	@Override
	public void hourHasChanged(int h) {
		// TODO Auto-generated method stub

	}

	@Override
	public void momentOfTheDayHasChanged(MomentOfTheDay newMomentOfTheDay) {
		switch (newMomentOfTheDay) {
		case MORNING:
			this.disableAlarmMode();
			break;
		case AFTERNOON:
		case NIGHT:
			this.enableAlarmMode();
			break;
		case EVENING:
			// do nothing, like default.
		default:
			break;
		}
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("[NIGHT][MANAGER] - Stopping Night Manager");
		this.timeService.unregister(this);
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("[NIGHT][MANAGER] - Starting Night Manager");
		this.timeService.register(this);
	}
	
	public void enableAlarmMode() {
		System.out.println("[NIGHT][MANAGER] - Alarm mode enabled");
		alarmService.enableAlarmMode();
	}
	
	public void disableAlarmMode() {
		System.out.println("[NIGHT][MANAGER] - Alarm mode disabled");
		alarmService.disableAlarmMode();
	}

	@Override
	public void turnAlarmOn() {
		this.enableAlarmMode();
	}

	@Override
	public void turnAlarmOff() {
		this.disableAlarmMode();	
	}

}
