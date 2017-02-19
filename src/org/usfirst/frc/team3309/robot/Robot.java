package org.usfirst.frc.team3309.robot;

import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.routines.GearIntakeMiddle;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team3309.subsystems.FuelIntake;
import org.usfirst.frc.team3309.subsystems.Turbine;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private SendableChooser<AutoRoutine> sideAutoChooser = new SendableChooser<AutoRoutine>();
	public static double LOOP_SPEED_MS = 20;

	public boolean getMatch() {
		return false;
	}

	public void robotInit() {
		// Main Auto Chooser
		mainAutoChooser.addObject("Straight 1 Gear", new GearIntakeMiddle());
		SmartDashboard.putData("AUTO", mainAutoChooser);
		// Start the Vision (connect to server)
		(new Thread(VisionServer.getInstance())).start();
		Sensors.read();
		Turret.getInstance().callForCalibration();
	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {
		Systems.init();
		Drive.getInstance().resetDrive();
		(new Thread((AutoRoutine) mainAutoChooser.getSelected())).start();
	}

	public void autonomousPeriodic() {
		Sensors.read();
		Systems.update();
		Drive.getInstance().sendToSmartDash();
		Actuators.actuate();
	}

	public void teleopInit() {
		Systems.init();
		System.out.println("DONE INIT");
	}

	public void teleopPeriodic() {
		Sensors.read();
		// Systems.update();

		Flywheel.getInstance().testVel();
		Flywheel.getInstance().sendToSmartDash();
		Hood.getInstance().updateTeleop(); // updateTeleop, check sensor
		Hood.getInstance().sendToSmartDash();
		Turbine.getInstance().manualControl(); // first
		// Turret.getInstance().updateTeleop();
		// Turret.getInstance().sendToSmartDash();

		Elevator.getInstance().manualControl();
		Elevator.getInstance().sendToSmartDash();
		FuelIntake.getInstance().updateTeleop();
		// Shooter.getInstance().sendToSmartDash();
		Climber.getInstance().manualControl();
		Drive.getInstance().updateTeleop();
		Drive.getInstance().sendToSmartDash();
		// DashboardHelper.updateTunable(Drive.getInstance());

		Actuators.actuate();
	}
}
