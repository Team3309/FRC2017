package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.ArrayList;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.VisionServer;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Flywheel extends ControlledSubsystem {

	private TalonSRXMC leftTalon = new TalonSRXMC(RobotMap.LEFT_SHOOTER_ID);
	private TalonSRXMC rightTalon = new TalonSRXMC(RobotMap.RIGHT_SHOOTER_ID);

	private double maxAccRPS = 60.0;
	private double aimVelRPS = 0.0;
	private double currentAimVelRPS = 0;
	private ArrayList<Double> averageGoalRPSArray = new ArrayList<Double>();

	public double getCurrentAimVelRPS() {
		return currentAimVelRPS;
	}

	public void setCurrentAimVelRPS(double currentAimVelRPS) {
		this.currentAimVelRPS = currentAimVelRPS;
	}

	
	
	
	private double aimAccRPS = 0.0;
	private double curVel = 0;
	private double lastVisionRPS = 0;
	private double offset = 0;
	private boolean isPressedAlready = false;
	private boolean isFirstFilter = true;
	private double maxAcc = 3;
	private NetworkTable table = NetworkTable.getTable("Flywheel");

	/**
	 * Shooter for singleton pattern
	 */
	private static Flywheel mFlywheel;

	private Flywheel() {
		super("Flywheel");
		this.setController(new FeedForwardWithPIDController(0.0038, 0, 0.025, 0.0000005, 0.00)); // old
																									// values
																									// 0.0038,
																									// 0,
																									// 0.025,
																									// 0.0000005,
																									// 0.00
		this.getController().setName("Flywheel Speed");
		this.rightTalon.reverseOutput(true);
		this.leftTalon.reverseSensor(true);

		this.leftTalon.enable();
		this.rightTalon.enable();

		this.leftTalon.enableBrakeMode(false);
		this.rightTalon.enableBrakeMode(false);

		((FeedForwardWithPIDController) this.getController()).setTHRESHOLD(10);
		NetworkTable.getTable("Climber").putNumber("k_TEST RPS", 140);
		this.leftTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		this.leftTalon.configEncoderCodesPerRev(12);
		this.leftTalon.configPeakOutputVoltage(12, -12);
		this.leftTalon.configNominalOutputVoltage(0, 0);
		this.rightTalon.configPeakOutputVoltage(12, -12);
		this.rightTalon.configNominalOutputVoltage(0, 0);
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

	private void talonSet(double goal) {
		if (goal == 0) {
			this.leftTalon.changeControlMode(TalonControlMode.PercentVbus);
			this.rightTalon.changeControlMode(TalonControlMode.PercentVbus);
			this.leftTalon.set(0);
			this.rightTalon.set(0);
			this.currentAimVelRPS = this.getRPS();
		} else {
			double acc = maxAcc;
			if (this.curVel > 35) {
				this.currentAimVelRPS = goal;
			} else if (this.currentAimVelRPS < goal - 10) {
				this.currentAimVelRPS += acc;
			} else if (this.currentAimVelRPS > goal + 10) {
				this.currentAimVelRPS -= acc;
			} else {
				this.currentAimVelRPS = this.aimVelRPS;
			}
			this.leftTalon.changeControlMode(TalonControlMode.Speed);
			this.rightTalon.changeControlMode(TalonControlMode.Follower);
			this.rightTalon.set(RobotMap.LEFT_SHOOTER_ID);
			this.leftTalon.set(currentAimVelRPS * 15.0 * (24.0 / 16.0));
		}
	}

	@Override
	public void initTeleop() {

	}

	@Override
	public void initAuto() {
		lastVisionRPS = 155;
	}

	@Override
	public void updateAuto() {
		maxAcc = 50;
		curVel = this.getRPS();
		if (Shooter.getInstance().isShouldBeShooting()) {
			// aimVelRPS = 180;

			if (VisionServer.getInstance().hasTargetsToAimAt()) {
				double curAimVelRPS = VisionServer.getInstance().getRPS();
				// if (Math.abs(curAimVelRPS - lastVisionRPS) > 10 &&
				// isFirstFilter) {
				// curAimVelRPS = lastVisionRPS;
				// }
				averageGoalRPSArray.add(curAimVelRPS);
				System.out.println("cur Vision val " + curAimVelRPS);
				if (averageGoalRPSArray.size() > 50) {
					averageGoalRPSArray.remove(0);
				}
				double sum = 0;
				for (double x : averageGoalRPSArray) {
					sum += x;
				}

				System.out.println("sume " + sum + " size " + averageGoalRPSArray.size());
				aimVelRPS = ((double) sum) / ((double) averageGoalRPSArray.size());
				isFirstFilter = false;
				System.out.println("FLYWHEEL VISION AIM " + aimVelRPS + " cur " + this.getRPS());
				System.out.println("elevator " + Elevator.getInstance().getSpeed());
				if (VisionServer.getInstance().getTarget().getHyp() < 0) {
					aimVelRPS = 155;
				}
				lastVisionRPS = aimVelRPS;
			} else {
				isFirstFilter = true;
				aimVelRPS = lastVisionRPS;
			}
		} else if (Shooter.getInstance().isShouldBeSpinningUp()) {
			aimVelRPS = 130;

		} else {
			aimVelRPS = 0;
			isFirstFilter = true;
		}
		talonSet(aimVelRPS + 1);
		// shootLikeRobie();
	}

	@Override
	public void updateTeleop() {
		curVel = this.getRPS();
		maxAcc = 70;
		if (Controls.operatorController.getPOV() == 180 && !isPressedAlready) {
			offset -= 1;
			isPressedAlready = true;
		} else if (Controls.operatorController.getPOV() == 0 && !isPressedAlready) {
			offset += 1;
			isPressedAlready = true;
		} else if (Controls.operatorController.getPOV() == 0 || Controls.operatorController.getPOV() == 180) {

		} else {
			isPressedAlready = false;
		}
		System.out.println(this.getRPS());
		// Find our base aim vel
		// System.out.println("constnats: " + ((FeedForwardWithPIDController)
		// this.getController()).getkV() + " kP "
		// + ((FeedForwardWithPIDController) this.getController()).kP);
		if (Controls.operatorController.getYButton() || Controls.driverController.getYButton()) {
			this.testVel();
		} else if (Controls.operatorController.getAButton()) {
			aimVelRPS = 75;
			this.leftTalon.configPeakOutputVoltage(7, -7);
			this.rightTalon.configPeakOutputVoltage(7, -7);
			Shooter.getInstance().setShouldBeShooting(true);
			Shooter.getInstance().setShouldBeSpinningUp(true);

		} else if (Controls.operatorController.getBButton()) {
			this.leftTalon.configPeakOutputVoltage(12, -12);
			this.rightTalon.configPeakOutputVoltage(12, -12);
			if (VisionServer.getInstance().hasTargetsToAimAt()) {

				double curAimVelRPS = VisionServer.getInstance().getRPS();
				// if (Math.abs(curAimVelRPS - lastVisionRPS) > 10 &&
				// isFirstFilter) {
				// curAimVelRPS = lastVisionRPS;
				// }
				averageGoalRPSArray.add(curAimVelRPS);
				System.out.println("cur Vision val " + curAimVelRPS);
				if (averageGoalRPSArray.size() > 50) {
					averageGoalRPSArray.remove(0);
				}
				double sum = 0;
				for (double x : averageGoalRPSArray) {
					sum += x;
				}
				System.out.println("sume " + sum + " size " + averageGoalRPSArray.size());
				aimVelRPS = ((double) sum) / ((double) averageGoalRPSArray.size());
				isFirstFilter = false;
				System.out.println("FLYWHEEL VISION AIM " + aimVelRPS);
				System.out.println("elevator " + Elevator.getInstance().getSpeed());
				lastVisionRPS = aimVelRPS;
				isFirstFilter = false;
				System.out.println("FLYWHEEL VISION AIM " + aimVelRPS);
				lastVisionRPS = aimVelRPS;
			} else {
				isFirstFilter = true;
				aimVelRPS = lastVisionRPS;
			}
		} else {
			this.leftTalon.configPeakOutputVoltage(12, -12);
			this.rightTalon.configPeakOutputVoltage(12, -12);
			offset = 0;
			aimVelRPS = 0;
			aimAccRPS = 0;
			averageGoalRPSArray = new ArrayList<Double>();
		}
		aimVelRPS += offset;

		// System.out.println("SENSOR STATUS " +
		// this.leftTalon.isSensorPresent(FeedbackDevice.QuadEncoder));
		// shootLikeRobie();
		talonSet(aimVelRPS);
	}

	// ANGLE CLOCKWISE POSITIVE

	// NEGATIVE POWERGF
	/**
	 * Raw power values
	 */
	public void manualControl() {
		this.leftTalon.changeControlMode(TalonControlMode.PercentVbus);
		this.rightTalon.changeControlMode(TalonControlMode.PercentVbus);
		if (Controls.operatorController.getAButton()) {
			this.setShooter(.05);
		} else if (Controls.operatorController.getXButton()) {
			this.setShooter(.5);
		} else if (Controls.operatorController.getYButton()) {
			this.setShooter(.55);
			Shooter.getInstance().setShouldBeShooting(true);
		} else {
			Shooter.getInstance().setShouldBeShooting(false);
			this.setShooter(0);
		}
	}

	public void testVel() {
		curVel = this.getRPS();
		if (Controls.operatorController.getYButton()) {
			aimVelRPS = NetworkTable.getTable("Climber").getNumber("k_TEST RPS", 75);
		} else {
			aimVelRPS = 0;
		}
	}

	private void bangBang() {
		if (this.curVel < aimVelRPS)
			this.setShooter(1);
		else
			this.setShooter(0);
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
				// aimVelRPS = curVel F+ maxAccRPS;
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
				output = .65;
			}
			// System.out.println(aimVelRPS);
			// System.out.println(output);.
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
		getController().sendToSmartDash();
		SmartDashboard.putNumber(this.getName() + " RPS", curVel);
		table.putNumber(this.getName() + " RPS", getRPS());
		table.putNumber(this.getName() + " Goal", this.aimVelRPS);
		table.putNumber("goal", this.leftTalon.getSetpoint());
		table.putNumber("rpm reading", leftTalon.getSpeed());
		table.putNumber("banner", Sensors.rawRPS);
		table.putNumber("rps goal", getAimVelRPS());
		NetworkTable.getTable("Climber").putNumber("rps", getRPS());
		NetworkTable.getTable("Climber").putNumber("aim rps", getAimVelRPS());
		NetworkTable.getTable("Climber").putNumber("rps error ", getAimVelRPS() - getRPS());
	}

	public double getRPS() {
		// System.out.println(Sensors.getFlywheelRPS());
		return ((this.leftTalon.getSpeed() * 4.0) / 60.0) * (16.0 / 24.0);
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

		if (this.getRPS() < aimVelRPS + 10 && this.getRPS() > aimVelRPS - 15)
			return true;
		return false;
	}

	private double getRPM() {
		return 60 * Sensors.getFlywheelRPS();
	}

	private void setShooter(double power) {
		leftTalon.set(power);
		rightTalon.set(-power);
	}

	public void resetVisionVals() {
		isFirstFilter = true;
	}
}
