package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Drive;

/**
 * Use this class to drive the robot an exact amount of encoders Uses three PID
 * controllers, one for angle, one for left, one for right.
 * 
 * @author Krager
 *
 */
public class DriveEncodersController extends Controller {

	protected PIDPositionController linearController = new PIDPositionController(.003, 0, 0);
	protected PIDPositionController angController = new PIDPositionController(0.166, 0.001, 0.002);
	protected double goalEncoder;
	protected double goalAngle;

	public DriveEncodersController(double goal) {
		linearController.setName("linear");
		angController.setName("ang");
		goalEncoder = goal;
		goalAngle = Sensors.getAngle();
		Drive.getInstance().changeToPercentMode();
	}

	public DriveEncodersController(double goal, double angle) {
		this(goal);
		goalAngle = angle;
	}

	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		// Input States for the two controllers
		InputState inputForLinear = new InputState();
		InputState inputForAng = new InputState();
		// Add the error for each controller from the inputState
		double linearError = (goalEncoder - Drive.getInstance().getDistanceTraveled());
		inputForLinear.setError(linearError);
		inputForAng.setError(goalAngle - inputState.getAngularPos());
		OutputSignal linearOutput = linearController.getOutputSignal(inputForLinear);
		OutputSignal angularOutput = angController.getOutputSignal(inputForAng);
		// Prepare the output
		OutputSignal signal = new OutputSignal();
		signal.setLeftMotor(linearOutput.getMotor() + angularOutput.getMotor());
		signal.setRightMotor(linearOutput.getMotor() - angularOutput.getMotor());
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
