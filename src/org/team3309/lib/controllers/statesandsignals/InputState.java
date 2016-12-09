package org.team3309.lib.controllers.statesandsignals;

import java.util.HashMap;

import org.usfirst.frc.team3309.robot.SensorDoesNotReturnException;

/**
 * An Object used to let a Controller know the current state of a subsystem.
 * Contains methods for default values so keys do not have to be memorized.
 * Additional keys can be added if the InputState is treated as a HashMap
 * 
 * @author TheMkrage
 * 
 */
@SuppressWarnings("serial")
public class InputState extends HashMap<String, Double> {

	public InputState() {
		super();
		this.setX(0.0);
		this.setY(0.0);
		this.setError(0.0);
	}

	// Default Key Methods
	public void setError(double error) {
		// System.out.println("SETTOMG : " + error);
		this.put("error", error);
	}

	public double getError() {
		return this.get("error");
	}

	// For Drives (and whatever else may use them)
	public void setX(double x) {
		this.put("x", x);
	}

	public double getX() {
		return this.get("x");
	}

	public void setY(double y) {
		this.put("y", y);
	}

	public double getY() {
		return this.get("y");
	}

	public void setAngularVel(double w) {
		this.put("angVel", w);
	}

	// Vel for Drive
	public double getAngularVel() {
		return this.get("angVel");
	}

	public void setLeftVel(double leftVel) {
		this.put("leftVel", leftVel);
	}

	public double getLeftVel() throws SensorDoesNotReturnException {
		return this.get("leftVel");
	}

	public void setRightVel(double rightVel) {
		this.put("rightVel", rightVel);
	}

	public double getRightVel() throws SensorDoesNotReturnException {
		return this.get("rightVel");
	}

	// Pos for Drive
	public void setAngularPos(double heading) {
		this.put("heading", heading);
	}

	public double getAngularPos() {
		return this.get("heading");
	}

	public void setLeftPos(double leftPos) {
		this.put("leftPos", leftPos);
	}

	public double getLeftPos() throws SensorDoesNotReturnException {
		return this.get("leftPos");
	}

	public void setRightPos(double rightPos) {
		this.put("rightPos", rightPos);
	}

	public double getRightPos() throws SensorDoesNotReturnException {
		return this.get("rightPos");
	}

	public void setTime(double time) {
		this.put("time", time);
	}

	public double getTime() {
		return this.get("time");
	}
}
