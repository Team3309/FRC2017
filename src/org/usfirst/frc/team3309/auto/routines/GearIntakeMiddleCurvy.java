package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class GearIntakeMiddleCurvy extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		changePoints.add(new VelocityChangePoint(100, 0));
		changePoints.add(new VelocityChangePoint(30, 9000));
		this.driveEncoder(500, 10, changePoints);
	}

}
