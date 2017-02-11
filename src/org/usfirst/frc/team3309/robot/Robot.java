package org.usfirst.frc.team3309.robot;

import org.team3309.lib.actuators.ContinuousRotationServo;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.Hopper;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
		Turret.getInstance().logger.close();
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {
		Systems.initAuto();
	}

	public void autonomousPeriodic() {
		Sensors.read();
		Systems.upateAuto();
		Actuators.actuate();
	}

	public void teleopInit() {
		Systems.initTeleop();
	}

	

	public void teleopPeriodic() {
		Sensors.read();
		Systems.updateTeleop();
		Elevator.getInstance().manualControl();
		Shooter.getInstance().sendToSmartDash();
		Actuators.actuate();
	}
}
