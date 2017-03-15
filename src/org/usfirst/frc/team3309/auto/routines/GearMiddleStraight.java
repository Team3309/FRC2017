package org.usfirst.frc.team3309.auto.routines;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class GearMiddleStraight extends SteamworksAutoRoutine {
	private final double TURRET_ANGLE_TO_SHOOT = 90;
	private final double FORWARD_DISTANCE = 15000;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		Turret.getInstance().turnToAngleAndSurvey(TURRET_ANGLE_TO_SHOOT);
		shoot();
		KragerTimer.delayMS(3000);
		this.stopShooting();
		driveEncoder(FORWARD_DISTANCE, 5);
		KragerTimer.delayMS(1000);
		openGearIntake();
		driveEncoder(-5000, 5);
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		Turret.getInstance().turnToAngleAndSurvey(-TURRET_ANGLE_TO_SHOOT);
		shoot();
		KragerTimer.delayMS(3000);
		this.stopShooting();
		driveEncoder(FORWARD_DISTANCE, 5);
		KragerTimer.delayMS(1000);
		openGearIntake();
		driveEncoder(-5000, 5);
	}
}
