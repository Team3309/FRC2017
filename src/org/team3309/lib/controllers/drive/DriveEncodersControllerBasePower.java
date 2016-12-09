package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.subsystems.Drive;

/**
 * Use this class to drive the robot an exact amount of encoders Uses three PID
 * controllers, one for angle, one for left, one for right.
 * 
 * @author Krager
 *
 */
public class DriveEncodersControllerBasePower extends DriveEncodersController {

	private double basePower = 0.0;
	private double originalBasePower = 0.0;
	private double pastBasePower = 0.0;
	private boolean isOver = false;

	public DriveEncodersControllerBasePower(double goal, double basePower) {
		super(goal);
		this.basePower = 0;
		this.originalBasePower = basePower;
	}

	@Override
	public void reset() {
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double currentDistance = 0;
		try { // if no encoders, return 0's
			currentDistance = Drive.getInstance().getDistanceTraveled();
		} catch (Exception e) {
			return new OutputSignal();
		}
		// Input States for the three controllers
		InputState inputForAng = inputState;
		// Add the error for each controller from the inputState
		inputForAng.setError(goalAngle - inputState.getAngularPos());
		OutputSignal angularOutput = angController.getOutputSignal(inputState);
		// Prepare the output
		OutputSignal signal = new OutputSignal();
		if (Math.abs(basePower) < Math.abs(originalBasePower)) {
			basePower += originalBasePower / 10;
		} else if (currentDistance > Math.abs(goalEncoder)) {
			// basePower = -originalBasePower;
			isOver = true;
		} else if (currentDistance < Math.abs(goalEncoder)) {
			basePower = originalBasePower;
		}
		System.out.println("Here is turn power: " + angularOutput.getMotor());
		System.out.println("ERror: " + inputForAng.getError());
		System.out.println("GoalAngle: " + goalAngle);
		signal.setLeftMotor(basePower);
		signal.setRightMotor(basePower);
		return signal;
	}

	private boolean isEncoderClose() {
		return Drive.getInstance().isEncoderCloseTo(goalEncoder);
	}

	@Override
	public boolean isCompleted() {
		return /* angController.isCompleted() && */ isEncoderClose() || isOver;
	}

	@Override
	public void sendToSmartDash() {
		angController.sendToSmartDash();
	}
}
