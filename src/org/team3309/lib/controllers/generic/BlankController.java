package org.team3309.lib.controllers.generic;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

/**
 * Controller that returns only zero values. Used when a subsystem is disabled,
 * but still requires a controller.
 * 
 * @author TheMkrage
 * 
 */
public class BlankController extends Controller {

	private double power = 0;

	@Override
	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		OutputSignal signal = new OutputSignal();
		signal.setLeftRightMotor(power, power);
		signal.setMotor(power);
		return signal; // Returns only zeros for everything
	}

	@Override
	public boolean isCompleted() {
		return true; // Is always complete
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
}
