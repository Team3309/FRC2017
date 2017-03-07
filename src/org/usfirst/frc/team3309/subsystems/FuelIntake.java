package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;

public class FuelIntake extends KragerSystem {

	private double MIN_VALUE_TO_MOVE = .15;
	public static FuelIntake instance;
	private TalonSRXMC fuel = new TalonSRXMC(RobotMap.FUEL_INTAKE_ID);

	public static FuelIntake getInstance() {
		if (instance == null)
			instance = new FuelIntake();
		return instance;
	}

	private FuelIntake() {
		super("FuelIntake");
	}

	Timer t = new Timer();

	@Override
	public void updateTeleop() {
		// if (Shooter.getInstance().isShouldBeShooting()) {
		// if (t.get() > 2)
		// this.t.reset();
		// else if (t.get() < .75)
		// this.fuel.setDesiredOutput(1);
		// else if (t.get() < 1.25)
		// this.fuel.setDesiredOutput(0);
		// else if (t.get() < 2)
		// this.fuel.setDesiredOutput(-1);
		// return;
		//
		// }
		double driverRightTrigger = Controls.driverController.getTriggerAxis(Hand.kRight);
		double driverLeftTrigger = Controls.driverController.getTriggerAxis(Hand.kLeft);
		double operatorRightTrigger = Controls.operatorController.getTriggerAxis(Hand.kRight);
		double operatorLeftTrigger = Controls.operatorController.getTriggerAxis(Hand.kLeft);
		if (driverRightTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(driverRightTrigger);
		} else if (driverLeftTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(-driverLeftTrigger);
		} else if (operatorRightTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(operatorRightTrigger);
		} else if (operatorLeftTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(-operatorLeftTrigger);
		} else {
			setFuelIntake(0);
		}
	}

	@Override
	public void updateAuto() {
		// use the setFuelIntake method to set

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
		t.start();
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	public void setFuelIntake(double power) {
		this.fuel.setDesiredOutput(power);
	}

}
