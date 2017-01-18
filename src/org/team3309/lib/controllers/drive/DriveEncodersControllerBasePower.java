package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.subsystems.Drive;

/**
 * Use this class to drive the robot an exact amount of encoders Uses three PID
 * controllers, one for angle, one for left, one for right.
 * 
 * @author Krager
 *
 */
public class DriveEncodersControllerBasePower extends DriveEncodersController {

	private double basePower = 0.0;
	private boolean isOver = false;

	public DriveEncodersControllerBasePower(double goal, double basePower) {
		super(goal);
		this.basePower = basePower;
	}

	@Override
	public void reset() {
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		// Input States for the controllers
		InputState inputForAng = inputState;
		inputForAng.setError(goalAngle - inputState.getAngularPos());
		OutputSignal angularOutput = angController.getOutputSignal(inputForAng);
		double angPower = angularOutput.getMotor();
		// Prepare the output
		OutputSignal signal = new OutputSignal();
		signal.setLeftMotor(basePower + angPower);
		signal.setRightMotor(basePower - angPower);
		return signal;
	}

	private boolean isEncoderClose() {
		return Drive.getInstance().isEncoderCloseTo(goalEncoder);
	}

	@Override
	public boolean isCompleted() {
		return isEncoderClose() || isOver;
	}

	@Override
	public void sendToSmartDash() {
		angController.sendToSmartDash();
	}
}
