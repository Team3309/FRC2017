package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.sensors.CounterSensor;
import org.team3309.lib.sensors.NavX;
import org.team3309.lib.sensors.Sensor;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * All the sensors on the robot
 * 
 * @author Krager
 *
 */
public class Sensors {

	private static List<Sensor> sensors = new LinkedList<Sensor>();
	private static NavX navX;
	private static CounterSensor flywheelCounter;

	static {
		System.out.println("INIT STATIC");
		navX = new NavX();
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
		return navX.getAngle();
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
		if (Math.abs(curRPS - previousFlywheelCounterRPS) > 100) {
			curRPS = previousFlywheelCounterRPS;
		}
		previousFlywheelCounterRPS = curRPS;
		return curRPS;
	}

	public static double getAngularVel() {
		return navX.getAngularVel();
	}

}