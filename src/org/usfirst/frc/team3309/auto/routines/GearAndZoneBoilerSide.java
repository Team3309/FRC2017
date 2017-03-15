package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.vision.VisionServer;

public class GearAndZoneBoilerSide extends SteamworksAutoRoutine {

	private final double ANGLE_TO_PLACE_GEAR = 60;
	private final double ANGLE_TO_HIT_HOPPER = 0;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.closeGearIntake();
		this.pivotUpGearIntake();
		// this should match
		// the exact in hopper and shoot curvy
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3500, 3500, 0, 0)); // 3000
		changePoints.add(new VelocityChangePoint(3500, 600, 13000));
		changePoints.add(new VelocityChangePoint(2500, 2500, 21000, -ANGLE_TO_PLACE_GEAR)); // 1000
		changePoints.add(new VelocityChangePoint(700, 700, 37000, -ANGLE_TO_PLACE_GEAR));
		this.driveEncoder(41500, 5, changePoints); // 413500
		// this.shoot();
		this.openGearIntake();
		this.pivotDownGearIntake();

		// Thread.sleep(250);
		LinkedList<VelocityChangePoint> changePoints3 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints3.add(new VelocityChangePoint(-2000, -2000, 0, -ANGLE_TO_PLACE_GEAR)); // 3000
		changePoints3.add(new VelocityChangePoint(-400, -400, -900));
		this.driveEncoder(-1000, 5, changePoints3);
		this.turnToAngle(ANGLE_TO_HIT_HOPPER, 1);

		LinkedList<VelocityChangePoint> changePoints2 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints2.add(new VelocityChangePoint(3500, 3500, 0, ANGLE_TO_HIT_HOPPER));// 3000
		changePoints2.add(new VelocityChangePoint(1100, 1100, 14000, ANGLE_TO_HIT_HOPPER)); // 700
		this.driveEncoder(16000, 5, changePoints2);

		this.waitForEndOfAuto();

	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.closeGearIntake();
		this.pivotUpGearIntake();
		// this should match
		// the exact in hopper and shoot curvy
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3500, 3500, 0, 0)); // 3000
		changePoints.add(new VelocityChangePoint(600, 3500, 13000));
		changePoints.add(new VelocityChangePoint(2500, 2500, 21000, ANGLE_TO_PLACE_GEAR)); // 1000
		changePoints.add(new VelocityChangePoint(700, 700, 37000, ANGLE_TO_PLACE_GEAR));
		this.driveEncoder(41500, 5, changePoints); // 413500

		// this.shoot();
		this.openGearIntake();
		this.pivotDownGearIntake();

		// Thread.sleep(250);
		LinkedList<VelocityChangePoint> changePoints3 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints3.add(new VelocityChangePoint(-2000, -2000, 0, ANGLE_TO_PLACE_GEAR)); // 3000
		changePoints3.add(new VelocityChangePoint(-400, -400, -900));
		this.driveEncoder(-1000, 5, changePoints3);
		this.turnToAngle(-ANGLE_TO_HIT_HOPPER, 1);

		LinkedList<VelocityChangePoint> changePoints2 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints2.add(new VelocityChangePoint(3500, 3500, 0, -ANGLE_TO_HIT_HOPPER));// 3000
		changePoints2.add(new VelocityChangePoint(1100, 1100, 14000, -ANGLE_TO_HIT_HOPPER)); // 700
		this.driveEncoder(16000, 5, changePoints2);

		this.waitForEndOfAuto();

	}

}
