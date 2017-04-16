package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class GearIntake extends KragerSystem {

	private static final double MIN_VALUE_TO_MOVE = .15;
	private static final double UP_POSITION = -.37;
	private boolean hasZippedInwards = true;
	public KragerTimer intakeTimer = new KragerTimer();
	private static final double DOWN_POSITION = .07;
	private static GearIntake instance;
	private TalonSRXMC gearIntake = new TalonSRXMC(RobotMap.GEAR_INTAKE_ID);
	private DoubleSolenoid intakePivot = new DoubleSolenoid(RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_A,
			RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_B);
	private DoubleSolenoid intakeClamp = new DoubleSolenoid(RobotMap.GEAR_INTAKE_WRIST_SOLENOID_A,
			RobotMap.GEAR_INTAKE_WRIST_SOLENOID_B);
	private NetworkTable table = NetworkTable.getTable("Intakes");

	public static GearIntake getInstance() {
		if (instance == null) {
			instance = new GearIntake();
		}
		return instance;
	}

	public GearIntake() {
		super("GearIntake");
		this.gearIntake.changeControlMode(TalonControlMode.PercentVbus);
		// this.gearIntake.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		this.gearIntake.setEncPosition(0);
		this.gearIntake.setPosition(0);
		this.gearIntake.setAnalogPosition(0);
		this.gearIntake.setPulseWidthPosition(0);
		this.gearIntake.setSetpoint(0);
		this.gearIntake.reverseOutput(true);
		intakeTimer.start();
	}

	@Override
	public void updateTeleop() {
		// boolean driverStart = Controls.driverController.getStartButton();
		boolean operatorLB = Controls.operatorController.getBumper(Hand.kLeft);
		// boolean driverSelect = Controls.driverController.getBackButton();
		boolean operatorRB = Controls.operatorController.getBumper(Hand.kRight);

		if (operatorRB || Controls.operatorController.getXButton()) {
			hasZippedInwards = false;
			pivotUpGearIntake();
		} else {
			if (!hasZippedInwards) {
				this.intakeTimer.reset();
				this.intakeTimer.start();
			}
			hasZippedInwards = true;
			pivotDownGearIntake();
		}

		if (Math.abs(Controls.operatorController.getTriggerAxis(Hand.kRight)) > .1) {
			this.setGearIntakeRoller(Controls.operatorController.getTriggerAxis(Hand.kRight));
		} else if (Math.abs(Controls.operatorController.getTriggerAxis(Hand.kLeft)) > .1) {
			this.setGearIntakeRoller(-Controls.operatorController.getTriggerAxis(Hand.kLeft) / 1.2);
		} else {
			if (this.isGearIntakeDown())
				this.setGearIntakeRoller(0);
			else
				this.setGearIntakeRoller(.15);
		}

		if (operatorLB) {
			closeGearIntake();
		} else {
			openGearIntake();
		}

	}

	public void closeGearIntake() {
		this.intakeClamp.set(Value.kForward);
	}

	public void openGearIntake() {
		this.intakeClamp.set(Value.kReverse);
	}

	public void pivotDownGearIntake() {
		this.intakePivot.set(Value.kReverse);
	}

	public void pivotUpGearIntake() {
		this.intakePivot.set(Value.kForward);
	}

	public boolean isGearIntakeDown() {
		return intakePivot.get() == Value.kReverse;
	}

	@Override
	public void updateAuto() {
		this.gearIntake.changeControlMode(TalonControlMode.PercentVbus);

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
		table.putNumber("raw angle", gearIntake.getEncPosition());
		table.putNumber("angle", getAngle());
		table.putNumber("goal", this.gearIntake.getSetpoint());
		table.putNumber("error", this.gearIntake.getError());
		table.putNumber("current", gearIntake.getOutputCurrent());
		table.putNumber("avgVolt", Sensors.getAverageGearVal());
	}

	@Override
	public void manualControl() {

		updateTeleop();
	}

	public void setGearIntakeRoller(double power) {

		gearIntake.set(power);
	}

	public double getGearIntakeRollerPower() {
		return gearIntake.get();
	}

	public double getAngle() {
		return gearIntake.getPosition();
	}

	public boolean hasGear() {
		return Sensors.hasGear();
	}
}