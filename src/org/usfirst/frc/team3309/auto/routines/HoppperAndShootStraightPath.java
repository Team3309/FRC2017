package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HoppperAndShootStraightPath extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		// driveForward
		this.driveEncoder(5000, 3);
		// turn 90 to face towards hopper
		this.turnToAngle(90, 2);
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// start fast and slow down to hit hopper
		changePoints.add(new VelocityChangePoint(100, 0));
		changePoints.add(new VelocityChangePoint(10, 500));
		this.driveEncoder(5000, 3, changePoints);
		this.shoot();
	}

}
