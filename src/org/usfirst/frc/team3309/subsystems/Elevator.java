package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Elevator extends ControlledSubsystem {

	private final double SHOOTING_VELOCITY = 9000;
	private double aimVel = 0;
	private CANTalon elevator = new CANTalon(RobotMap.ELEVATOR_ID);
	private CANTalon feedyWheel = new CANTalon(RobotMap.FEEDY_WHEEL_ID);
	private NetworkTable table = NetworkTable.getTable("Elevator");
	private static Elevator instance;

	public static Elevator getInstance() {
		if (instance == null)
			instance = new Elevator();
		return instance;
	}

	private Elevator() {
		super("Elevator");
		elevator.setFeedbackDevice(FeedbackDevice.QuadEncoder);

		this.elevator.changeControlMode(TalonControlMode.Speed);
		table.putNumber("k_aimVel", 0);
	}

	@Override
	public void initAuto() {
		this.elevator.changeControlMode(TalonControlMode.Speed);
		// this.elevator.changeControlMode(TalonControlMode.Speed);
	}

	@Override
	public void initTeleop() {
		this.elevator.changeControlMode(TalonControlMode.Speed);
	}

	@Override
	public void updateTeleop() {
		if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
			aimVel = SHOOTING_VELOCITY;
		} else {
			aimVel = 0;
		}
		elevator.changeControlMode(TalonControlMode.Speed);
		setElevator(aimVel);
	}

	@Override
	public void updateAuto() {
		if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
			aimVel = SHOOTING_VELOCITY;
		} else {
			aimVel = 0;
		}
		elevator.changeControlMode(TalonControlMode.Speed);
		setElevator(aimVel);
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setError(aimVel - elevator.getEncVelocity());
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.getController().sendToSmartDash();
		table.putNumber(this.getName() + " Vel", this.elevator.getEncVelocity());
		table.putNumber(this.getName() + " Pow", this.elevator.get());
		table.putNumber(this.getName() + " Error", this.elevator.getError());
		// System.out.println("state " +
		// this.elevator.isSensorPresent(FeedbackDevice.QuadEncoder));
	}

	@Override
	public void manualControl() {
		if (Controls.operatorController.getYButton()) {
			aimVel = .95;
		} else {
			aimVel = 0;
		}
		elevator.changeControlMode(TalonControlMode.PercentVbus);
		setElevator(aimVel);
	}

	private void setElevator(double power) {

		if (power == 0) {
			this.elevator.changeControlMode(TalonControlMode.PercentVbus);
			this.feedyWheel.set(0);
		} else {
			this.feedyWheel.set(1
					* KragerMath.sign(power));
		}
		this.elevator.set(power);
	}

}
