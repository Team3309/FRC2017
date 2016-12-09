package org.team3309.lib.controllers.drive.equations;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDVelocityController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;

public class DriveAngularAndForwardVelocityEquationController extends Controller {
	// Ang Vel Control
	private double aimAngularVelocity = 0.0;
	private final double MAX_ANGULAR_VELOCITY = 720;
	private PIDVelocityController angController = new PIDVelocityController(.4, 0, .01);

	// Left Vel Control
	private double aimLeftVelocity = 0.0;
	private final double MAX_LEFT_VELOCITY = 720;
	private PIDVelocityController leftController = new PIDVelocityController(.4, 0, .01);

	// Right Vel Control
	private double aimRightVelocity = 0.0;
	private final double MAX_RIGHT_VELOCITY = 720;
	private PIDVelocityController rightController = new PIDVelocityController(.4, 0, .01);

	@Override
	public void reset() {
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		aimAngularVelocity = MAX_ANGULAR_VELOCITY * Controls.driverController.getRightX();
		aimRightVelocity = MAX_RIGHT_VELOCITY * Controls.driverController.getLeftY();
		aimLeftVelocity = MAX_LEFT_VELOCITY * Controls.driverController.getLeftY();
		// Input States for the three controllers
		InputState inputForLeftVel = inputState;
		InputState inputForRightVel = inputState;
		InputState inputForAng = inputState;
		// Add the error for each controller from the inputState
		try {
			inputForLeftVel.setError(aimLeftVelocity - inputState.getLeftVel());
			inputForRightVel.setError(aimRightVelocity - inputState.getRightVel());
		} catch (Exception e) {
			return new OutputSignal();
		}
		inputForAng.setError(aimAngularVelocity - inputState.getAngularVel());
		OutputSignal leftOutput = leftController.getOutputSignal(inputForLeftVel);
		OutputSignal rightOutput = rightController.getOutputSignal(inputForRightVel);
		OutputSignal angularOutput = angController.getOutputSignal(inputState);
		// Prepare the output
		OutputSignal signal = new OutputSignal();
		signal.setLeftMotor(leftOutput.getMotor() - angularOutput.getMotor());
		signal.setRightMotor(rightOutput.getMotor() + angularOutput.getMotor());
		return signal;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
