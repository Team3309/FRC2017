package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;

public class FarHopperAndShootCurvyPath extends SteamworksAutoRoutine {
	private final double ANGLE_COMING_OUT_OF_TURN = 90;
	private final double ANGLE_FOR_TURNING = 75;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();

		// Turret.getInstance().turnToAngleAndSurvey(-270);
		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(35000));
		operations.add(new TurretTurnAndSurveyOperation(46900, ANGLE_FOR_TURNING)); // changed
																					// a
																					// negative
	//	operations.add(new DualShooter(47000));
		operations.add(new ShootOperation(47000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3000, 100));
		changePoints.add(new VelocityChangePoint(2000, 3500, 13800));
		changePoints.add(new VelocityChangePoint(3000, 3000, 46800, ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(2000, 2000, 57000, ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(500, 500, 61000, ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(64000, 5, changePoints, operations);
		System.out.println("WAIT COMPLETE ----------------------------- \n");
		// Turbine.getInstance().isReverse = true;
		// KragerTimer.delayMS(1000);
		// Turbine.getInstance().isReverse = false;
		// this.shoot();
		this.waitForEndOfAuto();
	}

	@Override

	public void blueRoutine() throws TimedOutException, InterruptedException {

		this.spinUp();
		// Turret.getInstance().turnToAngleAndSurvey(-270);
		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(35000));
		operations.add(new TurretTurnAndSurveyOperation(46900, -ANGLE_FOR_TURNING)); // changed
																						// a
																						// negative
		operations.add(new ShootOperation(47000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3000, 100));
		changePoints.add(new VelocityChangePoint(3500, 2000, 13800));
		changePoints.add(new VelocityChangePoint(3000, 3000, 46800, -ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(2000, 2000, 57000, -ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(500, 500, 61000, -ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(64000, 5, changePoints, operations);

		// Turbine.getInstance().isReverse = true;
		// KragerTimer.delayMS(1000);
		// Turbine.getInstance().isReverse = false;
		// this.shoot();
		this.waitForEndOfAuto();
	}
}
