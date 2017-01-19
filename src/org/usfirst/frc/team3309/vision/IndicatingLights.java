package org.usfirst.frc.team3309.vision;

import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class IndicatingLights {

	private enum IndicatorState {
		OFF, RED, LOCKED, BLUE
	}

	private static IndicatingLights instance;
	private double THRESHOLD_FOR_AZIMUTH = 3;
	private NetworkTable lightsTable = NetworkTable.getTable("Status");

	public static IndicatingLights getInstance() {
		if (instance == null) {
			instance = new IndicatingLights();
		}
		return instance;
	}

	private IndicatingLights() {

	}

	private void setIndicators(IndicatorState state) {
		switch (state) {

		case OFF:
			lightsTable.putBoolean("Red", false);
			lightsTable.putBoolean("Blue", false);
			lightsTable.putBoolean("Locked", false);
			lightsTable.putNumber("Power", Flywheel.getInstance().getPercent());
			break;

		case RED:
			lightsTable.putBoolean("Red", true);
			lightsTable.putBoolean("Blue", false);
			lightsTable.putBoolean("Locked", false);
			lightsTable.putNumber("Power", Flywheel.getInstance().getPercent());
			break;

		case LOCKED:
			lightsTable.putBoolean("Red", false);
			lightsTable.putBoolean("Blue", true);
			lightsTable.putBoolean("Locked", true);
			lightsTable.putNumber("Power", Flywheel.getInstance().getPercent());
			break;

		case BLUE:
			lightsTable.putBoolean("Red", false);
			lightsTable.putBoolean("Blue", true);
			lightsTable.putBoolean("Locked", false);
			lightsTable.putNumber("Power", Flywheel.getInstance().getPercent());
			break;

		default:
			lightsTable.putBoolean("Red", false);
			lightsTable.putBoolean("Blue", false);
			lightsTable.putBoolean("Locked", false);
			lightsTable.putNumber("Power", Flywheel.getInstance().getPercent());
			break;

		}
	}

	public void update() {
		if (Vision.getInstance().hasShot()) {
			Shot shot = Vision.getInstance().getShotToAimTowards();
			if (shot != null) {
				double azimuth = shot.getAzimuth();
				if (Math.abs(azimuth) < THRESHOLD_FOR_AZIMUTH) {
					this.setIndicators(IndicatorState.LOCKED);
				} else {
					this.setIndicators(IndicatorState.BLUE);
				}
			}
		} else {
			this.setIndicators(IndicatorState.RED);
		}
	}
}
