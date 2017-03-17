package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Hood extends ControlledSubsystem {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static final double MIN_ANGLE = 20;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 2280;
	private static final double MIN_RAW = 3900; // 3400 is comp, 3900 is prac
	private static final double MAX_POW = .1;
	private static final double MIN_POW_UP = .0001;
	private static final double MIN_POW_DOWN = .015;
	private static final double MIN_POW_DOWN_LOWER_BOUND = -.002;
	private AnalogInput hoodSensor = new AnalogInput(RobotMap.HOOD_SENSOR);
	private double goalAngle = 0;
	private double pastGoal = goalAngle;
	private double lastVisionAngle = 100;
	private double curAngle = 0;
	private ContinuousRotationServo servo = new ContinuousRotationServo(RobotMap.SERVO);
	private NetworkTable table = NetworkTable.getTable("Hood");
	// private PIDPositionController upController = new
	// PIDPositionController(.001, 0, 0);
	// private PIDPositionController downController = new
	// PIDPositionController(.001, 0, 0);

	private Hood() {
		super("Hood");
		NetworkTable.getTable("Climber").putNumber("k_pow", 0);
		this.setController(new PIDPositionController(.00018, .0000004, -.00011));
		table.putNumber("k_pow", 0);
	}

	/**
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Hood getInstance() {
		if (mHood == null) {
			mHood = new Hood();
		}
		return mHood;
	}

	public void updateTeleop() {
		curAngle = this.getAngle();

		// Find aim angle
		if (Controls.operatorController.getYButton()) {
			testPosControl();
		} else if (Controls.operatorController.getBButton()) {
			if (VisionServer.getInstance().hasTargetsToAimAt()) {
				goalAngle = VisionServer.getInstance().getHoodAngle();
				System.out.println("VISION HOOD " + goalAngle);
				lastVisionAngle = goalAngle;
			} else {
				goalAngle = lastVisionAngle;
			}
		} else {
			testPosControl();
			// goalAngle = lastVisionAngle;
		}

		if (Shooter.getInstance().isShouldBeShooting()) {
			this.bangBang();
		} else {
			getToGoalAndStop();
		}

		pastGoal = goalAngle;
	}

	private double integral = 0;

	public void getToGoalAndStop() {
		// testPosControl();
		double error = this.goalAngle - this.getAngle();
		double base = this.MIN_POW_DOWN;

		if (Math.abs(error) > 500) {
			integral = 0;
			base = .09;
		} else if (Math.abs(error) < 50) {
			base = 0;
			integral = 0;
		} else if (Math.abs(error) < 100) {
			integral += error;
			base = this.MIN_POW_DOWN * .03;
		}

		double kI = .0000002;

		double power = (integral * kI) + (base * KragerMath.sign(error));
		table.putNumber("error", error);
		table.putNumber("POW", power);
		table.putNumber("integral", (integral * kI));
		table.putNumber("base", (base * KragerMath.sign(error)));
		// System.out.println("power " + power);
		// System.out.println("base " + (base * KragerMath.sign(error)) + "
		// integral " + (integral * kI));
		this.setHood(power);
	}

	public void bangBang() {
		// this.testPosControl();
		double error = this.goalAngle - this.getAngle();
		double power = 0;
		if (error > 0) {
			power = .0001;
		} else
			power = -.0001;
		this.setHood(power);
	}

	@Override
	public void updateAuto() {
		// updateTeleop(); // just needs to do what vision says
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			System.out.println("VISION HOOD " + goalAngle);
			lastVisionAngle = goalAngle;
		} else {
			goalAngle = lastVisionAngle;
		}
		if (Shooter.getInstance().isShouldBeShooting()) {
			this.bangBang();
		} else {
			getToGoalAndStop();
		}
	}

	public void testPosControl() {
		goalAngle = NetworkTable.getTable("Climber").getNumber("k_aim Hood Angle", 20);
		NetworkTable.getTable("Climber").putNumber("k_aim Hood Angle", goalAngle);
	}

	@Override
	public InputState getInputState() {
		InputState input = new InputState();
		input.setError(goalAngle - getAngle());
		return input;
	}

	@Override
	public void sendToSmartDash() {
		this.getController().sendToSmartDash();
		table.putNumber(this.getName() + " pow", this.servo.getSpeed());
		table.putNumber("angle", getAngle());
		table.putNumber("aim", goalAngle);
		table.putNumber("raw angle", getAngleRaw());

	}

	@Override
	public void manualControl() {
		setHood(table.getNumber("k_pow", 0));
	}

	@Override
	public void initTeleop() {
		goalAngle = DEFAULT_ANGLE;
	}

	@Override
	public void initAuto() {

	}

	public double getAngle() {
		// TODO get Angle
		return -((hoodSensor.getValue() - MIN_RAW));
	}

	public double getAngleRaw() {
		// TODO get Angle
		return hoodSensor.getValue();
	}

	private void setHood(double power) {
		servo.set(power);
	}
}
