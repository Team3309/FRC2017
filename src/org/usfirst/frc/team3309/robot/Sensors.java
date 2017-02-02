package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.sensors.CounterSensor;
import org.team3309.lib.sensors.EncoderSensor;
import org.team3309.lib.sensors.NavX;
import org.team3309.lib.sensors.Sensor;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * All the sensors on the robot
 * 
 * @author Krager
 *
 */
public class Sensors {

	private static List<Sensor> sensors = new LinkedList<Sensor>();
	private static NavX navX;
	private static EncoderSensor leftDrive;
	private static EncoderSensor rightDrive;
	private static EncoderSensor turretSensor;
	private static CounterSensor flywheelCounter;
	private static DigitalInput turretRightHallEffect;
	private static DigitalInput turretLeftHallEffect;

	static {
		System.out.println("INIT STATIC");
		navX = new NavX();
		System.out.println("NAVX");
		leftDrive = new EncoderSensor(RobotMap.LEFT_ENCODER_A, RobotMap.LEFT_ENCODER_B, true);
		rightDrive = new EncoderSensor(RobotMap.RIGHT_ENCODER_A, RobotMap.RIGHT_ENCODER_B, false);
		turretSensor = new EncoderSensor(RobotMap.TURRET_ENCODER_A, RobotMap.TURRET_ENCODER_B, false);
		System.out.println("Turret");
		flywheelCounter = new CounterSensor(RobotMap.FLYWHEEL_SENSOR);
		turretRightHallEffect = new DigitalInput(RobotMap.TURRET_RIGHT_LIMIT);
		turretLeftHallEffect = new DigitalInput(RobotMap.TURRET_LEFT_LIMIT);
	}
	/**
	 * Degrees in each encoder count
	 */
	private static final double TURRET_DEGREES_PER_ENCODER = .076;
	private static double previousFlywheelCounterRPS = 0;

	public static void read() {
		try {
			for (Sensor x : sensors) {
				x.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addSensor(Sensor act) {
		Sensors.sensors.add(act);
	}

	public static double getAngle() {
		return navX.getAngle();
	}

	public static int getLeftDrive() {
		return leftDrive.getPosition();
	}

	public static int getRightDrive() {
		return rightDrive.getPosition();
	}

	public static double getRoll() {
		return navX.getRoll();
	}

	public static void resetDrive() {
		rightDrive.reset();
		leftDrive.reset();
	}

	public static double getFlywheelRPS() {
		double curRPS = 1 / flywheelCounter.getPeriod();
		// flywheel will never jump 100 RPS; make sure sensor isn't acting
		// strange
		if (Math.abs(curRPS - previousFlywheelCounterRPS) > 100) {
			curRPS = previousFlywheelCounterRPS;
		}
		previousFlywheelCounterRPS = curRPS;
		return curRPS;
	}

	public static boolean isTurretLeftLimitHit() {
		return turretLeftHallEffect.get();
	}

	public static boolean isTurretRightLimitHit() {
		return turretRightHallEffect.get();
	}

	public static double getTurretAngle() {
		if (turretRightHallEffect.get()) {

		} else if (turretLeftHallEffect.get()) {
			turretSensor.reset();
		}
		double curTurretEncoders = turretSensor.getPosition();
		double curTurretDegrees = curTurretEncoders * TURRET_DEGREES_PER_ENCODER;
		return curTurretDegrees;
	}

	public static double getTurretAngleVelocity() {
		return turretSensor.getRate();
	}

	public static double getAngularVel() {
		return navX.getAngularVel();
	}

	public static double getLeftDriveVel() {
		return leftDrive.getRate();
	}

	public static double getRightDriveVel() {
		return rightDrive.getRate();
	}
}