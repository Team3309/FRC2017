package org.team3309.lib.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

public class NavX extends Sensor {

	private AHRS navX = new AHRS(SPI.Port.kMXP);
	private double lastRate = 0;
	private double lastFusedHeading = 0;
	private double lastRoll = 0;

	public double getAngularVel() {
		return lastRate;
	}

	public double getAngle() {
		// return navX.getYaw();
		return lastFusedHeading;
	}

	public double getRoll() {
		return lastRoll;
	}

	@Override
	protected void update() {
		lastRate = navX.getRate();
		lastFusedHeading = navX.getFusedHeading();
		lastRoll = navX.getRoll();
	}
}
