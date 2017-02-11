package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.vision.VisionServer;

public class Hood extends ControlledSubsystem {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static double aimAngle;
	private static final double MIN_ANGLE = 0;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 0;
	private double goalAngle = 0;
	private double lastVisionAngle = 0;
	private ContinuousRotationServo servo = new ContinuousRotationServo(RobotMap.PWM_SERVO);

	private Hood(String name) {
		super(name);
	}

	/**
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Hood getInstance() {
		if (mHood == null) {
			mHood = new Hood("Hood");
		}
		return mHood;
	}

	public void updateTeleop() {
		double curAngle = this.getAngle();
		double output = 0;

		// Find aim angle
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			lastVisionAngle = goalAngle;
		} else {
			goalAngle = DEFAULT_ANGLE;
		}
		if (goalAngle >= 0) {
			output = this.teleopController.getOutputSignal(getInputState()).getMotor();
		}

		if ((curAngle > MAX_ANGLE && output > .2) || (curAngle < MIN_ANGLE
				&& output < -.2) /* || this.isOnTarget() */) {
			output = 0;
			// if ((curAngle < 4 && output < 0))
			// ((PIDController) this.teleopController).reset();
		}
		this.setHood(output);
	}

	@Override
	public void updateAuto() {
		updateTeleop(); // just needs to do what vision says
	}

	@Override
	public InputState getInputState() {
		InputState input = new InputState();
		input.setError(goalAngle - getAngle());
		return input;
	}

	@Override
	public void sendToSmartDash() {
		this.teleopController.sendToSmartDash();
	}

	@Override
	public void manualControl() {

	}

	@Override
	public void initTeleop() {
		aimAngle = DEFAULT_ANGLE;
		this.teleopController = new PIDPositionController(.001, 0, 0);
	}

	@Override
	public void initAuto() {

	}

	public double getAngle() {
		// TODO get Angle
		return 0;
	}

	private void setHood(double power) {

	}
}
