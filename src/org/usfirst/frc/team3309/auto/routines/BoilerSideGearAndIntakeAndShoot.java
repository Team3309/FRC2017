package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;

public class BoilerSideGearAndIntakeAndShoot extends SteamworksAutoRoutine {
	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 5);
		KragerTimer.delayMS(500);
		turnToAngle(-UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG, 5);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 5);
		placeGear();
		pivotUpGearIntake();
		driveEncoder(-5000, 1);
		this.turnToAngle(0, 1);

		this.setFuelIntake(.5);
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new TurretTurnAndSurveyOperation(32001, 0));
		operations.add(new ShootOperation(60000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 14000));
		changePoints.add(new VelocityChangePoint(2000, 2000, 31000));
		changePoints.add(new VelocityChangePoint(3500, 600, 38000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 55000));
		this.driveEncoder(64000, 9, changePoints, operations);
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 5);
		KragerTimer.delayMS(500);
		turnToAngle(UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG + 5, 5);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 5);
		placeGear();
		this.pivotUpGearIntake();
		driveEncoder(-5000, 5);
		this.turnToAngle(0, 1);

		this.setFuelIntake(.5);
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new TurretTurnAndSurveyOperation(32001, 0));
		operations.add(new ShootOperation(60000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 14000));
		changePoints.add(new VelocityChangePoint(2000, 2000, 31000));
		changePoints.add(new VelocityChangePoint(3500, 600, 38000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 55000));
		this.driveEncoder(64000, 9, changePoints, operations);
	}
}
