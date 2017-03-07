package org.usfirst.frc.team3309.auto;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HopperAndShootCurvyPathRed extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(100, 0));
		changePoints.add(new VelocityChangePoint(1000, 1500, 5000));
		changePoints.add(new VelocityChangePoint(500, 500, 35000));
		this.driveEncoder(40000, 5, changePoints);
		this.shoot();
	}

}