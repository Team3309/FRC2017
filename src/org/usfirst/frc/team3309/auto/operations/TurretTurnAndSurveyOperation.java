package org.usfirst.frc.team3309.auto.operations;

import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class TurretTurnAndSurveyOperation extends Operation {

	private double goalAngle;

	public TurretTurnAndSurveyOperation(double encoder, double goalAngle) {
		super(encoder);
		this.goalAngle = goalAngle;
	}

	@Override
	public void perform() throws InterruptedException, TimedOutException {
		Turret.getInstance().turnToAngleAndSurvey(goalAngle);
	}

}
