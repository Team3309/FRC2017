package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.Operation;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.auto.operations.ShootOperation;
import org.usfirst.frc.team3309.auto.operations.SpinUpOperation;
import org.usfirst.frc.team3309.auto.operations.TurretTurnAndSurveyOperation;

public class TurnInPlaceAutoRoutine extends SteamworksAutoRoutine {

	final double ANGLE_COMING_OUT_OF_TURN = 90;
	private final double ANGLE_FOR_TURNING = 90;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.turnToAngle(60, 5);
	}

	@Override

	public void blueRoutine() throws TimedOutException, InterruptedException {

		this.turnToAngle(-60, 5);
	}

}
