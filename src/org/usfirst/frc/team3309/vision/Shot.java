package org.usfirst.frc.team3309.vision;

public class Shot {
	private double goalRPS = 0;
	private double goalHoodAngle = 0;
	private double hyp = 0;

	public Shot() {
	}

	public Shot(double goalRPS, double goalHoodAngle, double hyp) {
		this.goalRPS = goalRPS;
		this.goalHoodAngle = goalHoodAngle;
		this.hyp = hyp;
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

	public double getHyp() {
		return hyp;
	}

	public String toString() {
		return "goalHood: " + this.goalHoodAngle + " goalRPS " + goalRPS;
	}

	public void setHyp(double hyp) {
		this.hyp = hyp;
	}
}
