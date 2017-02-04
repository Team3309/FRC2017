package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.Robot;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.vision.TargetInfo;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
		this.teleopController.setName("Turret");
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
			testVelControl(); // searchForGoal();
		}

		OutputSignal signal = this.teleopController.getOutputSignal(getInputState());
		setTurnClockwise(signal.getMotor());
		// add 1 loop to all angles
		for (int angle = 0; angle <= 360; angle++) {
			hash.replace(angle, hash.get(angle) + 1);
		}
		loopsSinceLastReset++;
		sumOfOmegaSinceLastReset += Sensors.getAngularVel();
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
		if (!(this.teleopController instanceof PIDPositionController)) {
			this.teleopController = new PIDPositionController(0.001, 000, 0000);
			this.teleopController.setName("TurretPOS ");
		}
		TargetInfo goal = VisionServer.getInstance().getTargets().get(0);
		double goalX = goal.getX();
		double degToTurn = (goalX / 2) * VisionServer.FIELD_OF_VIEW_DEGREES;
		// TODO make this line more accurate to camera frame
		double predictionOffset = (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000));
		goalAngle = this.getAngle() + degToTurn + predictionOffset;
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

	private final double LEFT_LIMIT = 330;
	private final double RIGHT_LIMIT = 30;
	private boolean shouldBeTurningClockwise = false;
	private final double MAX_ACC = 180; // 180 deg/s*s
	private final double MAX_VEL = 180; // 180 deg/s*s
	private double degreesToStartSlowing = RIGHT_LIMIT - 30;
	private double degreesWhichAccelerationStarted = 0;
	private boolean isFirstTimeAccelerating = true;

	public void testVelControl() {
		goalVel = SmartDashboard.getNumber("aim Turret Vel", 0);

	}

	public void searchForGoalVelControlledSurvey() {
		if (!(this.teleopController instanceof FeedForwardWithPIDController)) {
			this.teleopController = new FeedForwardWithPIDController(.001, .011, .01, 0, 0);
			this.teleopController.setName("TurretVel ");
		}
		int direction = shouldBeTurningClockwise ? -1 : 1;
		switch (this.currentState) {
		case ACCELERATING:
			if (isFirstTimeAccelerating) {
				degreesWhichAccelerationStarted = this.getAngle();
				isFirstTimeAccelerating = false;
			}
			if (Math.abs(currentVelocity) < MAX_VEL - 5) {
				goalVel = direction * (Math.abs(currentVelocity) + MAX_ACC);
			} else {
				this.currentState = TurretState.CONSTANT;
				double degreesCrossedDuringAcceleration = this.getAngle() - degreesWhichAccelerationStarted;
				degreesToStartSlowing = (shouldBeTurningClockwise ? RIGHT_LIMIT : LEFT_LIMIT)
						- degreesCrossedDuringAcceleration;
			}
		case DECELERATION:
			if (Math.abs(currentVelocity) > 5) {
				goalVel = direction * (Math.abs(currentVelocity) - MAX_ACC);
			}
			if (Math.abs(currentVelocity) < 3 || this.currentAngle > this.LEFT_LIMIT
					|| this.currentAngle < this.RIGHT_LIMIT) {
				goalVel = 0;
				this.currentState = TurretState.STOPPED;
			}
		case CONSTANT:
			goalVel = MAX_VEL;
			if (this.getAngle() > degreesToStartSlowing && !shouldBeTurningClockwise
					|| this.getAngle() < degreesToStartSlowing && shouldBeTurningClockwise) {
				this.currentState = TurretState.DECELERATION;
			}
		case STOPPED:
			isFirstTimeAccelerating = false;
			// if stopped,find which way to turn and do so
			if (this.currentAngle < 180) {
				shouldBeTurningClockwise = false;
			} else {
				shouldBeTurningClockwise = true;
			}
			this.currentState = TurretState.ACCELERATING;
		default:

		}
		((FeedForwardWithPIDController) this.teleopController).setAimVel(goalVel);
	}

	@Override
	public void updateAuto() {
		updateTeleop();
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		if (this.teleopController instanceof FeedForwardWithPIDController
				|| this.autoController instanceof FeedForwardWithPIDController)
			s.setError(goalVel - getVelocity());
		else
			s.setError(goalAngle - getAngle());
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.teleopController.sendToSmartDash();
		SmartDashboard.putNumber("Angle", getAngle());
		SmartDashboard.putNumber("Velocity", getVelocity());
		SmartDashboard.putNumber("Goal Angle", this.goalAngle);
		SmartDashboard.putNumber("Goal Vel", this.goalVel);
		if (VisionServer.getInstance().hasTargetsToAimAt())
			SmartDashboard.putNumber("hyp", VisionServer.getInstance().getTarget().getHyp());
		SmartDashboard.putNumber("prediction degrees", (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000)));
		SmartDashboard.putNumber("power", this.turretMC.getDesiredOutput());
		SmartDashboard.putString("State", this.currentState.name());
	}

	@Override
	public void manualControl() {
		setTurnClockwise(Controls.operatorController.getX(Hand.kRight));

	}

	private void setTurnClockwise(double power) {
		turretMC.setDesiredOutput(power);
	}

	private void setTurnCounterClockwise(double power) {
		setTurnClockwise(-power);
	}

	public double getAngleRelativeToField() {
		return this.getAngle() + Sensors.getAngle();
	}

	public double getAngle() {
		return turretMC.getPosition();
	}

	private KragerTimer timerSinceLastVisionGoalSeen = new KragerTimer(1000);
	private int loopsSinceLastReset = 0;
	private double sumOfOmegaSinceLastReset = 0;

	public void resetAngVelocityCounts() {
		timerSinceLastVisionGoalSeen.stop();
		timerSinceLastVisionGoalSeen.reset();
		timerSinceLastVisionGoalSeen.start();
		loopsSinceLastReset = 1;
		sumOfOmegaSinceLastReset = 0;
	}

}
