package org.usfirst.frc.team3309.auto.operations;

import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;

public class PivotDownOperation extends Operation {

	public PivotDownOperation(int counts) {
		super(counts);
	}
	@Override
	public void perform() throws InterruptedException, TimedOutException {
		GearIntake.getInstance().pivotDownGearIntake();
	}

}
