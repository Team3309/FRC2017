package org.team3309.lib.controllers.drive;

import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.sensors.Sensors;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveAngleController extends PIDPositionController {
	double startingAngle = 0;
	double goalAngle = 0;

	public DriveAngleController(double goal) {
		super(4.6, 0.2, 3.502);
		this.setName("Angle");
		// SmartDashboard.putNumber(this.getName() + " goal(set me)", goal);
		this.setTHRESHOLD(.5);
		this.kILimit = .55;

		startingAngle = Sensors.getAngle();
		goalAngle = goal;
	}

	private double lastTime = 0;
	private final double MIN_POW = .32;

	public OutputSignal getOutputSignal(InputState inputState) {
		// this.kI = this.kI/100.0;
		double error = (goalAngle - inputState.getAngularPos());
		if (Math.abs(error) > 180) {
			error = -KragerMath.sign(error) * (360 - Math.abs(error));
			System.out.println("New Error: " + error);
		}
		error *= .01;
		double left = 0;
		SmartDashboard.putNumber("VISION ErRROR", error);
		InputState state = new InputState();
		state.setError(error);
		left = super.getOutputSignal(state).getMotor();
		/*
		 * if (Math.abs(error) > .15) { if (left < 0) { left = -.275; } else {
		 * left = .275; } }
		 */
		/*
		 * double left = super.getOutputSignal(state).getMotor(); if (left < 0)
		 * { left -= MIN_POW; } else { left += MIN_POW; }
		 * 
		 * if (Math.abs(left) > .6) { if (left > 0) { left = .6; } else { left =
		 * -.6; } }
		 */
		/*
		 * if (Math.abs(error) < 2.25) { //System.out.println("HERE"); left =
		 * super.getOutputSignal(state).getMotor(); }else if (Math.abs(error) <
		 * 5) { if (error < 0) { left = -.42; }else if (error > 0) { left = .42;
		 * } }else if (Math.abs(error) < 15) { if (error < 0) { left = -.42;
		 * }else if (error > 0) { left = .42; } }else if (Math.abs(error) <
		 * Integer.MAX_VALUE) { if (error < 0) { left = -.42; }else if (error >
		 * 0) { left = .42; } }
		 */

		OutputSignal signal = new OutputSignal();
		signal.setLeftRightMotor(left, -left);
		// System.out.println("Time: " + (System.currentTimeMillis() -
		// lastTime));
		lastTime = System.currentTimeMillis();

		return signal;
	}

	public void sendToSmartDash() {
		super.sendToSmartDash();
		SmartDashboard.putNumber(this.getName() + " AIM ANGLE", this.goalAngle);
		/*
		 * try { if (this.goalAngle != SmartDashboard.getNumber(this.getName() +
		 * " goal(set me)")) { this.goalAngle =
		 * SmartDashboard.getNumber(this.getName() + " goal(set me)");
		 * this.reset(); } } catch (Exception e) { e.printStackTrace(); }
		 */
	}

	public void setGoalAngle(double angle) {
		this.goalAngle = angle;
	}

	public double getGoalAngle() {
		return this.goalAngle;
	}
}