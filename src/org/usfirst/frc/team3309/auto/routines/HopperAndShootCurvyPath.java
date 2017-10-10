package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

public class HopperAndShootCurvyPath extends SteamworksAutoRoutine {

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
		operations.add(new ShootOperation(44000)); // 38000

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3700, 0)); // 3700
		changePoints.add(new VelocityChangePoint(600, 3500, 14500));
		changePoints.add(new VelocityChangePoint(2000, 2000, 30200, ANGLE_COMING_OUT_OF_TURN));
		// changePoints.add(new VelocityChangePoint(50, 50, 25000,
		// ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(150, 150, 44000, ANGLE_COMING_OUT_OF_TURN)); // 75 75
		this.driveEncoder(49700, 4, changePoints, operations); // 50000 6

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
		operations.add(new SpinUpOperation(32000));
		operations.add(new TurretTurnAndSurveyOperation(32001, -ANGLE_FOR_TURNING));
		operations.add(new ShootOperation(44000)); // 38000

		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(3700, 0)); // 3700
		changePoints.add(new VelocityChangePoint(3500, 600, 14500));
		changePoints.add(new VelocityChangePoint(2000, 2000, 30200, -ANGLE_COMING_OUT_OF_TURN));
		changePoints.add(new VelocityChangePoint(150, 150, 44000, -ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(50000, 4, changePoints, operations); // 6

		// Turbine.getInstance().isReverse = true;
		// KragerTimer.delayMS(1000);
		// Turbine.getInstance().isReverse = false;

		// this.shoot();
		this.waitForEndOfAuto();
	}

}
