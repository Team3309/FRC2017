package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class GearIntake extends KragerSystem {

	private static final double MIN_VALUE_TO_MOVE = .15;
	private static final double UP_POSITION = -.37;

	private static final double DOWN_POSITION = .07;
	private static GearIntake instance;
	private boolean hasChangedForThisPress = false;
	private CANTalon gearIntake = new CANTalon(RobotMap.GEAR_INTAKE_ID);
	private DoubleSolenoid intakePivot = new DoubleSolenoid(RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_A,
			RobotMap.GEAR_INTAKE_PIVOT_SOLENOID_B);
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
	}

	@Override
	public void updateTeleop() {
		// boolean driverStart = Controls.driverController.getStartButton();
		boolean operatorLB = Controls.operatorController.getBumper(Hand.kLeft);
		// boolean driverSelect = Controls.driverController.getBackButton();
		boolean operatorRB = Controls.operatorController.getBumper(Hand.kRight);

		if (operatorRB) {
			pivotDownGearIntake();
		} else {
			pivotUpGearIntake();
		}

		if (operatorLB) {
			this.gearIntake.set(1);
		} else {
			this.gearIntake.set(.05);
		}
	}

	public void pivotDownGearIntake() {
		this.intakePivot.set(Value.kForward);
	}

	public void pivotUpGearIntake() {
		this.intakePivot.set(Value.kReverse);
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
}