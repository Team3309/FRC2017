package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;

public class Drive extends ControlledSubsystem {

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal encoder averageÃ�.
	 */
	private static final double DRIVE_ENCODER_LENIENCY = 9;

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal angle
	 */
	private static final double DRIVE_GYRO_LENIENCY = .5;
	private static Drive drive;
	private TalonSRXMC right = new TalonSRXMC(RobotMap.RIGHT_DRIVE);
	private TalonSRXMC left = new TalonSRXMC(RobotMap.LEFT_DRIVE);

	public static Drive getInstance() {
		if (drive == null)
			drive = new Drive("Drive");

		return drive;
	}

	private Drive(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {
		// TODO Auto-generated method stub

	}

	@Override
	public void manualControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	/**
	 * Stops current running controller and sets motors to zero
	 */
	public void stopDrive() {
		autoController = new BlankController();
		setLeftRight(0, 0);
	}

	/**
	 * Sets motors left then right
	 * 
	 * @param left
	 *            leftMotorSpeed
	 * @param right
	 *            rightMotorSpeed
	 */
	public void setLeftRight(double left, double right) {
		// System.out.println("Drive CLass LEFT: " + left + " RIht: " + right);
		setRightLeft(right, left);
	}

	/**
	 * Sets motors right then left
	 * 
	 * @param right
	 *            rightMotorSpeed
	 * @param left
	 *            leftMotorSpeed
	 */
	public void setRightLeft(double right, double left) {
		// System.out.println("Drive CLass LEFT: " + left + " RIht: " + right);
		setLeft(left);
		setRight(right);
	}

	/**
	 * Sets the right side of the drive
	 * 
	 * @param right
	 *            rightMotorSpeed
	 */
	public void setRight(double right) {
		this.right.setDesiredOutput(-right);
	}

	/**
	 * Sets the left side of the drive
	 * 
	 * @param left
	 *            leftMotorSpeed
	 */
	public void setLeft(double left) {
		this.left.setDesiredOutput(left);
	}

	/**
	 * Returns the average of the two encoders to see the ditstance traveled
	 * 
	 * @return the average of the left and right to get the distance traveled
	 */
	public double getDistanceTraveled() {
		return (Math.abs(Sensors.getLeftDrive()) + Math.abs(Sensors.getRightDrive())) / 2;
	}

	/**
	 * returns if the current average of encoders (aka distance traveled) is
	 * close to the encoderGoal. Uses DRIVE_ENCODER_LENIENCY to tell if it is
	 * close.
	 * 
	 * @param encoderGoal
	 *            Encoder drive should be at
	 * @return
	 */
	public boolean isEncoderCloseTo(double encoderGoal) {
		double factor = 1;
		if (encoderGoal < 0) {
			factor = -1;
		}
		try {
			if (getDistanceTraveled() * factor < encoderGoal + DRIVE_ENCODER_LENIENCY
					&& factor * getDistanceTraveled() > encoderGoal - DRIVE_ENCODER_LENIENCY) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * returns if the current angle is close to the angleGoal. Uses
	 * DRIVE_ANGLE_LENIENCY to tell if it is close.
	 * 
	 * @param angleGoal
	 *            Angle drive should be at
	 * @return
	 */
	public boolean isAngleCloseTo(double angleGoal) {
		try {
			if (getAngle() < angleGoal + DRIVE_GYRO_LENIENCY
					&& getDistanceTraveled() > angleGoal - DRIVE_GYRO_LENIENCY) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public double getAngle() {
		return Sensors.getAngle();
	}

}
