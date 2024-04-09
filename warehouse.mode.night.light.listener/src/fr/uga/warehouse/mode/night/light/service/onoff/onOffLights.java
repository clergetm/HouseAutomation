package fr.uga.warehouse.mode.night.light.service.onoff;

public interface onOffLights {
	/**
	 * Turn all lights on
	 */
	public void nightTurnLightsOn();
	/**
	 * Turn all forcefully lightened lights off 
	 */
	public void nightTurnLightsOffSoft();
	/**
	 * Turn all lights off 
	 */
	public void nightTurnLightsOff();
}
