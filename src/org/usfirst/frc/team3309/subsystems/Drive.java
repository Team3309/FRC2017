package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.SparkMC;
import org.team3309.lib.controllers.drive.DriveAngleVelocityController;
import org.team3309.lib.controllers.drive.equations.DriveCheezyDriveEquation;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Solenoid;

public class Drive extends ControlledSubsystem {

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal encoder averageÃ�.
	 */
	private static final double DRIVE_ENCODER_LENIENCY = 20;

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal angle
	 */
	private static final double DRIVE_GYRO_LENIENCY = .5;
	private static Drive drive;
	private SparkMC right = new SparkMC(RobotMap.RIGHT_DRIVE);
	private SparkMC left = new SparkMC(RobotMap.LEFT_DRIVE);
	private Solenoid sol = new Solenoid(RobotMap.SHIFTER);

	private boolean isLowGear = true;
	public boolean lowGearInAuto = false;
	boolean isReset = false;

	public static Drive getInstance() {
		if (drive == null)
			drive = new Drive("Drive");

		return drive;
	}

	private Drive(String name) {
		super(name);
	}

	@Override
	public void updateTeleop() {
		if (Controls.driverController.getAButton() && !isReset) {
			DriveAngleVelocityController driveAngleHardCore = new DriveAngleVelocityController(this.getAngle());
			driveAngleHardCore.setCompletable(false);
			driveAngleHardCore.turningController.setConstants(6, 0, 16);
			this.setTeleopController(driveAngleHardCore);
			isReset = true;
		} else if (Controls.operatorController.getAButton()) {

		} else {
			this.setTeleopController(new DriveCheezyDriveEquation());
		}

		if (Controls.driverController.getBumper(Hand.kLeft)) {
			isLowGear = true;
			sol.set(false);
		} else {
			isLowGear = false;
			sol.set(true);
		}
		OutputSignal output = teleopController.getOutputSignal(getInputState());
		setLeftRight(output.getLeftMotor(), output.getRightMotor());
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
		teleopController.sendToSmartDash();

	}

	@Override
	public void manualControl() {

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
		return (Sensors.getLeftDrive() + Sensors.getRightDrive()) / 2;
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
		try {
			if (getDistanceTraveled() < encoderGoal + DRIVE_ENCODER_LENIENCY
					&& getDistanceTraveled() > encoderGoal - DRIVE_ENCODER_LENIENCY) {
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
