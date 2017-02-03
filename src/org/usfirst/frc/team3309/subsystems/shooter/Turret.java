package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.vision.TargetInfo;
import org.usfirst.frc.team3309.vision.VisionServer;

public class Turret extends ControlledSubsystem {

	private static Turret instance;
	private double currentAngle = getAngle();
	private double pastAngle = getAngle();
	private double goalAngle = getAngle();
	private double goalVel = getAngle();
	private double currentVelocity = getVelocity();
	private TurretState currentState = TurretState.STOPPED;
	// angle and loops since it last of spotted
	private HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
	private TalonSRXMC turretMC = new TalonSRXMC(RobotMap.TURRET_ID);

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret("Turret");
		return instance;
	}

	private double getVelocity() {
		return turretMC.getVelocity();
	}

	private Turret(String name) {
		super(name);
		// Place the angles and time loops since lastseen into the hashmap
		// 0 - 360 degrees
		for (int angle = 0; angle <= 360; angle++) {
			hash.put(angle, 0);
		}
		this.teleopController = new FeedForwardWithPIDController(.001, .011, .01, .01, .01);
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
		currentVelocity = getVelocity();
		updateValuesSeen();
		// if you see the goal, aim at it
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			moveTowardsGoal();
		} else {
			searchForGoal();
		}
		OutputSignal signal = this.teleopController.getOutputSignal(getInputState());
		setTurnClockwise(signal.getMotor());
		// add 1 loop to all angles
		for (int angle = 0; angle <= 360; angle++) {
			hash.replace(angle, hash.get(angle) + 1);
		}
	}

	/**
	 * all the values seen in the last loop are set back to 0
	 */
	private void updateValuesSeen() {
		int error = (int) (currentAngle - pastAngle);
		int factor = (error > 0) ? 1 : -1;
		// 30's are for field of vision
		for (int angle = (int) pastAngle - 30; angle * factor < currentAngle * factor + 30; angle++) {
			if (angle >= 0)
				hash.replace(angle, 0);
		}
	}

	private void moveTowardsGoal() {
		TargetInfo goal = VisionServer.getInstance().getTargets().get(0);
		double goalX = goal.getX();
		double degToTurn = (goalX / 2) * VisionServer.FIELD_OF_VIEW_DEGREES;
		goalAngle = this.getAngle() + degToTurn;
	}

	public void searchForGoal() {
		double largestHeuristic = Double.MIN_VALUE;
		int angleToAimTowards = Integer.MIN_VALUE;
		// find the angle that needs most surveying
		Iterator<Entry<Integer, Integer>> it = hash.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> pair = it.next();
			// current - possibleGoal
			double degreesAway = Math.abs(getAngle() - pair.getKey());
			// TODO may cause error due to Integer to int casting
			double heuristic = largestHeuristic * .95 + (1 - (degreesAway * .5));
			if (heuristic > largestHeuristic) {
				largestHeuristic = (int) pair.getValue();
				angleToAimTowards = (int) pair.getKey();
			}
			it.remove();
		}
		goalAngle = angleToAimTowards;
	}

	private final double LEFT_LIMIT = 30;
	private final double RIGHT_LIMIT = 330;
	private boolean shouldBeTurningClockwise = false;
	private final double MAX_ACC = 180; // 180 deg/s*s
	private final double MAX_VEL = 180; // 180 deg/s*s
	private double degreesToStartSlowing = RIGHT_LIMIT - 30;
	private double degreesWhichAccelerationStarted = 0;
	private boolean isFirstTimeAccelerating = true;

	public void searchForGoalVelControlledSurvey() {
		switch (this.currentState) {
		case ACCELERATING:
			if (isFirstTimeAccelerating) {
				degreesWhichAccelerationStarted = this.getAngle();
				isFirstTimeAccelerating = false;
			}
			if (Math.abs(currentVelocity) < MAX_VEL - 5) {
				goalVel = currentVelocity + MAX_ACC;
			} else {
				this.currentState = TurretState.CONSTANT;
				double degreesCrossedDuringAcceleration = this.getAngle() - degreesWhichAccelerationStarted;
				degreesToStartSlowing = degreesCrossedDuringAcceleration;
			}
		case DECELERATION:
			if (Math.abs(currentVelocity) > 5) {
				goalVel = currentVelocity - MAX_ACC;
			}
		case CONSTANT:
			goalVel = MAX_VEL;
		case STOPPED:
			// if stopped,find which way to turn and do so
			if (this.currentAngle < 180) {
				shouldBeTurningClockwise = false;
			} else {
				shouldBeTurningClockwise = true;
			}
			this.currentState = TurretState.ACCELERATING;
		default:

		}
		int direction = 0;
		if (shouldBeTurningClockwise) {
			if (currentVelocity < MAX_VEL - 10) {

			}
		} else {

		}

	}

	public void searchForGoalOpenLoop() {

	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setError(goalAngle - getAngle());
		return s;
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

	public double getAngleRelativeToField() {
		// TODO make this
		return Sensors.getAngle();
	}

	public double getAngle() {
		return turretMC.getPosition();
	}

}
