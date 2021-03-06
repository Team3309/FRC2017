package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.KragerTimer;
import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;
import org.usfirst.frc.team3309.subsystems.GearIntake;
import org.usfirst.frc.team3309.vision.VisionServer;

public class CompBotOriginalHopperAndGearAuto extends SteamworksAutoRoutine {

	private final double ANGLE_TO_PLACE_GEAR = 60;
	private final double ANGLE_TO_HIT_HOPPER = 94;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		/*
		 * this.closeGearIntake(); this.pivotUpGearIntake();
		 */
		this.setFuelIntake(1);
		GearIntake.getInstance().setGearIntakeRoller(.5);
		KragerTimer.delayMS(200);
		this.setFuelIntake(0);
		GearIntake.getInstance().setGearIntakeRoller(1);
		this.driveEncoder(10000, 1.25);
		this.pivotUpGearIntake();
		KragerTimer.delayMS(200);
		GearIntake.getInstance().setGearIntakeRoller(.25);
		// this should match
		// the exact in hopper and shoot curvy
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(2200, 2200, 0, 0)); // 3500
																		// 3500
		changePoints.add(new VelocityChangePoint(3500, 600, 12000));
		changePoints.add(new VelocityChangePoint(2500, 2500, 20000, -ANGLE_TO_PLACE_GEAR)); // 1000
		changePoints.add(new VelocityChangePoint(500, 500, 21000, -ANGLE_TO_PLACE_GEAR)); // 700
																							// 700
																							// 37000
		this.driveEncoder(45000, 5, changePoints); // 45000
		// this.shoot();
		this.openGearIntake();
		KragerTimer.delayMS(200);
		GearIntake.getInstance().setGearIntakeRoller(-.75);
		this.pivotDownGearIntake();
		
		// Thread.sleep(250);
		LinkedList<VelocityChangePoint> changePoints3 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints3.add(new VelocityChangePoint(-2000, -2000, 0, -ANGLE_TO_PLACE_GEAR)); // 3000
		changePoints3.add(new VelocityChangePoint(-400, -400, -900));   // -900
		this.driveEncoder(-1000, 5, changePoints3);
		this.turnToAngle(ANGLE_TO_HIT_HOPPER, 1);
		this.shoot();
		LinkedList<VelocityChangePoint> changePoints2 = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints2.add(new VelocityChangePoint(3000, 3000, 0, 75));// 3500   ANGLE_TO_HIT_HOPPER 94
		changePoints2.add(new VelocityChangePoint(700, 700, 14000, 75)); // 1100 ANGLE_TO_HIT_HOPPER 94
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
		changePoints.add(new VelocityChangePoint(600, 3500, 12500));
		changePoints.add(new VelocityChangePoint(2500, 2500, 20500, ANGLE_TO_PLACE_GEAR)); // 1000
		changePoints.add(new VelocityChangePoint(700, 700, 37000, ANGLE_TO_PLACE_GEAR));
		this.driveEncoder(46000, 5, changePoints); // 413500

		// this.shoot();
		this.openGearIntake();
		Thread.sleep(250);
		this.pivotDownGearIntake();

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
		this.shoot();

		this.waitForEndOfAuto();

	}

}
