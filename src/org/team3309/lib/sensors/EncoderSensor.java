package org.team3309.lib.sensors;

import edu.wpi.first.wpilibj.Encoder;

public class EncoderSensor extends Sensor {

	private Encoder encoder;
	private double lastRate = 0;
	private int lastPosition = 0;

	public EncoderSensor(int a, int b, boolean bool) {
		encoder = new Encoder(a, b, bool);
	}

	public double getRate() {
		return lastRate;
	}

	public int getPosition() {
		return lastPosition;
	}

	@Override
	protected void update() {
		lastRate = encoder.getRate();
		lastPosition = encoder.get();
	}

	public void reset() {
		encoder.reset();
	}

}
