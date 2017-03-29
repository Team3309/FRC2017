package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Turbine extends ControlledSubsystem {

	private static Turbine instance;
	private NetworkTable table = NetworkTable.getTable("Turbine");
	private TalonSRXMC hopperMC = new TalonSRXMC(RobotMap.HOPPER_ID);
	private Spark vibrate = new Spark(2);

	public static Turbine getInstance() {
		if (instance == null) {
			instance = new Turbine();
		}
		return instance;
	}

	private Turbine() {
		super("Turbine");
		hopperMC.setFeedbackDevice(FeedbackDevice.QuadEncoder);
	}

	@Override
	public void updateTeleop() {
		// boolean operatorXButton = Controls.operatorController.getXButton();
		// // sort
		// hopperMC.changeControlMode(TalonControlMode.Speed);

		if (Shooter.getInstance().isShouldBeShooting() &&
				Flywheel.getInstance().isShooterInRange()) {
			setHopper(1);
		} else if (Controls.driverController.getXButton()) {
			setHopper(-1);
		} else {
			setHopper(0);
		}

	}

	@Override
	public void updateAuto() {
		if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
			setHopper(1);
		} else {
			setHopper(0);
		}
	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {
		table.putNumber("vel", this.hopperMC.getSpeed());

	}

	@Override
	public void manualControl() {
		updateTeleop();

	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	public void setHopper(double power) {
		hopperMC.set(-power);
		if (power != 0) {
			this.vibrate.set(1 * KragerMath.sign(power));
		} else {
			this.vibrate.set(0);
		}
	}
}
