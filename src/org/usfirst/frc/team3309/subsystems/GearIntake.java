package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.tunable.Dashboard;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class GearIntake extends KragerSystem {

	private static final double MIN_VALUE_TO_MOVE = .15;
	private static GearIntake instance;
	private boolean hasChangedForThisPress = false;
	private DoubleSolenoid gearIntakePivot = new DoubleSolenoid(RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_A,
			RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_B);
	private DoubleSolenoid gearIntakeWrist = new DoubleSolenoid(RobotMap.GEAR_INTAKE_WRIST_SOLENOID_A,
			RobotMap.GEAR_INTAKE_WRIST_SOLENOID_B);
	private TalonSRXMC gearIntake = new TalonSRXMC(RobotMap.GEAR_INTAKE_ID);

	public static GearIntake getInstance() {
		if (instance == null) {
			instance = new GearIntake();
		}
		return instance;
	}

	public GearIntake() {
		super("GearIntake");
	}

	@Override
	public void updateTeleop() {
		boolean driverStart = Controls.driverController.getStartButton();
		boolean operatorLB = Controls.operatorController.getBumper(Hand.kLeft);
		boolean driverSelect = Controls.driverController.getBackButton();
		boolean operatorRB = Controls.operatorController.getBumper(Hand.kRight);

		if ((driverStart || operatorRB) && !hasChangedForThisPress) {
			this.extendPivot();
			this.extendWrist();
			this.setGearIntakeRoller(1);
		} else if ((driverStart || operatorLB)) {

		} else {
			this.retractPivot();
			this.retractWrist();
			this.setGearIntakeRoller(0);
			hasChangedForThisPress = false;
		}

		if (operatorLB) {
			this.setGearIntakeRoller(-1);
		}

		// // only use driver inputs if the gear mechanism is extended
		// double operatorRightTrigger =
		// Controls.operatorController.getTriggerAxis(Hand.kRight);
		// double operatorLeftTrigger =
		// Controls.operatorController.getTriggerAxis(Hand.kLeft);
		// double driverRightTrigger =
		// Controls.driverController.getTriggerAxis(Hand.kRight);
		// double driverLeftTrigger =
		// Controls.driverController.getTriggerAxis(Hand.kLeft);
		// if (driverRightTrigger > MIN_VALUE_TO_MOVE && this.isPivotExtended())
		// {
		// setGearIntakeRoller(driverRightTrigger);
		// } else if (driverLeftTrigger > MIN_VALUE_TO_MOVE &&
		// this.isPivotExtended()) {
		// setGearIntakeRoller(-driverLeftTrigger);
		// } else if (operatorRightTrigger > MIN_VALUE_TO_MOVE) {
		// setGearIntakeRoller(operatorRightTrigger);
		// } else if (operatorLeftTrigger > MIN_VALUE_TO_MOVE) {
		// setGearIntakeRoller(-operatorLeftTrigger);
		// } else {
		// setGearIntakeRoller(0);
		// }
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

	public void setGearIntakeRoller(double power) {
		gearIntake.setDesiredOutput(power);
	}

	@Dashboard(displayName = "GearIntakeRollerPower")
	public double getGearIntakeRollerPower() {
		return gearIntake.getDesiredOutput();
	}

	public void retractPivot() {
		gearIntakePivot.set(Value.kReverse);
	}

	public void extendPivot() {
		gearIntakePivot.set(Value.kForward);
	}

	public void togglePivot() {
		if (gearIntakePivot.get() == Value.kForward)
			gearIntakePivot.set(Value.kReverse);
		else
			gearIntakePivot.set(Value.kForward);
	}

	public boolean isPivotRetracted() {
		return gearIntakeWrist.get() == Value.kReverse;
		// false = retracted, so flip the output
	}

	@Dashboard(displayName = "isPistonExtended")
	public boolean isPivotExtended() {
		return gearIntakeWrist.get() == Value.kForward;// true = extended
	}

	public void retractWrist() {
		gearIntakeWrist.set(Value.kReverse);
	}

	public void extendWrist() {
		gearIntakeWrist.set(Value.kForward);
	}

	public void toggleWrist() {
		if (gearIntakeWrist.get() == Value.kForward)
			gearIntakeWrist.set(Value.kReverse);
		else
			gearIntakeWrist.set(Value.kForward);
	}

	public boolean isWristRetracted() {
		return gearIntakeWrist.get() == Value.kReverse; // false = retracted, so
														// flip the output
	}

	@Dashboard(displayName = "isWristExtended")
	public boolean isWristExtended() {
		return gearIntakeWrist.get() == Value.kForward; // true = extended
	}

}
