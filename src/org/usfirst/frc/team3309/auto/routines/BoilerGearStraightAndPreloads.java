package org.usfirst.frc.team3309.auto.routines;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class BoilerGearStraightAndPreloads extends SteamworksAutoRoutine {

	private final double TURRET_ANGLE_TO_SHOOT = 135;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 3);
		turnToAngle(-UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG, 5);
		KragerTimer.delayMS(500);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 3);
		placeGear();
		Turret.getInstance().turnToAngleAndSurvey(-180);
		driveEncoder(-15000, 1);
		shoot();
		KragerTimer.delayMS(5000);
		this.stopShooting();
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 3);
		turnToAngle(UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG + 5, 5);
		KragerTimer.delayMS(500);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 3);
		placeGear();
		Turret.getInstance().turnToAngleAndSurvey(-180);
		driveEncoder(-15000, 1);
		shoot();
		KragerTimer.delayMS(5000);
		this.stopShooting();
	}
}