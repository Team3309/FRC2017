package org.usfirst.frc.team3309.robot;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.AllianceColor;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.routines.CompBotOriginalHopperAndGearAuto;
import org.usfirst.frc.team3309.auto.routines.GearIntakeMiddleCurvy;
import org.usfirst.frc.team3309.auto.routines.GearMiddleStraight;
import org.usfirst.frc.team3309.auto.routines.GearStraightBoilerSide;
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
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private static SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private static SendableChooser<AllianceColor> redBlueAutoChooser = new SendableChooser<AllianceColor>();
	private Thread autoThread;
	private Compressor c = new Compressor();
	// private CameraServer cServer = CameraServer.getInstance();
	public static AnalogOutput indicatorLight = new AnalogOutput(RobotMap.INDICATOR_LIGHT);

	public void robotInit() {
		// Main Auto Chooser
		Sensors.read();
		mainAutoChooser.addObject("Gear Curvey", new GearIntakeMiddleCurvy());
		mainAutoChooser.addObject("Hopper Curvey", new HopperAndShootCurvyPath());
		mainAutoChooser.addObject("Gear and Hopper Curvey OG", new CompBotOriginalHopperAndGearAuto());
		mainAutoChooser.addObject("Gear BOILER SIDE Straight", new GearStraightBoilerSide());
		mainAutoChooser.addObject("Gear Middle Straight", new GearMiddleStraight());
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
	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {
		// Systems.smartDashboard();
		Turret.getInstance().checkForCalibration();
		if (Turret.getInstance().hasCalibratedSinceRobotInit)
			indicatorLight.setVoltage(1.875);
		else
			indicatorLight.setVoltage(0);
		Controls.operatorController.setRumble(RumbleType.kLeftRumble, 0);
		Controls.operatorController.setRumble(RumbleType.kRightRumble, 0);
		Controls.driverController.setRumble(RumbleType.kLeftRumble, 0);
		Controls.driverController.setRumble(RumbleType.kRightRumble, 0);
	}

	public void autonomousInit() {
		Systems.init();
		Drive.getInstance().resetDrive();
		autoThread = (new Thread((AutoRoutine) mainAutoChooser.getSelected()));
		autoThread.start();
	}

	public void autonomousPeriodic() {
		Drive.getInstance().setLowGear();
		Sensors.read();
		try {
			Systems.update();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//LightRing.getInstance().update();
		Systems.smartDashboard();
		Actuators.actuate();
	}

	public void teleopInit() {
		Systems.init();
		if (autoThread != null)
			autoThread.stop();
	}

	private KragerTimer profileTimer = new KragerTimer();
	private boolean isSmartDash = true;
	private boolean isActuating = true;

	public void teleopPeriodic() {
		profileTimer.reset();
		profileTimer.start();

		profileTimer.log("NEW LOOP -------------------------");
		Sensors.read();

		profileTimer.log("Sensors");
		if (isActuating) {
			Flywheel.getInstance().updateTeleop();
			Hood.getInstance().updateTeleop();
			Turbine.getInstance().updateTeleop();
			Turret.getInstance().updateTeleop();
			Shooter.getInstance().updateTeleop();
			LightRing.getInstance().update();
			profileTimer.log("Shooter");
			Elevator.getInstance().updateTeleop();
			FuelIntake.getInstance().updateTeleop();
			GearIntake.getInstance().updateTeleop();
			Climber.getInstance().manualControl();
			profileTimer.log("Climber");
			Drive.getInstance().updateTeleop();
		}
		profileTimer.log("Subsystems");
		if (isSmartDash) {
			Flywheel.getInstance().sendToSmartDash();
			Hood.getInstance().sendToSmartDash();
			Turret.getInstance().sendToSmartDash();
			Shooter.getInstance().sendToSmartDash();
			Elevator.getInstance().sendToSmartDash();
			GearIntake.getInstance().sendToSmartDash();
			Climber.getInstance().sendToSmartDash();
			Drive.getInstance().sendToSmartDash();
		}
		profileTimer.log("Smart Dash");

		Actuators.actuate();
		profileTimer.log("Actuators");
		// KragerTimer.delayMS(50);
	}

	public static AllianceColor getAllianceColor() {
		return redBlueAutoChooser.getSelected();
	}

	public static AutoRoutine getAutoRoutine() {
		return mainAutoChooser.getSelected();
	}
}
