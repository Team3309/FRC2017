package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.vision.VisionServer;

public class Turret extends ControlledSubsystem {

	private static Turret instance;
	private double currentAngle = getAngle();
	private double pastAngle = getAngle();
	// angle and loops since it last of spotted
	HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret("Turret");
		return instance;
	}

	private Turret(String name) {
		super(name);
		// Place the angles and time loops since lastseen into the hashmap
		// 0 - 360 degrees
		for (int angle = 0; angle <= 360; angle++) {
			hash.put(angle, 0);
		}
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
	public void updateTeleop() {
		currentAngle = getAngle();
		updateValuesSeen();
		// if you see the goal, aim at it
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			moveTowardsGoal();
		} else {
			searchForGoal();
		}
	}

	private void updateValuesSeen() {
		int error = (int) (currentAngle - pastAngle);
		int factor = (error > 0) ? 1 : -1;
		for (int angle = (int) pastAngle; angle * factor < currentAngle * factor; angle++) {
			hash.replace(angle, 0);
		}
	}

	private void moveTowardsGoal() {
		// TODO Auto-generated method stub

	}

	public void searchForGoal() {
		int largestTimeGap = Integer.MIN_VALUE;
		int angleToAimTowards = Integer.MIN_VALUE;
		// find the angle that needs most surveying
		Iterator<Entry<Integer, Integer>> it = hash.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> pair = it.next();
			if ((Integer) pair.getValue() > largestTimeGap) {
				// TODO may cause error due to Integer to int casting
				largestTimeGap = (int) pair.getValue();
				angleToAimTowards = (int) pair.getKey();
			}
			it.remove();
		}

	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {

	}

	@Override
	public void manualControl() {
		// TODO Auto-generated method stub

	}

	private void setTurnClockwise(double power) {
		// TODO MOTOR SETUP
	}

	private void setTurnCounterClockwise(double power) {
		setTurnClockwise(-power);
	}

	public double getAngle() {
		return Sensors.getTurretAngle();
	}

}
