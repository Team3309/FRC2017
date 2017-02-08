package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class Elevator extends ControlledSubsystem {

	private final double STAGING_VELOCITY = 2;
	private final double SHOOTING_VELOCITY = 10;
	private double aimVel = 0;
	private CANTalon elevator = new CANTalon(RobotMap.ELEVATOR_ID);
	private static Elevator instance;

	public static Elevator getInstance() {
		if (instance == null)
			instance = new Elevator("Elevator");
		return instance;
	}

	private Elevator(String name) {
		super(name);
		elevator.setFeedbackDevice(FeedbackDevice.QuadEncoder); 
		elevator.changeControlMode(TalonControlMode.Speed);
	}

	@Override
	public void initAuto() {
		System.out.println("I would like to be a marketing director at seven eleven.");
	}

	@Override
	public void initTeleop() {
		System.out.println("I would like to be a control systems engineer at mcdonalds");
	}

	@Override
	public void updateTeleop() {
		boolean operatorXButton = Controls.operatorController.getXButton(); // sort
		if (operatorXButton) {
			aimVel = STAGING_VELOCITY;
		} else if (Shooter.getInstance().isShouldBeShooting()) {
			aimVel = SHOOTING_VELOCITY;
		} else {
			aimVel = 0;
		}
		OutputSignal out = this.teleopController.getOutputSignal(getInputState());
		setElevator(out.getMotor());
	}

	@Override
	public void updateAuto() {
		updateTeleop();
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setError(aimVel - elevator.getEncVelocity());
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.teleopController.sendToSmartDash();

	}

	@Override
	public void manualControl() {

	}

	private void setElevator(double power) {
		this.elevator.set(power);
	}

}
