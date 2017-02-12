package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.drive.DriveAngleVelocityController;
import org.team3309.lib.controllers.drive.equations.DriveCheezyDriveEquation;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.CANSpeedController.ControlMode;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;

public class Drive extends ControlledSubsystem {

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal encoder average.
	 */
	private static final double DRIVE_ENCODER_LENIENCY = 20;

	/**
	 * Used to give a certain gap that the drive would be ok with being within
	 * its goal angle
	 */
	private static final double DRIVE_GYRO_LENIENCY = .5;
	private static Drive drive;
	private TalonSRXMC right0 = new TalonSRXMC(RobotMap.DRIVE_RIGHT_0_ID);
	private TalonSRXMC right1 = new TalonSRXMC(RobotMap.DRIVE_RIGHT_1_ID);
	private TalonSRXMC right2 = new TalonSRXMC(RobotMap.DRIVE_RIGHT_2_ID);
	private TalonSRXMC left0 = new TalonSRXMC(RobotMap.DRIVE_LEFT_0_ID);
	private TalonSRXMC left1 = new TalonSRXMC(RobotMap.DRIVE_LEFT_1_ID);
	private TalonSRXMC left2 = new TalonSRXMC(RobotMap.DRIVE_LEFT_2_ID);

	private boolean isLowGear = true;
	private boolean hasPIDBreakStarted = false;

	public static Drive getInstance() {
		if (drive == null)
			drive = new Drive();
		return drive;
	}

	private Drive() {
		super("Drive");
	}

	@Override
	public void updateTeleop() {
		if (Controls.driverController.getAButton() && !hasPIDBreakStarted) {
			DriveAngleVelocityController drivePIDBreak = new DriveAngleVelocityController(this.getAngle());
			drivePIDBreak.setCompletable(false);
			drivePIDBreak.turningController.setConstants(6, 0, 16);
			this.setController(drivePIDBreak);
			hasPIDBreakStarted = true;
		} else {
			if (!Controls.driverController.getAButton())
				hasPIDBreakStarted = false;
			this.setController(new DriveCheezyDriveEquation());
		}

		if (Controls.driverController.getBumper(Hand.kLeft)) {
			isLowGear = true;
		} else
			isLowGear = false;
		OutputSignal output = controller.getOutputSignal(getInputState());
		setLeftRight(output.getLeftMotor(), output.getRightMotor());
	}

	public void changeToVelocityMode() {
		right0.getTalon().changeControlMode(TalonControlMode.Speed);
		right1.getTalon().changeControlMode(TalonControlMode.Follower);
		right2.getTalon().changeControlMode(TalonControlMode.Follower);
		right1.getTalon().set(RobotMap.DRIVE_RIGHT_0_ID);
		right2.getTalon().set(RobotMap.DRIVE_RIGHT_0_ID);
		left0.getTalon().changeControlMode(TalonControlMode.Speed);
		left1.getTalon().changeControlMode(TalonControlMode.Follower);
		left2.getTalon().changeControlMode(TalonControlMode.Follower);
		left1.getTalon().set(RobotMap.DRIVE_LEFT_0_ID);
		left2.getTalon().set(RobotMap.DRIVE_LEFT_0_ID);
	}

	public void changeToPercentMode() {
		right0.getTalon().changeControlMode(TalonControlMode.PercentVbus);
		right1.getTalon().changeControlMode(TalonControlMode.PercentVbus);
		right2.getTalon().changeControlMode(TalonControlMode.PercentVbus);
		left0.getTalon().changeControlMode(TalonControlMode.PercentVbus);
		left1.getTalon().changeControlMode(TalonControlMode.PercentVbus);
		left2.getTalon().changeControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public void updateAuto() {
		OutputSignal output = controller.getOutputSignal(getInputState());
		setLeftRight(output.getLeftMotor(), output.getRightMotor());
	}

	public InputState getInputState() {
		InputState input = new InputState();
		input.setAngularPos(Sensors.getAngle());
		input.setAngularVel(Sensors.getAngularVel());
		input.setLeftPos(Sensors.getLeftDrive());
		input.setLeftVel(Sensors.getLeftDriveVel());
		input.setRightVel(Sensors.getRightDriveVel());
		input.setRightPos(Sensors.getRightDrive());
		return input;
	}

	@Override
	public void sendToSmartDash() {
		controller.sendToSmartDash();
		SmartDashboard.putNumber(this.getName() + " right pow", right0.getTalon().getPosition());
		SmartDashboard.putNumber(this.getName() + " left pow", left0.getTalon().getPosition());
		SmartDashboard.putNumber(this.getName() + " angle", getAngle());
		SmartDashboard.putNumber(this.getName() + " angle vel", Sensors.getAngularVel());
		// SmartDashboard.putNumber(this.getName() + " left pos" )
	}

	@Override
	public void manualControl() {
		updateTeleop();
	}

	@Override
	public void initTeleop() {
		controller = new DriveCheezyDriveEquation();
		changeToPercentMode();
	}

	@Override
	public void initAuto() {
		controller = new BlankController();
	}

	/**
	 * Stops current running controller and sets motors to zero
	 */
	public void stopDrive() {
		controller = new BlankController();
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
		if (this.right0.getTalon().getControlMode() == TalonControlMode.Speed) {
			this.right0.setDesiredOutput(right);
		} else {
			this.right0.setDesiredOutput(-right);
			this.right1.setDesiredOutput(-right);
			this.right2.setDesiredOutput(-right);
		}

	}

	/**
	 * Sets the left side of the drive
	 * 
	 * @param left
	 *            leftMotorSpeed
	 */
	public void setLeft(double left) {
		if (left0.getTalon().getControlMode() == TalonControlMode.Speed) {
			this.left0.setDesiredOutput(left);
		} else {
			this.left0.setDesiredOutput(left);
			this.left1.setDesiredOutput(left);
			this.left2.setDesiredOutput(left);
		}
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
