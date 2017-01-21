package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;

public class Hood extends ControlledSubsystem {
	/**
	 * Shooter for singleton pattern
	 */
	private static Hood mHood;

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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void manualControl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub
		
	}
}
