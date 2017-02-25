package org.usfirst.frc.team3309.auto;

import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.Drive;

public class HopperAndShootStraightPathBlue extends AutoRoutine {
	@Override
	public void routine() throws TimedOutException, InterruptedException {
		System.out.println("AUTO RUNS");
		Drive.getInstance().setLowGear();
		this.driveEncoder(31500, 10);
		System.out.println("turb to anga");
		this.turnToAngle(90, 10000);
		Thread.sleep(1000);
		// this.waitForDriveAngle(90, 1);
		System.out.println("GO FORARD");
		this.driveEncoder(15500, 10);
		this.waitForEndOfAuto();
	}
}