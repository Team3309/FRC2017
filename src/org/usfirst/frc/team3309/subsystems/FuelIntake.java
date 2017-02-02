package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class FuelIntake extends KragerSystem {

	private double MIN_VALUE_TO_MOVE = .15;
	public static FuelIntake instance;
	private TalonSRXMC fuel = new TalonSRXMC(RobotMap.FUEL_INTAKE_ID);

	public static FuelIntake getInstance() {
		if (instance == null)
			instance = new FuelIntake("FuelIntake");
		return instance;
	}

	private FuelIntake(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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

	public void setFuelIntake(double power) {

	}

}
