package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.communications.BlackBox;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Flywheel extends ControlledSubsystem {

	private TalonSRXMC leftSpark = new TalonSRXMC(RobotMap.LEFT_SHOOTER_ID);
	private TalonSRXMC rightSpark = new TalonSRXMC(RobotMap.RIGHT_SHOOTER_ID);

	private double maxAccRPS = 31.0;
	private double aimVelRPS = 0.0;
	private double aimAccRPS = 0.0;
	private double curVel = 0;
	private double lastVisionRPS = 0;

	/**
	 * Shooter for singleton pattern
	 */
	private static Flywheel mFlywheel;

	private Flywheel(String name) {
		super(name);
		this.teleopController = new FeedForwardWithPIDController(.006, 0, .035, 0.000, 0.00);
		this.autoController = new FeedForwardWithPIDController(.006, 0, .035, 0.000, 0.00);
		this.teleopController.setName("Flywheel");
		this.rightSpark.setReversed(true);
		this.autoController.setName("Flywheel");
		((FeedForwardWithPIDController) this.teleopController).setTHRESHOLD(10);
		((FeedForwardWithPIDController) this.autoController).setTHRESHOLD(10);
		SmartDashboard.putNumber("TEST RPS", 140);
	}

	/**
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Flywheel getInstance() {
		if (mFlywheel == null) {
			mFlywheel = new Flywheel("Flywheel");
		}
		return mFlywheel;
	}

	public void setAimVelRPSAuto(double power) {
		this.aimVelRPS = power;
	}

	@Override
	public void initTeleop() {

	}

	@Override
	public void initAuto() {

	}

	@Override
	public void updateAuto() {
		curVel = this.getRPS();
		updateTeleop(); // uses same vision
	}

	@Override
	public void updateTeleop() {
		curVel = this.getRPS();
		// Find our base aim vel
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			aimVelRPS = VisionServer.getInstance().getRPS();
			lastVisionRPS = aimVelRPS;
		} else if (Controls.operatorController.getPOV() == 0) {
			aimVelRPS = lastVisionRPS;
		} else {
			aimVelRPS = 0;
			aimAccRPS = 0;
		}
		shootLikeRobie();
	}

	/**
	 * Raw power values
	 */
	public void manualControl() {
		curVel = this.getRPS();
		double power = 0;
		if (Controls.operatorController.getAButton()) {
			power = .7;
			BlackBox.logThis("curRPS", Sensors.getFlywheelRPS());
			BlackBox.writeLog();
		} else if (Controls.operatorController.getXButton()) {
			power = .8;
		} else if (Controls.operatorController.getYButton()) {
			power = .9;
		} else {
			power = 0;
		}
		this.rightSpark.setDesiredOutput(power);
		this.leftSpark.setDesiredOutput(power);
	}

	/**
	 * Feed Forward with dynamic aims
	 */
	private void shootLikeRobie() {
		if (aimVelRPS == 0) {
		} else {
			if (curVel < aimVelRPS - 32) {
				aimAccRPS = maxAccRPS;
				aimVelRPS = curVel + maxAccRPS;

			} else if (curVel > aimVelRPS + 32) {
				aimAccRPS = 0;
				// aimVelRPS = curVel + maxAccRPS;
			} else {
				aimAccRPS = 0;
			}
		}
		// Send our target velocity to the mController
		if (this.teleopController instanceof FeedForwardWithPIDController) {
			((FeedForwardWithPIDController) this.teleopController).setAimAcc(aimAccRPS);
			((FeedForwardWithPIDController) this.teleopController).setAimVel(aimVelRPS);
		}
		double output = this.teleopController.getOutputSignal(this.getInputState()).getMotor();
		if (output > 1) {
			output = 1;
		} else if (output < 0) {
			output = 0;
		}
		// Get value and set to motors
		if (aimVelRPS == 0) {
			this.setShooter(0);
		} else {
			if (curVel < 30) {
				output = .45;
			}
			this.setShooter(output);
		}
	}

	@Override
	public InputState getInputState() {
		InputState input = new InputState();
		input.setError(aimVelRPS - curVel);
		return input;
	}

	public double getPercent() {
		if (aimVelRPS == 0)
			return 0;
		double x = (double) curVel / (aimVelRPS - 10) * 100;
		if (x > 100) {
			x = 100;
		}
		return x;
	}

	@Override
	public void sendToSmartDash() {
		teleopController.sendToSmartDash();
		SmartDashboard.putNumber(this.getName() + " RPM", curVel * 60);
		SmartDashboard.putNumber(this.getName() + " RPS", curVel);
		SmartDashboard.putNumber(this.getName() + " Goal", this.aimVelRPS);
		SmartDashboard.putNumber(this.getName() + " Left", leftSpark.getDesiredOutput());
		SmartDashboard.putNumber(this.getName() + " Right", rightSpark.getDesiredOutput());
	}

	private double getRPS() {
		return Sensors.getFlywheelRPS();
	}

	public double getAimVelRPS() {
		return aimVelRPS;
	}

	public void setAimVelRPS(double aimVelRPS) {
		this.aimVelRPS = aimVelRPS;
	}

	public double getAimAccRPS() {
		return aimAccRPS;
	}

	public void setAimAccRPS(double aimAccRPS) {
		this.aimAccRPS = aimAccRPS;
	}

	public boolean isShooterInRange() {
		if (this.getRPS() < aimVelRPS + 6 && this.getRPS() > aimVelRPS - 6)
			return true;
		return false;
	}

	private double getRPM() {
		return 60 * Sensors.getFlywheelRPS();
	}

	private void setShooter(double power) {
		leftSpark.setDesiredOutput(power);
		rightSpark.setDesiredOutput(power);
	}

}
