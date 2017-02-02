package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

public class Elevator extends ControlledSubsystem {

	private final double STAGING_VELOCITY = 2;
	private final double SHOOTING_VELOCITY = 10;
	private double aimVel = 0;
	private TalonSRXMC elevator = new TalonSRXMC(RobotMap.ELEVATOR_ID);
	private static Elevator instance;

	public static Elevator getInstance() {
		if (instance == null)
			instance = new Elevator("Elevator");
		return instance;
	}

	private Elevator(String name) {
		super(name);
		this.teleopController = new FeedForwardWithPIDController(0, 0, 0, 0, 0);
		this.autoController = new FeedForwardWithPIDController(0, 0, 0, 0, 0);
	}

	@Override
	public void initAuto() {

	}

	@Override
	public void initTeleop() {

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
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setError(aimVel - elevator.getVelocity());
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
		this.elevator.setDesiredOutput(power);
	}

}
