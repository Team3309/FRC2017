package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;

public class Elevator extends ControlledSubsystem {

	public Elevator(String name) {
		super(name);
		this.teleopController = new FeedForwardWithPIDController(0, 0, 0, 0, 0);
		this.autoController = new FeedForwardWithPIDController(0, 0, 0, 0, 0);
	}

	@Override
	public void updateTeleop() {
		
	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {
		this.teleopController.sendToSmartDash();

	}

	@Override
	public void manualControl() {

	}

	@Override
	public void initTeleop() {

	}

	@Override
	public void initAuto() {

	}

}
