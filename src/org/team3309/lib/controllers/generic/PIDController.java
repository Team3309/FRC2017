package org.team3309.lib.controllers.generic;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Basic PID Controller. The isCompleted Method will always return false.
 * 
 * @author TheMkrage
 *
 */
public abstract class PIDController extends Controller {

	private boolean useSmartDash = true;

	public boolean isUseSmartDash() {
		return useSmartDash;
	}

	public void setUseSmartDash(boolean useSmartDash) {
		this.useSmartDash = useSmartDash;
	}

	/**
	 * if small gain, help with smartdash
	 */
	private boolean isSmallGain = false;
	/**
	 * Gains
	 */
	public double kP, kD, kI;
	/**
	 * Limit of the Integral. mIntegral is capped off at the kILimit.
	 */
	public double kILimit = .5;
	/**
	 * Stores previous error
	 */
	protected double previousError = 0;
	protected double previousPValue = 0;
	protected double previousIValue = 0;
	protected double previousDValue = 0;
	/**
	 * Running Integral term to use between loops.
	 */
	public double mIntegral = 0;
	/**
	 * Tells if Controller ends when it maintains a low error for a certain
	 * amount of time.
	 */
	protected boolean completable = true;
	/**
	 * Margin of how close the error can be close to 0.
	 */
	protected double THRESHOLD = 30;
	/**
	 * Time the error must stay between the certain margin within the threshold.
	 */
	protected double TIME_TO_BE_COMPLETE_MILLISECONDS = .250;
	/**
	 * Timer to count how much time the error has been low.
	 */
	protected KragerTimer doneTimer = new KragerTimer(TIME_TO_BE_COMPLETE_MILLISECONDS);
	private double factor = 1;

	public PIDController(double kP, double kI, double kD) {
		if ((kP < .001 || kI < .001 || kD < .001) && kP != 0 && kI != 0 && kD != 0) {
			this.isSmallGain = true;
		} else {
			this.isSmallGain = false;
		}
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;

		if (isSmallGain) {
			factor = 10;
		} else {
			factor = 1;
		}
		SmartDashboard.putNumber(this.getName() + " kP", kP * Math.pow(factor, 3));
		SmartDashboard.putNumber(this.getName() + " kI", kI * Math.pow(factor, 3));
		SmartDashboard.putNumber(this.getName() + " kD", kD * Math.pow(factor, 3));
	}

	public PIDController(double kP, double kI, double kD, double kILimit) {
		this(kP, kI, kD);
		this.kILimit = kILimit;
	}

	// You would want to set the mIntegral and previousError to zero when
	// reusing a PID Loop
	@Override
	public void reset() {
		mIntegral = 0;
		previousError = 0;
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		double error = inputState.getError();

		// Add to mIntegral term
		mIntegral += error;

		// Check for integral hitting the limit
		if (mIntegral * kI > kILimit)
			mIntegral = kILimit / kI;

		if (mIntegral * kI < -kILimit)
			mIntegral = -kILimit / kI;

		// Make OutputSignal and fill it with calculated values
		OutputSignal signal = new OutputSignal();
		previousPValue = (kP * error);
		previousIValue = (kI * mIntegral);
		previousDValue = (kD * (error - previousError));
		double output = (kP * error) + (kI * mIntegral) + (kD * (error - previousError));
		// System.out.println("Kp: " + (kP * error) + "kI: " + (kI * mIntegral)
		// + "kD: " + (kD * (error - previousError)));
		signal.setMotor(output);
		previousError = error;
		return signal;
	}

	/**
	 * @return if timer can end
	 */
	public boolean isCompletable() {
		return completable;
	}

	/**
	 * @param completable
	 *            whether the loop can end execution
	 */
	public void setCompletable(boolean completable) {
		this.completable = completable;
	}

	/**
	 * @param tHRESHOLD
	 *            gap that
	 */
	public void setTHRESHOLD(double tHRESHOLD) {
		THRESHOLD = tHRESHOLD;
	}

	/**
	 * @param tIME_TO_BE_COMPLETE_MILLISECONDS
	 *            time
	 */
	public void setTIME_TO_BE_COMPLETE_MILLISECONDS(double tIME_TO_BE_COMPLETE_MILLISECONDS) {
		TIME_TO_BE_COMPLETE_MILLISECONDS = tIME_TO_BE_COMPLETE_MILLISECONDS;
	}

	public void setConstants(double kP, double kI, double kD) {

		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}

	@Override
	public boolean isCompleted() {
		// If the Controller is completable, then the error will need to be
		// between a certain threshold before isCompleted return true

		if (completable) {
			return this.doneTimer.isConditionMaintained(Math.abs(previousError) < THRESHOLD);
		}
		return this.doneTimer.isConditionMaintained(false);
	}

	public void printConstants() {
		System.out.println(this.getName() + "KP: " + this.kP + " KI: " + this.kI + " KD: " + this.kD);
	}

	@Override
	public void sendToSmartDash() {
		if (this.useSmartDash) {
			kP = SmartDashboard.getNumber(this.getName() + " kP", kP) * Math.pow(factor, -3);
			kI = SmartDashboard.getNumber(this.getName() + " kI", kI) * Math.pow(factor, -3);
			kD = SmartDashboard.getNumber(this.getName() + " kD", kD) * Math.pow(factor, -3);
			SmartDashboard.putNumber(this.getName() + " kP", kP * Math.pow(factor, 3));
			SmartDashboard.putNumber(this.getName() + " kI", kI * Math.pow(factor, 3));
			SmartDashboard.putNumber(this.getName() + " kD", kD * Math.pow(factor, 3));
			SmartDashboard.putNumber(this.getName() + " ERROR", this.previousError);
			SmartDashboard.putNumber(this.getName() + " FACTOR", factor);
			SmartDashboard.putNumber(this.getName() + " P CONTRIBUTION", this.previousPValue);
			SmartDashboard.putNumber(this.getName() + " I CONTRIBUTION", this.previousIValue);
			SmartDashboard.putNumber(this.getName() + " D CONTRIBUTION", this.previousDValue);
		}

	}
}
