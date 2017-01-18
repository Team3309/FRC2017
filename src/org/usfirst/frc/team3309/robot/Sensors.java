package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.sensors.EncoderSensor;
import org.team3309.lib.sensors.NavX;
import org.team3309.lib.sensors.Sensor;

/**
 * All the sensors on the robot
 * 
 * @author Krager
 *
 */
public class Sensors {
	private static List<Sensor> sensors = new LinkedList<Sensor>();
	private static NavX navX = new NavX();
	private static EncoderSensor leftDrive = new EncoderSensor(RobotMap.LEFT_ENCODER_A, RobotMap.LEFT_ENCODER_B, true);
	private static EncoderSensor rightDrive = new EncoderSensor(RobotMap.RIGHT_ENCODER_A, RobotMap.LEFT_ENCODER_B,
			false);

	public static void read() {
		for (Sensor x : sensors)
			x.read();
	}

	public static void addSensor(Sensor act) {
		Sensors.sensors.add(act);
	}

	public static double getAngle() {
		return navX.getAngle();
	}

	public static int getLeftDrive() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int getRightDrive() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static double getRoll() {
		// TODO Auto-generated method stub
		return navX.getRoll();
	}

	public static void resetDrive() {
		rightDrive.reset();
		leftDrive.reset();
	}
}