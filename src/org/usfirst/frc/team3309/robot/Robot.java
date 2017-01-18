package org.usfirst.frc.team3309.robot;

import org.team3309.lib.auto.AutoRoutine;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {

	private SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private SendableChooser<AutoRoutine> sideAutoChooser = new SendableChooser<AutoRoutine>();

	public void robotInit() {

		// Main Auto Chooser
		mainAutoChooser.addObject("Straight 1 Gear", null);
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {

	}

	public void autonomousPeriodic() {
		Sensors.read();

		Actuators.actuate();
	}

	public void teleopInit() {

	}

	public void teleopPeriodic() {
		Sensors.read();

		Actuators.actuate();
	}
}
