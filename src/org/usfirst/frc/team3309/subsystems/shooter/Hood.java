package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.sensors.Sensors;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.SensorDoesNotReturnException;
import org.usfirst.frc.team3309.vision.Vision;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	private static TalonSRXMC hoodTalon = new TalonSRXMC(RobotMap.HOOD_ID);

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
		if (Controls.operatorController.getAButton()) {
			goalAngle = 10.35;
		} else if (Controls.operatorController.getBButton()) {
			goalAngle = 25.0;
		} else if (Controls.operatorController.getXButton()) {
			goalAngle = 35.5;
		} else if (Controls.driverController.getYButton()) {
			// goalAngle = 28.6;
			goalAngle = SmartDashboard.getNumber("Test Angle");
		} else if (Controls.operatorController.getStartButton()) {
			if (Vision.getInstance().getShotToAimTowards() != null) {
				goalAngle = Vision.getInstance().getShotToAimTowards().getGoalHoodAngle();
				lastVisionAngle = Vision.getInstance().getShotToAimTowards().getGoalHoodAngle();
				if (!printed && FeedyWheel.getInstance().feedyWheelSpark.getDesiredOutput() < 0) {
					System.out.println("\n SHOOTING --------------- ");
					System.out.println("HOOD: " + goalAngle + " VISION: "
							+ Vision.getInstance().getShotToAimTowards().getYCoordinate());
					System.out.println("--------\n");
					printed = true;
				}
			} else
				goalAngle = 22;
			// System.out.println("Goal Angle: " + goalAngle);
		} else if (Controls.operatorController.getPOV() == 0) {
			goalAngle = lastVisionAngle;
		} else {
			offset = 0;
			printed = false;
			goalAngle = HOOD_DOWN_ANGLE;
		}
		goalAngle += offset;
		if (goalAngle >= 0) {
			output = this.teleopController.getOutputSignal(getInputState()).getMotor();
		}

		if ((curAngle > 50 && output > -1) || (curAngle < 4 && output < 0) || this.isOnTarget()) {
			output = 0;
			// if ((curAngle < 4 && output < 0))
			// ((PIDController) this.teleopController).reset();
		}
		this.setHood(output);
	}

	@Override
	public void updateAuto() {

	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {

	}

	@Override
	public void manualControl() {

	}

	@Override
	public void initTeleop() {
		aimAngle = DEFAULT_ANGLE;
	}

	@Override
	public void initAuto() {

	}

	public double getAngle() {
		return hoodTalon.getTalon().getPosition();
	}
}
