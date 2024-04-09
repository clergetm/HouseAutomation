package fr.uga.warehouse.mode.night.alarm.listener.impl;

import fr.uga.warehouse.mode.night.alarm.service.onoff.onOffAlarm;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.sound.Speaker;
import fr.liglab.adele.icasa.device.sound.AudioSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AlarmListenerImpl implements onOffAlarm, DeviceListener<GenericDevice> {

	/** The property location as a const */
	public static final String PROPERTY_LOCATION_NAME = "Location";
	/** The name of the location for unknown value */
	public static final String PROPERTY_LOCATION_UNKNOWN_VALUE = "unknown";
	/** The custom property to check the device's type */
	public static final String PROPERTY_OBJECT_NAME = "AlarmListener";
	
	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for speakers dependency */
	private Speaker[] speakers;
	
	private AudioSource[] audioSources;
	
	private boolean isAlarmMode;
	
	private static final String[] indoorLocations = {"Stockage 2", "Stockage 1", "Stockage 3", "Accueil", "Contrôle"};

	/**
	 * Enable the Alarm mode. If a presence is detected inside, all alarms will go wild
	 */
	@Override
	public void enableAlarmMode() {
		println("AlarmListener AlarmMode : ON.");
		this.isAlarmMode = true;
	}

	/**
	 * Disable the Alarm mode. If the alarm has been triggered, reset it.
	 */
	@Override
	public void disableAlarmMode() {
		println("AlarmListener AlarmMode : OFF.");
		this.isAlarmMode = false;
		this.resetAlarm();
	}
	
	private boolean __DEBUG = true;
	
	/**
	 * Custom printer
	 * @param str string to print
	 * @param additionnalInfo additional information, like SPEAKER
	 */
	private void println(String str, String additionnalInfo) {
		additionnalInfo = additionnalInfo =="" ? additionnalInfo : "["+additionnalInfo+"]";
		if (__DEBUG) System.out.println("[NIGHT]"+additionnalInfo+" - "+str);
	}
	/**
	 * Custom printer displaying [NIGHT] before each print
	 * @param str string to print
	 */
	private void println(String str) {
		println(str, "");
	}

	/** Bind Method for presenceSensors dependency */
	public void bindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		println("bind presence sensor.", "PRESENCE");
		presenceSensor.addListener(this);
		presenceSensor.setPropertyValue(AlarmListenerImpl.PROPERTY_OBJECT_NAME, PresenceSensor.class.getName());
	}

	/** Unbind Method for presenceSensors dependency */
	public void unbindPresenceSensors(PresenceSensor presenceSensor, Map properties) {
		println("unbind presence sensor.", "PRESENCE");
		presenceSensor.removeProperty(AlarmListenerImpl.PROPERTY_OBJECT_NAME);
		presenceSensor.removeListener(this);
	}

	/** Bind Method for speakers dependency */
	public void bindSpeakers(Speaker speaker, Map properties) {
		println("bind presence speakers.", "SPEAKER");
		speaker.addListener(this);
		speaker.setPropertyValue(AlarmListenerImpl.PROPERTY_OBJECT_NAME, Speaker.class.getName());
		if (audioSources.length != 0) {
			speaker.setAudioSource(audioSources[0]);
		}
		speaker.setState(Speaker.STATE_DEACTIVATED);
	}

	/** Unbind Method for speakers dependency */
	public void unbindSpeakers(Speaker speaker, Map properties) {
		println("unbind presence speakers.", "SPEAKER");
		speaker.removeProperty(AlarmListenerImpl.PROPERTY_OBJECT_NAME);
		speaker.removeListener(this);
		this.triggerAlarm();
	}
	
	public void bindAudioSource(AudioSource audioSource, Map properties) {
		println("bind audiosource", "AUDIOSOURCE");
		audioSource.addListener(this);
		audioSource.setPropertyValue(AlarmListenerImpl.PROPERTY_OBJECT_NAME, AudioSource.class.getName());
		if (this.audioSources.length == 1) {
			// first AudioSource added
			this.addAudioSourceToSpeakers(audioSource);
		}
	}
	
	private void addAudioSourceToSpeakers(AudioSource audioSource) {
		for (int i=0; i<speakers.length; i++) {
			speakers[i].setAudioSource(audioSource);
		}
	}
	
	public void unbindAudioSource(AudioSource audioSource, Map properties) {
		println("unbind audiosource", "AUDIOSOURCE");
		audioSource.removeProperty(AlarmListenerImpl.PROPERTY_OBJECT_NAME);
		audioSource.removeListener(this);
		
	}
	
	/** Component Lifecycle Method */
	public void start() {
		println("Starting AlarmListener.");
	}

	/** Component Lifecycle Method */
	public void stop() {
		println("Stopping AlarmListener.");
	}
	
	private synchronized List<Speaker> getSpeakersFromLocation(String location) {
		List<Speaker> speakersToReturn = new ArrayList<>();
		for (Speaker spk : this.speakers) {
			if (spk.getPropertyValue(AlarmListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				speakersToReturn.add(spk);
			}
		}
		return speakersToReturn;
	}
	
	private synchronized List<PresenceSensor> getPresenceSensorFromLocation(String location) {
		List<PresenceSensor> sensors = new ArrayList<>();
		for (PresenceSensor sensor : this.presenceSensors) {
			if (sensor.getPropertyValue(AlarmListenerImpl.PROPERTY_LOCATION_NAME).equals(location)) {
				sensors.add(sensor);
			}
		}
		return sensors;
	}
	
	// return true if there is at least one speaker in location, else return false
	private boolean isASpeakerRemaining(String location) {
		return !getSpeakersFromLocation(location).isEmpty();
	}

	private boolean isASensorRemaining(String location) {
		return !getPresenceSensorFromLocation(location).isEmpty();
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
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		
		// We need to be in alarmMode
		if (this.isAlarmMode == false) {
			return;
		}
		
		String location = (String) device.getPropertyValue(AlarmListenerImpl.PROPERTY_LOCATION_NAME);
		String objectType = (String) device.getPropertyValue(AlarmListenerImpl.PROPERTY_OBJECT_NAME);

		switch (propertyName) {
		// The sensor detects a new presence
		case PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE:
			// We have to activate associated speaker (= unmute)
			if (PresenceSensor.class.getName().equals(objectType)) {
				PresenceSensor sensor = (PresenceSensor) device;
				if (sensor.getSensedPresence()) {
					if (this.isLocationIndoor(location)) {
						this.triggerAlarm();
					} else {
						this.speakersLoud(location);
					}
					println("Something spotted at location " + location, "ALERT");
				} else {	
					this.speakersMute(location);
					println("Nothing spotted at location " + location, "");
				}
			}
			break;
		// We are moving a device
		case AlarmListenerImpl.PROPERTY_LOCATION_NAME:
			println("Movement !", "INFO");
			// We are moving a Speaker
			if (Speaker.class.getName().equals(objectType)) {
				Speaker spk = (Speaker) device;
				println("Info : Moving speaker from "+oldValue+" to "+newValue);
				speakerMute(spk); // Just a move of speaker in other location, we mute him
				
				// we check that there is speakers remaining in old location :
				if (oldValue != null && oldValue != AlarmListenerImpl.PROPERTY_LOCATION_UNKNOWN_VALUE && !this.isASpeakerRemaining((String) oldValue)) {
					// if not we active all alarms !!
					this.speakersLoud();
					println("No more speaker in location " + oldValue, "ALERT");
				}
				
				// We are moving a PresenceSensor
			} else if (PresenceSensor.class.getName().equals(objectType)) {
				if (oldValue != null && oldValue != AlarmListenerImpl.PROPERTY_LOCATION_UNKNOWN_VALUE && !this.isASensorRemaining((String) oldValue)) {
					// if not we active all alarms !!
					this.speakersLoud();
					println("Alert : No more sensor in location " + oldValue);
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	private boolean isLocationIndoor(String location) {
		for(String inloc : this.indoorLocations) {
			if (inloc.equals(location)) {
				return true;
			}
		}
		return false;
	}
	
	private volatile boolean AlarmTriggered = false;
	
	/** 
	 * Activate Alarm Mode (can't be de-activated, unless using the proper alarm switch)
	 */
	private void triggerAlarm() {
		AlarmTriggered = true;
		this.speakersLoud();
		this.callPolice();
	}
	
	/**
	 * De-activate the alarm
	 */
	private void resetAlarm() {
		AlarmTriggered = false;
		this.speakersMute();
	}
	
	/**
	 * Turn on all alarms at given location
	 * @param location
	 */
	private void speakersLoud(String location) {
		println("=-----SPEAKER ✔-----=", location);
		for (Speaker aspeaker : this.getSpeakersFromLocation(location)) {
			speakerLoud(aspeaker); // we found someone so we scream
		}
		println("=-----------------=");
	}
	
	/**
	 * Turn on all alarms
	 */
	private void speakersLoud() {
		println("=-----SPEAKER ✔-----=");
		for (Speaker aspeaker : speakers) {
			speakerLoud(aspeaker); // we found someone so we scream
		}
		println("=-----------------=");
	}
	
	/**
	 * Turn off all speakers at given location
	 * @param location
	 */
	private void speakersMute(String location) {
		if (AlarmTriggered) return; // can't turn off alarm if it's currently triggered
		println("=-----SPEAKER ✘-----=", location);
		for (Speaker spk : this.getSpeakersFromLocation(location)) {
			speakerMute(spk);
			
		}
		println("=-----------------=", location);
	}
	
	/**
	 * Turn off all speakers
	 */
	private void speakersMute() {
		if (AlarmTriggered) return; // can't turn off alarm if it's currently triggered
		println("=-----SPEAKER ✘-----=");
		for (Speaker spk : speakers) {
			speakerMute(spk);
			
		}
		println("=-----------------=");
	}
	
	/**
	 * Turn on one single speaker
	 * @param spk
	 */
	private void speakerLoud(Speaker spk) {
		println("Start speaker "+spk.getSerialNumber(), "SPEAKER");
		spk.setState(Speaker.STATE_ACTIVATED);
		spk.setVolume(1);
		setAudioSourceRun(true);
	}
	
	/**
	 * Turn off one single speaker
	 * @param spk
	 */
	private void speakerMute(Speaker spk) {
		if (AlarmTriggered) return; // can't turn off alarm if it's currently triggered
		println("Mute speaker "+spk.getSerialNumber(), "SPEAKER");
		setAudioSourceRun(false);
		spk.setVolume(0);
		spk.setState(Speaker.STATE_DEACTIVATED);
	}
	
	private void setAudioSourceRun(boolean run) {
		if (audioSources.length > 0) {
			if (run) {
				audioSources[0].play();
			} else {
				audioSources[0].pause();
			}
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
	
    /**
     * Play a sound in a thread.
     * @author MathysC
     *
     * @param path The path from {@code Development/}  to a sound in a .wav format only.
     * 
     * @see https://stackoverflow.com/questions/23255162/looping-audio-on-separate-thread-in-java
     * @see https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
     */
    private void callPolice() {
        // Create a thread and immediately start it.
        new Thread(() -> {
        	for (int i=0; i<10; i++) {
        		int call = 10-i;
        		println("Calling police in "+call+"s.", "POLICE");
        		if (this.AlarmTriggered == false) {
        			println("Alarm de-activated, Police call interrupted", "POLICE");
        			return;
        		}
        		try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
            println("====================", "POLICE");
            println("Calling the police !", "POLICE");
            println("====================", "POLICE");
        }).start();

    }
    
    

}
