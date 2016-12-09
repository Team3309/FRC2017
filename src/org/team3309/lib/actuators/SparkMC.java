package org.team3309.lib.actuators;

import edu.wpi.first.wpilibj.Spark;

public class SparkMC extends Actuator {
	private Spark spark;
	private boolean isReversed = false;
	private double desiredOutput = 0.0;

	public SparkMC(int port) {
		spark = new Spark(port);
	}

	@Override
	protected void output() {
		spark.set(desiredOutput * (isReversed ? -1: 1));
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

}
