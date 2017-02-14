package org.usfirst.frc.team3309.subsystems;

import java.awt.geom.Ellipse2D;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends ControlledSubsystem {

	private final double STAGING_VELOCITY = 2;
	private final double SHOOTING_VELOCITY = 10;
	private double aimVel = 0;
	private CANTalon elevator = new CANTalon(RobotMap.ELEVATOR_ID);
	private CANTalon feedyWheel = new CANTalon(RobotMap.FEEDY_WHEEL_ID);
	private static Elevator instance;

	public static Elevator getInstance() {
		if (instance == null)
			instance = new Elevator();
		return instance;
	}

	private Elevator() {
		super("Elevator");
		// elevator.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		// elevator.changeControlMode(TalonControlMode.Speed);
		this.elevator.changeControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public void initAuto() {
		// this.elevator.changeControlMode(TalonControlMode.Speed);
	}

	@Override
	public void initTeleop() {
		this.elevator.changeControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public void updateTeleop() {
		if (Controls.operatorController.getBButton()) {
			aimVel = STAGING_VELOCITY;
		} else if (Shooter.getInstance().isShouldBeShooting()) {
			aimVel = SHOOTING_VELOCITY;
		} else {
			aimVel = 0;
		}
		setElevator(aimVel);
	}

	@Override
	public void updateAuto() {
		updateTeleop();
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		// s.setError(aimVel - elevator.getEncVelocity());
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.controller.sendToSmartDash();
		// SmartDashboard.putNumber(this.getName() + " Vel",
		// this.elevator.getEncPosition());
		// SmartDashboard.putNumber(this.getName() + " Pow",
		// this.elevator.getPosition());
	}

	@Override
	public void manualControl() {

		if (Controls.operatorController.getBumper(Hand.kRight)) {
			setElevator(1);
		} else if (Controls.operatorController.getBumper(Hand.kLeft)) {
			setElevator(-.5);
		} else {
			setElevator(0);
		}
	}

	private void setElevator(double power) {
		this.elevator.set(power);
		this.feedyWheel.set(power);
	}

}
