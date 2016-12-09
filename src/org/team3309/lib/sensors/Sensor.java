package org.team3309.lib.sensors;

import org.team3309.lib.actuators.Actuators;

public abstract class Sensor {
protected abstract void update();
	
	public Sensor() {
		Sensors.addSensor(this);
	}
	public void read() {
		update();
	}
}
