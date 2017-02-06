package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.communications.BlackBox;
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
	private TalonSRXMC turretMC = new TalonSRXMC(RobotMap.TURRET_ID);
	private double currentAngle = getAngle();
	private double pastAngle = getAngle();
	private double pastGoalAngle = getAngle();
	private double goalAngle = getAngle();
	private double goalVel = getVelocity();
	private double currentVelocity = getVelocity();
	private TurretState currentState = TurretState.STOPPED;
	// angle and loops since it last of spotted
	private HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret("Turret");
		return instance;
	}

	private double getVelocity() {
		return ((double) turretMC.getTalon().getEncVelocity() / 14745) * 360.0;
	}

	private Turret(String name) {
		super(name);
		// Place the angles and time loops since lastseen into the hashmap
		// 0 - 360 degrees
		for (int angle = 0; angle <= 360; angle++) {
			hash.put(angle, 0);
		}
		this.teleopController = new PIDPositionController(0, .009, .00, .1);
		this.teleopController.setName("TURRET POS");
	}

	@Override
	public void initTeleop() {
		resetSensor();
	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTeleop() {
		currentAngle = getAngle();
		currentVelocity = getVelocity();
		if (Controls.operatorController.getAButton()) {
			resetSensor();
		}

		// updateValuesSeen();
		// if you see the goal, aim at it

		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			System.out.println("Vision");
			moveTowardsGoal();
		} else {
			// System.out.println("TESTING POS");
			// testPosControl();
			searchForGoalVelControlledSurvey(); // searchForGoal();
		}

		OutputSignal signal = this.teleopController.getOutputSignal(getInputState());

		// setTurnClockwise(signal.getMotor());
		// add 1 loop to all angles
		for (int angle = 0; angle <= 360; angle++) {
			hash.replace(angle, hash.get(angle) + 1);
		}
		loopsSinceLastReset++;
		sumOfOmegaSinceLastReset += Sensors.getAngularVel();
		pastGoalAngle = goalAngle;
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
			this.teleopController = new PIDPositionController(165, .1, -300, .1);
			this.teleopController.setName("TURR SLOW PID");
		}
		TargetInfo goal = VisionServer.getInstance().getTargets().get(0);
		double goalX = goal.getX();
		System.out.println("GOAL X " + goalX);
		double degToTurn = (goalX / 2) * VisionServer.FIELD_OF_VIEW_DEGREES;
		// TODO make this line more accurate to camera frame
		double predictionOffset = (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000));
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

	private final double LEFT_LIMIT = 240;
	private final double RIGHT_LIMIT = 120;
	private final double MAX_ACC = .8; // 180 deg/s*s
	private final double MAX_VEL = 20; // 180 deg/s*s

	public void testVelControl() {
		goalVel = SmartDashboard.getNumber("aim Turret Vel", 0);
		SmartDashboard.putNumber("aim Turret Vel", goalVel);

	}

	public void testPosControl() {
		goalAngle = SmartDashboard.getNumber("aim Turret Pos", 0);
		if (goalAngle != pastGoalAngle) {
			if (Math.abs(this.currentAngle - goalAngle) < 60) {
				this.teleopController = new PIDPositionController(165, .1, -300, .1);
				this.teleopController.setName("TURR SLOW PID");
			} else {
				this.teleopController = new PIDPositionController(27, 1.0, 0, .1);
				this.teleopController.setName("TURR FAST PID");
			}
		}
		SmartDashboard.putNumber("aim Turret Pos", goalAngle);
	}

	private double desiredVelTarget = -MAX_VEL;
	private double desiredVel = 0;
	private double desiredAngle = this.RIGHT_LIMIT;

	public void searchForGoalVelControlledSurvey() {
		if (!(this.teleopController instanceof FeedForwardWithPIDController)) {
			this.teleopController = new FeedForwardWithPIDController(.017, 0.00, .009, .00, .00);
			this.teleopController.setName("TurretVel ");
		}
		if (desiredVel < desiredVelTarget) {
			desiredVel += Math.min(desiredVelTarget - desiredVel, MAX_ACC);
		} else if (desiredVel > desiredVelTarget) {
			desiredVel -= Math.max(desiredVelTarget - desiredVel, MAX_ACC);
		}

		if (getAngle() > this.LEFT_LIMIT) {
			desiredAngle = RIGHT_LIMIT;
			desiredVelTarget = -MAX_VEL;
		} else if (getAngle() < this.RIGHT_LIMIT) {
			desiredAngle = LEFT_LIMIT;
			desiredVelTarget = MAX_VEL;
		}

		this.goalVel = this.desiredVel;
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
				|| this.autoController instanceof FeedForwardWithPIDController) {

			((FeedForwardWithPIDController) this.teleopController).setAimVel(goalVel);
			s.setError((goalVel - getVelocity()));
		} else
			s.setError((goalAngle - getAngle()) / 10000);
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.teleopController.sendToSmartDash();
		SmartDashboard.putNumber("Turret Angle", getAngle());
		SmartDashboard.putNumber("Turret Velocity", getVelocity());
		SmartDashboard.putNumber("Turret Goal Angle", this.goalAngle);
		SmartDashboard.putNumber("Turret Goal Vel", this.goalVel);
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			SmartDashboard.putNumber("Turret hyp", VisionServer.getInstance().getTarget().getHyp());
			SmartDashboard.putNumber("TURRET X", VisionServer.getInstance().getTarget().getY());
		}
		SmartDashboard.putNumber("Turret prediction degrees", (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000)));
		SmartDashboard.putNumber("Turret power", this.turretMC.getDesiredOutput());
		SmartDashboard.putString("Turret State", this.currentState.name());
		SmartDashboard.putNumber("Desired Target Vel", this.desiredVelTarget);
	}

	@Override
	public void manualControl() {
		setTurnClockwise(Controls.operatorController.getX(Hand.kRight) / 2);
		if (Controls.operatorController.getAButton()) {
			resetSensor();
		}
		if (Controls.operatorController.getBButton()) {
			setTurnClockwise(.3);
			BlackBox.logThis("Turret", getAngle());
			BlackBox.writeLog();
		}

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
		return ((double) turretMC.getTalon().getEncPosition() / 14745.6) * 360;
	}

	public void resetSensor() {
		turretMC.getTalon().setEncPosition(7373);
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
