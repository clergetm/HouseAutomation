package fr.uga.warehouse.mode.night.light.listener.impl;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.uga.warehouse.mode.night.light.service.onoff.onOffLights;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fr.liglab.adele.icasa.device.sound.Speaker;

public class NightLightListenerImpl implements onOffLights, DeviceListener<GenericDevice> {

	/** The property location as a const */
	public static final String PROPERTY_LOCATION_NAME = "Location";
	/** The name of the location for unknown value */
	public static final String PROPERTY_LOCATION_UNKNOWN_VALUE = "unknown";
	/** The custom property to check the device's type */
	public static final String PROPERTY_OBJECT_NAME = "NightLightListener";

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	/** True if all the lights have been turned on using the onOffLights service **/
	private boolean AllLightsOn = false;

	private ArrayList<BinaryLight> alreadyOnLights = new ArrayList<BinaryLight>();
	/** Field for speakers dependency */
	private Speaker[] speakers;
	
	private ArrayList<String> loudLocations = new ArrayList<String>();

	private void println(String toPrint) {
		System.out.println("[NIGHT][LightListener] - " + toPrint);
	}

	/** Bind Method for presenceSensors dependency */
	public void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		println("Bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
		presenceSensor.setPropertyValue(NightLightListenerImpl.PROPERTY_OBJECT_NAME, PresenceSensor.class.getName());
	}

	/** Unbind Method for presenceSensors dependency */
	public void unbindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeProperty(NightLightListenerImpl.PROPERTY_OBJECT_NAME);
		presenceSensor.removeListener(this);
	}

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLights(BinaryLight binaryLight, Map properties) {
		println("Bind binary light " + binaryLight.getSerialNumber());
		binaryLight.addListener(this);
		binaryLight.setPropertyValue(NightLightListenerImpl.PROPERTY_OBJECT_NAME, BinaryLight.class.getName());
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLights(BinaryLight binaryLight, Map properties) {
		println("Unind binary light " + binaryLight.getSerialNumber());
		binaryLight.removeProperty(NightLightListenerImpl.PROPERTY_OBJECT_NAME);
		binaryLight.removeListener(this);
	}
	
	/** Bind Method for speakers dependency */
	public void bindSpeaker(Speaker speaker, Map properties) {
		println("bind speaker " + speaker.getSerialNumber());
		speaker.addListener(this);
		speaker.setPropertyValue(NightLightListenerImpl.PROPERTY_OBJECT_NAME, Speaker.class.getName());
	}

	/** Unbind Method for speakers dependency */
	public void unbindSpeaker(Speaker speaker, Map properties) {
		println("Unind speaker " + speaker.getSerialNumber());
		speaker.removeProperty(NightLightListenerImpl.PROPERTY_OBJECT_NAME);
		speaker.removeListener(this);
	}

	/**
	 * Turn all the lights on. Keep track of the lights already on before turning all of them on.
	 */
	@Override
	public void nightTurnLightsOn() {
		println("Turn on all lights");
		this.AllLightsOn = true;
		for (BinaryLight bl : binaryLights) {
			if (bl.getPowerStatus()) {
				// light is already on
				alreadyOnLights.add(bl);
			} else {
				bl.setPowerStatus(true);
			}
		}
	}

	/**
	 * Turn all the lights off, if they were off before turning them on
	 */
	@Override
	public void nightTurnLightsOffSoft() {
		println("Turn off all lights");
		this.AllLightsOn = false;
		for (BinaryLight bl : binaryLights) {
			if (bl.getPowerStatus() && !alreadyOnLights.contains(bl)) {
				bl.setPowerStatus(false);
			}
		}
		this.alreadyOnLights.clear();
	}

	@Override
	public void nightTurnLightsOff() {
		println("Turn off all lights");
		this.AllLightsOn = false;
		for (BinaryLight bl : binaryLights) {
			if (bl.getPowerStatus()) {
				bl.setPowerStatus(false);
			}
		}
		this.alreadyOnLights.clear();

	}
	
	private synchronized List<BinaryLight> getBinaryLightsFromLocation(String location) {
		List<BinaryLight> blToReturn = new ArrayList<>();
		for (BinaryLight bl : this.binaryLights) {
			if (bl.getPropertyValue(NightLightListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				blToReturn.add(bl);
			}
		}
		return blToReturn;
	}
	
	private synchronized List<Speaker> getSpeakersFromLocation(String location) {
		List<Speaker> speakersToReturn = new ArrayList<>();
		for (Speaker spk : this.speakers) {
			if (spk.getPropertyValue(NightLightListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				speakersToReturn.add(spk);
			}
		}
		return speakersToReturn;
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object arg2, Object arg3) {
		String location = (String) device.getPropertyValue(NightLightListenerImpl.PROPERTY_LOCATION_NAME);
		String objectType = (String) device.getPropertyValue(NightLightListenerImpl.PROPERTY_OBJECT_NAME);

		// needs to be in an if, because the "state" property is wrong
		if (objectType.equals("fr.liglab.adele.icasa.device.sound.Speaker") && propertyName.toString().equals("state"))
		{
			
			Speaker speaker = (Speaker) device;
			if (speaker.getState() == Speaker.STATE_ACTIVATED.toString()) {
				// speaker is turned on -> alarm mode
				if (!loudLocations.contains(location)) {
					// this speaker loud is the first 
					println("Speaker activated at "+location+". Turning on lights.");
					this.SetLightPower(location, true);
				}
				loudLocations.add(location);
			} else {
				if (loudLocations.contains(location)) {
					println("Speaker de-activated at "+location+". Turning off lights.");
					// speaker is turned off -> mute location -> turn off lights
					this.SetLightPower(location, false);
					loudLocations.remove(location);
				}
			}
		}
		
		switch (propertyName) {
			// The sensor detects a new presence
			case PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE:
				// We have to activate associated speaker (= unmute)
				if (PresenceSensor.class.getName().equals(objectType)) {
					PresenceSensor sensor = (PresenceSensor) device;
					if (sensor.getSensedPresence()) {
						this.SetLightPower(location, true);
					} else {
						if (!this.AnySpeakerOn(location)) {
							// if alarm is off, turn off lights
							this.SetLightPower(location, false);
						}
					}
				}
				break;
			default:
				break;
		}

	}
	
	private boolean AnySpeakerOn(String location) {
		List<Speaker> arr = this.getSpeakersFromLocation(location);
		for (Speaker spk : arr) {
			if (spk.getState() == Speaker.STATE_ACTIVATED) {
				return true;
			}
		}
		return false;
	}
	
	private void SetLightPower(String location, boolean power) {
		for (BinaryLight bl : this.getBinaryLightsFromLocation(location)) {
			bl.setPowerStatus(power);
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub

	}
	
	/** Component Lifecycle Method */
	public void start() {
		println("Starting NightLightListener.");
	}

	/** Component Lifecycle Method */
	public void stop() {
		println("Stopping NightLightListener.");
	}

}
