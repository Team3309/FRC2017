package org.team3309.lib.actuators;

import org.usfirst.frc.team3309.robot.Actuators;

public abstract class Actuator{
	protected abstract void output();
	
	public Actuator() {
		Actuators.addActuator(this);
	}
	public void actuate() {
		output();
	}
}
