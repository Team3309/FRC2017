package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.actuators.ContinuousRotationServo;
import org.team3309.lib.controllers.generic.PIDPositionController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.tunable.IDashboard;
import org.team3309.lib.tunable.Dashboard;
import org.team3309.lib.tunable.DashboardHelper;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hood extends ControlledSubsystem implements IDashboard {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static final double MIN_ANGLE = 20;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 2280;
	private static final double MIN_RAW = 3400;
	private static final double MAX_POW = .1;
	private static final double MIN_POW = .005;

	private AnalogInput hoodSensor = new AnalogInput(RobotMap.HOOD_SENSOR);
	private double goalAngle = 0;
	private double pastGoal = goalAngle;
	private double lastVisionAngle = 0;
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
		table.putNumber("k_pow", 0);
		this.setController(new PIDPositionController(.00011, .0000003, -.000139));

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
		double output = 0;

		// Find aim angle
		if (VisionServer.getInstance().hasTargetsToAimAt()) {
			goalAngle = VisionServer.getInstance().getHoodAngle();
			lastVisionAngle = goalAngle;
		} else {
			goalAngle = lastVisionAngle;
		}
		testPosControl();
		if (goalAngle != pastGoal)
			this.getController().reset();
		if (goalAngle >= 1.0) {
			output = this.getController().getOutputSignal(getInputState()).getMotor();
		}

		if ((curAngle > MAX_ANGLE && output > .2) || (curAngle < MIN_ANGLE && output < -.2)) {
			output = 0;
			// TODO POSSIBLE RESET FOR DIFFERENT ANGLES
		}

		if (Math.abs(goalAngle - getAngle()) < 50) {
			getController().reset();
			if (Math.abs(goalAngle - getAngle()) < 25) {
				output = 0;
			} else {
				this.setHood(.0025 * KragerMath.sign(goalAngle - getAngle()));
				return;
			}
		}

		if (Math.abs(output) > MAX_POW)
			output = MAX_POW * KragerMath.sign(output);
		if (Math.abs(output) < MIN_POW)
			output = 0;
		pastGoal = goalAngle;
		this.setHood(output);
	}

	@Override
	public void updateAuto() {
		updateTeleop(); // just needs to do what vision says
	}

	public void testPosControl() {
		goalAngle = table.getNumber("k_aim Hood Angle", 20);
		table.putNumber("k_aim Hood Angle", goalAngle);
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
		table.putNumber("raw angle", getAngleRaw());
	}

	@Override
	public void manualControl() {
		// setHood(table.getNumber("k_pow", 0));
		// if (Controls.operatorController.getAButton())
		// setHood(.02);
		// else if (Controls.operatorController.getXButton())
		// setHood(-.02);
		// else'
		if (Controls.operatorController.getAButton())
			setHood(Controls.operatorController.getX(Hand.kRight) / 100);
		else
			setHood(0.0);
		System.out.println(servo.getSpeed());
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
