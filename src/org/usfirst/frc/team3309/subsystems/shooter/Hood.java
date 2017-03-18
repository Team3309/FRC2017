package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Hood extends KragerSystem {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private double goalAngle = 0;
	private double lastVisionAngle = 0;
	private ContinuousRotationServo servo = new ContinuousRotationServo(RobotMap.SERVO);
	private NetworkTable table = NetworkTable.getTable("Hood");

	private Hood() {
		super("Hood");
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
		// Find aim angle
		if (Controls.operatorController.getYButton()) {
			testPosControl();
		} else if (Controls.operatorController.getBButton()) {
			if (VisionServer.getInstance().hasTargetsToAimAt()) {
				goalAngle = VisionServer.getInstance().getHoodAngle();
				System.out.println("VISION HOOD " + goalAngle);
				lastVisionAngle = goalAngle;
			} else {
				goalAngle = lastVisionAngle;
			}
		} else {
			testPosControl();
		}
		this.servo.setPosition(goalAngle);
	}

	@Override
	public void updateAuto() {
		// just needs to do what vision says
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			System.out.println("VISION HOOD " + goalAngle);
			lastVisionAngle = goalAngle;
		} else {
			goalAngle = lastVisionAngle;
		}
	}

	public void testPosControl() {
		goalAngle = NetworkTable.getTable("Climber").getNumber("k_aim Hood Angle", 20);
		NetworkTable.getTable("Climber").putNumber("k_aim Hood Angle", goalAngle);
	}

	@Override
	public void sendToSmartDash() {
		table.putNumber("angle", getAngle());
		table.putNumber("aim", goalAngle);

	}

	@Override
	public void manualControl() {

	}

	@Override
	public void initTeleop() {
		goalAngle = 0;
	}

	@Override
	public void initAuto() {
		goalAngle = 0;
	}

	public double getAngle() {
		return this.servo.getPosition();
	}

	private void setHood(double power) {
		servo.set(power);
	}
}
