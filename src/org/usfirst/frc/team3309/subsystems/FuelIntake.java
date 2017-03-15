package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;

public class FuelIntake extends KragerSystem {

	private double MIN_VALUE_TO_MOVE = .15;
	public static FuelIntake instance;
	private CANTalon fuel = new CANTalon(RobotMap.FUEL_INTAKE_ID);

	public static FuelIntake getInstance() {
		if (instance == null)
			instance = new FuelIntake();
		return instance;
	}

	private FuelIntake() {
		super("FuelIntake");
	}

	@Override
	public void updateTeleop() {

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

	}

	@Override
	public void manualControl() {
		updateTeleop();

	}

	@Override
	public void initTeleop() {

	}

	@Override
	public void initAuto() {

	}

	public void setFuelIntake(double power) {
		this.fuel.set(power);
	}

}
