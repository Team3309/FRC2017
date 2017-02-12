package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.tunable.IDashboard;
import org.team3309.lib.tunable.Dashboard;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hood extends ControlledSubsystem implements IDashboard {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static final double MIN_ANGLE = 0;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 200;
	private double goalAngle = 0;
	private double lastVisionAngle = 0;
	@Dashboard(tunable = true)
	private double curAngle = 0;
	private ContinuousRotationServo servo = new ContinuousRotationServo(RobotMap.SERVO);

	private Hood() {
		super("Hood");
		this.controller = new PIDPositionController(.001, 0, 0);
	}

	/**
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Hood getInstance() {
		if (mHood == null) {
			mHood = new Hood();
		}
		return mHood;
	}

	public void updateTeleop() {
		curAngle = this.getAngle();
		double output = 0;

		// Find aim angle
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			lastVisionAngle = goalAngle;
		} else {
			goalAngle = lastVisionAngle;
		}
		// testPosControl();
		if (goalAngle >= 0) {
			output = this.controller.getOutputSignal(getInputState()).getMotor();
		}

		if ((curAngle > MAX_ANGLE && output > .2) || (curAngle < MIN_ANGLE
				&& output < -.2) /* || this.isOnTarget() */) {
			output = 0;
			// TODO POSSIBLE RESET FOR DIFFERENT ANGLES
		}

		if (Math.abs(output) > .03)
			output = .03 * KragerMath.sign(output);
		this.setHood(output);
	}

	@Override
	public void updateAuto() {
		updateTeleop(); // just needs to do what vision says
	}

	public void testPosControl() {
		goalAngle = SmartDashboard.getNumber("aim Hood Angle", 0);
		SmartDashboard.putNumber("aim Hood Angle", goalAngle);
	}

	@Override
	public InputState getInputState() {
		InputState input = new InputState();
		input.setError(goalAngle - getAngle());
		return input;
	}

	@Override
	public void sendToSmartDash() {
		this.controller.sendToSmartDash();
	}

	@Override
	public void manualControl() {
		setHood(Controls.operatorController.getY(Hand.kRight));
	}

	@Override
	public void initTeleop() {
		goalAngle = DEFAULT_ANGLE;
	}

	@Override
	public void initAuto() {

	}

	public double getAngle() {
		// TODO get Angle
		return 0;
	}

	private void setHood(double power) {
		servo.set(power);
	}

	@Override
	public String getTableName() {
		return "SmartDashboard";
	}

	@Override
	public String getObjectName() {
		return "";
	}
}
