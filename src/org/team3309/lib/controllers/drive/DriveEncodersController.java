package org.team3309.lib.controllers.drive;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
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
	protected PIDPositionController angController = new PIDPositionController(40, 0, 0);
	protected double goalEncoder;
	protected double goalAngle;
	private LinkedList<Operation> operations = new LinkedList<Operation>();
	private double pastLinearOutput = 0;
	private double pastAngOutput = 0;

	public DriveEncodersController(double goal) {
		this.setSubsystemID("Drivetrain");
		linearController.setName("linear");
		linearController.setCompletable(true);
		linearController.setTHRESHOLD(2000);
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
		double currentEncoder = Drive.getInstance().getDistanceTraveled();
		double closestPoint = Integer.MAX_VALUE;
		Operation currentOperation = null;
		for (Operation operation : operations) {
			if (Math.abs(currentEncoder) > Math.abs(operation.encoder)) {
				if (Math.abs(Math.abs(currentEncoder) - Math.abs(operation.encoder)) < closestPoint) {
					currentOperation = operation;
					closestPoint = Math.abs(Math.abs(currentEncoder) - Math.abs(operation.encoder));
				}
			}
		}
		if (currentOperation != null) {
			try {
				currentOperation.perform();
			} catch (InterruptedException | TimedOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public void setOperation(LinkedList<Operation> operations2) {
		this.operations = operations2;
	}
}
