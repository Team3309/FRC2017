package org.usfirst.frc.team3309.robot;

import org.usfirst.frc.team3309.auto.AllianceColor;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.routines.GearIntakeMiddleCurvy;
import org.usfirst.frc.team3309.auto.routines.HopperAndGearCurvy;
import org.usfirst.frc.team3309.auto.routines.HopperAndShootCurvyPath;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team3309.subsystems.FuelIntake;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.subsystems.Turbine;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;
import org.usfirst.frc.team3309.vision.VisionServer;

import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private static SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private static SendableChooser<AllianceColor> redBlueAutoChooser = new SendableChooser<AllianceColor>();
	private Thread autoThread;
	private Compressor c = new Compressor();
	private AnalogOutput indicatorLight = new AnalogOutput(RobotMap.INDICATOR_LIGHT);

	public void robotInit() {
		// Main Auto Chooser
		Sensors.read();
		mainAutoChooser.addObject("Gear Curvey", new GearIntakeMiddleCurvy());
		mainAutoChooser.addObject("Hopper Curvey", new HopperAndShootCurvyPath());
		mainAutoChooser.addObject("Gear and Hopper Curvey", new HopperAndGearCurvy());

		redBlueAutoChooser.addObject("Red", AllianceColor.RED);
		redBlueAutoChooser.addObject("Blue", AllianceColor.BLUE);
		SmartDashboard.putData("Color", redBlueAutoChooser);
		SmartDashboard.putData("AUTO", mainAutoChooser);

		// Start the Vision (connect to server)
		Climber.getInstance();
		Drive.getInstance();
		Elevator.getInstance();
		FuelIntake.getInstance();
		GearIntake.getInstance();
		Shooter.getInstance();
		Turbine.getInstance();
		Flywheel.getInstance();
		Hood.getInstance();
		Turret.getInstance().callForCalibration();
		(new Thread(VisionServer.getInstance())).start();

		c.start();
	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {
		Turret.getInstance().checkForCalibration();
		if (Turret.getInstance().hasCalibratedSinceRobotInit)
			indicatorLight.setVoltage(4096);
		else
			indicatorLight.setVoltage(2048);
	}

	public void autonomousInit() {
		Systems.init();
		Drive.getInstance().resetDrive();
		autoThread = (new Thread((AutoRoutine) mainAutoChooser.getSelected()));
		autoThread.start();
	}

	public void autonomousPeriodic() {
		Sensors.read();
		indicatorLight.setVoltage(0);
		try {
			Systems.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Flywheel.getInstance().sendToSmartDash();
		Hood.getInstance().sendToSmartDash();
		Drive.getInstance().sendToSmartDash();
		Actuators.actuate();
	}

	public void teleopInit() {
		Systems.init();
		if (autoThread != null)
			autoThread.stop();
		System.out.println("DONE INIT");
	}

	public void teleopPeriodic() {
		Sensors.read();
		// Systems.update();
		indicatorLight.setVoltage(0);
		Flywheel.getInstance().updateTeleop();
		Flywheel.getInstance().sendToSmartDash();
		Hood.getInstance().updateTeleop(); // updateTeleop, check sensor
		Hood.getInstance().sendToSmartDash();
		Turbine.getInstance().manualControl(); // first
		Turret.getInstance().updateTeleop();
		Turret.getInstance().sendToSmartDash();
		Shooter.getInstance().updateTeleop();
		Elevator.getInstance().manualControl();
		Elevator.getInstance().sendToSmartDash();
		FuelIntake.getInstance().updateTeleop();
		// Shooter.getInstance().sendToSmartDash();
		// GearIntake.getInstance().updateTeleop();
		Climber.getInstance().manualControl();
		Climber.getInstance().sendToSmartDash();
		Drive.getInstance().updateTeleop();
		Drive.getInstance().sendToSmartDash();
		// DashboardHelper.updateTunable(Drive.getInstance());

		Actuators.actuate();
	}

	public static AllianceColor getAllianceColor() {
		return redBlueAutoChooser.getSelected();
	}

	public static AutoRoutine getAutoRoutine() {
		return mainAutoChooser.getSelected();
	}
}
