package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

public class Hopper extends ControlledSubsystem {

	private static Hopper instance;
	private TalonSRXMC hopper = new TalonSRXMC(RobotMap.HOPPER_ID);

	public static Hopper getInstance() {
		if (instance == null) {
			instance = new Hopper();
		}
		return instance;
	}

	private Hopper() {
		super("Hopper");
	}

	@Override
	public void updateTeleop() {
		boolean operatorBButton = Controls.operatorController.getBButton(); // sort
		if (operatorBButton || Shooter.getInstance().isShouldBeShooting()) {
			setHopper(1);
		} else {
			setHopper(0);
		}
	}

	@Override
	public void updateAuto() {
		updateTeleop();

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
		updateTeleop();

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
		hopper.setDesiredOutput(power);
	}
}
