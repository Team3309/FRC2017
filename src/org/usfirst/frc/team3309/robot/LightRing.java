package org.usfirst.frc.team3309.robot;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.subsystems.Shooter;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Solenoid;

public class LightRing extends Solenoid {

	private static LightRing instance;
	private boolean hasBlinkedForGear = false;
	private KragerTimer blinkTimer = new KragerTimer();

	public static LightRing getInstance() {
		if (instance == null)
			instance = new LightRing();
		return instance;
	}

	private LightRing() {
		super(RobotMap.LIGHT_RING);
		this.set(true);
	}

	public void update() {
		if (GearIntake.getInstance().hasGear() && !Shooter.getInstance().isShouldBeSpinningUp()
				&& !(Turret.getInstance().getState() == Turret.TurretState.SURVEY
						|| Turret.getInstance().getState() == Turret.TurretState.USING_VISION)) {
			if (!hasBlinkedForGear) {
				hasBlinkedForGear = true;
				blinkTimer.start();
				blinkTimer.reset();
			}
			Controls.driverController.setRumble(RumbleType.kLeftRumble, .5);
			Controls.driverController.setRumble(RumbleType.kRightRumble, .5);
			Controls.operatorController.setRumble(RumbleType.kLeftRumble, .5);
			Controls.operatorController.setRumble(RumbleType.kRightRumble, .5);
			if (blinkTimer.get() < .25) {
				this.set(true);
			} else if (blinkTimer.get() < .5) {
				this.set(false);
			} else {
				blinkTimer.reset();
			}
		} else {
			blinkTimer.stop();
			hasBlinkedForGear = false;
			Controls.driverController.setRumble(RumbleType.kLeftRumble, 0);
			Controls.driverController.setRumble(RumbleType.kRightRumble, 0);
			Controls.operatorController.setRumble(RumbleType.kLeftRumble, 0);
			Controls.operatorController.setRumble(RumbleType.kRightRumble, 0);
		}
	}
}