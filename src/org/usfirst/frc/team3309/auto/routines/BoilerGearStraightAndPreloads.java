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

		this.setFuelIntake(1);
		GearIntake.getInstance().setGearIntakeRoller(.5);
		KragerTimer.delayMS(250);
		this.setFuelIntake(0);
		GearIntake.getInstance().setGearIntakeRoller(1);
		this.driveEncoder(12000, 1.25);
		this.closeGearIntake();
		this.pivotUpGearIntake();
		KragerTimer.delayMS(250);
		this.driveEncoder(-2000, 1.5);
		GearIntake.getInstance().setGearIntakeRoller(.25);
		this.driveEncoder(18000, 3);
		this.placeGear();
		this.driveEncoder(-10000, 2);
		this.turnToAngle(78, 2);
		KragerTimer.delayMS(150);
		this.driveEncoder(32000, 2);
		this.shoot();

	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.setFuelIntake(1);
		GearIntake.getInstance().setGearIntakeRoller(.5);
		KragerTimer.delayMS(250);
		this.setFuelIntake(0);
		GearIntake.getInstance().setGearIntakeRoller(1);
		this.driveEncoder(12000, 1.25);
		this.closeGearIntake();
		this.pivotUpGearIntake();
		KragerTimer.delayMS(250);
		this.driveEncoder(-2000, 1.5);
		GearIntake.getInstance().setGearIntakeRoller(.25);
		this.driveEncoder(18000, 3);
		this.placeGear();
		this.driveEncoder(-10000, 2);
		this.turnToAngle(-78, 2);
		KragerTimer.delayMS(150);
		this.driveEncoder(32000, 2);
		this.shoot();
	}
}