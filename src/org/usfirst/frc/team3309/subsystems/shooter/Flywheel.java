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

	private TalonSRXMC leftTalon = new TalonSRXMC(RobotMap.LEFT_SHOOTER_ID);
	private TalonSRXMC rightTalon = new TalonSRXMC(RobotMap.RIGHT_SHOOTER_ID);

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
		this.controller = new FeedForwardWithPIDController(.006, 0, .035, 0.000, 0.00);
		this.controller.setName("Flywheel");
		this.rightTalon.setReversed(true);
		((FeedForwardWithPIDController) this.controller).setTHRESHOLD(10);
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

	// ANGLE CLOCKWISE POSITIV
	// NEGATIVE POWERGF
	/**
	 * Raw power values
	 */
	public void manualControl() {
		curVel = this.getRPS();
		if (Controls.operatorController.getBButton()) {
			aimVelRPS = 80;
		} else if (Controls.operatorController.getXButton()) {
			aimVelRPS = 100;
		} else if (Controls.operatorController.getYButton()) {
			aimVelRPS = SmartDashboard.getNumber("Flywheel aim vel testable", 0);
		} else {
			aimVelRPS = 0;
		}
		shootLikeRobie();
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
		if (this.controller instanceof FeedForwardWithPIDController) {
			((FeedForwardWithPIDController) this.controller).setAimAcc(aimAccRPS);
			((FeedForwardWithPIDController) this.controller).setAimVel(aimVelRPS);
		}
		double output = this.controller.getOutputSignal(this.getInputState()).getMotor();
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
		controller.sendToSmartDash();
		SmartDashboard.putNumber(this.getName() + " RPM", curVel * 60);
		SmartDashboard.putNumber(this.getName() + " RPS", curVel);
		SmartDashboard.putNumber(this.getName() + " Goal", this.aimVelRPS);
		SmartDashboard.putNumber(this.getName() + " Left", leftTalon.getDesiredOutput());
		SmartDashboard.putNumber(this.getName() + " Right", rightTalon.getDesiredOutput());
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
		leftTalon.setDesiredOutput(power);
		rightTalon.setDesiredOutput(power);
	}

}
