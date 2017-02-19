package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.tunable.Dashboard;
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

	protected PIDPositionController linearController = new PIDPositionController(.15, 0, 0);
	// protected PIDPositionController angController = new
	// PIDPositionController(0.166, 0.001, 0.002);
	protected PIDPositionController angController = new PIDPositionController(37, .13, 17);
	@Dashboard(displayName = "Goal Encoder")
	protected double goalEncoder;
	protected double goalAngle;
	@Dashboard(displayName = "LinearOutput")
	private double pastLinearOutput = 0;
	@Dashboard(displayName = "PastOutput")
	private double pastAngOutput = 0;

	public DriveEncodersController(double goal) {
		this.setSubsystemID("Drivetrain");
		linearController.setName("linear");
		linearController.setCompletable(true);
		linearController.setTHRESHOLD(1000);
		linearController.setTIME_TO_BE_COMPLETE_MILLISECONDS(100);
		linearController.setSubsystemID(this.subsystemID);
		angController.setName("ang");
		angController.setSubsystemID(this.subsystemID);
		goalEncoder = goal;
		goalAngle = Sensors.getAngle();
		Drive.getInstance().changeToVelocityMode();
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
		pastLinearOutput = linearOutput.getMotor();
		pastAngOutput = angularOutput.getMotor();
		signal.setLeftMotor(linearOutput.getMotor() + angularOutput.getMotor());
		signal.setRightMotor(linearOutput.getMotor() - angularOutput.getMotor());
		return signal;
	}

	@Override
	public boolean isCompleted() {
		return linearController.isCompleted();
	}

	@Override
	public void sendToSmartDash() {
		linearController.sendToSmartDash();
		angController.sendToSmartDash();
	}
}
