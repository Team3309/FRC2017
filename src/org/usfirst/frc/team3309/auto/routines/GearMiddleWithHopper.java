package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class GearMiddleWithHopper extends SteamworksAutoRoutine {
	private final double TURRET_ANGLE_TO_SHOOT = 90;
	private final double FORWARD_DISTANCE = 24500;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		this.driveEncoder(20500, 2.5);
		placeGear();
		// backup and turn towards Hopper
		this.driveEncoder(-15000, 2);
		this.turnToAngle(45, 1.5);
		this.driveEncoder(15000, 2);
		Turret.getInstance().turnToAngleAndSurvey(90);

		KragerTimer.delayMS(1000);

		shoot();
		KragerTimer.delayMS(4000);
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		this.driveEncoder(20500, 2.5);
		placeGear();
		// backup and turn towards Hopper
		this.driveEncoder(-15000, 2);
		this.turnToAngle(-45, 1.5);
		this.driveEncoder(15000, 2);
		Turret.getInstance().turnToAngleAndSurvey(-90);
		KragerTimer.delayMS(1000);

		shoot();
		KragerTimer.delayMS(4000);
	}
}
