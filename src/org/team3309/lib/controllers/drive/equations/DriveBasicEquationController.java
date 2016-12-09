package org.team3309.lib.controllers.drive.equations;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;

/**
 * Meant to be the "Oh Snap! The encoders and gyro don't work!" drive equation. This
 * drive equation simply directly sets the motor values to the throttle and
 * turn of the two joysticks. Arcade Drive.
 * 
 * @author TheMkrage
 *
 */
public class DriveBasicEquationController extends Controller {

	@Override
	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		OutputSignal signal = new OutputSignal();
		double throttle = Controls.driverController.getLeftY(), turn = -Controls.driverController
				.getRightX();
		double rightPower = throttle + turn;
		double leftPower = throttle - turn;
		signal.setLeftRightMotor(leftPower, rightPower);
		return signal;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
