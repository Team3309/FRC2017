package org.usfirst.frc.team3309.auto;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.drive.DriveAngleVelocityController;
import org.team3309.lib.controllers.drive.DriveEncoderVelocityWithSetPointsController;
import org.team3309.lib.controllers.drive.DriveEncodersVelocityController;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.shooter.FeedyWheel;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.vision.Shot;
import org.usfirst.frc.team3309.vision.Vision;

import edu.wpi.first.wpilibj.DriverStation;
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
			if (waitTimer.get() > timeout) {
				throw new TimedOutException();
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

	public void driveEncoder(double goal, double timeout, LinkedList<VelocityChangePoint> arrayOfVel) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		x.setEncoderChanges(arrayOfVel);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void driveEncoder(double goal, double maxEnc, double timeout) {
		Sensors.resetDrive();
		DriveEncodersVelocityController x = new DriveEncodersVelocityController(goal);
		x.setMAX_ENCODER_VEL(maxEnc);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void driveEncoder(double goal, double maxEnc, double timeout, boolean rampUp) {
		Sensors.resetDrive();
		DriveEncodersVelocityController x = new DriveEncodersVelocityController(goal);
		x.setMAX_ENCODER_VEL(maxEnc);
		x.setRampUp(rampUp);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	protected void driveEncoder(double goal, double maxEnc, double timeout, LinkedList<VelocityChangePoint> w,
			LinkedList<Operation> operations, boolean rampUp) {
		Sensors.resetDrive();
		DriveEncodersVelocityController x = new DriveEncodersVelocityController(goal);
		x.setMAX_ENCODER_VEL(maxEnc);
		x.setRampUp(rampUp);
		x.setOperations(operations);
		x.setEncoderChanges(w);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();

	}

	public void driveEncoder(double goal, double maxEnc, double timeout, LinkedList<VelocityChangePoint> arrayOfVel,
			LinkedList<Operation> operations) {
		Sensors.resetDrive();
		DriveEncoderVelocityWithSetPointsController x = new DriveEncoderVelocityWithSetPointsController(goal);
		x.setMAX_ENCODER_VEL(maxEnc);
		x.setEncoderChanges(arrayOfVel);
		x.setOperations(operations);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {
		}
		mDrive.stopDrive();
	}

	public void turnToAngle(double goal, double timeout) {
		DriveAngleVelocityController x = new DriveAngleVelocityController(goal);
		Drive.getInstance().setAutoController(x);
		try {
			this.waitForController(x, timeout);
		} catch (Exception e) {

		}fdas
		mDrive.stopDrive();
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
