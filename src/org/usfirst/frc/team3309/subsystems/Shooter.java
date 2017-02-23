package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class Shooter extends KragerSystem {

	private boolean shouldBeShooting = false;
	private boolean shouldBeSpinningUp = false;
	private static Shooter instance;

	public static Shooter getInstance() {
		if (instance == null) {
			instance = new Shooter();
		}
		return instance;
	}

	private Shooter() {
		super("Shooter");
	}

	@Override
	public void updateTeleop() {
		if (Controls.operatorController.getYButton()) {
			shouldBeShooting = true;
		} else {
			shouldBeShooting = false;
		}
	}

	@Override
	public void updateAuto() {
	}

	@Override
	public void initTeleop() {
		shouldBeShooting = false;
		shouldBeSpinningUp = false;
	}

	@Override
	public void initAuto() {

	}

	@Override
	public void sendToSmartDash() {
		Flywheel.getInstance().sendToSmartDash();
		Turret.getInstance().sendToSmartDash();
		Hood.getInstance().sendToSmartDash();
	}

	@Override
	public void manualControl() {

	}

	public void setShouldBeShooting(boolean b) {
		shouldBeShooting = b;
	}

	public boolean isShouldBeShooting() {
		return shouldBeShooting;
	}

	public void setShouldBeSpinningUp(boolean b) {
		shouldBeSpinningUp = b;
	}

	public boolean isShouldBeSpinningUp() {
		return shouldBeSpinningUp;
	}
}
