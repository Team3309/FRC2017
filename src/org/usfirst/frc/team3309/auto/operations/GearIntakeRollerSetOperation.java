package org.usfirst.frc.team3309.auto.operations;

import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;

public class GearIntakeRollerSetOperation extends Operation {

	private double power = 0;

	public GearIntakeRollerSetOperation(double encoder, double power) {
		this.power = power;
	}

	@Override
	public void perform() throws InterruptedException, TimedOutException {
		GearIntake.getInstance().setGearIntakeRoller(power);
	}

}
