package org.usfirst.frc.team3309.auto.operations;

import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class ShootOperation extends Operation {
	public ShootOperation(int encoderGoal) {
		super(encoderGoal);
	}

	@Override
	public void perform() throws InterruptedException, TimedOutException {
		this.spinUp();
		this.shoot();
	}
}
