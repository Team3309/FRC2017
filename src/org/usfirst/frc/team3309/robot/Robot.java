package org.usfirst.frc.team3309.robot;

import org.usfirst.frc.team3309.auto.AllianceColor;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.routines.CompBotOriginalHopperAndGearAuto;
import org.usfirst.frc.team3309.auto.routines.GearAndZoneBoilerSide;
import org.usfirst.frc.team3309.auto.routines.GearIntakeMiddleCurvy;
import org.usfirst.frc.team3309.auto.routines.HopperAndShootCurvyPath;
import org.usfirst.frc.team3309.auto.routines.NoAutoRoutine;
import org.usfirst.frc.team3309.driverstation.Controls;
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

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private static SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private static SendableChooser<AllianceColor> redBlueAutoChooser = new SendableChooser<AllianceColor>();
	private Thread autoThread;
	private Compressor c = new Compressor();
	// private CameraServer cServer = CameraServer.getInstance();
	private AnalogOutput indicatorLight = new AnalogOutput(RobotMap.INDICATOR_LIGHT);

	public void robotInit() {
		// Main Auto Chooser
		Sensors.read();
		mainAutoChooser.addObject("Gear Curvey", new GearIntakeMiddleCurvy());
		mainAutoChooser.addObject("Hopper Curvey", new HopperAndShootCurvyPath());
		mainAutoChooser.addObject("Gear and Hopper Curvey OG", new CompBotOriginalHopperAndGearAuto());
		// mainAutoChooser.addObject("Gear and Hopper Curvey", new
		// HopperAndGearCurvy());
		mainAutoChooser.addObject("Gear and Zone BOILER SIDE", new GearAndZoneBoilerSide());
		mainAutoChooser.addObject("No Auto", new NoAutoRoutine());
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
		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		c.start();
		cam.setFPS(15);
		// cServer.startAutomaticCapture("cam0", 0);
	}

	public void disabledInit() {
		NetworkTable table = NetworkTable.getTable("Drivetrain");
		table.putNumber("k_PWM", 0);
	}

	public void disabledPeriodic() {
		NetworkTable table = NetworkTable.getTable("Drivetrain");
		// Systems.smartDashboard();
		Turret.getInstance().checkForCalibration();
		// System.out.println("RAW " + indicatorLight.getRaw());
		if (Turret.getInstance().hasCalibratedSinceRobotInit)
			indicatorLight.setVoltage(2.5);
		else
			indicatorLight.setVoltage(0);
		Controls.operatorController.setRumble(RumbleType.kLeftRumble, 0);
	}

	public void autonomousInit() {
		Systems.init();
		Drive.getInstance().resetDrive();
		autoThread = (new Thread((AutoRoutine) mainAutoChooser.getSelected()));
		autoThread.start();
	}

	public void autonomousPeriodic() {
		Sensors.read();
		// indicatorLight.setVoltage(5);
		try {
			Systems.update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Actuators.actuate();

	}

	public void teleopInit() {
		Systems.init();
		if (autoThread != null)
			autoThread.stop();
	}

	public void teleopPeriodic() {
		Sensors.read();
		// indicatorLight.setVoltage(5);
		Flywheel.getInstance().updateTeleop();
		// Flywheel.getInstance().sendToSmartDash();
		Hood.getInstance().updateTeleop();
		// Hood.getInstance().sendToSmartDash();
		Turbine.getInstance().updateTeleop(); // first
		Turret.getInstance().updateTeleop();
		// Turret.getInstance().sendToSmartDash();
		Shooter.getInstance().updateTeleop();
		// Shooter.getInstance().sendToSmartDash();
		// Elevator.getInstance().sendToSmartDash();
		Elevator.getInstance().updateTeleop();
		FuelIntake.getInstance().updateTeleop();
		GearIntake.getInstance().updateTeleop();
		// GearIntake.getInstance().sendToSmartDash();
		Climber.getInstance().manualControl();
		// Climber.getInstance().sendToSmartDash();
		Drive.getInstance().updateTeleop();
		// Drive.getInstance().sendToSmartDash();

		Actuators.actuate();
	}

	public static AllianceColor getAllianceColor() {
		return redBlueAutoChooser.getSelected();
	}

	public static AutoRoutine getAutoRoutine() {
		return mainAutoChooser.getSelected();
	}
}
