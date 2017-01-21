package org.team3309.lib.controllers.turret;

import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class TurretLeftRightVelocityCompensatedController extends FeedForwardWithPIDController {

	private boolean isGoingLeft = false;
	private final double DEGREES_TO_SLOW_DOWN = 25;
	private static final double TURRET_MAX_DEGREES = Turret.getInstance().getMaxDegrees();
	private static final double SLOW_VEL = .2;
	private static final double FAST_VEL = .35;

	public TurretLeftRightVelocityCompensatedController(double kV, double kA, double kP, double kI, double kD) {
		super(kV, kA, kP, kI, kD);
	}

	@Override
	public void reset() {

	}

	private void checkForHittingSides() {
		if (Sensors.isTurretRightLimitHit()) {
			isGoingLeft = true;
		} else if (Sensors.isTurretLeftLimitHit()) {
			isGoingLeft = false;
		}
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		OutputSignal toBeReturned = new OutputSignal();
		double factor = 1;
		checkForHittingSides();
		if (isGoingLeft)
			factor = -1;
		double aimVel = 0;
		// Decide which speed to use (slower when close to corners)
		if (Sensors.getTurretAngle() > TURRET_MAX_DEGREES - DEGREES_TO_SLOW_DOWN
				|| Sensors.getTurretAngle() < DEGREES_TO_SLOW_DOWN) {
			aimVel = SLOW_VEL * factor;
		} else {
			aimVel = FAST_VEL * factor;
		}
		InputState stateForVelController = new InputState();
		stateForVelController.setError(aimVel - Sensors.getTurretAngleVelocity());
		this.setAimVel(aimVel);
		return super.getOutputSignal(stateForVelController);
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
