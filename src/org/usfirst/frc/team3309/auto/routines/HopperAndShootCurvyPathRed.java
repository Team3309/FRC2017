package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HopperAndShootCurvyPathRed extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		this.spinUp();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 17000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, 90)); // vel,
																			// goal,
																			// angle
		this.driveEncoder(40000, 9, changePoints);
		this.shoot();
	}

}
