package fr.uga.warehouse.mode.night.light.manager;

public interface NightLightAdministration {
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
