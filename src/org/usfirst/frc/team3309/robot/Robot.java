package org.usfirst.frc.team3309.robot;

import org.team3309.lib.tunable.DashboardHelper;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team3309.subsystems.Turbine;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {

	private SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private SendableChooser<AutoRoutine> sideAutoChooser = new SendableChooser<AutoRoutine>();
	public static double LOOP_SPEED_MS = 20;

	public boolean getMatch() {
		return false;
	}

	public void robotInit() {
		// Main Auto Chooser
		mainAutoChooser.addObject("Straight 1 Gear", null);
		mainAutoChooser.addObject("", null);
		mainAutoChooser.addObject("Straight 1 Gear", null);

		// Start the Vision (connect to server)
		(new Thread(VisionServer.getInstance())).start();

		Turret.getInstance().callForCalibration();
	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {
		Systems.init();
	}

	public void autonomousPeriodic() {
		Sensors.read();
		Systems.update();
		Actuators.actuate();
	}

	public void teleopInit() {
		Systems.init();
	}

	public void teleopPeriodic() {
		Sensors.read();
		// Systems.update();

		Flywheel.getInstance().manualControl();
		Hood.getInstance().manualControl(); // updateTeleop, check sensor
		Turbine.getInstance().manualControl(); // first
		Turret.getInstance().manualControl();
		Elevator.getInstance().manualControl();
		Shooter.getInstance().sendToSmartDash();
		Climber.getInstance().manualControl();
		Drive.getInstance().updateTeleop();
		DashboardHelper.updateTunable(Flywheel.getInstance());

		Actuators.actuate();
	}
}
