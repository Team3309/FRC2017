package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team339.robot.Sensors;

public class DriveWhileOnADefenseController extends Controller {

	private final double THRESHOLD = .05;
	private Controller controllerToRun;
	private boolean hasBeganGoingOverDefense = false;
	private boolean hasGoneOverDefense = false;
	private double countsOfAfter = 0;

	public DriveWhileOnADefenseController(Controller x) {
		this.controllerToRun = x;
	}

	@Override
	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		// IF roll is greater than thresh, start keeping track
		System.out.println("ROOLLL: " + Sensors.getRoll());
		if (Sensors.getRoll() > THRESHOLD) {
			hasBeganGoingOverDefense = true;
		} else {
			if (hasBeganGoingOverDefense) {
				countsOfAfter++;
			}
		}

		if (countsOfAfter > 50) {
			hasGoneOverDefense = true;
		}
		return controllerToRun.getOutputSignal(inputState);
	}

	@Override
	public boolean isCompleted() {
		return hasGoneOverDefense;
	}

}
