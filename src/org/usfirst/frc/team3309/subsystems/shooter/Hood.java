package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.usfirst.frc.team3309.robot.RobotMap;

public class Hood extends ControlledSubsystem {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;
	private static double aimAngle;
	private static final double MIN_ANGLE = 0;
	private static final double DEFAULT_ANGLE = 0;
	private static final double MAX_ANGLE = 0;
	private static TalonSRXMC hoodTalon = new TalonSRXMC(RobotMap.HOOD_ID);

	private Hood(String name) {
		super(name);
	}

	/**
	 * Singleton Pattern
	 * 
	 * @return the single instance
	 */
	public static Hood getInstance() {
		if (mHood == null) {
			mHood = new Hood("Hood");
		}
		return mHood;
	}

	@Override
	public void updateTeleop() {

	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public InputState getInputState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendToSmartDash() {

	}

	@Override
	public void manualControl() {

	}

	@Override
	public void initTeleop() {
		aimAngle = DEFAULT_ANGLE;
	}

	@Override
	public void initAuto() {
	}

	public double getAngle() {
		return hoodTalon.getTalon().getPosition();
	}
}
