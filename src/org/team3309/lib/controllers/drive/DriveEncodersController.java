package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.Sensors;

/**
 * Use this class to drive the robot an exact amount of encoders Uses three PID
 * controllers, one for angle, one for left, one for right.
 * 
 * @author Krager
 *
 */
public class DriveEncodersController extends Controller {

	private PIDPositionController linearController = new PIDPositionController(.003, 0, 0);
	// private PIDPositionController rightController = new
	// PIDPositionController(.2, 0, 0);
	protected PIDPositionController angController = new PIDPositionController(0.166, 0.001, 0.002);
	protected double goalEncoder;
	protected double goalAngle;

	public DriveEncodersController(double goal) {
		linearController.setName("linear");
		angController.setName("ang");
		goalEncoder = goal;
		goalAngle = Sensors.getAngle();
	}

	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		// Input States for the three controllers
		InputState inputForLinear = new InputState();
		// InputState inputForRightVel = inputState;
		InputState inputForAng = new InputState();
		// Add the error for each controller from the inputState
		double x = 0;
		try {
			x = (goalEncoder - inputState.getLeftPos());
		} catch (Exception e) {
			return new OutputSignal();
		}
		inputForLinear.setError(x);
		inputForAng.setError(goalAngle - inputState.getAngularPos());
		OutputSignal linearOutput = linearController.getOutputSignal(inputForLinear);
		OutputSignal angularOutput = angController.getOutputSignal(inputForAng);
		// Prepare the output
		OutputSignal signal = new OutputSignal();
		signal.setLeftMotor(linearOutput.getMotor() - angularOutput.getMotor());
		signal.setRightMotor(linearOutput.getMotor() + angularOutput.getMotor());
		return signal;
	}

	@Override
	public boolean isCompleted() {
		return linearController.isCompleted() && angController.isCompleted();
	}

	@Override
	public void sendToSmartDash() {
		linearController.sendToSmartDash();
		angController.sendToSmartDash();
	}
}
