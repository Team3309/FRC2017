package org.team3309.lib.controllers.generic;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

/**
 * PID Controller used for velocity. It adds the PID value to the previous one,
 * so it maintains value.
 * 
 * @author TheMkrage
 *
 */
public class PIDVelocityController extends PIDController {
	/**
	 * Variable to store running velocity so the feedback can be added to the
	 * previous one.
	 */
	private double runningVelocity = 0.0;

	public PIDVelocityController(double kP, double kI, double kD) {
		super(kP, kI, kD);
	}

	public PIDVelocityController(double kP, double kI, double kD, double kILimit) {
		super(kP, kI, kD, kILimit);
	}

	// Adds the previous velocity with the new PID feedback
	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		OutputSignal signal = new OutputSignal();
		runningVelocity += super.getOutputSignal(inputState).getMotor();
		signal.setMotor(runningVelocity);
		return signal;
	}
}
