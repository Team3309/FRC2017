package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

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
		// this.hopperMC.getTalon().reverseOutput(true);
	}

	private Timer timer = new Timer();

	@Override
	public void updateTeleop() {
		boolean operatorBButton = Controls.operatorController.getXButton(); // sort
		if (Shooter.getInstance().isShouldBeSpinningUp())
			vibrate.set(1);
		else
			vibrate.set(0);
		if (Shooter.getInstance().isShouldBeShooting()) {
			setHopper(1);
		} else if (operatorBButton) {
			setHopper(-.5);
		} else {
			setHopper(0);
		}
	}

	@Override
	public void updateAuto() {
		if (Shooter.getInstance().isShouldBeShooting()) {
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
		timer.start();
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	public void setHopper(double power) {
		hopperMC.setDesiredOutput(-power);

	}
}
