package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;

public class Climber extends KragerSystem {

	private static Climber instance;
	private final double UP_POWER = .8;

	public static Climber getInstance() {
		if (instance == null) {
			instance = new Climber("Climber");
		}
		return instance;
	}

	private Climber(String name) {
		super(name);
	}

	@Override
	public void updateTeleop() {
		boolean operatorStartButton = Controls.operatorController.getStartButton();
		boolean operatorBackButton = Controls.operatorController.getBackButton();
		if (operatorStartButton) {
			setClimber(UP_POWER);
		} else if (operatorBackButton) {
			setClimber(-1);
		} else {
			setClimber(0);
		}
	}

	@Override
	public void updateAuto() {
		// NO CLIMBING IN AUTO
	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// NO CLIMBING IN AUTO
	}

	@Override
	public void sendToSmartDash() {
	}

	@Override
	public void manualControl() {
		updateTeleop();
	}

	public void setClimber(double power) {

	}

}
