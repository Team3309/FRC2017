package org.team3309.lib.controllers.statesandsignals;

import java.util.HashMap;

/**
 * Signal that is sent to a subsystem from a controller. It is then parsed and
 * sent to motors from the subsystem.
 * 
 * @author TheMkrage
 *
 */
@SuppressWarnings("serial")
public class OutputSignal extends HashMap<String, Double> {

	public OutputSignal() {
		super();
		this.setMotor(0);
		this.setLeftRightMotor(0, 0);
	}

	// Default Key Methods
	public void setMotor(double motor) {
		this.put("motor", motor);
	}

	public double getMotor() {
		return this.get("motor");
	}

	public void setRightMotor(double rightPower) {
		this.put("right", rightPower);
	}

	public double getRightMotor() {
		return this.get("right");
	}

	public void setLeftMotor(double leftPower) {
		this.put("left", leftPower);
	}

	public double getLeftMotor() {
		return this.get("left");
	}

	public void setLeftRightMotor(double leftPower, double rightPower) {
		setLeftMotor(leftPower);
		setRightMotor(rightPower);
	}

}
