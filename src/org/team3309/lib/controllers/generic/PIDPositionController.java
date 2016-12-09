package org.team3309.lib.controllers.generic;

/**
 * Used to maintain a position of an error. Returns just the direct PID Value.
 * 
 * @author TheMkrage
 *
 */
public class PIDPositionController extends PIDController {
	public PIDPositionController(double kP, double kI, double kD) {
		super(kP, kI, kD);
	}

	public PIDPositionController(double kP, double kI, double kD, double kILimit) {
		super(kP, kI, kD, kILimit);
	}
}
