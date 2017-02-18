package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.controllers.turret.TurretVelocitySuveyController;
import org.team3309.lib.tunable.Dashboard;
import org.team3309.lib.tunable.DashboardHelper;
import org.team3309.lib.tunable.IDashboard;
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
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret extends ControlledSubsystem implements IDashboard {

	private static Turret instance;
	private DigitalInput hallEffectSensor = new DigitalInput(RobotMap.HALL_EFFECT_SENSOR);
	private CANTalon turretMC = new CANTalon(RobotMap.TURRET_ID, 20);
	private double currentAngle = getAngle();
	private double pastAngle = getAngle();
	private double goalAngle = getAngle();
	private boolean hasCalibratedSinceRobotInit = false;
	// angle and loops since it last of spotted
	private HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
	private double RIGHT_ABSOLUTE_LIMIT = 330;
	private double LEFT_ABSOLUTE_LIMIT = -120;
	private double lastGoalX = 0;
	private double lastVisionAngle = getAngle();
	private NetworkTable table = NetworkTable.getTable("Turret");

	private boolean isMarked = false;

	public final double LEFT_LIMIT = -40;
	public final double RIGHT_LIMIT = 150;
	public final double MAX_ACC = .8; // 180 deg/s*s
	public final double MAX_VEL = 30; // 180 deg/s*s

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret();
		return instance;
	}

	private double getVelocity() {
		return ((double) turretMC.getEncVelocity() / 14745) * 360.0;
	}

	private Turret() {
		super("Turret");
		// Place the angles and time loops since lastseen into the hashmap
		// 0 - 360 degrees
		for (int angle = 0; angle <= 360; angle++) {
			hash.put(angle, 0);
		}
		turretMC.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		turretMC.reverseOutput(true);
		turretMC.reverseSensor(true);
		this.setController(new PIDPositionController(.00190, .009, .00, .1));
		this.getController().setName("TURRET POS");

		table.putNumber("k_aim Turret Pos", 0);
	}

	@Override
	public void initTeleop() {
		calTimer.start();
		this.getController().reset();
		goalAngle = getAngle();
	}

	@Override
	public void initAuto() {
		calTimer.start();
	}

	@Override
	public void updateTeleop() {
		currentAngle = getAngle();
		if (!hasCalibratedSinceRobotInit) {
			calibrate();
			return;
		}
		if (isHallEffectHit() && getAngle() < 300) {
			this.turretMC.setEncPosition(0);
		}
		// updateValuesSeen();
		// if you see the goal, aim at it

		if (VisionServer.getInstance().hasTargetsToAimAt() && !isMarked) {
			moveTowardsGoal();
			System.out.println("AIMING");
		} else {
			if (isMarked) {
				if (this.getAngle() > this.goalAngle - 5 && this.getAngle() < this.goalAngle + 5)
					isMarked = false;
			} else if (Controls.driverController.getYButton() || Controls.operatorController.getYButton()) {
				goalAngle = lastVisionAngle;
			} else {
				System.out.println("BUG");
				this.changeToVelocityMode();
			}
		}

		// this.testPosControl();
		OutputSignal signal = this.getController().getOutputSignal(getInputState());
		if (turretMC.getControlMode() == TalonControlMode.Position) {
			turretMC.set((goalAngle / 360) * 14745.6);
		} else {
			turretMC.set(signal.getMotor());
		}
		// add 1 loop to all angles
		for (int angle = 0; angle <= 360; angle++) {
			hash.replace(angle, hash.get(angle) + 1);
		}
		System.out.println("GOAL ANG");
		loopsSinceLastReset++;
		sumOfOmegaSinceLastReset += Sensors.getAngularVel();
	}

	public void changeToPositionMode() {
		turretMC.changeControlMode(TalonControlMode.Position);
		// position is done on talon.
		this.setController(new BlankController());
	}

	public void changeToVelocityMode() {
		turretMC.changeControlMode(TalonControlMode.PercentVbus);
		if (!(this.getController() instanceof TurretVelocitySuveyController)) {
			TurretVelocitySuveyController con = new TurretVelocitySuveyController();
			con.setName("Turret Survey ");
			this.setController(con);
		}
	}

	private double startingDegrees = getAngle();
	private Timer calTimer = new Timer();

	private void calibrate() {
		changeToPositionMode();
		if (calTimer.get() < 1)
			this.turretMC.set((((startingDegrees + 20)) / 360) * 14745.6);
		else {
			this.turretMC.set((((startingDegrees - 40)) / 360) * 14745.6);
		}

		if (calTimer.get() > 2)
			calTimer.reset();
		if (this.isHallEffectHit()) {
			this.turretMC.set(0);
			hasCalibratedSinceRobotInit = true;
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
		double goalX = goal.getZ();
		if (lastGoalX != goalX) {
			this.resetAngVelocityCounts();
		}
		lastGoalX = goalX;
		// System.out.println("GOAL X " + goalX);
		double degToTurn = ((goalX) / .8) * (VisionServer.FIELD_OF_VIEW_DEGREES);
		double predictionOffset = Sensors.getAngle() - this.robotAngleAtLastGoal;
		System.out.println("sum of " + sumOfOmegaSinceLastReset + " loops snce " + loopsSinceLastReset);
		System.out.println("predictionOffset " + predictionOffset);
		table.putNumber("prediction offset", predictionOffset);

		goalAngle = this.getAngle() + degToTurn + predictionOffset;

		if (goalAngle > RIGHT_ABSOLUTE_LIMIT) {
			goalAngle -= 360;
			isMarked = true;
		}
		if (goalAngle < LEFT_ABSOLUTE_LIMIT) {
			goalAngle += 360;
			isMarked = true;
		}
		lastVisionAngle = goalAngle;

	}

	public void searchForGoal() {
		this.changeToVelocityMode();
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

	public void testPosControl() {
		changeToPositionMode();
		goalAngle = table.getNumber("k_aim Turret Pos", 0);
		table.putNumber("k_aim Turret Pos", goalAngle);
		// if (Controls.driverController.getYButton())
		// this.getController().reset();
	}

	@Override
	public void updateAuto() {
		updateTeleop();
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setAngularPos(getAngle());
		s.setAngularVel(this.getVelocity());
		s.setError((goalAngle - getAngle()) / 10000);
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.getController().sendToSmartDash();
		DashboardHelper.updateTunable(this.getController());
		table.putNumber(this.getName() + " power ", this.turretMC.get());
		table.putNumber("turret_angle", getAngle());
		table.putNumber(this.getName() + " Goal Angle", this.goalAngle);
		SmartDashboard.putNumber(this.getName() + " get", this.turretMC.get());
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			table.putNumber(this.getName() + " hyp", VisionServer.getInstance().getTarget().getHyp());
			table.putNumber(this.getName() + " X", VisionServer.getInstance().getTarget().getZ());
		}
		SmartDashboard.putNumber(this.getName() + " prediction degrees",
				(sumOfOmegaSinceLastReset / (double) loopsSinceLastReset)
						* (loopsSinceLastReset * (Robot.LOOP_SPEED_MS / 1000)));
		table.putNumber(this.getName() + " closed loop error", this.turretMC.getClosedLoopError());
		table.putNumber(this.getName() + " error", (turretMC.getError() / 147445) * 360);
		SmartDashboard.putBoolean(this.getName() + " hall effect", this.hallEffectSensor.get());
		// NetworkTable.getTable("turret").putNumber("angle", getAngle());
	}

	@Override
	public void manualControl() {
		setTurnClockwise(Controls.operatorController.getX(Hand.kLeft));
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

	@Dashboard(tunable = false, displayName = "turret_angle")
	public double getAngle() {
		return -((double) turretMC.getEncPosition() / 14745.6) * 360;
	}

	public void resetSensor() {
		turretMC.setEncPosition(0);
	}

	private KragerTimer timerSinceLastVisionGoalSeen = new KragerTimer(1000);
	private int loopsSinceLastReset = 0;
	private double sumOfOmegaSinceLastReset = 0;
	private double robotAngleAtLastGoal = getAngle();

	public void resetAngVelocityCounts() {
		robotAngleAtLastGoal = Sensors.getAngle();
		timerSinceLastVisionGoalSeen.stop();
		timerSinceLastVisionGoalSeen.reset();
		timerSinceLastVisionGoalSeen.start();
		loopsSinceLastReset = 1;
		sumOfOmegaSinceLastReset = 0;
	}

	public void callForCalibration() {
		hasCalibratedSinceRobotInit = false;
		startingDegrees = this.getAngle();
	}

	@Override
	public String getTableName() {
		return "Turret";
	}

	@Override
	public String getObjectName() {
		// TODO Auto-generated method stub
		return "";
	}
}
