package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.tunable.Dashboard;
import org.team3309.lib.tunable.DashboardHelper;
import org.team3309.lib.tunable.IDashboard;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Hood extends ControlledSubsystem implements IDashboard {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static final double MIN_ANGLE = 20;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 2280;
	private static final double MIN_RAW = 3910; // 3400 is comp, 3900 is comp
	private static final double MAX_POW = .1;
	private static final double MIN_POW_UP = .0001;
	private static final double MIN_POW_DOWN = .015;
	private static final double MIN_POW_DOWN_LOWER_BOUND = -.002;
	private AnalogInput hoodSensor = new AnalogInput(RobotMap.HOOD_SENSOR);
	private double goalAngle = 0;
	private double pastGoal = goalAngle;
	private double lastVisionAngle = 100;
	@Dashboard(tunable = false)
	private double curAngle = 0;
	private ContinuousRotationServo servo = new ContinuousRotationServo(RobotMap.SERVO);
	private NetworkTable table = NetworkTable.getTable("Hood");
	// private PIDPositionController upController = new
	// PIDPositionController(.001, 0, 0);
	// private PIDPositionController downController = new
	// PIDPositionController(.001, 0, 0);

	private Hood() {
		super("Hood");
		// NetworkTable.getTable("Climber").putNumber("k_pow", 0);
		this.setController(new PIDPositionController(.00018, .0000004, -.00011));

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
		} else if (VisionServer.getInstance().hasTargetsToAimAt() && Controls.operatorController.getBButton()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			System.out.println("VISION HOOD " + goalAngle);
			lastVisionAngle = goalAngle;
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
		//System.out.println("power " + power);
	//	System.out.println("base " + (base * KragerMath.sign(error)) + " integral " + (integral * kI));
		this.setHood(power);
	}

	public void bangBang() {
		// this.testPosControl();
		double error = this.goalAngle - this.getAngle();
		double power = 0;
		if (error > 0) {
			power = .003;
		} else
			power = -.003;
		this.setHood(power);
	}

	@Override
	public void updateAuto() {
		// updateTeleop(); // just needs to do what vision says
		goalAngle = 600;
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

		DashboardHelper.updateTunable(getController());
		this.getController().sendToSmartDash();
		table.putNumber(this.getName() + " pow", this.servo.getSpeed());
		table.putNumber("angle", getAngle());
		table.putNumber("aim", goalAngle);
		table.putNumber("raw angle", getAngleRaw());

	}

	@Override
	public void manualControl() {
		// setHood(table.getNumber("k_pow", 0));
		if (Controls.operatorController.getAButton())
			setHood(.02);
		else
			setHood(0);
		// else if (Controls.operatorController.getXButton())
		// setHood(-.02);
		// else'
		// setHood(0.0);
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

	@Override
	public String getTableName() {
		return "Hood";
	}

	@Override
	public String getObjectName() {
		return "";
	}
}
