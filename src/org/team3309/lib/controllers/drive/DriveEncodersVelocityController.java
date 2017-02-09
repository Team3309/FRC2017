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

public class DriveEncodersVelocityController extends Controller {

	/**
	 * Highest Level Controller, sends aimVel into leftSide and rightSide
	 * Controllers
	 */
	private PIDPositionController encodersController = new PIDPositionController(1, 0, 0);
	/**
	 * Helps turn when both sides have the same aim velocity
	 */
	private PIDPositionController turningController = new PIDPositionController(.06, 0, 0);
	// Controller for each side
	private FeedForwardWithPIDController leftSideController = new FeedForwardWithPIDController(.006, 0, .003, .001, 0);
	private FeedForwardWithPIDController rightSideController = new FeedForwardWithPIDController(.006, 0, .004, .001, 0);
	private KragerTimer doneTimer = new KragerTimer(.5);
	private LinkedList<Operation> operations = new LinkedList<Operation>();
	private double goalAngle = 0;
	private double goalEncoder = 0;
	private double pastAim = 0;
	private final double MAX_ACC = 5;
	private double MAX_ENCODER_VEL = 100.3309;

	private boolean isRampUp = false;

	public DriveEncodersVelocityController(double encoderGoal) {
		Drive.getInstance().changeToVelocityMode();
		encodersController.setConstants(2, 0, 1.015);
		turningController.setConstants(.06, 0, 0);
		leftSideController.setConstants(.006, 0, .003, .001, 0);
		rightSideController.setConstants(.006, 0, .004, .001, 0);

		goalAngle = Sensors.getAngle();
		this.goalEncoder = encoderGoal;
		this.setName("DRIVE ENCODER VEL");
		this.leftSideController.setName("LEFT IDE VEL CONTROLER");
		this.rightSideController.setName("RIGHT IDE VEL CONTROLER");
		this.encodersController.setName("ENCODER Controller");
		this.turningController.setName("Turning Controller");
		SmartDashboard.putNumber(this.getName() + " Vel to Go At", 0);
	}

	@Override
	public void reset() {
		goalAngle = Sensors.getAngle();
		encodersController.reset();
		rightSideController.reset();
		leftSideController.reset();
		turningController.reset();
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double currentEncoder = Drive.getInstance().getDistanceTraveled();
		// Find an operation that should be done
		double closestPoint = Integer.MAX_VALUE;
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
		double error = goalEncoder - currentEncoder;
		SmartDashboard.putNumber("Goal Angle", goalAngle);
		InputState state = new InputState();
		state.setError(error); // sets angle error to be sent in turning PID
		OutputSignal outputOfTurningController = encodersController.getOutputSignal(state); // outputs
		OutputSignal toBeReturnedSignal = new OutputSignal();
		InputState leftState = new InputState();
		InputState rightState = new InputState();
		if (Math.abs(outputOfTurningController.getMotor()) > MAX_ENCODER_VEL) {
			if (outputOfTurningController.getMotor() > 0) {
				outputOfTurningController.setMotor(MAX_ENCODER_VEL);
			} else {
				outputOfTurningController.setMotor(-MAX_ENCODER_VEL);
			}
		}
		double rightAimVel = outputOfTurningController.getMotor();
		double leftAimVel = outputOfTurningController.getMotor();
		System.out.println("CODE SAYS RIGHT AIM " + rightAimVel + " LEFT AIM: " + leftAimVel);
		if (Math.abs(rightAimVel) > MAX_ENCODER_VEL) {
			if (rightAimVel > 0) {
				rightAimVel = MAX_ENCODER_VEL;
			} else {
				rightAimVel = -MAX_ENCODER_VEL;
			}
		}
		if (Math.abs(leftAimVel) > MAX_ENCODER_VEL) {
			if (leftAimVel > 0) {
				leftAimVel = MAX_ENCODER_VEL;
			} else {
				leftAimVel = -MAX_ENCODER_VEL;
			}
		}
		if (isRampUp) {
			if (rightAimVel < 0)
				rightAimVel = pastAim - MAX_ACC;
			else
				rightAimVel = pastAim + MAX_ACC;
			if (Math.abs(rightAimVel) > Math.abs(this.MAX_ENCODER_VEL)) {
				if (rightAimVel > 0) {
					rightAimVel = MAX_ENCODER_VEL;
				} else {
					rightAimVel = -this.MAX_ENCODER_VEL;
				}
				isRampUp = false;
			}
			leftAimVel = rightAimVel;
		}
		leftState.setError(leftAimVel - inputState.getLeftVel());
		rightState.setError(rightAimVel - inputState.getRightVel());
		InputState turningState = new InputState();
		turningState.setError(goalAngle - inputState.getAngularPos());
		leftSideController.setAimVel(leftAimVel);
		rightSideController.setAimVel(rightAimVel);
		double rightSideOutput = rightSideController.getOutputSignal(rightState).getMotor();
		double leftSideOutput = leftSideController.getOutputSignal(leftState).getMotor();
		// if going straight, control angle too
		if (this.MAX_ENCODER_VEL == this.MAX_ENCODER_VEL) {
			if (Math.abs(inputState.getAngularPos() - goalAngle) > 30) {
				goalAngle = inputState.getAngularPos();
			}
			double turn = turningController.getOutputSignal(turningState).getMotor();
			System.out.println("Setting " + (leftSideOutput + turn) + " right " + (rightSideOutput - turn));
			toBeReturnedSignal.setLeftRightMotor(leftSideOutput + turn, rightSideOutput - turn);
		} else {
			System.out.println("Setting " + -leftSideOutput + " right " + rightSideOutput);
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

	public void sendToSmartDash() {
		leftSideController.sendToSmartDash();
		rightSideController.sendToSmartDash();
		encodersController.sendToSmartDash();
		turningController.sendToSmartDash();
	}

	public double getMAX_ENCODER_VEL() {
		return MAX_ENCODER_VEL;
	}

	public void setMAX_ENCODER_VEL(double mAX_ENCODER_VEL) {
		MAX_ENCODER_VEL = mAX_ENCODER_VEL;
	}

}
