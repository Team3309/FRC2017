package org.usfirst.frc.team3309.robot;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.AllianceColor;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.routines.BoilerGearAndCloseHopperStraight;
import org.usfirst.frc.team3309.auto.routines.BoilerGearStraightAndPreloads;
import org.usfirst.frc.team3309.auto.routines.BoilerSideGearAndIntakeAndShoot;
import org.usfirst.frc.team3309.auto.routines.CloseHopperAndShootCurvy;
import org.usfirst.frc.team3309.auto.routines.CompBotOriginalHopperAndGearAuto;
import org.usfirst.frc.team3309.auto.routines.FarHopperAndShootCurvyPath;
import org.usfirst.frc.team3309.auto.routines.FarSideGearStraight;
import org.usfirst.frc.team3309.auto.routines.GearMiddleStraight;
import org.usfirst.frc.team3309.auto.routines.HopperAndShootCurvyPath;
import org.usfirst.frc.team3309.auto.routines.NoAutoRoutine;
import org.usfirst.frc.team3309.auto.routines.TurnInPlaceAutoRoutine;
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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private static SendableChooser<AutoRoutine> mainAutoChooser = new SendableChooser<AutoRoutine>();
	private static SendableChooser<AllianceColor> redBlueAutoChooser = new SendableChooser<AllianceColor>();
	private Thread autoThread;
	private Compressor c = new Compressor();
	// private CameraServer cServer = CameraServer.getInstance();
	public static AnalogOutput indicatorLight = new AnalogOutput(RobotMap.INDICATOR_LIGHT);
	
	@Override
	public void robotInit() {
		// Main Auto Chooser
		Sensors.read();
		mainAutoChooser.addObject("Close Hopper", new CloseHopperAndShootCurvy());
		mainAutoChooser.addObject("Far Hopper", new FarHopperAndShootCurvyPath());
		mainAutoChooser.addObject("Far Side Gear", new FarSideGearStraight());
		mainAutoChooser.addObject("Boiler Gear and Close Hopper Curvy", new CompBotOriginalHopperAndGearAuto());
		mainAutoChooser.addObject("Boiler Gear and Close Hopper Straight", new BoilerGearAndCloseHopperStraight());
		mainAutoChooser.addObject("Boiler Gear Straight, Shoot Preloads", new BoilerGearStraightAndPreloads());
		mainAutoChooser.addObject("Boiler Gear,Intake, and Shoot", new BoilerSideGearAndIntakeAndShoot());
		mainAutoChooser.addObject("Middle Gear Straight, Shoot Preloads", new GearMiddleStraight());
		mainAutoChooser.addObject("TurnInPlace", new TurnInPlaceAutoRoutine());
		mainAutoChooser.addObject("Close Hopper PreOC", new HopperAndShootCurvyPath());
		mainAutoChooser.addObject("No Auto", new NoAutoRoutine());
		redBlueAutoChooser.addObject("Red", AllianceColor.RED);
		redBlueAutoChooser.addObject("Blue", AllianceColor.BLUE);
		SmartDashboard.putData("Color", redBlueAutoChooser);
		SmartDashboard.putData("AUTO", mainAutoChooser);

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

		// Start the Vision (connect to server)
		(new Thread(VisionServer.getInstance())).start();
		UsbCamera cam = CameraServer.getInstance().startAutomaticCapture();
		c.start();
		cam.setFPS(15);
	}
	
	@Override
	public void disabledInit() {

	}
	
	@Override
	public void disabledPeriodic() {
		// Systems.smartDashboard();
		Turret.getInstance().checkForCalibration();
		if (Turret.getInstance().hasCalibratedSinceRobotInit)
			indicatorLight.setVoltage(1.46);
		else
			indicatorLight.setVoltage(.49);
		Controls.operatorController.setRumble(RumbleType.kLeftRumble, 0);
		Controls.operatorController.setRumble(RumbleType.kRightRumble, 0);
		Controls.driverController.setRumble(RumbleType.kLeftRumble, 0);
		Controls.driverController.setRumble(RumbleType.kRightRumble, 0);
	}

	@Override
	public void autonomousInit() {
		Systems.init();
		Drive.getInstance().resetDrive();
		autoThread = (new Thread((AutoRoutine) mainAutoChooser.getSelected()));
		autoThread.start();
	}

	@Override
	public void autonomousPeriodic() {
		c.stop();
		Drive.getInstance().setLowGear();
		Sensors.read();
		try {
			Systems.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// LightRing.getInstance().update();
		Systems.smartDashboard();
		Actuators.actuate();
		KragerTimer.delayMS(20);
	}

	@Override
	public void teleopInit() {
		Systems.init();
		if (autoThread != null)
			autoThread.stop();
	}

	private KragerTimer profileTimer = new KragerTimer();
	private boolean isSmartDash = true;
	private boolean isActuating = true;

	@Override
	public void teleopPeriodic() {
		c.start();
		profileTimer.reset();
		profileTimer.start();
		this.isActuating = true;
		// profileTimer.log("NEW LOOP -------------------------");
		Sensors.read();
		// Elevator.getInstance().manualControl();
		// profileTimer.log("Sensors");
		if (isActuating) {
			Shooter.getInstance().updateTeleop();
			Flywheel.getInstance().updateTeleop();
			Hood.getInstance().updateTeleop();
			Turbine.getInstance().updateTeleop();
			Turret.getInstance().updateTeleop();
			LightRing.getInstance().update();
			// profileTimer.log("Shooter");
			Elevator.getInstance().updateTeleop();
			FuelIntake.getInstance().updateTeleop();
			GearIntake.getInstance().updateTeleop();
			Climber.getInstance().manualControl();
			// profileTimer.log("Climber");
			Drive.getInstance().updateTeleop();
		}
		// profileTimer.log("Subsystems");
		if (isSmartDash) {
			Flywheel.getInstance().sendToSmartDash();
			Hood.getInstance().sendToSmartDash();
			Turret.getInstance().sendToSmartDash();
			Shooter.getInstance().sendToSmartDash();
			Elevator.getInstance().sendToSmartDash();
			GearIntake.getInstance().sendToSmartDash();
			Climber.getInstance().sendToSmartDash();
			Drive.getInstance().sendToSmartDash();
			Turbine.getInstance().sendToSmartDash();
		}
		// profileTimer.log("Smart Dash");

		Actuators.actuate();
		// profileTimer.log("Actuators");
		KragerTimer.delayMS(20);
	}

	public static AllianceColor getAllianceColor() {
		return redBlueAutoChooser.getSelected();
	}
	
	public static AutoRoutine getAutoRoutine() {
		return mainAutoChooser.getSelected();
	}
}
