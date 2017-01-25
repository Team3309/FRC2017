package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Solenoid;

public class GearIntake extends KragerSystem {

	private static final double MIN_VALUE_TO_MOVE = .15;
	private static GearIntake instance;
	private boolean hasChangedForThisPress = false;
	private Solenoid gearIntakePivot = new Solenoid(RobotMap.GEAR_INTAKE_SOLENOID);

	public static GearIntake getInstance() {
		if (instance == null) {
			instance = new GearIntake("GearIntake");
		}
		return instance;
	}

	public GearIntake(String name) {
		super(name);
	}

	@Override
	public void updateTeleop() {
		boolean driverStart = Controls.driverController.getStartButton();
		boolean operatorLB = Controls.operatorController.getBumper(Hand.kLeft);
		if ((driverStart || operatorLB) && !hasChangedForThisPress) {
			hasChangedForThisPress = true;
			toggleGearIntake();
		} else if ((driverStart || operatorLB)) {

		} else {
			hasChangedForThisPress = false;
		}
		// only use driver inputs if the gear mechanism is extended
		double operatorRightTrigger = Controls.operatorController.getTriggerAxis(Hand.kRight);
		double operatorLeftTrigger = Controls.operatorController.getTriggerAxis(Hand.kLeft);
		double driverRightTrigger = Controls.driverController.getTriggerAxis(Hand.kRight);
		double driverLeftTrigger = Controls.driverController.getTriggerAxis(Hand.kLeft);
		if (driverRightTrigger > MIN_VALUE_TO_MOVE && this.isExtended()) {
			setGearIntakeRoller(driverRightTrigger);
		} else if (driverLeftTrigger > MIN_VALUE_TO_MOVE && this.isExtended()) {
			setGearIntakeRoller(-driverLeftTrigger);
		} else if (operatorRightTrigger > MIN_VALUE_TO_MOVE) {
			setGearIntakeRoller(operatorRightTrigger);
		} else if (operatorLeftTrigger > MIN_VALUE_TO_MOVE) {
			setGearIntakeRoller(-operatorLeftTrigger);
		} else {
			setGearIntakeRoller(0);
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

	}

	@Override
	public void manualControl() {
		updateTeleop();
	}

	public void retractGearIntake() {
		gearIntakePivot.set(false);
	}

	public void extendGearIntake() {
		gearIntakePivot.set(true);
	}

	public void toggleGearIntake() {
		gearIntakePivot.set(!gearIntakePivot.get());
	}

	public void setGearIntakeRoller(double power) {

	}

	public boolean isRetracted() {
		return !gearIntakePivot.get(); // false = retracted, so flip the output
	}

	public boolean isExtended() {
		return gearIntakePivot.get(); // true = extended
	}

}
