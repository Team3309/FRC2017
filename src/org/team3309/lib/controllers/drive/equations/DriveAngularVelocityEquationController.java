package org.team3309.lib.controllers.drive.equations;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDVelocityController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;

public class DriveAngularVelocityEquationController extends Controller {
	private double aimAngularVelocity = 0.0;
	private final double MAX_ANGULAR_VELOCITY = 720;
	private PIDVelocityController angController = new PIDVelocityController(.4, 0, .01);

	@Override
	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		aimAngularVelocity = MAX_ANGULAR_VELOCITY * Controls.driverController.getLeftX();
		inputState.setError(aimAngularVelocity - inputState.getAngularVel());
		OutputSignal angularOutput = angController.getOutputSignal(inputState);
		OutputSignal signal = new OutputSignal();
		signal.setLeftMotor(Controls.driverController.getLeftY() - angularOutput.getMotor());
		signal.setRightMotor(Controls.driverController.getLeftY() + angularOutput.getMotor());
		return signal;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
