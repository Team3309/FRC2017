package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

public class Climber extends KragerSystem {

	private static Climber instance;
	private final double UP_POWER = .8;
	private TalonSRXMC climber775 = new TalonSRXMC(RobotMap.CLIMBER_ID);
	private final double CURRENT_LIMIT = 8;
	private int countsOfCurrentLimit = 0;

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
			if (hasHitTop())
				setClimber(UP_POWER);
			else
				setClimber(0);
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

	public boolean hasHitTop() {
		if (climber775.getTalon().getOutputCurrent() > CURRENT_LIMIT) {
			countsOfCurrentLimit++;
		} else {
			countsOfCurrentLimit = 0;
		}
		return countsOfCurrentLimit > 10;
	}

	public void setClimber(double power) {
		climber775.setDesiredOutput(power);
	}

}
