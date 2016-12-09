package org.team3309.lib.controllers.drive;

public class VelocityChangePoint {
	public double rightVelocity = 0;
	public double leftVelocity = 0;
	public double encoder = 0;

	public VelocityChangePoint(double vel, double enc) {
		this.rightVelocity = vel;
		this.leftVelocity = vel;
		this.encoder = enc;
	}

	public VelocityChangePoint(double rightVel, double leftVel, double enc) {
		this.rightVelocity = rightVel;
		this.leftVelocity = leftVel;
		this.encoder = enc;
	}
}
