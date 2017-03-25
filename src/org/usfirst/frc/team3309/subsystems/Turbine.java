package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

import edu.wpi.first.wpilibj.Spark;

public class Turbine extends ControlledSubsystem {

	private static Turbine instance;
	private TalonSRXMC hopperMC = new TalonSRXMC(RobotMap.HOPPER_ID);
	private Spark vibrate = new Spark(2);

	public static Turbine getInstance() {
		if (instance == null) {
			instance = new Turbine();
		}
		return instance;
	}

	private Turbine() {
		super("Turbine");
	}

	@Override
	public void updateTeleop() {
		boolean operatorXButton = Controls.operatorController.getXButton(); // sort
		if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
			setHopper(1);
		} else if (operatorXButton) {
			setHopper(-.5);
		} else {
			setHopper(0);
		}
	}

	@Override
	public void updateAuto() {
		if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
			setHopper(1);
		} else {
			setHopper(0);
		}
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
		hopperMC.set(-power);
		if (power != 0) {
			this.vibrate.set(1);
		} else {
			this.vibrate.set(0);
		}
	}
}
