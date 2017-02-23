package org.usfirst.frc.team3309.auto.routines;

import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.Drive;

public class GearIntakeMiddle extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {

		System.out.println("AUTO RUNS");
		Drive.getInstance().setLowGear();
		this.driveEncoder(34000, 3);
		System.out.println("turb to anga");
		this.turnToAngle(90, 2.5);
		Thread.sleep(1000);
		// this.waitForDriveAngle(90, 1);
		System.out.println("GO FORARD");
		spinUp();
		this.setFuelIntake(1);
		this.driveEncoder(17000, 3);
		this.shoot();
		this.waitForEndOfAuto();
	}
}
