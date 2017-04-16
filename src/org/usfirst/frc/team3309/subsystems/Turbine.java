package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.KragerMath;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Turbine extends ControlledSubsystem {

	private static Turbine instance;
	private KragerTimer agitationTimer = new KragerTimer();
	private NetworkTable table = NetworkTable.getTable("Turbine");
	private TalonSRXMC hopperMC = new TalonSRXMC(RobotMap.HOPPER_ID);
	private Spark vibrate = new Spark(2);
	private double agitationFactor = 1;
	public boolean isReverse = false;

	public static Turbine getInstance() {
		if (instance == null) {
			instance = new Turbine();
		}
		return instance;
	}

	private Turbine() {
		super("Turbine");
		agitationTimer.start();
		hopperMC.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		// hopperMC.configEncoderCodesPerRev(4096);
	}

	@Override
	public void updateTeleop() {
		// boolean operatorXButton = Controls.operatorController.getXButton();
		// // sort
		hopperMC.changeControlMode(TalonControlMode.PercentVbus);

		if (Shooter.getInstance().isShouldBeShooting() &&
				Flywheel.getInstance().isShooterInRange()) {
			// setHopper(1);
			setHopper(1);

		} else if (Controls.driverController.getXButton()) {
			setHopper(-500);
			// setHopper(0);
		} else {
			setHopper(0);
		}

	}

	@Override
	public void updateAuto() {
		if (isReverse) {
			setHopper(-1);
		} else if (Shooter.getInstance().isShouldBeShooting() && Flywheel.getInstance().isShooterInRange()) {
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
		table.putNumber("curren", hopperMC.getOutputCurrent());
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
		manageAgitationTimer();
		if (power != 0) {
			this.vibrate.set(1 * agitationFactor);
			hopperMC.set(-power);
		} else {

			this.vibrate.set(0);
			hopperMC.changeControlMode(TalonControlMode.PercentVbus);
			hopperMC.set(-power);
		}
	}

	private void manageAgitationTimer() {
		if (this.agitationTimer.get() > 4)
			agitationTimer.reset();
		if (this.agitationTimer.get() < 3) {
			agitationFactor = 1;
		} else {
			agitationFactor = -1;
		}

	}
}
