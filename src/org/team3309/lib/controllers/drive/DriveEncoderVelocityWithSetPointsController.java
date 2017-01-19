package org.team3309.lib.controllers.drive;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveEncoderVelocityWithSetPointsController extends Controller {

	/**
	 * Helps turn when both sides have the same aim velocity
	 */
	private PIDPositionController turningController = new PIDPositionController(.06, 0, 0);
	// Controller for each side
	private FeedForwardWithPIDController leftSideController = new FeedForwardWithPIDController(.006, 0, .003, .001, 0);
	private FeedForwardWithPIDController rightSideController = new FeedForwardWithPIDController(.006, 0, .004, .001, 0);
	private KragerTimer doneTimer = new KragerTimer(.5);
	private LinkedList<VelocityChangePoint> encoderChanges = new LinkedList<VelocityChangePoint>();
	private LinkedList<Operation> operations = new LinkedList<Operation>();
	private double goalAngle = 0;
	private double goalEncoder = 0;
	private double pastAim = 0;
	private final double MAX_ACC = 5;
	private final double DEFAULT_STARTING_VEL = 100;
	private boolean isRampUp = false;

	public DriveEncoderVelocityWithSetPointsController(double encoderGoal) {

		turningController.setConstants(.06, 0, 0);
		leftSideController.setConstants(.006, 0, .003, .001, 0);
		rightSideController.setConstants(.006, 0, .004, .001, 0);

		goalAngle = Sensors.getAngle();
		this.goalEncoder = encoderGoal;
		this.setName("DRIVE ENCODER VEL");
		this.leftSideController.setName("LEFT IDE VEL CONTROLER");
		this.rightSideController.setName("RIGHT IDE VEL CONTROLER");
		this.turningController.setName("Turning Controller");
		SmartDashboard.putNumber(this.getName() + " Vel to Go At", 0);
		/*
		 * add a starting point at the main vel (will continue to run at this
		 * vel if no other points are added)
		 */
		encoderChanges.add(new VelocityChangePoint(DEFAULT_STARTING_VEL, 0));
	}

	@Override
	public void reset() {
		goalAngle = Sensors.getAngle();
		rightSideController.reset();
		leftSideController.reset();
		turningController.reset();
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double currentEncoder = Drive.getInstance().getDistanceTraveled();
		VelocityChangePoint currentVelocityPoint = new VelocityChangePoint(DEFAULT_STARTING_VEL, 0);
		// Find closest velocity point to travel at
		double closestPoint = Integer.MAX_VALUE;
		for (VelocityChangePoint curPoint : encoderChanges) {
			if (Math.abs(currentEncoder) > Math.abs(curPoint.encoderValueToChangeAt)) {
				if (Math.abs(Math.abs(currentEncoder) - Math.abs(curPoint.encoderValueToChangeAt)) < closestPoint) {
					currentVelocityPoint = curPoint;
					closestPoint = Math.abs(Math.abs(currentEncoder) - Math.abs(curPoint.encoderValueToChangeAt));
				}
			}
		}
		// Find an operation that should be done
		closestPoint = Integer.MAX_VALUE;
		Operation currentOperation = null;
		for (Operation operation : operations) {
			if (Math.abs(currentEncoder) > Math.abs(operation.encoder)) {
				if (Math.abs(Math.abs(currentEncoder) - Math.abs(operation.encoder)) < closestPoint) {
					currentOperation = operation;
					closestPoint = Math.abs(Math.abs(currentEncoder) - Math.abs(operation.encoder));
				}
			}
		}
		// remove the operation from the list to prevent repeats and execute the
		// operation
		if (currentOperation != null) {
			operations.remove(currentOperation);
			try {
				currentOperation.perform();
			} catch (InterruptedException e) {
			} catch (TimedOutException e) {
			}
		}
		InputState leftState = new InputState();
		InputState rightState = new InputState();
		double rightAimVel = currentVelocityPoint.rightVelocityNew;
		double leftAimVel = currentVelocityPoint.leftVelocityNew;
		// used when going from rest to default speed
		if (isRampUp) {
			if (rightAimVel < 0)
				rightAimVel = pastAim - MAX_ACC;
			else
				rightAimVel = pastAim + MAX_ACC;
			// if robot has reached a good moving speed, stop ramp up sequence
			if (Math.abs(rightAimVel) > Math.abs(DEFAULT_STARTING_VEL)) {
				isRampUp = false;
			}
			leftAimVel = rightAimVel;
		}
		// Use the left and right side PID loops to get the output values
		leftState.setError(leftAimVel - inputState.getLeftVel());
		rightState.setError(rightAimVel - inputState.getRightVel());
		leftSideController.setAimVel(leftAimVel);
		rightSideController.setAimVel(rightAimVel);
		double rightSideOutput = rightSideController.getOutputSignal(rightState).getMotor();
		double leftSideOutput = leftSideController.getOutputSignal(leftState).getMotor();
		OutputSignal toBeReturnedSignal = new OutputSignal();
		// if going straight, control angle too
		if (leftAimVel == rightAimVel) {
			InputState turningState = new InputState();
			turningState.setError(goalAngle - inputState.getAngularPos());
			// if the error is huge (meaning the robot has turned from dif
			// velocities on each side) then set the current angle to the new
			// goal
			if (Math.abs(inputState.getAngularPos() - goalAngle) > 30) {
				goalAngle = inputState.getAngularPos();
			}
			double turn = turningController.getOutputSignal(turningState).getMotor();
			toBeReturnedSignal.setLeftRightMotor(leftSideOutput + turn, rightSideOutput - turn);
		} else {
			toBeReturnedSignal.setLeftRightMotor(leftSideOutput, rightSideOutput);
		}
		pastAim = rightAimVel;
		return toBeReturnedSignal;
	}

	public LinkedList<Operation> getOperations() {
		return operations;
	}

	public void setOperations(LinkedList<Operation> operations) {
		this.operations = operations;
	}

	public double getGoalAngle() {
		return goalAngle;
	}

	public void setGoalAngle(double goalAngle) {
		this.goalAngle = goalAngle;
	}

	public double getGoalEncoder() {
		return goalEncoder;
	}

	public void setGoalEncoder(double goalEncoder) {
		this.goalEncoder = goalEncoder;
	}

	public void setRampUp(boolean bool) {
		this.isRampUp = bool;
	}

	@Override
	public boolean isCompleted() {
		return doneTimer.isConditionMaintained(Drive.getInstance().isEncoderCloseTo(goalEncoder));
	}

	public LinkedList<VelocityChangePoint> getEncoderChanges() {
		return encoderChanges;
	}

	public void setEncoderChanges(LinkedList<VelocityChangePoint> encoderChanges) {
		encoderChanges.add(new VelocityChangePoint(DEFAULT_STARTING_VEL, 0));
		this.encoderChanges = encoderChanges;
	}

	public void sendToSmartDash() {
		leftSideController.sendToSmartDash();
		rightSideController.sendToSmartDash();
		turningController.sendToSmartDash();
	}

}
