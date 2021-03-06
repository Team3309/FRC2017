package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.DriveEncodersController;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj.Timer;

public class Shooter extends KragerSystem {

	private boolean shouldBeShooting = false;
	private boolean shouldBeSpinningUp = false;
	private boolean isTimerStarted = false;
	private boolean shouldBeShootingAuto = false;
	private static Shooter instance;

	public KragerTimer shootingTimer = new KragerTimer();

	public static Shooter getInstance() {
		if (instance == null) {
			instance = new Shooter();
		}
		return instance;
	}

	private Shooter() {
		super("Shooter");
	}

	Timer startupTimer = new Timer();
	

	@Override
	public void updateTeleop() {
		
		if (Flywheel.getInstance().getAimVelRPS() != 0) {

		} else {
			startupTimer.start();
			startupTimer.reset();
		}
		if ((Flywheel.getInstance().isShooterInRange()
				&& startupTimer.get() > 4.5)
				|| (Flywheel.getInstance().getRPS() > Flywheel.getInstance().getAimVelRPS() - 3
						&& Flywheel.getInstance().getAimVelRPS() != 0)) {
			if (!isTimerStarted) {
				shootingTimer.start();
				isTimerStarted = true;
			}

			shouldBeShooting = true;
			
		

		} else if (Sensors.getFlywheelRPS() != 0) {
			shouldBeSpinningUp = true;
			shouldBeShooting = false;
		} else {
			shouldBeShooting = false;
			shouldBeSpinningUp = false;
			isTimerStarted = false;
		}
	}

	@Override
	public void updateAuto() {
	}

	@Override
	public void initTeleop() {
		shouldBeShooting = false;
		shouldBeSpinningUp = false;
		shootingTimer.reset();
	}

	@Override
	public void initAuto() {
		shootingTimer.reset();
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
		if (b) {
			shootingTimer.start();
		}
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
