package org.usfirst.frc.team3309.auto;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.operations.defenses.Operation;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;

public class CustomAuto extends AutoRoutine {

	private Operation defense;
	private Operation startingPosition;

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		defense.perform();
		KragerTimer.delayMS(200);
		
		mDrive.setHighGear(true);
		Hood.getInstance().setGoalAngle(40.3);
		Flywheel.getInstance().setAimVelRPSAuto(130);
		KragerTimer.delayMS(200);
		startingPosition.perform();
		mDrive.stopDrive();
	}

	public Operation getDefense() {
		return defense;
	}

	public void setDefense(Operation defense) {
		this.defense = defense;
	}

	public Operation getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(Operation operation) {
		this.startingPosition = operation;
	}

}
