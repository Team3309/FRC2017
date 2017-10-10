package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerMath;
import org.team3309.lib.KragerSystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class FuelIntake extends KragerSystem {

	private double MIN_VALUE_TO_MOVE = .15;
	public static FuelIntake instance;
	private TalonSRXMC fuel = new TalonSRXMC(RobotMap.FUEL_INTAKE_ID);
	private KragerTimer intakeTimer = new KragerTimer();

	public static FuelIntake getInstance() {
		if (instance == null)
			instance = new FuelIntake();
		return instance;
	}

	private FuelIntake() {
		super("FuelIntake");
		intakeTimer.start();
	}
	
	
	@Override
	public void updateTeleop() {

		double driverRightTrigger = Controls.driverController.getTriggerAxis(Hand.kRight);
		double driverLeftTrigger = Controls.driverController.getTriggerAxis(Hand.kLeft);
		double operatorRightTrigger = Controls.operatorController.getTriggerAxis(Hand.kRight);
		double operatorLeftTrigger = Controls.operatorController.getTriggerAxis(Hand.kLeft);
		if (Controls.operatorController.getXButton() && intakeTimer.get() > 1) {
			// if (GearIntake.getInstance().)
			this.setFuelIntake(.9);
		} else if (Controls.operatorController.getXButton()) {

		} else if (driverRightTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(driverRightTrigger);
		} else if (driverLeftTrigger > MIN_VALUE_TO_MOVE) {
			setFuelIntake(-driverLeftTrigger);
		} /*
			 * else if (operatorRightTrigger > MIN_VALUE_TO_MOVE) {
			 * setFuelIntake(operatorRightTrigger); } else if
			 * (operatorLeftTrigger > MIN_VALUE_TO_MOVE) {
			 * setFuelIntake(-operatorLe ftTrigger); }
			 */else {
			intakeTimer.reset();
			setFuelIntake(KragerMath.threshold(-Controls.operatorController.getY(Hand.kLeft)));
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
		this.setFuelIntake(.25);
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
