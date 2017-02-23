package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HopperAndGearCurvyRed extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {

		// this should match
		// the exact in hopper and shoot curvy
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3500, 3500, 0, 0)); // 3000
		changePoints.add(new VelocityChangePoint(3500, 600, 13000));
		changePoints.add(new VelocityChangePoint(2500, 2500, 21000, -60)); // 1000
		changePoints.add(new VelocityChangePoint(700, 700, 37000, -60));
		this.driveEncoder(41000, 5, changePoints); // 413500
		this.spinUp();
		// this.shoot();

		// Thread.sleep(250);
		LinkedList<VelocityChangePoint> changePoints3 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints3.add(new VelocityChangePoint(-2000, -2000, 0, -60)); // 3000
		changePoints3.add(new VelocityChangePoint(-400, -400, -900));
		this.driveEncoder(-1000, 5, changePoints3);
		this.turnToAngle(93, 1);

		LinkedList<VelocityChangePoint> changePoints2 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints2.add(new VelocityChangePoint(3500, 3500, 0, 93));// 3000
		changePoints2.add(new VelocityChangePoint(1100, 1100, 14000, 93)); // 700
		this.driveEncoder(16000, 5, changePoints2);
		this.shoot();
		this.waitForEndOfAuto();

	}

}
