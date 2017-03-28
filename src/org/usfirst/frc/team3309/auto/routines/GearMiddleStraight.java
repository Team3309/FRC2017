package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.GearIntakeRollerSetOperation;
import org.usfirst.frc.team3309.auto.operations.PivotUpOperation;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class GearMiddleStraight extends SteamworksAutoRoutine {
	private final double TURRET_ANGLE_TO_SHOOT = 90;
	private final double FORWARD_DISTANCE = 24500;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {

		Turret.getInstance().returnHome();
		this.setFuelIntake(1);
		GearIntake.getInstance().setGearIntakeRoller(.5);
		KragerTimer.delayMS(500);
		this.setFuelIntake(0);
		this.driveEncoder(10000, 1.25);
		this.pivotUpGearIntake();
		this.driveEncoder(-2000, .75);
		GearIntake.getInstance().setGearIntakeRoller(1);
		KragerTimer.delayMS(250);
		GearIntake.getInstance().setGearIntakeRoller(0);
		this.driveEncoder(20500, 2.5);
		GearIntake.getInstance().setGearIntakeRoller(-.5);
		this.pivotDownGearIntake();
		openGearIntake();
		GearIntake.getInstance().setGearIntakeRoller(-1);
		Turret.getInstance().turnToAngleAndSurvey(-330);
		// this.spinUp();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(-3000, 0));
		changePoints.add(new VelocityChangePoint(-600, -3500, 2000));
		changePoints.add(new VelocityChangePoint(-2000, -2000, 15000, -90));

		this.driveEncoder(25000, 9, changePoints);

		this.spinUp();
		KragerTimer.delayMS(1000);

		shoot();
		KragerTimer.delayMS(4000);
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		Turret.getInstance().returnHome();
		this.setFuelIntake(1);
		GearIntake.getInstance().setGearIntakeRoller(.5);
		KragerTimer.delayMS(500);
		this.setFuelIntake(0);
		this.driveEncoder(10000, 1.25);
		this.pivotUpGearIntake();
		this.driveEncoder(-2000, .75);
		GearIntake.getInstance().setGearIntakeRoller(1);
		KragerTimer.delayMS(250);
		GearIntake.getInstance().setGearIntakeRoller(0);
		this.driveEncoder(20500, 2.5);
		GearIntake.getInstance().setGearIntakeRoller(-.5);
		this.pivotDownGearIntake();
		openGearIntake();
		GearIntake.getInstance().setGearIntakeRoller(-1);
		Turret.getInstance().turnToAngleAndSurvey(-135);
		// this.spinUp();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(-3000, 0));
		changePoints.add(new VelocityChangePoint(-3500, -600, 2000));
		changePoints.add(new VelocityChangePoint(-2000, -2000, 15000, 90));

		this.driveEncoder(25000, 9, changePoints);

		this.spinUp();
		KragerTimer.delayMS(1000);
		shoot();
		KragerTimer.delayMS(4000);
	}
}
