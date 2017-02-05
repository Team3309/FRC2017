package org.usfirst.frc.team3309.robot;

import org.team3309.lib.communications.BlackBox;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.Hopper;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {

	private SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private SendableChooser<AutoRoutine> sideAutoChooser = new SendableChooser<AutoRoutine>();
	public static double LOOP_SPEED_MS = 20;
	public String TIMEZONE = "SS"; // to be moved later
	public String[] LOG_HEADER = { "Timestamp", "RPS" };

	public boolean getMatch() {
		return false;
	}

	public void robotInit() {
		// Main Auto Chooser
		mainAutoChooser.addObject("Straight 1 Gear", null);

		// Start the Vision (connect to server)
		new Thread(VisionServer.getInstance()).start();

		BlackBox.initializeLog(LOG_HEADER, getMatch(), false); // starts logging
																// values
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
		BlackBox.writeString("TELEOP STARTED");
		Shooter.getInstance().initTeleop();
	}

	public void teleopPeriodic() {
		Sensors.read();
		Drive.getInstance().updateTeleop();
		Drive.getInstance().sendToSmartDash();
		Hopper.getInstance().updateTeleop();
		Shooter.getInstance().updateTeleop();
		Shooter.getInstance().sendToSmartDash();
		Climber.getInstance().updateTeleop();
		GearIntake.getInstance().updateTeleop();
		Actuators.actuate();

	}
}
