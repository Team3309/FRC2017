package org.team3309.lib.controllers.generic;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.tunable.Dashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FeedForwardWithPIDController extends PIDController {
	@Dashboard(displayName = "kA", tunable = true)
	private double kA = 0.0;
	@Dashboard(displayName = "kV", tunable = true)
	private double kV = 0.0;
	private double aimAcc = 0.0;
	@Dashboard(displayName = "aimVel")
	private double aimVel = 0.0;

	/**
	 * 
	 * @param kV
	 * @param kA
	 * @param kP
	 * @param kI
	 * @param kD
	 */
	public FeedForwardWithPIDController(double kV, double kA, double kP, double kI, double kD) {
		super(kP, kI, kD);
		this.kA = kA;
		this.kV = kV;
	}

	/**
	 * 
	 * @param kV
	 * @param kA
	 * @param kP
	 * @param kI
	 * @param kD
	 * @param kILimit
	 */
	public FeedForwardWithPIDController(double kV, double kA, double kP, double kI, double kD, double kILimit) {
		super(kP, kI, kD, kILimit);
		this.kA = kA;
		this.kV = kV;
	}

	public void setConstants(double kV, double kA, double kP, double kI, double kD) {
		super.setConstants(kP, kI, kD);
		this.kV = kV;
		this.kA = kA;
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double power = super.getOutputSignal(inputState).getMotor() + this.kA * aimAcc + this.kV * aimVel;
		OutputSignal signal = new OutputSignal();
		signal.setMotor(power);
		return signal;
	}

	@Override
	public boolean isCompleted() {
		if (this.completable) {
			return super.isCompleted();
		} else {
			return false;
		}
	}

	public double getkA() {
		return kA;
	}

	public void setkA(double kA) {
		this.kA = kA;
	}

	public double getkV() {
		return kV;
	}

	public void setkV(double kV) {
		this.kV = kV;
	}

	public double getAimAcc() {
		return aimAcc;
	}

	public void setAimAcc(double aimAcc) {
		this.aimAcc = aimAcc;
	}

	public double getAimVel() {
		return aimVel;
	}

	public void setAimVel(double aimVel) {
		this.aimVel = aimVel;
	}

	public void sendToSmartDash() {
		super.sendToSmartDash();
		NetworkTable table = NetworkTable.getTable(this.subsystemID);
		kA = table.getNumber("k_A " + this.getName(), kA);
		kV = table.getNumber("k_V " + this.getName(), kV);
		table.putNumber(this.getName() + " aimVel", this.aimVel);
		table.putNumber(this.getName() + " aimAcc", this.aimAcc);
		table.putNumber("k_A " + this.getName(), kA);
		table.putNumber("k_V " + this.getName(), kV);
	}

}