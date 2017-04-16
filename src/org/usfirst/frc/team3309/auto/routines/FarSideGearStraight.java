package org.usfirst.frc.team3309.auto.routines;

import org.team3309.lib.KragerTimer;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class FarSideGearStraight extends SteamworksAutoRoutine {

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG + 5, 3);
		turnToAngle(UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG + 5, 5);
		KragerTimer.delayMS(500);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 3);
		placeGear();
		driveEncoder(-5000, 1);
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		Turret.getInstance().returnHome();
		this.outtakeIntakeAndPivotGear();
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_TURN_TOWARDS_PEG, 3);
		KragerTimer.delayMS(500);
		turnToAngle(-UniversalAutoConstants.ANGLE_TO_TURN_TOWARDS_PEG, 5);
		driveEncoder(UniversalAutoConstants.FORWARD_DISTANCE_TO_PEG, 3);
		placeGear();
		driveEncoder(-5000, 1);
	}
}
