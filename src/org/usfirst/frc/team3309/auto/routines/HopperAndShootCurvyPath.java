package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;

public class HopperAndShootCurvyPath extends SteamworksAutoRoutine {

	private final double ANGLE_COMING_OUT_OF_TURN = 90;
	private final double ANGLE_FOR_TURNING = 90;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		// Turret.getInstance().turnToAngleAndSurvey(-270);
		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(32000));
		operations.add(new TurretTurnAndSurveyOperation(32001, -ANGLE_FOR_TURNING));
		operations.add(new ShootOperation(40000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 14000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(500, 500, 42000, ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(45000, 9, changePoints, operations);
		this.shoot();
		this.waitForEndOfAuto();
	}

	@Override

	public void blueRoutine() throws TimedOutException, InterruptedException {

		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(32000));
		operations.add(new TurretTurnAndSurveyOperation(32001, -ANGLE_FOR_TURNING));
		operations.add(new ShootOperation(40000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(3500, 600, 14000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, -ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(500, 500, 42000, -ANGLE_COMING_OUT_OF_TURN));

		this.driveEncoder(45000, 9, changePoints, operations);
		this.shoot();
		this.waitForEndOfAuto();
	}

}
