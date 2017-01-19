package org.usfirst.frc.team3309.vision;

public class Shot {
	private double goalRPS = 0;
	private double goalHoodAngle = 0;
	private double azimuth = 0;
	private double yCoordinate = 0;

	public Shot(double azimuth) {
		this.setAzimuth(azimuth);
	}

	public double getYCoordinate() {
		return yCoordinate;
	}

	public void setYCoordinate(double distance) {
		this.yCoordinate = distance;
	}

	public Shot(double goalRPS, double goalHoodAngle, double azimuth, double distance) {
		this.goalRPS = goalRPS;
		this.goalHoodAngle = goalHoodAngle;
		this.yCoordinate = distance;
		this.setAzimuth(azimuth);
	}
	
	public Shot(double goalRPS, double goalHoodAngle, double distance) {
		this.goalRPS = goalRPS;
		this.goalHoodAngle = goalHoodAngle;
		this.yCoordinate = distance;
	}

	public double getGoalRPS() {
		return goalRPS;
	}

	public void setGoalRPS(double goalRPS) {
		this.goalRPS = goalRPS;
	}

	public double getGoalHoodAngle() {
		return goalHoodAngle;
	}

	public void setGoalHoodAngle(double goalHoodAngle) {
		this.goalHoodAngle = goalHoodAngle;
	}

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}
	
	public String toString() {
		return "goalHood: " + this.goalHoodAngle + " goalRPS " + goalRPS;
	}
}
