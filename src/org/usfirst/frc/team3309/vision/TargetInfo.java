package org.usfirst.frc.team3309.vision;

import org.team3309.lib.KragerMath;

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
		return z + .03; // comp bot had a +.04
	}

	public double getHyp() {
		return KragerMath.sign(getY()) * (Math.pow(getY(), 2) + Math.pow(getZ(), 2));
	}
}
