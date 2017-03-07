package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.drive.DriveAngleVelocityController;
import org.team3309.lib.controllers.drive.equations.DriveCheezyDriveEquation;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.tunable.Dashboard;
import org.team3309.lib.tunable.DashboardHelper;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

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
	private static final double DRIVE_GYRO_LENIENCY = 5;
	private static Drive drive;
	private CANTalon right0 = new CANTalon(RobotMap.DRIVE_RIGHT_0_ID);
	private CANTalon right1 = new CANTalon(RobotMap.DRIVE_RIGHT_1_ID);
	private CANTalon right2 = new CANTalon(RobotMap.DRIVE_RIGHT_2_ID);
	private CANTalon left0 = new CANTalon(RobotMap.DRIVE_LEFT_0_ID);
	private CANTalon left1 = new CANTalon(RobotMap.DRIVE_LEFT_1_ID);
	private CANTalon left2 = new CANTalon(RobotMap.DRIVE_LEFT_2_ID);
	private NetworkTable table = NetworkTable.getTable("Drivetrain");
	private Solenoid shifter = new Solenoid(RobotMap.SHIFTER);

	private boolean isLowGear = true;
	private boolean hasPIDBreakStarted = false;

	@Dashboard(displayName = "k_test Velocity", tunable = true)

	public static Drive getInstance() {
		if (drive == null)
			drive = new Drive();
		return drive;
	}

	private Drive() {
		super("Drivetrain");

		right0.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		left0.setFeedbackDevice(FeedbackDevice.AnalogEncoder);
		table.putNumber("k_testVel", 0);
	}

	@Override
	public void updateTeleop() {
		this.changeToPercentMode();
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
			isLowGear = false;
		} else
			isLowGear = true;

		// System.out.println("right pos " + this.getRightPos());
		shifter.set(isLowGear);
		OutputSignal output = getController().getOutputSignal(getInputState());
		setLeftRight(output.getLeftMotor(), output.getRightMotor());
	}

	public void changeToVelocityMode() {
		right0.changeControlMode(TalonControlMode.Speed);

		right1.changeControlMode(TalonControlMode.Follower);
		right2.changeControlMode(TalonControlMode.Follower);
		right1.set(RobotMap.DRIVE_RIGHT_0_ID);
		right2.set(RobotMap.DRIVE_RIGHT_0_ID);
		left0.changeControlMode(TalonControlMode.Speed);
		left1.changeControlMode(TalonControlMode.Follower);
		left2.changeControlMode(TalonControlMode.Follower);
		left1.set(RobotMap.DRIVE_LEFT_0_ID);
		left2.set(RobotMap.DRIVE_LEFT_0_ID);
	}

	public void changeToPercentMode() {
		right0.changeControlMode(TalonControlMode.PercentVbus);
		right1.changeControlMode(TalonControlMode.PercentVbus);
		right2.changeControlMode(TalonControlMode.PercentVbus);
		left0.changeControlMode(TalonControlMode.PercentVbus);
		left1.changeControlMode(TalonControlMode.PercentVbus);
		left2.changeControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public void updateAuto() {
		this.changeToVelocityMode();
		OutputSignal output = getController().getOutputSignal(getInputState());
		setLeftRight(output.getLeftMotor(), output.getRightMotor());
	}

	public InputState getInputState() {
		InputState input = new InputState();
		input.setAngularPos(Sensors.getAngle());
		input.setAngularVel(Sensors.getAngularVel());
		input.setLeftPos(left0.getAnalogInPosition());
		input.setLeftVel(left0.getAnalogInVelocity());
		input.setRightVel(right0.getAnalogInVelocity());
		input.setRightPos(right0.getAnalogInPosition());
		return input;
	}

	@Override
	public void sendToSmartDash() {
		getController().sendToSmartDash();
		DashboardHelper.updateTunable(getController());
		table.putNumber("current", right0.getOutputCurrent());
		table.putNumber(this.getName() + " right pos", this.getRightPos());
		table.putNumber(this.getName() + " left pos", this.getLeftPos());
		table.putNumber(this.getName() + " right vel", this.getRightVel());
		table.putNumber(this.getName() + " left vel", -this.getLeftVel());
		table.putNumber(this.getName() + " angle", getAngle());
		// table.putNumber(this.getName() + " angle raw", getAngle());
		// table.putNumber(this.getName() + " angle", getAngle());
		table.putNumber(this.getName() + " angle vel", Sensors.getAngularVel());
		table.putNumber("right error", this.right0.getClosedLoopError());
		table.putNumber("left error", this.left0.getClosedLoopError());
		table.putNumber("wheel", KragerMath.threshold(Controls.driverController.getX(Hand.kRight)));
	}

	@Override
	public void manualControl() {
		// double lol = .3;
		// if (driverRemote.getAButton()) {
		// setRight(lol);
		// setLeft(-lol);
		//
		// } else {
		// setRight(0);
		// setLeft(0);
		// }
		updateTeleop();
	}

	@Override
	public void initTeleop() {
		this.setController(new DriveCheezyDriveEquation());
		this.stopDrive();
	}

	@Override
	public void initAuto() {
		this.setController(new BlankController());
	}

	/**
	 * Stops current running controller and sets motors to zero
	 */
	public void stopDrive() {
		this.changeToPercentMode();
		this.setController(new BlankController());
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
		if (this.right0.getControlMode() == TalonControlMode.Speed) {
			// System.out.println("SPEED ");
			this.right0.set(right);
		} else {
			// System.out.println("NOT SPEEd");
			this.right0.set(-right);
			this.right1.set(-right);
			this.right2.set(-right);
		}

	}

	/**
	 * Sets the left side of the drive
	 * 
	 * @param left
	 *            leftMotorSpeed
	 */
	public void setLeft(double left) {
		if (left0.getControlMode() == TalonControlMode.Speed) {
			// System.out.println("SPEED ");
			this.left0.set(-left);
		} else {
			// System.out.println("NOT SPEEd");
			this.left0.set(left);
			this.left1.set(left);
			this.left2.set(left);
		}
	}

	/**
	 * Returns the average of the two encoders to see the ditstance traveled
	 * 
	 * @return the average of the left and right to get the distance traveled
	 */
	public double getDistanceTraveled() {
		return (right0.getAnalogInPosition() - left0.getAnalogInPosition()) / 2;
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
			if (getAngle() < angleGoal + DRIVE_GYRO_LENIENCY && getAngle() > angleGoal - DRIVE_GYRO_LENIENCY) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	@Dashboard(displayName = "angle")
	public double getAngle() {
		return Sensors.getAngle();
	}

	@Dashboard(displayName = "leftVel")
	public double getLeftVel() {
		return this.left0.getAnalogInVelocity();
	}

	@Dashboard(displayName = "leftPos")
	public double getLeftPos() {
		return this.left0.getAnalogInPosition();
	}

	@Dashboard(displayName = "rightVel")
	public double getRightVel() {
		return this.right0.getAnalogInVelocity();
	}

	@Dashboard(displayName = "rightPos")
	public double getRightPos() {
		return this.right0.getAnalogInPosition();
	}

	public void testVel() {
		this.changeToVelocityMode();
		double testVel = table.getNumber("k_testVel", 0);
		setRight(testVel);
		setLeft(testVel);
	}

	public void resetDrive() {
		right0.setAnalogPosition(0);
		left0.setAnalogPosition(0);
	}

	public void setHighGear() {
		shifter.set(true);
	}

	public void setLowGear() {
		shifter.set(false);
	}

}
