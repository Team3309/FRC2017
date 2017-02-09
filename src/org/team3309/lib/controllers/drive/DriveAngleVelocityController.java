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

	public PIDPositionController turningController = new PIDPositionController(3, 0.5, 16.015);
	private KragerTimer doneTimer = new KragerTimer(.5);
	protected double goalAngle = 0;
	private boolean isCompletable = true;

	public DriveAngleVelocityController(double aimAngle) {
		this.setName("DRIVE ANGLE VEL");
		turningController.setConstants(6, 0, 13.015);

		this.turningController.setName("Turning Angle Controller");
		this.turningController.kILimit = 100;
		goalAngle = aimAngle;

		SmartDashboard.putNumber(this.getName() + " Vel to Turn At", 0);

		Drive.getInstance().changeToVelocityMode();
		// SmartDashboard.putNumber("" , value);
	}

	@Override
	public void reset() {
		this.turningController.reset();

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double error = goalAngle - inputState.getAngularPos();
		if (Math.abs(error) > 180) {
			error = -KragerMath.sign(error) * (360 - Math.abs(error));
			System.out.println("New Error: " + error);
		}
		SmartDashboard.putNumber("VISION ErRROR", error);
		SmartDashboard.putNumber("Goal Angle", goalAngle);
		InputState state = new InputState();
		state.setError(error); // sets angle error to be sent in turning PID
		OutputSignal outputOfTurningController = turningController.getOutputSignal(state); // outputs
		SmartDashboard.putNumber("DRIVE ANGLE VEL Output", outputOfTurningController.getMotor());
		return outputOfTurningController;
	}

	@Override
	public boolean isCompleted() {
		if (isCompletable)
			return doneTimer.isConditionMaintained(Drive.getInstance().isAngleCloseTo(goalAngle));
		return false;
	}

	public void sendToSmartDash() {
		turningController.sendToSmartDash();
	}

	public boolean isCompletable() {
		return isCompletable;
	}

	public void setCompletable(boolean isCompletable) {
		this.isCompletable = isCompletable;
	}
}
