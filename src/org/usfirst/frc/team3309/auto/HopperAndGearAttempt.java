package org.usfirst.frc.team3309.auto;


import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HopperAndGearAttempt extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		// this should match the exact in hopper and shoot curvy
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(100, 0));
		changePoints.add(new VelocityChangePoint(10, 10, 500));
		changePoints.add(new VelocityChangePoint(10, 10, 500));
		this.driveEncoder(100, 3, changePoints);
		this.shoot();

		// backup while shooting and place the gear
		this.driveEncoder(-500, 10, 9);
		this.turnToAngle(5, 2);
		this.driveEncoder(-100, 5);

	}

}
