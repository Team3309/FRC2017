package org.team3309.lib.controllers.drive;

import org.team3309.lib.KragerMath;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.subsystems.Drive;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveAngleVelocityController extends Controller {
	private FeedForwardWithPIDController leftSideController = new FeedForwardWithPIDController(.006, 0, .003, .001, 0);
	private FeedForwardWithPIDController rightSideController = new FeedForwardWithPIDController(.006, 0, .003, .001, 0);
	public PIDPositionController turningController = new PIDPositionController(3, 0.5, 16.015);
	private KragerTimer doneTimer = new KragerTimer(.5);
	protected double goalAngle = 0;
	private boolean isCompletable = true;

	public DriveAngleVelocityController(double aimAngle) {
		this.setName("DRIVE ANGLE VEL");
		// System.out.println("LOW GEAR: " + Drive.getInstance().isLowGear());
		if (Drive.getInstance().isLowGear()) {
			leftSideController.setConstants(.006, 0, .003, .0003, 0);
			rightSideController.setConstants(.006, 0, .003, .0003, 0);
			turningController.setConstants(6, 0, 13.015);
			System.out.println("LOW GEAR!");
		} else {
			leftSideController.setConstants(.006, 0, .009, .001, 0);
			rightSideController.setConstants(.006, 0, .009, .001, 0);
			turningController.setConstants(3, 0.5, 16.015);
		}
		this.leftSideController.setName("LEFT IDE VEL CONTROLER");
		this.rightSideController.setName("RIGHT IDE VEL CONTROLER");
		this.turningController.setName("Turning Angle Controller");
		leftSideController.kILimit = .2;
		rightSideController.kILimit = .2;
		this.turningController.kILimit = 100;
		goalAngle = aimAngle;

		SmartDashboard.putNumber(this.getName() + " Vel to Turn At", 0);
		// SmartDashboard.putNumber("" , value);
	}

	@Override
	public void reset() {
		this.leftSideController.reset();
		this.rightSideController.reset();
		this.turningController.reset();

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double error = goalAngle - inputState.getAngularPos();
		// double dashAimTurnVel = SmartDashboard.getNumber(this.getName() + "
		// Vel to Turn At");
		if (Math.abs(error) > 180) {
			error = -KragerMath.sign(error) * (360 - Math.abs(error));
			System.out.println("New Error: " + error);
		}
		SmartDashboard.putNumber("VISION ErRROR", error);
		SmartDashboard.putNumber("Goal Angle", goalAngle);
		InputState state = new InputState();
		state.setError(error); // sets angle error to be sent in turning PID
		OutputSignal outputOfTurningController = turningController.getOutputSignal(state); // outputs
																							// which
																							// "power"/
																							// vel
																							// to
																							// send
		SmartDashboard.putNumber("DRIVE ANGLE VEL Output", outputOfTurningController.getMotor());
		// System.out.println("OUTPUT FOR TURNING: " +
		// outputOfTurningController.getMotor());
		OutputSignal toBeReturnedSignal = new OutputSignal();
		InputState leftState = new InputState();
		InputState rightState = new InputState();
		// System.out.println("ANGLE: ");
		// turningController.printConstants();
		// leftSideController.printConstants();
		// rightSideController.printConstants();
		// System.out.println("HERE IS THE AIM VEL " + dashAimTurnVel);
		// /if (Math.abs(outputOfTurningController.getMotor()) < 4)
		// outputOfTurningController.setMotor(0);
		try {
			leftState.setError(-outputOfTurningController.getMotor() - inputState.getLeftVel());
			rightState.setError(-outputOfTurningController.getMotor() - inputState.getRightVel());
		} catch (Exception e) {
			return new OutputSignal();
		}
		// rightSideController.setAimVel(outputOfTurningController.getMotor());
		// leftSideController.setAimVel(outputOfTurningController.getMotor());
		// leftSideController.setAimVel(dashAimTurnVel);
		// rightSideController.setAimVel(dashAimTurnVel);
		// leftState.setError(dashAimTurnVel - inputState.getLeftVel());
		// rightState.setError(dashAimTurnVel - inputState.getRightVel());

		double leftSide = leftSideController.getOutputSignal(leftState).getMotor();
		double rightSide = rightSideController.getOutputSignal(rightState).getMotor();
		if (Math.abs(leftSide) > .5) {
			if (leftSide < 0) {
				leftSide = -.5;
			} else if (leftSide > 0) {
				leftSide = .5;
			}
		}
		if (Math.abs(rightSide) > .5) {
			if (rightSide < 0) {
				rightSide = -.5;
			} else if (rightSide > 0) {
				rightSide = .5;
			}
		}

		toBeReturnedSignal.setLeftRightMotor(-leftSide, rightSide);
		// System.out.println("left: " +
		// leftSideController.getOutputSignal(leftState).getMotor() + " right: "
		// + rightSideController.getOutputSignal(rightState).getMotor());
		return toBeReturnedSignal;
	}

	@Override
	public boolean isCompleted() {
		if (isCompletable)
			return doneTimer.isConditionMaintained(Drive.getInstance().isAngleCloseTo(goalAngle));
		return false;
	}

	public void sendToSmartDash() {
		leftSideController.sendToSmartDash();
		rightSideController.sendToSmartDash();
		turningController.sendToSmartDash();
	}

	public boolean isCompletable() {
		return isCompletable;
	}

	public void setCompletable(boolean isCompletable) {
		this.isCompletable = isCompletable;
	}
}
