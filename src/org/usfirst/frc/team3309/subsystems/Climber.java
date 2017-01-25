package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;

public class Climber extends KragerSystem {
	
	private static Climber instance;

	public static Climber getInstance() {
		if (instance == null) {
			instance = new Climber("Climber");
		}
		return instance;
	}
	
	private Climber(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateTeleop() {
		boolean operatorStartButton = Controls.operatorController.getStartButton();
		boolean operatorBackButton = Controls.operatorController.getBackButton();
		if (operatorStartButton) {
			setClimber(1);
		} else if (operatorBackButton) {
			setClimber(-1);
		} else {
			setClimber(0);
		}
	}

	
	@Override
	public void updateAuto() {
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

	@Override
	public void sendToSmartDash() {
		// TODO Auto-generated method stub

	}

	@Override
	public void manualControl() {
		// TODO Auto-generated method stub

	}

	public void setClimber(double power) {
	
	}
	
}

