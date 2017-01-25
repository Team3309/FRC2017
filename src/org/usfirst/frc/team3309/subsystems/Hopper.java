package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;

public class Hopper extends ControlledSubsystem {

	public Hopper(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateTeleop() {
		boolean operatorXButton = Controls.operatorController.getXButton();
		if (operatorXButton) {
			setHopper(1);
		} else {
			setHopper(0);
		}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void manualControl() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	public void setHopper(double power) {

	}
}
