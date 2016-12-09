package org.team3309.lib.sensors;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class CounterSensor extends Sensor {

	private double lastPeriod = 0;
	public Counter counter;

	public CounterSensor(int port) {
		counter = new Counter(port);
	}

	public CounterSensor(DigitalInput digitalInput) {
		counter = new Counter(digitalInput);
	}

	public double getPeriod() {
		return lastPeriod;
	}

	@Override
	protected void update() {
		lastPeriod = counter.getPeriod();
	}

}
