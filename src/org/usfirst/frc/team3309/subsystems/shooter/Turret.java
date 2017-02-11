package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.communications.SimpleCsvLogger;
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

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret extends ControlledSubsystem {

	private static Turret instance;
	private CANTalon turretMC = new CANTalon(RobotMap.TURRET_ID);
	private double currentAngle = getAngle();
	private double pastAngle = getAngle();
	private double pastGoalAngle = getAngle();
	private double goalAngle = getAngle();
	private double goalVel = getVelocity();
	private double currentVelocity = getVelocity();
	private TurretState currentState = TurretState.STOPPED;
	private boolean hasCalibratedSinceEnable = false;
	// angle and loops since it last of spotted
	private HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
	private double RIGHT_ABSOLUTE_LIMIT = -90;
	private double LEFT_ABSOLUTE_LIMIT = 360;
	String[] data_fields = { "time", "angle" };
	String[] units_fields = { "ms", "deg" };
	public SimpleCsvLogger logger = new SimpleCsvLogger();
	private DigitalInput hallEffectSensor = new DigitalInput(RobotMap.HALL_EFFECT_SENSOR);
	private double lastVisionAngle = getAngle();

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret("Turret");
		return instance;
	}

	private double getVelocity() {
		return ((double) turretMC.getEncVelocity() / 14745) * 360.0;
	}

	private Turret(String name) {
		super(name);

		// Place the angles and time loops since lastseen into the hashmap
		// 0 - 360 degrees
		for (int angle = 0; angle <= 360; angle++) {
			hash.put(angle, 0);
		}

		turretMC.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		turretMC.reverseOutput(true);
		turretMC.reverseSensor(true);
		// turretMC.setPID(.00165, .000001, 0);
		this.teleopController = new PIDPositionController(.00190, .009, .00, .1);
		this.teleopController.setName("TURRET POS");
	}

	@Override
	public void initTeleop() {
		calTimer.start();
		this.teleopController.reset();
		logger.init(data_fields, units_fields);
		goalAngle = getAngle();

	}

	@Override
	public void initAuto() {
		calTimer.start();
	}

	@Override
	public void updateTeleop() {
		currentAngle = getAngle();
		currentVelocity = getVelocity();
		if (!hasCalibratedSinceEnable) {
			calibrate();
			return;
		}
		if (isHallEffectHit() && getAngle() < 300) {
			this.turretMC.setEncPosition(0);
		}

		// updateValuesSeen();

		// if you see the goal, aim at it
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			moveTowardsGoal();
		} else {
			if (Controls.driverController.getYButton() || Controls.operatorController.getYButton()) {
				goalAngle = lastVisionAngle;
			} else {
				searchForGoalVelControlledSurvey(); //
			}
			// searchForGoal();
		}

		OutputSignal signal = this.teleopController.getOutputSignal(getInputState());

		if (turretMC.getControlMode() == TalonControlMode.Position) {
			turretMC.set((goalAngle / 360) * 14745.6);
		} else {
			turretMC.set(signal.getMotor());
		}
		// setTurnClockwise(signal.getMotor());
		// add 1 loop to all angles
		for (int angle = 0; angle <= 360; angle++) {
			hash.replace(angle, hash.get(angle) + 1);
		}
		loopsSinceLastReset++;
		sumOfOmegaSinceLastReset += Sensors.getAngularVel();
		pastGoalAngle = goalAngle;
	}

	private void changeToPositionMode() {
		turretMC.changeControlMode(TalonControlMode.Position);
	}

	private void changeToVelocityMode() {
		turretMC.changeControlMode(TalonControlMode.PercentVbus);
		if (!(this.teleopController instanceof FeedForwardWithPIDController)) {
			this.teleopController = new FeedForwardWithPIDController(.017, 0.00, .009, .00, .00);
			this.teleopController.setName("TurretVel ");
		}
	}

	private double calibrationFactor = 1;
	private double startingDegrees = getAngle();
	private Timer calTimer = new Timer();

	private void calibrate() {

		changeToPositionMode();
		System.out.println("CALIBRATING");
		if (calTimer.get() < 1)
			this.turretMC.set((((startingDegrees + 30)) / 360) * 14745.6);
		else {
			this.turretMC.set((((startingDegrees - 60)) / 360) * 14745.6);
		}

		if (calTimer.get() > 2)
			calTimer.reset();
		calibrationFactor++;
		if (this.isHallEffectHit()) {
			this.turretMC.set(0);
			hasCalibratedSinceEnable = true;
			this.turretMC.setEncPosition(0);
			this.turretMC.setForwardSoftLimit(this.LEFT_ABSOLUTE_LIMIT);
			this.turretMC.setReverseSoftLimit(this.RIGHT_ABSOLUTE_LIMIT);
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
		changeToPositionMode();
		TargetInfo goal = VisionServer.getInstance().getTargets().get(0);
		double goalX = goal.getY();
		System.out.println("GOAL X " + goalX);
		double degToTurn = (goalX) * (VisionServer.FIELD_OF_VIEW_DEGREES);
		// TODO make this line more accurate to camera frame
		double predictionOffset = (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000));
		goalAngle = this.getAngle() + degToTurn;

		System.out.println("deg to turn " + degToTurn);
		if (goalAngle > 360) {
			goalAngle -= 360;
		}

		if (goalAngle < -70) {
			goalAngle += 360;
		}
		lastVisionAngle = goalAngle;
		System.out.println("GOAL AGLE " + goalAngle + " cur angle " + getAngle());
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
	private final double RIGHT_LIMIT = 180;
	private final double MAX_ACC = .8; // 180 deg/s*s
	private final double MAX_VEL = 20; // 180 deg/s*s

	public void testVelControl() {
		goalVel = SmartDashboard.getNumber("aim Turret Vel", 0);
		SmartDashboard.putNumber("aim Turret Vel", goalVel);

	}

	public void testPosControl() {
		goalAngle = SmartDashboard.getNumber("aim Turret Pos", 0);
		if (goalAngle != pastGoalAngle) {

			// if (Math.abs(this.currentAngle - goalAngle) < 60) {
			this.teleopController = new PIDPositionController(22.611, .037177, 0, .075);
			this.teleopController.setName("TURR SLOW PID");
			this.teleopController.reset();

			// } else {
			// this.teleopController = new PIDPositionController(27, 1.0, 0,
			// .1);
			// this.teleopController.setName("TURR FAST PID");
			// }
		}
		SmartDashboard.putNumber("aim Turret Pos", goalAngle);
		if (Controls.driverController.getYButton())
			this.teleopController.reset();
	}

	private double desiredVelTarget = -MAX_VEL;
	private double desiredVel = 0;
	private double desiredAngle = this.RIGHT_LIMIT;

	public void searchForGoalVelControlledSurvey() {
		this.changeToVelocityMode();
		if (desiredVel < desiredVelTarget) {
			desiredVel += Math.min(desiredVelTarget - desiredVel, MAX_ACC);
		} else if (desiredVel > desiredVelTarget) {
			desiredVel -= Math.max(desiredVelTarget - desiredVel, MAX_ACC);
		}

		if (getAngle() < this.LEFT_LIMIT) {
			desiredAngle = RIGHT_LIMIT;
			desiredVelTarget = -MAX_VEL;
		} else if (getAngle() > this.RIGHT_LIMIT) {
			desiredAngle = LEFT_LIMIT;
			desiredVelTarget = MAX_VEL;
		}
		System.out.println("Targetting: " + desiredAngle + " curAngle " + getAngle() + " goalVel: " + this.desiredVel
				+ " veltarget: " + this.desiredVelTarget);

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
		SmartDashboard.putNumber("Turert power ", this.turretMC.get());
		SmartDashboard.putNumber("Turret Angle", getAngle());
		// SmartDashboard.putNumber("Turret Velocity", getVelocity());
		SmartDashboard.putNumber("Turret Goal Angle", this.turretMC.getSetpoint());
		SmartDashboard.putNumber("Turret get", this.turretMC.get());
		// SmartDashboard.putNumber("Turret Goal Vel", this.goalVel);
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			SmartDashboard.putNumber("Turret hyp", VisionServer.getInstance().getTarget().getHyp());
			SmartDashboard.putNumber("TURRET X", VisionServer.getInstance().getTarget().getY());
		}
		SmartDashboard.putNumber("Turret prediction degrees", (sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
				* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000)));
		SmartDashboard.putNumber("Turret closed loop error", this.turretMC.getClosedLoopError());
		// SmartDashboard.putString("Turret State", this.currentState.name());
		// SmartDashboard.putNumber("Desired Target Vel",
		// this.desiredVelTarget);
		SmartDashboard.putNumber("TURRET error", (turretMC.getError() / 147445) * 360);
		SmartDashboard.putBoolean("hall effect", this.hallEffectSensor.get());
		// NetworkTable.getTable("turret").putNumber("angle", getAngle());
	}

	@Override
	public void manualControl() {
		// logger.writeData(Timer.getFPGATimestamp(), getAngle());
		// setTurnClockwise(.4);
		setTurnClockwise(Controls.operatorController.getX(Hand.kRight));
		if (isHallEffectHit()) { // MAKE MEMES EVERYDAY
			this.turretMC.setEncPosition(0);
		}
		// if (Controls.operatorController.getAButton()) {
		// resetSensor();
		// }
		// if (Controls.operatorController.getBButton()) {
		// setTurnClockwise(.3);
		// BlackBox.logThis("Turret", getAngle());
		// BlackBox.writeLog();
		// }

	}

	private boolean isHallEffectHit() {
		return !this.hallEffectSensor.get();
	}

	private void setTurnClockwise(double power) {
		turretMC.set(power);
	}

	private void setTurnCounterClockwise(double power) {
		setTurnClockwise(-power);
	}

	public double getAngleRelativeToField() {
		return this.getAngle() + Sensors.getAngle();
	}

	public double getAngle() {
		return -((double) turretMC.getEncPosition() / 14745.6) * 360;
	}

	public void resetSensor() {
		turretMC.setEncPosition(0);
	}

	public void myCodesBetter() {
		System.out.println("Im better than you");
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

	public void callForCalibration() {
		hasCalibratedSinceEnable = false;
		startingDegrees = this.getAngle();
		calibrationFactor = -30;
	}
}
