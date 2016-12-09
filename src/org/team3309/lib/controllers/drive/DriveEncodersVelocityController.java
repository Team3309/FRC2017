package org.team3309.lib.controllers.drive;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.sensors.Sensors;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.defenses.Operation;
import org.usfirst.frc.team3309.robot.SensorDoesNotReturnException;
import org.usfirst.frc.team3309.subsystems.Drive;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveEncodersVelocityController extends Controller {

	private FeedForwardWithPIDController leftSideController = new FeedForwardWithPIDController(.006, 0, .003, .001, 0);
	private FeedForwardWithPIDController rightSideController = new FeedForwardWithPIDController(.006, 0, .004, .001, 0);
	private PIDPositionController encodersController = new PIDPositionController(1, 0, 0);
	private PIDPositionController turningController = new PIDPositionController(.06, 0, 0);
	private KragerTimer doneTimer = new KragerTimer(.5);
	private LinkedList<VelocityChangePoint> encoderChanges = new LinkedList<VelocityChangePoint>();
	private LinkedList<Operation> operations = new LinkedList<Operation>();
	private double goalAngle = 0;
	private double goalEncoder = 0;
	private double pastAim = 0;
	private final double MAX_ACC = 5;
	private double MAX_ENCODER_VEL_RIGHT = 100;
	private double MAX_ENCODER_VEL_LEFT = 100;
	private boolean isRampUp = false;

	public DriveEncodersVelocityController(double encoderGoal) {
		if (Drive.getInstance().isLowGear()) {
			encodersController.setConstants(2, 0, 1.015);
			turningController.setConstants(.06, 0, 0);
			leftSideController.setConstants(.006, 0, .003, .001, 0);
			rightSideController.setConstants(.006, 0, .004, .001, 0);
		} else {
			encodersController.setConstants(2.9, 0, .215);
			turningController.setConstants(.04, 0, 0);
			leftSideController.setConstants(.006, 0, .009, .001, 0);
			rightSideController.setConstants(.006, 0, .009, .001, 0);
		}
		goalAngle = Sensors.getAngle();
		this.goalEncoder = encoderGoal;
		this.setName("DRIVE ENCODER VEL");
		this.leftSideController.setName("LEFT IDE VEL CONTROLER");
		this.rightSideController.setName("RIGHT IDE VEL CONTROLER");
		this.encodersController.setName("ENCODER Controller");
		this.turningController.setName("Turning Controller");
		SmartDashboard.putNumber(this.getName() + " Vel to Go At", 0);
		encoderChanges.add(new VelocityChangePoint(MAX_ENCODER_VEL_RIGHT, 0));
	}

	@Override
	public void reset() {
		goalAngle = Sensors.getAngle();
		encodersController.reset();
		rightSideController.reset();
		leftSideController.reset();
		turningController.reset();
	}

	Timer time = new Timer();
	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		time.reset();
		time.start();
		double currentEncoder = 0;
		try {
			currentEncoder = (Math.abs(inputState.getRightPos()) + Math.abs(inputState.getLeftPos())) / 2;
		} catch (Exception e) {

		}
		if (goalEncoder < 0) {
			currentEncoder = -currentEncoder;
		}
		double closestPoint = Integer.MAX_VALUE;
		VelocityChangePoint currentVelocityPoint = new VelocityChangePoint(MAX_ENCODER_VEL_RIGHT, 0);
		for (VelocityChangePoint curPoint : encoderChanges) {
			if (Math.abs(currentEncoder) > Math.abs(curPoint.encoder)) {
				if (Math.abs(Math.abs(currentEncoder) - Math.abs(curPoint.encoder)) < closestPoint) {
					currentVelocityPoint = curPoint;
					closestPoint = Math.abs(Math.abs(currentEncoder) - Math.abs(curPoint.encoder));
				}
			}
		}
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
		System.out.println(" 1 : " + time.get());
		if (currentOperation != null) {
			/*try {
				currentOperation.perform();
			} catch (InterruptedException | TimedOutException e) {
				e.printStackTrace();
			}*/
		}
		this.setMAX_ENCODER_VEL(currentVelocityPoint.rightVelocity, currentVelocityPoint.leftVelocity);
		double error = goalEncoder - currentEncoder;
		// double dashAimTurnVel = SmartDashboard.getNumber(this.getName() + "
		// Vel to Go At");
		SmartDashboard.putNumber("Goal Angle", goalAngle);
		InputState state = new InputState();
		state.setError(error); // sets angle error to be sent in turning PID
		OutputSignal outputOfTurningController = encodersController.getOutputSignal(state); // outputs
		OutputSignal toBeReturnedSignal = new OutputSignal();
		InputState leftState = new InputState();
		InputState rightState = new InputState();
		if (Math.abs(outputOfTurningController.getMotor()) > MAX_ENCODER_VEL_LEFT) {
			if (outputOfTurningController.getMotor() > 0) {
				outputOfTurningController.setMotor(MAX_ENCODER_VEL_LEFT);
			} else {
				outputOfTurningController.setMotor(-MAX_ENCODER_VEL_LEFT);
			}
		}
		System.out.println(" 2 : " + time.get());
		double rightAimVel = outputOfTurningController.getMotor();
		double leftAimVel = outputOfTurningController.getMotor();
		System.out.println("CODE SAYS RIGHT AIM " + rightAimVel + " LEFT AIM: " + leftAimVel);
		if (Math.abs(rightAimVel) > MAX_ENCODER_VEL_RIGHT) {
			if (rightAimVel > 0) {
				rightAimVel = MAX_ENCODER_VEL_RIGHT;
			} else {
				rightAimVel = -MAX_ENCODER_VEL_RIGHT;
			}
		}
		if (Math.abs(leftAimVel) > MAX_ENCODER_VEL_LEFT) {
			if (leftAimVel > 0) {
				leftAimVel = MAX_ENCODER_VEL_LEFT;
			} else {
				leftAimVel = -MAX_ENCODER_VEL_LEFT;
			}
		}
		System.out.println(" 3 : " + time.get());
		if (isRampUp) {
			if (rightAimVel < 0)
				rightAimVel = pastAim - MAX_ACC;
			else
				rightAimVel = pastAim + MAX_ACC;
			if (Math.abs(rightAimVel) > Math.abs(this.MAX_ENCODER_VEL_RIGHT)) {
				if (rightAimVel > 0) {
					rightAimVel = MAX_ENCODER_VEL_RIGHT;
				} else {
					rightAimVel = -this.MAX_ENCODER_VEL_RIGHT;
				}
				isRampUp = false;
			}
		}
		leftAimVel = -leftAimVel;
		System.out.println("AFTER, CODE SAYS RIGHT AIM " + rightAimVel + " LEFT AIM: " + leftAimVel);
		SmartDashboard.putNumber("DRIVE Encoder VEL Output", outputOfTurningController.getMotor());
		SmartDashboard.putNumber("DRIVE ENCODER RIGHT", rightAimVel);
		SmartDashboard.putNumber("DRIVE ENCODER LEFT", leftAimVel);
		try {
			System.out.println("LEFT VEL: " + inputState.getLeftVel() + " right vel: " + inputState.getRightVel());
			System.out.println("Left Error: " + (leftAimVel - inputState.getLeftVel()) + " Right Error: "
					+ (rightAimVel - inputState.getRightVel()));
		} catch (SensorDoesNotReturnException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(" 4 : " + time.get());
		try { // if encoders are broken
			leftState.setError(leftAimVel - inputState.getLeftVel());
			rightState.setError(rightAimVel - inputState.getRightVel());
		} catch (Exception e) {
			return new OutputSignal();
		}
		// leftSideController.setAimVel(dashAimTurnVel);
		// rightSideController.setAimVel(-dashAimTurnVel);
		// leftState.setError(dashAimTurnVel - inputState.getLeftVel());
		// rightState.setError(-dashAimTurnVel - inputState.getRightVel());
		InputState turningState = new InputState();
		turningState.setError(goalAngle - inputState.getAngularPos());

		double rightSideOutput = rightSideController.getOutputSignal(rightState).getMotor();
		double leftSideOutput = leftSideController.getOutputSignal(leftState).getMotor();
		// System.out.println("AIM VELs " + leftAimVel + " right Aim Vel " +
		// -rightAimVel);
		// SmartDashboard.putString("HardCore Power", "RIGHT: " +
		// rightSideOutput + " LEFT " +leftSideOutput);
		System.out.println(" 5 : " + time.get());
		if (this.MAX_ENCODER_VEL_LEFT == this.MAX_ENCODER_VEL_RIGHT) {
			if (Math.abs(inputState.getAngularPos() - goalAngle) > 30) {
				goalAngle = inputState.getAngularPos();
			}
			double turn = turningController.getOutputSignal(turningState).getMotor();
			System.out.println("Setting " + (-leftSideOutput + turn) + " right " + (rightSideOutput - turn));
			toBeReturnedSignal.setLeftRightMotor(-leftSideOutput + turn, -leftSideOutput - turn);// -
																									// +
		} else {
			System.out.println("Setting " + -leftSideOutput + " right " + rightSideOutput);
			toBeReturnedSignal.setLeftRightMotor(-leftSideOutput, rightSideOutput);
		}

		pastAim = rightAimVel;
		System.out.println(" 6 : " + time.get());
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

	public double getMAX_ENCODER_VEL_RIGHT() {
		return MAX_ENCODER_VEL_RIGHT;
	}

	public double getMAX_ENCODER_VEL_LEFT() {
		return MAX_ENCODER_VEL_LEFT;
	}

	public void setMAX_ENCODER_VEL(double mAX_ENCODER_VEL) {
		MAX_ENCODER_VEL_RIGHT = mAX_ENCODER_VEL;
		MAX_ENCODER_VEL_LEFT = mAX_ENCODER_VEL;
		/*
		 * if (encoderChanges.size() == 1) { encoderChanges = new
		 * LinkedList<VelocityChangePoint>(); encoderChanges.add(new
		 * VelocityChangePoint(MAX_ENCODER_VEL_RIGHT, 0)); }
		 */
	}

	public void setMAX_ENCODER_VEL(double mAX_ENCODER_VEL_RIGHT, double mAX_ENCODER_VEL_LEFT) {
		MAX_ENCODER_VEL_RIGHT = mAX_ENCODER_VEL_RIGHT;
		MAX_ENCODER_VEL_LEFT = mAX_ENCODER_VEL_LEFT;
		/*
		 * if (encoderChanges.size() == 1) { encoderChanges = new
		 * LinkedList<VelocityChangePoint>(); encoderChanges.add(new
		 * VelocityChangePoint(MAX_ENCODER_VEL_RIGHT, 0)); }
		 */
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
		encoderChanges.add(new VelocityChangePoint(MAX_ENCODER_VEL_RIGHT, MAX_ENCODER_VEL_LEFT, 0));
		this.encoderChanges = encoderChanges;
	}

	public void sendToSmartDash() {
		leftSideController.sendToSmartDash();
		rightSideController.sendToSmartDash();
		encodersController.sendToSmartDash();
		turningController.sendToSmartDash();
	}

}
