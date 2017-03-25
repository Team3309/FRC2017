package org.usfirst.frc.team3309.auto.operations;

import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;

public class PivotUpOperation extends Operation {

	public PivotUpOperation(int i) {
		super(i);
	}

	@Override
	public void perform() throws InterruptedException, TimedOutException {
		GearIntake.getInstance().pivotUpGearIntake();
	}

}
