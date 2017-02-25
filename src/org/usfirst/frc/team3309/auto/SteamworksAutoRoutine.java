package org.usfirst.frc.team3309.auto;

import org.usfirst.frc.team3309.robot.Robot;

public abstract class SteamworksAutoRoutine extends AutoRoutine {

	/**
	 * The red auto routine.
	 * 
	 * @throws InterruptedException
	 * @throws TimedOutException
	 */
	public abstract void redRoutine() throws TimedOutException, InterruptedException;

	/**
	 * The blue auto routine.
	 * 
	 * @throws InterruptedException
	 * @throws TimedOutException
	 */
	public abstract void blueRoutine() throws TimedOutException, InterruptedException;

	public void routine() throws TimedOutException, InterruptedException {
		if (Robot.getAllianceColor() == AllianceColor.RED)
			redRoutine();
		else if (Robot.getAllianceColor() == AllianceColor.BLUE)
			blueRoutine();
		else
			this.waitForEndOfAuto();
	}
}
