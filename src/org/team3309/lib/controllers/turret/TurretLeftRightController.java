package org.team3309.lib.controllers.turret;

import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

/**
 * A controller that rotates the turret back and forth until it finds the goal
 * 
 * @author Krager
 *
 */
public class TurretLeftRightController extends Controller {

	private boolean isGoingLeft = false;
	private final double DEGREES_TO_SLOW_DOWN = 25;
	private static final double TURRET_MAX_DEGREES = 0;
	private static final double SLOW_SPEED = .2;
	private static final double FAST_SPEED = .35;

	public TurretLeftRightController() {

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
		// Decide which speed to use (slower when close to corners)
		if (Sensors.getTurretAngle() > TURRET_MAX_DEGREES - DEGREES_TO_SLOW_DOWN
				|| Sensors.getTurretAngle() < DEGREES_TO_SLOW_DOWN) {
			toBeReturned.setMotor(SLOW_SPEED * factor);
		} else {
			toBeReturned.setMotor(FAST_SPEED * factor);
		}
		return toBeReturned;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
