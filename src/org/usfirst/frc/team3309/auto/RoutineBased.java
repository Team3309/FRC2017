package org.usfirst.frc.team3309.auto;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.drive.DriveAngleVelocityController;
import org.team3309.lib.controllers.drive.DriveEncoderVelocityWithSetPointsController;
import org.team3309.lib.controllers.drive.DriveEncodersController;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.FuelIntake;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;

public class RoutineBased {

	/**
	 * Tracks how long auto has been running
	 */
	protected Timer autoTimer = new Timer();
	// All the subsystems
	protected Drive mDrive = Drive.getInstance();

	public void waitForController(Controller c, double timeout) throws TimedOutException, InterruptedException {
		Timer waitTimer = new Timer();

		waitTimer.start();
		while (!c.isCompleted()) {
			System.out.println();
			if (waitTimer.get() > timeout) {
				break;
			}
			KragerTimer.delayMS(100);
		}
	}

	/**
	 * Waits for drive train to complete its current controller's task
	 * 
	 * @param timeout
	 *            if it hits this (ms) , then it will timeout
	 * @throws TimedOutException
	 *             if waits for more than specified timeout
	 * @throws InterruptedException
	 */
	public void waitForDrive(double timeout) throws TimedOutException, InterruptedException {
		Timer waitTimer = new Timer();
		waitTimer.start();
		while (!mDrive.isOnTarget()) {
			if (waitTimer.get() > timeout) {
				throw new TimedOutException();
			}
			KragerTimer.delayMS(100);
		}
	}

	/**
	 * Waits for drive train to meet its encoder goal
	 * 
	 * @param timeout
	 *            if it hits this (ms) , then it will timeout
	 * @throws TimedOutException
	 *             if waits for more than specified timeout
	 * @throws InterruptedException
	 */
	public void waitForDriveEncoder(double encoderGoal, double timeout) throws TimedOutException {
		Timer waitTimer = new Timer();
		waitTimer.start();
		while (!mDrive.isEncoderCloseTo(encoderGoal)) {
			if (waitTimer.get() > timeout)
				throw new TimedOutException();
			KragerTimer.delayMS(50);
		}
	}

	/**
	 * Waits for drive train to meet its angle oder goal
	 * 
	 * @param timeout
	 *            if it hits this (ms) , then it will timeout
	 * @throws TimedOutException
	 *             if waits for more than specified timeout
	 * @throws InterruptedException
	 */
	public void waitForDriveAngle(double angleGoal, double timeout) throws TimedOutException {
		Timer waitTimer = new Timer();
		waitTimer.start();
		while (!mDrive.isAngleCloseTo(angleGoal)) {
			if (waitTimer.get() > timeout)
				throw new TimedOutException();
			KragerTimer.delayMS(100);
		}
	}

	public void driveEncoder(double goal, double timeout) {
		Sensors.resetDrive();
		DriveEncodersController x = new DriveEncodersController(goal);
		System.out.println("SETTINGF");
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void driveEncoder(double goal, double timeout, LinkedList<VelocityChangePoint> arrayOfVel) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		x.setEncoderChanges(arrayOfVel);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void driveEncoder(double goal, double maxEnc, double timeout) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		LinkedList<VelocityChangePoint> arr = new LinkedList<VelocityChangePoint>();
		arr.add(new VelocityChangePoint(maxEnc, 0));
		x.setEncoderChanges(arr);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void driveEncoder(double goal, double maxEnc, double timeout, boolean rampUp) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		LinkedList<VelocityChangePoint> arr = new LinkedList<VelocityChangePoint>();
		arr.add(new VelocityChangePoint(maxEnc, 0));
		x.setEncoderChanges(arr);
		x.setRampUp(rampUp);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	protected void driveEncoder(double goal, double timeout, LinkedList<VelocityChangePoint> w,
			LinkedList<Operation> operations, boolean rampUp) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		x.setRampUp(rampUp);
		x.setOperations(operations);
		x.setEncoderChanges(w);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();

	}

	public void driveEncoder(double goal, double timeout, LinkedList<VelocityChangePoint> arrayOfVel,
			LinkedList<Operation> operations) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		x.setEncoderChanges(arrayOfVel);
		x.setOperations(operations);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void turnToAngle(double goal, double timeout) {
		DriveAngleVelocityController x = new DriveAngleVelocityController(goal);
		Drive.getInstance().setController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {

		}
		mDrive.stopDrive();
	}

	public void shoot() {
		System.out.println("SHOOTING NOW");
		Shooter.getInstance().setShouldBeShooting(true);
	}

	public void spinUp() {
		System.out.println("Spinning NOW");
		Shooter.getInstance().setShouldBeSpinningUp(true);
	}

	public void deployGearIntake() {
		GearIntake.getInstance().extendPivot();
		GearIntake.getInstance().extendWrist();
	}

	public void retractGearIntake() {
		GearIntake.getInstance().retractPivot();
		GearIntake.getInstance().retractWrist();
	}

	public void setFuelIntake(double power) {
		FuelIntake.getInstance().setFuelIntake(power);
	}

	public void waitForEndOfAuto() {
		while (autoTimer.get() < 14.900) {
			try {
				KragerTimer.delayMS(100);
			} catch (Exception e) {

			}
		}
	}
}
