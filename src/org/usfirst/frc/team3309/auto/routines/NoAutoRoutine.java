package org.usfirst.frc.team3309.auto.routines;

import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class NoAutoRoutine extends SteamworksAutoRoutine {

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.waitForEndOfAuto();

	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.waitForEndOfAuto();
	}

}
