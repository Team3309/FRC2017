package org.team3309.lib.actuators;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWM.PeriodMultiplier;

public class ContinuousRotationServo extends PWM {
	public ContinuousRotationServo(int channel) {
		super(channel);

		setBounds(.80, 1.485, 1.5, 1.515, 2.2);
		// enableDeadbandElimination(false);
	}

	@Override
	public void setBounds(double min, double dead_min, double center, double dead_max, double max) {
		// super.setBounds(min, dead_min, center, dead_max, max);
		super.setBounds(max, dead_max, center, dead_min, min);
	}

	public void set(double value) {
		if (value == 0) { // setSpeed(0); // this.setZeroLatch();
			this.setDisabled();
		} else {
			this.setPosition(value);
		}
	}
}