package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.GearIntakeRollerSetOperation;
import org.usfirst.frc.team3309.auto.operations.PivotUpOperation;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class GearMiddleStraight extends SteamworksAutoRoutine {
	private final double TURRET_ANGLE_TO_SHOOT = 90;
	private final double FORWARD_DISTANCE = 24500;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		this.driveEncoder(21000, 2);    // 20500
		placeGear();
		// this.spinUp();
		KragerTimer.delayMS(250);
		// this.pivotUpGearIntake();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(-3000, 0));
		changePoints.add(new VelocityChangePoint(-3500, -600, 2000));
		changePoints.add(new VelocityChangePoint(-1000, -1000, 15000, 90));
		this.driveEncoder(15001, 9, changePoints);

		// this.setFuelIntake(1);

		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new TurretTurnAndSurveyOperation(10000, 0)); // 15000

		LinkedList<VelocityChangePoint> changePoints1 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints1.add(new VelocityChangePoint(3000, 0)); 
		this.driveEncoder(17000, 9, changePoints1, operations); // 25000
		this.shoot();
		Flywheel.getInstance().resetVisionVals();
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		this.driveEncoder(21000, 2);
		placeGear();
		// this.spinUp();
		KragerTimer.delayMS(250);
		// this.pivotUpGearIntake();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(-3000, 0));
		changePoints.add(new VelocityChangePoint(-600, -3500, 2000));
		changePoints.add(new VelocityChangePoint(-1000, -1000, 15000, -90));
		this.driveEncoder(15001, 9, changePoints);

		// this.setFuelIntake(1);

		LinkedList<Operation> operations = new LinkedList<Operation>();
		operations.add(new TurretTurnAndSurveyOperation(10000, 0)); // 15000

		LinkedList<VelocityChangePoint> changePoints1 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints1.add(new VelocityChangePoint(3000, 0));
		this.driveEncoder(17000, 9, changePoints1, operations);  // 25000
		this.shoot();
		Flywheel.getInstance().resetVisionVals();
	}
}
