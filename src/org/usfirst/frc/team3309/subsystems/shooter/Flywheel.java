
package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.communications.BlackBox;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.tunable.Dashboard;
import org.team3309.lib.tunable.DashboardHelper;
import org.team3309.lib.tunable.IDashboard;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Flywheel extends ControlledSubsystem implements IDashboard {

	private TalonSRXMC leftTalon = new TalonSRXMC(RobotMap.LEFT_SHOOTER_ID);
	private TalonSRXMC rightTalon = new TalonSRXMC(RobotMap.RIGHT_SHOOTER_ID);

	private double maxAccRPS = 60.0;
	private double aimVelRPS = 0.0;
	private double aimAccRPS = 0.0;
	private double curVel = 0;
	private double lastVisionRPS = 0;
	private NetworkTable table = NetworkTable.getTable("Flywheel");

	/**
	 * Shooter for singleton pattern
	 */
	private static Flywheel mFlywheel;

	private Flywheel() {
		super("Flywheel");
		this.setController(new FeedForwardWithPIDController(0.004, 0, 0.022, 0.0000008, 0.00));
		this.getController().setName("Flywheel Speed");
		this.rightTalon.setReversed(true);
		((FeedForwardWithPIDController) this.getController()).setTHRESHOLD(10);
		NetworkTable.getTable("Climber").putNumber("k_TEST RPS", 140);
	}

	/**
	 * 
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Flywheel getInstance() {
		if (mFlywheel == null) {
			mFlywheel = new Flywheel();
		}
		return mFlywheel;
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
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			aimVelRPS = 180;
			// aimVelRPS = VisionServer.getInstance().getRPS();
			lastVisionRPS = aimVelRPS;
		} else if (Shooter.getInstance().isShouldBeSpinningUp()) {
			aimVelRPS = 180;
		}
		shootLikeRobie();
	}

	@Override
	public void updateTeleop() {
		curVel = this.getRPS();
		// Find our base aim vel
		if (Controls.operatorController.getYButton() || Controls.driverController.getYButton()) {
			this.testVel();
		} else if (Controls.operatorController.getXButton()) {
			aimVelRPS = 180;
		} else if (Controls.operatorController.getAButton()) {
			if (VisionServer.getInstance().hasTargetsToAimAt()) {
				aimVelRPS = VisionServer.getInstance().getRPS();
				System.out.println("FLYWHEEL VISION AIM " + aimVelRPS);
				lastVisionRPS = aimVelRPS;
			} else {
				aimVelRPS = lastVisionRPS;
			}
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
		if (Controls.operatorController.getAButton()) {
			this.setShooter(.45);
		} else if (Controls.operatorController.getXButton()) {
			this.setShooter(.5);
		} else if (Controls.operatorController.getYButton()) {
			this.setShooter(.55);
		} else {
			this.setShooter(0);
		}
	}

	public void testVel() {
		curVel = this.getRPS();
		if (Controls.operatorController.getYButton()) {
			aimVelRPS = NetworkTable.getTable("Climber").getNumber("k_TEST RPS", 0);
		} else {
			aimVelRPS = 0;
		}
	}

	/**
	 * Feed Forward with dynamic aims
	 */
	private void shootLikeRobie() {
		if (aimVelRPS == 0) {
		} else {
			if (curVel < aimVelRPS - maxAccRPS) {
				aimAccRPS = maxAccRPS;
				aimVelRPS = curVel + maxAccRPS;

			} else if (curVel > aimVelRPS + maxAccRPS) {
				aimAccRPS = 0;
				// aimVelRPS = curVel + maxAccRPS;
			} else {
				aimAccRPS = 0;
			}
		}
		// Send our target velocity to the mController
		if (this.getController() instanceof FeedForwardWithPIDController) {
			((FeedForwardWithPIDController) this.getController()).setAimAcc(aimAccRPS);
			((FeedForwardWithPIDController) this.getController()).setAimVel(aimVelRPS);
		}
		double output = this.getController().getOutputSignal(this.getInputState()).getMotor();
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
			// System.out.println(aimVelRPS);
			// System.out.println(output);
			this.setShooter(output);
		}
	}

	@Override
	public InputState getInputState() {
		InputState input = new InputState();
		input.setError(aimVelRPS - curVel);
		return input;
	}

	@Dashboard(displayName = "Percent")
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
		getController().sendToSmartDash();
		DashboardHelper.updateTunable(this.getController());
		SmartDashboard.putNumber(this.getName() + " RPM", curVel * 60);
		table.putNumber(this.getName() + " RPS", getRPS());
		table.putNumber(this.getName() + " Goal", this.aimVelRPS);
		table.putNumber(this.getName() + " Left", leftTalon.getDesiredOutput());

		SmartDashboard.putNumber(this.getName() + " Right", rightTalon.getDesiredOutput());
	}

	@Dashboard(displayName = "rps")
	private double getRPS() {
		// System.out.println(Sensors.getFlywheelRPS());
		return Sensors.getFlywheelRPS();
	}

	@Dashboard(displayName = "aimVel")
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

	@Dashboard(displayName = "isShooterInRange")
	public boolean isShooterInRange() {
		if (this.getRPS() < aimVelRPS + 6 && this.getRPS() > aimVelRPS - 6)
			return true;
		return false;
	}

	@Dashboard(displayName = "RPM")
	private double getRPM() {
		return 60 * Sensors.getFlywheelRPS();
	}

	private void setShooter(double power) {
		leftTalon.setDesiredOutput(power);
		rightTalon.setDesiredOutput(power);
	}

	@Override
	public String getTableName() {
		return "Flywheel";
	}

	@Override
	public String getObjectName() {
		return "";
	}

}
