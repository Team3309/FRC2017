package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;
import org.usfirst.frc.team3309.subsystems.Turbine;

public class CloseHopperAndShootCurvy extends SteamworksAutoRoutine {

	private final double ANGLE_COMING_OUT_OF_TURN = 90;
	private final double ANGLE_FOR_TURNING = 90;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		// Turret.getInstance().turnToAngleAndSurvey(-270);
		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(32000));
		operations.add(new TurretTurnAndSurveyOperation(32001, ANGLE_FOR_TURNING));
		operations.add(new ShootOperation(38000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 14000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 30000, ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(50, 50, 42000, ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(44000, 6, changePoints, operations);

		// Turbine.getInstance().isReverse = true;
		// KragerTimer.delayMS(1000);
		// Turbine.getInstance().isReverse = false;
		// this.shoot();
		this.waitForEndOfAuto();
	}

	@Override

	public void blueRoutine() throws TimedOutException, InterruptedException {

		this.spinUp();
		// this.openGearIntake();
		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new SpinUpOperation(32000));
		operations.add(new TurretTurnAndSurveyOperation(32001, -ANGLE_FOR_TURNING));
		operations.add(new ShootOperation(35000));

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(3500, 600, 14000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, -ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(300, 300, 44000, -ANGLE_COMING_OUT_OF_TURN));

		this.driveEncoder(47000, 6, changePoints, operations);
		// Turbine.getInstance().isReverse = true;
		/// KragerTimer.delayMS(1000);
		// Turbine.getInstance().isReverse = false;
		// this.shoot();
		this.waitForEndOfAuto();
	}

}
