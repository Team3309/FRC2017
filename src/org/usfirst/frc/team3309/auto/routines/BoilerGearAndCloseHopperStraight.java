package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class BoilerGearAndCloseHopperStraight extends SteamworksAutoRoutine {

	private final double TURRET_ANGLE_TO_SHOOT = 90;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 5);
		turnToAngle(-UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG, 5);
		KragerTimer.delayMS(500);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 5);
		placeGear();
		driveEncoder(-15000, 5);
		this.turnToAngle(UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER, 1.5);
		Turret.getInstance().turnToAngleAndSurvey(TURRET_ANGLE_TO_SHOOT);
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new ShootOperation(5000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(1000, 1000, 0, UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER));
		changePoints.add(new VelocityChangePoint(500, 500, 10000, UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER));
		this.driveEncoder(15000, 4, changePoints, operations);
		shoot();
		this.waitForEndOfAuto();
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 5);
		turnToAngle(UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG + 5, 5);
		KragerTimer.delayMS(500);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 5);
		placeGear();
		driveEncoder(-15000, 5);
		this.turnToAngle(-UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER, 1.5);
		Turret.getInstance().turnToAngleAndSurvey(-TURRET_ANGLE_TO_SHOOT);
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new ShootOperation(5000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(1000, 1000, 0, -UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER));
		changePoints
				.add(new VelocityChangePoint(500, 500, 10000, -UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_HOPPER));
		this.driveEncoder(15000, 4, changePoints, operations);
		shoot();
		this.waitForEndOfAuto();
	}
}