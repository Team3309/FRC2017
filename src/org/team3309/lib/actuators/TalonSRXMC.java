package org.team3309.lib.actuators;

import com.ctre.CANTalon;

public class TalonSRXMC extends Actuator {

	private CANTalon talon;
	private boolean isReversed = false;
	private double desiredOutput = 0.0;
	private double lastPosition = 0;
	private double lastVelocity = 0;

	public TalonSRXMC(int port) {
		talon = new CANTalon(port);
	}

	@Override
	protected void output() {
		// System.out.println("DES " + desiredOutput);
		talon.set(desiredOutput);
		// if(talon.)

	}

	public double getPosition() {
		return lastPosition;
	}

	public boolean isReversed() {
		return isReversed;
	}

	public void setReversed(boolean isReversed) {
		this.isReversed = isReversed;
	}

	public double getDesiredOutput() {
		return desiredOutput;
	}

	public void setDesiredOutput(double desiredOutput) {
		this.desiredOutput = desiredOutput;
	}

	public CANTalon getTalon() {
		return talon;
	}

}
