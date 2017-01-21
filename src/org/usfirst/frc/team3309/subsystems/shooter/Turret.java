package org.usfirst.frc.team3309.subsystems.shooter;

import org.team3309.lib.ControlledSubsystem;
import org.team3309.lib.controllers.statesandsignals.InputState;

public class Turret extends ControlledSubsystem {

	private static Turret instance;
	private static final double TURRET_MAX_DEGREES = 270;

	public static Turret getInstance() {
		if (instance == null)
			instance = new Turret("Turret");
		return instance;
	}

	private Turret(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initTeleop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAuto() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTeleop() {

	}

	@Override
	public void updateAuto() {
		// TODO Auto-generated method stub

	}

	public void searchForGoal() {

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
		// TODO Auto-generated method stub

	}

	private void setTurnClockwise(double power) {
		// TODO MOTOR SETUP
	}

	private void setTurnCounterClockwise(double power) {
		setTurnClockwise(-power);
	}

	public double getMaxDegrees() {
		return TURRET_MAX_DEGREES;
	}

}
