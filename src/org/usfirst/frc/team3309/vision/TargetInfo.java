package org.usfirst.frc.team3309.vision;

public class TargetInfo {
	protected double x = 1.0;
	protected double y;
	protected double z;

	public TargetInfo(double y, double z) {
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z + .04;
	}

	public double getHyp() {
		return Math.pow(getY(), 2) + Math.pow(getZ(), 2);
	}
}
