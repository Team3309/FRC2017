package org.usfirst.frc.team3309.auto.routines;

import java.util.LinkedList;

import org.team3309.lib.controllers.drive.VelocityChangePoint;
import org.usfirst.frc.team3309.auto.SteamworksAutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class HopperAndShootStraightPath extends SteamworksAutoRoutine {

	private final double ANGLE_COMING_OUT_OF_TURN = 90;

	@Override
	public void redRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.closeGearIntake();
		this.pivotUpGearIntake();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(600, 3500, 17000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(40000, 9, changePoints);
		this.shoot();
	}

	@Override
	public void blueRoutine() throws TimedOutException, InterruptedException {
		this.spinUp();
		this.closeGearIntake();
		this.pivotUpGearIntake();
		LinkedList<VelocityChangePoint> changePoints = new LinkedList<VelocityChangePoint>();
		// do a curvy path to the shooting locations
		changePoints.add(new VelocityChangePoint(3000, 0));
		changePoints.add(new VelocityChangePoint(3500, 600, 17000));
		changePoints.add(new VelocityChangePoint(1000, 1000, 31000, -ANGLE_COMING_OUT_OF_TURN));
		this.driveEncoder(40000, 9, changePoints);
		this.shoot();
	}

}