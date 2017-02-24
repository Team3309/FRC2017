package org.usfirst.frc.team3309.subsystems;

import java.awt.geom.Ellipse2D;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.tunable.DashboardHelper;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.vision.VisionServer;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends ControlledSubsystem {

	private final double SHOOTING_VELOCITY = 10;
	private double aimVel = 0;
	private CANTalon elevator = new CANTalon(RobotMap.ELEVATOR_ID);
	private CANTalon feedyWheel = new CANTalon(RobotMap.FEEDY_WHEEL_ID);
	private NetworkTable table = NetworkTable.getTable("Elevator");
	private static Elevator instance;

	public static Elevator getInstance() {
		if (instance == null)
			instance = new Elevator();
		return instance;
	}

	private Elevator() {
		super("Elevator");
		// elevator.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		// elevator.changeControlMode(TalonControlMode.Speed);
		this.elevator.changeControlMode(TalonControlMode.Speed);
		table.putNumber("k_aimVel", 0);
	}

	@Override
	public void initAuto() {
		this.elevator.changeControlMode(TalonControlMode.PercentVbus);
		// this.elevator.changeControlMode(TalonControlMode.Speed);
	}

	@Override
	public void initTeleop() {
		this.elevator.changeControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public void updateTeleop() {
		System.out.println(elevator.isSensorPresent(FeedbackDevice.QuadEncoder));
		if (Controls.operatorController.getAButton()) {
			aimVel = table.getNumber("k_aimVel", 0);
		} else if (Shooter.getInstance().isShouldBeShooting()) {
			aimVel = SHOOTING_VELOCITY;
		} else {
			aimVel = 0;
		}
		setElevator(aimVel);
	}

	@Override
	public void updateAuto() {
		if (Shooter.getInstance().isShouldBeShooting() && VisionServer.getInstance().hasTargetsToAimAt()) {
			this.elevator.set(.95);
			this.feedyWheel.set(1);
		}
	}

	@Override
	public InputState getInputState() {
		InputState s = new InputState();
		s.setError(aimVel - elevator.getEncVelocity());
		return s;
	}

	@Override
	public void sendToSmartDash() {
		this.getController().sendToSmartDash();
		DashboardHelper.updateTunable(this.getController());
		table.putNumber(this.getName() + " Vel", this.elevator.getEncVelocity());
		table.putNumber(this.getName() + " Pow", this.elevator.getPosition());
		table.putNumber(this.getName() + " Error", aimVel - elevator.getEncVelocity());
	}

	@Override
	public void manualControl() {

		if (Controls.operatorController.getBumper(Hand.kRight)) {
			setElevator(.95);
		} else if (Controls.operatorController.getBumper(Hand.kLeft)) {
			setElevator(-.5);
		} else {
			setElevator(0);
		}
	}

	private void setElevator(double power) {
		this.elevator.set(power);
		this.feedyWheel.set(power);
	}

}
