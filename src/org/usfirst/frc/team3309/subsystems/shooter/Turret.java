package org.usfirst.frc.team3309.subsystems.shooter;

import java.util.HashMap;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.controllers.turret.TurretVelocitySuveyController;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.TargetInfo;
import org.usfirst.frc.team3309.vision.VisionServer;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret extends ControlledSubsystem {

	private static Turret instance;
	private DigitalInput hallEffectSensor = new DigitalInput(RobotMap.HALL_EFFECT_SENSOR);
	private CANTalon turretMC = new CANTalon(RobotMap.TURRET_ID, 10);

	public boolean hasCalibratedSinceRobotInit = false;
	// angle and loops since it last of spotted
	private HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
	public double RIGHT_ABSOLUTE_LIMIT = 270;
	public double LEFT_ABSOLUTE_LIMIT = -90;
	private double lastGoalX = 0;
	private double lastVisionAngle = getAngle();
	private NetworkTable table = NetworkTable.getTable("Turret");
	private double goalAngle = getAngle();
	private double robotAngleWhenGoalLost = 0;
	private double turretGoalWhenLost = 0;
	private double lastGoalAngleFromVision = 0;
	private boolean isFirstLostLogged = false;
	private boolean isSurvey = false;
	private double kAngVel = .1;

	public final double LEFT_LIMIT = 10;
	public final double RIGHT_LIMIT = 170;
	public final double MAX_ACC = 1.5; // 180 deg/s*s
	public final double MAX_VEL = 30; // 180 deg/s*s

	private TurretState currentState = TurretState.HOME;
	private Timer calTimer = new Timer();

	public enum TurretState {
		SURVEY, HOME, CLIMBING, HOLD, USING_VISION
	}

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
		this.currentState = TurretState.HOME;
		isSurvey = true;
	}

	@Override
	public void initAuto() {
		calTimer.start();
		this.currentState = TurretState.SURVEY;
		this.getController().reset();
		goalAngle = getAngle();
		isSurvey = true;
	}

	@Override
	public void updateTeleop() {
		if (!hasCalibratedSinceRobotInit) {
			calibrate();
			return;
		}

		// STATE UPDATES
		// If climbing,
		if (Climber.getInstance().isClimbing()) {
			currentState = TurretState.CLIMBING;
		}

		if (Controls.driverController.getPOV() == 90 || Controls.operatorController.getPOV() == 90) {
			currentState = TurretState.SURVEY;
		} else if (Controls.driverController.getPOV() == 270 || Controls.operatorController.getPOV() == 270) {
			currentState = TurretState.HOME;
		}

		// STATE MATH
		// if you see the goal, aim at it
		if (VisionServer.getInstance().hasTargetsToAimAt()
				&& (currentState != TurretState.HOME && currentState != TurretState.CLIMBING)
				&& Math.abs(goalAngle - this.getAngle()) < 180) {
			currentState = TurretState.USING_VISION;
			moveTowardsGoal();
		} else if (currentState == TurretState.SURVEY) {
			survey();
		} else if (currentState == TurretState.CLIMBING) {
			this.changeToPositionMode();
			goalAngle = 90;
		} else if (currentState == TurretState.HOME) {
			this.changeToPositionMode();
			goalAngle = 0;
		} else {
			if (currentState != TurretState.HOLD) {
				System.out.println("RESET");
				currentState = TurretState.HOLD;
				robotAngleWhenGoalLost = Sensors.getAngle();
				turretGoalWhenLost = goalAngle;
			}
			double robotAngleOffset = -(Sensors.getAngle() - robotAngleWhenGoalLost);
			goalAngle = robotAngleOffset + turretGoalWhenLost;
			correctGoalAngleBounds();
			if (this.getAngle() > goalAngle - 2 && this.getAngle() < goalAngle + 2
					&& !VisionServer.getInstance().hasTargetsToAimAt() && !Shooter.getInstance().isShouldBeShooting()) {
				currentState = TurretState.SURVEY;
			} else {
				this.changeToPositionMode();
			}
		}

		// SET THE MOTORS
		correctGoalAngleBounds();

		OutputSignal signal = this.getController().getOutputSignal(getInputState());

		if (turretMC.getControlMode() == TalonControlMode.Position) {
			// Stop when you are close enough
			if (this.getAngle() > goalAngle - .5 && this.getAngle() < goalAngle + .5) {
				this.turretMC.changeControlMode(TalonControlMode.PercentVbus);
				this.turretMC.set(0);
			} else {
				this.changeToPositionMode();
				turretMC.set((goalAngle / 360) * 14745.6);
			}
		} else {
			turretMC.set(signal.getMotor());
		}

		// Turn off sensor if it is not found
		if (turretMC.isSensorPresent(FeedbackDevice.QuadEncoder) == FeedbackDeviceStatus.FeedbackStatusNotPresent) {
			turretMC.changeControlMode(TalonControlMode.PercentVbus);
			turretMC.set(0);
		}
	}

	private void correctGoalAngleBounds() {
		while (goalAngle > RIGHT_ABSOLUTE_LIMIT || goalAngle < LEFT_ABSOLUTE_LIMIT) {
			if (goalAngle > RIGHT_ABSOLUTE_LIMIT) {
				goalAngle -= 360;
			}
			if (goalAngle < LEFT_ABSOLUTE_LIMIT) {
				goalAngle += 360;
			}
		}
	}

	public void changeToPositionMode() {
		turretMC.changeControlMode(TalonControlMode.Position);
		// position is done on talon.
		this.setController(new BlankController());
	}

	public void survey() {
		turretMC.changeControlMode(TalonControlMode.PercentVbus);
		if (!(this.getController() instanceof TurretVelocitySuveyController)) {
			TurretVelocitySuveyController con = new TurretVelocitySuveyController();
			con.setName("Turret Survey ");
			double predictedGoal = this.turretGoalWhenLost + (Sensors.getAngle() - this.robotAngleWhenGoalLost);
			double error = predictedGoal - this.getAngle();
			if (error > 0) {
				con.setDesiredAngle(this.RIGHT_LIMIT);
			} else {
				con.setDesiredAngle(this.LEFT_LIMIT);
			}
			this.setController(con);
		}
	}

	private void calibrate() {
		if (!hasCalibratedSinceRobotInit) {
			this.turretMC.changeControlMode(TalonControlMode.PercentVbus);
			if (calTimer.get() < 2)
				this.turretMC.set(0);
			else {
				// this.turretMC.set(-.25);
			}
			if (calTimer.get() > 5)
				calTimer.reset();
			this.checkForCalibration();
		}
	}

	private void moveTowardsGoal() {
		changeToPositionMode();
		TargetInfo goal = VisionServer.getInstance().getTarget();
		double goalX = goal.getZ();
		if (lastGoalX != goalX) {
			// System.out.println("SEE NEW GOAL AIMING");
			double degToTurn = ((goalX) / .8) * (VisionServer.FIELD_OF_VIEW_DEGREES);
			goalAngle = this.getAngle() + degToTurn;
			this.resetAngVelocityCounts(); // WORK FASTER!!!!!!!
		} else {
			// System.out.println("have old goal still");
			double predictionOffset = -(Sensors.getAngle() - this.robotAngleAtLastGoal);
			table.putNumber("prediction offset", predictionOffset);
			double angularCompensation = Sensors.getAngularVel() * this.kAngVel;
			goalAngle = lastGoalAngleFromVision + predictionOffset + angularCompensation;
		}
		lastGoalX = goalX;
		lastVisionAngle = goalAngle;
	}

	public void testPosControl() {
		changeToPositionMode();
		goalAngle = table.getNumber("k_aim Turret Pos", 0);
		table.putNumber("k_aim Turret Pos", goalAngle);
	}

	public void turnToAngleAndSurvey(double newGoal) {
		currentState = TurretState.HOLD;
		robotAngleWhenGoalLost = Sensors.getAngle();
		goalAngle = newGoal;
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

		table.putNumber(this.getName() + " power ", this.turretMC.get());
		table.putNumber("turret_angle", getAngle());
		table.putNumber(this.getName() + " Goal Angle", this.goalAngle);
		kAngVel = table.getNumber("k_AngVel", kAngVel);
		table.putNumber("k_AngVel", kAngVel);
		SmartDashboard.putNumber(this.getName() + " get", this.turretMC.get());
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			NetworkTable.getTable("Climber").putNumber(" X", VisionServer.getInstance().getTarget().getZ());
			NetworkTable.getTable("Climber").putNumber(" Hyp", VisionServer.getInstance().getTarget().getHyp());
		} else {
			NetworkTable.getTable("Climber").putNumber(" X", 1000);
			NetworkTable.getTable("Climber").putNumber(" Hyp", 1000);
		}
		table.putNumber(this.getName() + " closed loop error", this.turretMC.getClosedLoopError());
		table.putNumber(this.getName() + " error", (turretMC.getError() / 147445) * 360);
		SmartDashboard.putBoolean(this.getName() + " hall effect", this.hallEffectSensor.get());
	}

	@Override
	public void manualControl() {
		setTurnClockwise(Controls.operatorController.getX(Hand.kLeft));
	}

	public boolean isHallEffectHit() {
		return !this.hallEffectSensor.get();
	}

	private void setTurnClockwise(double power) {
		turretMC.set(power);
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

	private double robotAngleAtLastGoal = getAngle();

	public void resetAngVelocityCounts() {
		robotAngleAtLastGoal = Sensors.getAngle();
		lastGoalAngleFromVision = goalAngle;
	}

	public void callForCalibration() {
		hasCalibratedSinceRobotInit = false;
	}

	public void checkForCalibration() {
		if (!hasCalibratedSinceRobotInit && this.isHallEffectHit()) {
			this.turretMC.set(0);
			hasCalibratedSinceRobotInit = true;
			this.turretMC.setEncPosition(0);
			this.turretMC.setForwardSoftLimit(this.LEFT_ABSOLUTE_LIMIT);
			this.turretMC.setReverseSoftLimit(this.RIGHT_ABSOLUTE_LIMIT);
		}
	}
}
