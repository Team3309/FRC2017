package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.sensors.CounterSensor;
import org.team3309.lib.sensors.FriarGyro;
import org.team3309.lib.sensors.Sensor;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

/**
 * All the sensors on the robot
 * 
 * @author Krager
 *
 */
public class Sensors {

	private static List<Sensor> sensors = new LinkedList<Sensor>();
	private static AHRS navX;
	private static CounterSensor flywheelCounter;
	private static float lastPitch;
	private static FriarGyro gyro;

	static {
		System.out.println("INIT STATIC");
		navX = new AHRS(SPI.Port.kMXP);
		gyro = new FriarGyro(0);
		System.out.println("NAVX");

		System.out.println("Turret");
		flywheelCounter = new CounterSensor(RobotMap.FLYWHEEL_SENSOR);
	}

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
		return gyro.getAngle();
	}

	public static double getAngle1() {
		return navX.getRoll();
	}

	public static double getRoll() {
		return navX.getRoll();
	}

	public static void resetDrive() {

	}

	public static double getFlywheelRPS() {
		double curRPS = 1 / flywheelCounter.getPeriod();
		// flywheel will never jump 100 RPS; make sure sensor isn't acting
		// strange
		if (Math.abs(curRPS) > 500) {
			System.out.println("past " + previousFlywheelCounterRPS);
			System.out.println("cur " + curRPS);
			return previousFlywheelCounterRPS;
		}
		previousFlywheelCounterRPS = curRPS;
		return curRPS;
	}

	public static double getAngularVel() {
		double toReturn = (navX.getPitch() - lastPitch) / 22;
		lastPitch = navX.getPitch();
		return toReturn;
	}

}