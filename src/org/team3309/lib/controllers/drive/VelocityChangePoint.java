package org.team3309.lib.controllers.drive;

public class VelocityChangePoint {
	public double rightVelocityNew = 0;
	public double leftVelocityNew = 0;
	public double encoderValueToChangeAt = 0;
	public Double goalAngle = null;

	public VelocityChangePoint(double vel, double enc) {
		this.rightVelocityNew = vel;
		this.leftVelocityNew = vel;
		this.encoderValueToChangeAt = enc;
	}

	public VelocityChangePoint(double rightVel, double leftVel, double enc) {
		this.rightVelocityNew = rightVel;
		this.leftVelocityNew = leftVel;
		this.encoderValueToChangeAt = enc;
	}

	public VelocityChangePoint(double rightVel, double leftVel, double enc, double goalAngle) {
		this.rightVelocityNew = rightVel;
		this.leftVelocityNew = leftVel;
		this.encoderValueToChangeAt = enc;
		this.goalAngle = goalAngle;
	}
}
