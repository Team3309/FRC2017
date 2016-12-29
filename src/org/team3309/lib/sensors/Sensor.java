package org.team3309.lib.sensors;

import org.team3309.lib.actuators.Actuators;
import org.usfirst.frc.team339.robot.Sensors;

public abstract class Sensor {
protected abstract void update();
	
	public Sensor() {
		Sensors.addSensor(this);
	}
	public void read() {
		update();
	}
}
