package org.usfirst.frc.team339.robot;

import org.team3309.lib.actuators.Actuators;
import org.usfirst.frc.team3309.driverstation.XboxController;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Robot extends IterativeRobot {

	public void robotInit() {
		;
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {

	}

	public void autonomousPeriodic() {

	}

	public void teleopInit() {

	}

	public void teleopPeriodic() {
		Sensors.read();

		Actuators.actuate();
	}
}
