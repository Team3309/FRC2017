package org.usfirst.frc.team3309.robot;

import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {

	private SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private SendableChooser<AutoRoutine> sideAutoChooser = new SendableChooser<AutoRoutine>();

	public void robotInit() {
		// Main Auto Chooser
		mainAutoChooser.addObject("Straight 1 Gear", null);

		// Start the Vision (connect to server)
		new Thread(VisionServer.getInstance()).start();
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
		Drive.getInstance().updateTeleop();
		Actuators.actuate();
	}
}
