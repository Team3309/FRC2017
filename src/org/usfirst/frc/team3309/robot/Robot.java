package org.usfirst.frc.team3309.robot;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	public void robotInit() {
		
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
