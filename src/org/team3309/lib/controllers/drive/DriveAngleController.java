package org.team3309.lib.controllers.drive;

import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Drive;

public class DriveAngleController extends PIDPositionController {
	double startingAngle = 0;
	double goalAngle = 0;

	public DriveAngleController(double goal) {
		super(4.6, 0.2, 3.502);
		Drive.getInstance().changeToPercentMode();
		this.setName("Angle");
		// SmartDashboard.putNumber(this.getName() + " goal(set me)", goal);
		this.setTHRESHOLD(.5);
		this.kILimit = .55;

		startingAngle = Sensors.getAngle();
		goalAngle = goal;
	}

	public OutputSignal getOutputSignal(InputState inputState) {
		double error = (goalAngle - inputState.getAngularPos());
		if (Math.abs(error) > 180) {
			error = -KragerMath.sign(error) * (360 - Math.abs(error));
			System.out.println("New Error: " + error);
		}
		double left = 0;
		InputState state = new InputState();
		state.setError(error);
		left = super.getOutputSignal(state).getMotor();
		OutputSignal signal = new OutputSignal();
		signal.setLeftRightMotor(left, -left);

		return signal;
	}

	public void sendToSmartDash() {
		super.sendToSmartDash();
	}

	public void setGoalAngle(double angle) {
		this.goalAngle = angle;
	}

	public double getGoalAngle() {
		return this.goalAngle;
	}
}