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

	public PIDPositionController turningController = new PIDPositionController(37, .13, 17);
	private KragerTimer doneTimer = new KragerTimer(.5);
	protected double goalAngle = 0;
	private boolean isCompletable = true;

	public DriveAngleVelocityController(double aimAngle) {
		this.setName("DRIVE ANGLE VEL");
		this.setSubsystemID("Drivetrain");
		this.turningController.setSubsystemID("Drivetrain");
		this.turningController.setName("Turning Angle Controller");
		this.turningController.kILimit = 100;
		goalAngle = aimAngle;
		this.setCompletable(true);
		Drive.getInstance().changeToVelocityMode();
	}

	@Override
	public void reset() {
		this.turningController.reset();
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double error = goalAngle - inputState.getAngularPos();
		InputState state = new InputState();
		state.setError(error); // sets angle error to be sent in turning PID
		OutputSignal outputOfTurningController = turningController.getOutputSignal(state); // outputs
		OutputSignal x = new OutputSignal();
		x.setLeftMotor(outputOfTurningController.getMotor());
		x.setRightMotor(-outputOfTurningController.getMotor());
		return x;
	}

	@Override
	public boolean isCompleted() {

		boolean isDone = doneTimer.isConditionMaintained(Drive.getInstance().isAngleCloseTo(goalAngle));
		System.out.println("is done " + isDone);
		System.out.println("cur Angle " + Drive.getInstance().getAngle() + " goalAngle " + goalAngle);
		if (isDone)
			Drive.getInstance().stopDrive();
		return isDone;
	}

	public void sendToSmartDash() {
		System.out.println("ERROR " + turningController.kP);
		turningController.sendToSmartDash();
	}

	public boolean isCompletable() {
		return isCompletable;
	}

	public void setCompletable(boolean isCompletable) {
		this.isCompletable = isCompletable;
	}
}
