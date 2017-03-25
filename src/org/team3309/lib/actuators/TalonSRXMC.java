package org.team3309.lib.actuators;

import com.ctre.CANTalon;

public class TalonSRXMC extends CANTalon {

	private TalonControlMode currentControlMode = TalonControlMode.PercentVbus;

	public TalonSRXMC(int port) {
		super(port);
	}

	public TalonSRXMC(int turretId, int i) {
		super(turretId, i);
	}

	@Override
	public void changeControlMode(TalonControlMode controlMode) {
		if (!controlMode.equals(currentControlMode))
			super.changeControlMode(controlMode);
	}
}
