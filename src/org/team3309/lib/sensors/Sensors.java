package org.team3309.lib.sensors;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.actuators.Actuator;
import org.team3309.lib.actuators.Actuators;
import org.usfirst.frc.team3309.robot.Constants;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.robot.SensorDoesNotReturnException;
import org.usfirst.frc.team3309.subsystems.Drive;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

/**
 * All the sensors on the robot
 * 
 * @author Krager
 *
 */
public class Sensors {
	private static List<Sensor> sensors = new LinkedList<Sensor>();

	public static void read() {
		for (Sensor x : sensors)
			x.read();
	}

	public static void addSensor(Sensor act) {
		Sensors.sensors.add(act);
	}

	// Drive
	private static EncoderSensor leftDrive;
	private static EncoderSensor rightDrive;
	private static double pastLeftEncoder = 0.0;
	private static double pastRightEncoder = 0.0;
	private static double rightBadCounts = 0;
	private static double leftBadCounts = 0;
	private static NavX navX;
	// Shooter
	private static CounterSensor flywheelEncoder;
	private static CounterSensor hoodEncoder;
	private static double pastFlywheelRPS = 0.0;

	private static PowerDistributionPanel pdp = new PowerDistributionPanel();

	public static void init() {
	}

	static {
		System.out.println("STARTING INIT");
		rightDrive = new EncoderSensor(RobotMap.ENCODERS_A_RIGHT_DRIVE_DIGITAL, RobotMap.ENCODERS_B_RIGHT_DRIVE_DIGITAL,
				false);
		leftDrive = new EncoderSensor(RobotMap.ENCODERS_A_LEFT_DRIVE_DIGITAL, RobotMap.ENCODERS_B_LEFT_DRIVE_DIGITAL,
				false);
		flywheelEncoder = new CounterSensor(RobotMap.SHOOTER_OPTICAL_SENSOR);
		// navX = new AHRS(SerialPort.Port.kMXP);
		navX = new NavX();
		hoodEncoder = new CounterSensor(new DigitalInput(RobotMap.HOOD_ABS));
		hoodEncoder.counter.setSemiPeriodMode(true);
		hoodEncoder.counter.setReverseDirection(false);
		hoodEncoder.counter.reset();
		pdp = new PowerDistributionPanel();
	}

	public static double getAngularVel() {
		return navX.getAngularVel();
	}

	public static double getAngle() {
		// return navX.getYaw();
		return navX.getAngle();
	}

	public static double getRoll() {
		return navX.getRoll();
	}

	public static void resetDrive() {
		rightDrive.reset();
		leftDrive.reset();
	}

	public static double getRightDrive() throws SensorDoesNotReturnException {
		double curEncoder = rightDrive.getPosition() / 100;
		// if (Math.abs(curEncoder - pastRightEncoder) > 5 &&
		// Drive.getInstance().getRightPower() > .7) {
		// rightBadCounts++;
		// }
		// if (rightBadCounts > 100) {
		// throw new SensorDoesNotReturnException();
		// }
		pastRightEncoder = curEncoder;
		return curEncoder;
	}

	public static double getRightDriveVel() throws SensorDoesNotReturnException {
		// if (rightBadCounts > 100) {
		// throw new SensorDoesNotReturnException();
		// }
		return rightDrive.getRate() / 100;
	}

	public static double getLeftDrive() throws SensorDoesNotReturnException {
		double curEncoder = leftDrive.getPosition() / 100;
		// if (Math.abs(curEncoder - pastLeftEncoder) > 5 &&
		// Drive.getInstance().getLeftPower() > .7) {
		// leftBadCounts++;
		// }
		// if (leftBadCounts > 100) {
		// throw new SensorDoesNotReturnException();
		// }
		pastLeftEncoder = curEncoder;
		return curEncoder;
	}

	public static double getLeftDriveVel() throws SensorDoesNotReturnException {
		// if (leftBadCounts > 100) {
		// throw new SensorDoesNotReturnException();
		// }
		return -leftDrive.getRate() / 100;
	}

	// Shooter
	public static double getShooterRPS() throws SensorDoesNotReturnException {
		double currentShooter = (1 / flywheelEncoder.getPeriod());
		if (Math.abs(1 / flywheelEncoder.getPeriod()) - (pastFlywheelRPS) > 350)
			throw new SensorDoesNotReturnException();

		pastFlywheelRPS = currentShooter;
		return currentShooter;
	}

	public static double getHoodAngle() throws SensorDoesNotReturnException {
		double hoodAngle = Constants.getHoodBottomValue()
				- ((1000000.0 * (hoodEncoder.getPeriod())) * (360.0 / 4096.0));
		int counts = 0;
		while (hoodAngle > 360 || hoodAngle < -20) {
			if (hoodAngle > 360) {
				hoodAngle -= 360;
			}
			if (hoodAngle < -20) {
				hoodAngle += 360;
			}
			if (counts > 1000) { // if its infinity, or just way off
				throw new SensorDoesNotReturnException();
			}
			counts++;
		}
		return hoodAngle;
	}
}