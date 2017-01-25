package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class Shooter extends KragerSystem {

	private boolean shouldBeShooting = false;
	private static Shooter instance;

	public static Shooter getInstance() {
		if (instance == null) {
			instance = new Shooter("Shooter");
		}
		return instance;
	}

	private Shooter(String name) {
		super(name);
	}

	@Override
	public void updateTeleop() {
		Flywheel.getInstance().updateTeleop();
		Turret.getInstance().updateTeleop();
		Hood.getInstance().updateTeleop();
		if (Controls.operatorController.getAButton()) {
			shouldBeShooting = true;
		} else {
			shouldBeShooting = false;
		}
	}

	@Override
	public void updateAuto() {
		Flywheel.getInstance().updateAuto();
		Turret.getInstance().updateAuto();
		Hood.getInstance().updateAuto();
	}

	@Override
	public void initTeleop() {
		Flywheel.getInstance().initTeleop();
		Turret.getInstance().initTeleop();
		Hood.getInstance().initTeleop();
	}

	@Override
	public void initAuto() {
		Flywheel.getInstance().initAuto();
		Turret.getInstance().initAuto();
		Hood.getInstance().initAuto();
	}

	@Override
	public void sendToSmartDash() {
		Flywheel.getInstance().sendToSmartDash();
		Turret.getInstance().sendToSmartDash();
		Hood.getInstance().sendToSmartDash();
	}

	@Override
	public void manualControl() {
		Flywheel.getInstance().manualControl();
		Turret.getInstance().manualControl();
		Hood.getInstance().manualControl();
	}

	public boolean isShouldBeShooting() {
		return shouldBeShooting;
	}

}
