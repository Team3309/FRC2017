/*
 * Copyright 2016 Vinnie Magro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.usfirst.frc.team3309.vision;

import java.util.List;

import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision implements Runnable {

	private final Thread thread;
	public double BRIGHTNESS = .3;

	// These are the shots
	/*
	 * private static Shot[] shots = { new Shot(140, 27.5, .57291), new
	 * Shot(140, 31.3003309, 0.308), new Shot(140, 33.303309, 0.091666), new
	 * Shot(160, 33.8, .0708), new Shot(160, 34.2, -.04375), new Shot(160, 34.9,
	 * -.164), new Shot(160, 37.9, -.2541), new Shot(160, 38.2, -.3565), new
	 * Shot(160, 40.1, -.46458), new Shot(160, 41.6, -.56041), new Shot(180,
	 * 42.4, -.702), new Shot(180, 46, -.777), new Shot(180, 42.5, -.94555) };
	 */
	/*
	 * private static Shot[] shots = { new Shot(120, 22.4, .509), new Shot(120,
	 * 23.6, .417), new Shot(120, 23.9, .346), new Shot(120, 25.8, .231), new
	 * Shot(120, 27.6, .029), new Shot(120, 29.2, -.059), new Shot(120, 30.2,
	 * -.15), new Shot(120, 30.9, -.28), new Shot(130, 34.8, -.281), new
	 * Shot(130, 35.1, -.365), new Shot(130, 36.4, -.454), new Shot(150, 37,
	 * -.455), new Shot(150, 37.5, -.579), new Shot(150, 38, -.637), new
	 * Shot(150, 38.5, -.68), new Shot(150, 39.3, -.725), new Shot(150, 40.1,
	 * -.98) };
	 */
	// -.479
	/*
	 * private static Shot[] shots = { new Shot(120, 24.8, .509), new Shot(120,
	 * 25.6, .417), new Shot(120, 25.9, .346), new Shot(120, 27.3, .231), new
	 * Shot(120, 29.1, .029), new Shot(120, 30.7, -.059), new Shot(120, 31.7,
	 * -.15), new Shot(120, 31.9, -.28), new Shot(130, 35.8, -.281), new
	 * Shot(130, 36.1, -.365), new Shot(130, 37.9, -.454), new Shot(150, 38.5,
	 * -.455), new Shot(150, 39, -.579), new Shot(150, 39.5, -.637), new
	 * Shot(150, 40, -.68), new Shot(150, 41, -.725), new Shot(150, 41.7, -.98)
	 * };
	 */
	private static Shot[] worseShots = { new Shot(140, 22.4, .454), new Shot(120, 23.6, .417),
			new Shot(120, 23.9, .346), new Shot(120, 25.8, .231), new Shot(120, 27.6, .029), new Shot(120, 29.2, -.059),
			new Shot(120, 30.2, -.15), new Shot(120, 30.9, -.28), new Shot(130, 34.8, -.281),
			new Shot(130, 35.1, -.365), new Shot(130, 36.4, -.454), new Shot(150, 37, -.455),
			new Shot(150, 37.5, -.579), new Shot(150, 38, -.637), new Shot(150, 38.5, -.68), new Shot(150, 39.3, -.725),
			new Shot(150, 40.1, -.98) };

	/*
	 * private static Shot[] shots = { new Shot(120, 23.8, .610), new Shot(120,
	 * 24.6, .440), new Shot(120, 25.95, .321), new Shot(120, 28.8, .077), new
	 * Shot(120, 30.3, -.042), new Shot(120, 32.2, -.180), new Shot(130, 33.9,
	 * -.181), new Shot(130, 36.5, -.3), new Shot(130, 37.3, -.481), new
	 * Shot(150, 37.3, -.482), new Shot(150, 38.1, -.623), new Shot(150, 38.4,
	 * -.727) };
	 */

	// PREVIOS
	/*
	 * private static Shot[] shots = { new Shot(120, 23.4, .610), new Shot(120,
	 * 24.2, .440), new Shot(120, 25.55, .321), new Shot(120, 28.4, .077), new
	 * Shot(120, 29.9, -.042), new Shot(120, 31.8, -.180), new Shot(130, 30.5,
	 * -.181), new Shot(130, 31.5, -.255), new Shot(130, 32.5, -.323), new
	 * Shot(150, 32.5, -.324), new Shot(150, 33, -.435), new Shot(170, 34.5,
	 * -.436), new Shot(170, 34.6, -.535), new Shot(170, 34.7, -.567), new
	 * Shot(170, 35, -.656), new Shot(170, 35.2, -.72), new Shot(170, 35.5,
	 * -.83) };
	 */

	private static Shot[] shots = { new Shot(120, 24.85, .59), new Shot(120, 27.85, .346), new Shot(120, 30.85, .227),
			new Shot(120, 31.95, .09), new Shot(120, 32.95, -.037), new Shot(120, 34.05, -.148),
			new Shot(120, 35.05, -.25), new Shot(120, 35.55, -.329), new Shot(120, 35.5, -.4),
			new Shot(130, 36.4, -.401), new Shot(130, 39.8, -.479), new Shot(130, 40.5, -.562),
			new Shot(130, 41.2, -.615), new Shot(130, 41.7, -.735), new Shot(130, 42.1, -.812),
			new Shot(140, 45.7, -.813), new Shot(140, 46.1, -.98) };

	/*
	 * private static Shot[] shots = { new Shot(120, 26, .59), new Shot(120, 29,
	 * .346), new Shot(120, 32, .227), new Shot(120, 33.6, .09), new Shot(120,
	 * 34.6, -.037), new Shot(120, 35.7, -.148), new Shot(120, 36.7, -.25), new
	 * Shot(120, 37.7, -.329), new Shot(120, 38.6, -.4), new Shot(130, 41.9,
	 * -.401), new Shot(130, 42.3, -.479), new Shot(130, 43, -.562), new
	 * Shot(130, 43.7, -.615), new Shot(130, 44.7, -.735), new Shot(130, 45.1,
	 * -.812), new Shot(140, 48.7, -.813), new Shot(140, 49.1, -.98) };
	 */

	/*
	 * private static Shot[] shots = { new Shot(120, 29, .681), new Shot(120,
	 * 31.4, 0.450), new Shot(120, 33.4, 0.308), new Shot(120, 35.4, .148), new
	 * Shot(120, 36.4, -.004), new Shot(140, 38.5, -.005), new Shot(140, 40.0,
	 * -.140), new Shot(140, 42.4, -.279), new Shot(140, 44, -.4), new Shot(140,
	 * 45.3, -.490), new Shot(160, 46.3, -.491), new Shot(160, 47.4, -.59), new
	 * Shot(160, 50.9, -.94555) };
	 */
	// new Shot(goalRPS, goalHood, y)

	private static Vision instance;
	private double goalHoodAngle = 0;
	private double goalRPS = 0;
	private Goal currentGoalToAimTowards;
	private Shot currentShotToAimTowards;

	public static Vision getInstance() {
		if (instance == null) {
			instance = new Vision();
		}
		return instance;
	}

	private DigitalOutput out = new DigitalOutput(RobotMap.LIGHT);

	private Vision() {
		thread = new Thread(this);
		SmartDashboard.putNumber("VISION BRIGHTNESS", BRIGHTNESS);
	}

	public void start() {
		VisionClient.getInstance().start();
		out.enablePWM(0);
		out.setPWMRate(19000);
		thread.start();
	}

	/**
	 * Sets light between 0 - 1
	 *
	 * @param power
	 */
	public void setLight(double power) {
		out.updateDutyCycle(power);
	}

	public Shot getShotToAimTowards() {
		return currentShotToAimTowards;
	}

	public boolean hasShot() {
		return currentGoalToAimTowards != null;
	}

	public double getGoalHoodAngle() {
		return currentShotToAimTowards.getGoalHoodAngle();
	}

	public double getGoalRPS() {
		return currentShotToAimTowards.getGoalRPS();
	}

	@Override
	public void run() {
		while (true) {
			this.BRIGHTNESS = SmartDashboard.getNumber("VISION BRIGHTNESS", .2);
			List<Goal> currentGoals = VisionClient.getInstance().getGoals();
			// Find the closest preset value to the vision shot
			double currentBiggest = 0;
			if (currentGoals.size() != 0) {
				for (Goal x : currentGoals) {
					if (Math.abs(x.width) > currentBiggest) {
						currentBiggest = x.width;
						currentGoalToAimTowards = x;
					}
				}
			} else {
				currentGoalToAimTowards = null;
			}
			// Find Shot To Aim At
			if (currentGoalToAimTowards == null) {
				currentShotToAimTowards = null;
			} else {
				double closestShot = Integer.MAX_VALUE;
				double currentY = currentGoalToAimTowards.y;
				Shot shotToBeSet = new Shot(currentGoalToAimTowards.azimuth);
				for (int i = 0; i < shots.length; i++) {
					Shot shot = shots[i];
					if (Math.abs(currentY - shot.getYCoordinate()) < closestShot) {
						closestShot = Math.abs(currentY - shot.getYCoordinate());
						shotToBeSet.setGoalHoodAngle(shot.getGoalHoodAngle());
						shotToBeSet.setGoalRPS(shot.getGoalRPS());
						shotToBeSet.setYCoordinate(shot.getYCoordinate());
						// Find out which of the other shots need to be found to
						// make an equation
						// y = slope * x + b
						if (currentY > shot.getYCoordinate()) {
							// System.out.println("I am at " + currentY +
							// "looking at " + shot.getYCoordinate()
							// + "which is lower than me");
							if (i != 0) {
								Shot previousShot = shots[i - 1];
								double slope = (previousShot.getGoalHoodAngle() - shot.getGoalHoodAngle())
										/ (previousShot.getYCoordinate() - shot.getYCoordinate());
								// System.out.println("m = (" +
								// previousShot.getGoalHoodAngle() + " - "
								// + shot.getGoalHoodAngle() + " )/( " +
								// previousShot.getYCoordinate() + " - "
								// + shot.getYCoordinate());
								double b = previousShot.getGoalHoodAngle() - (slope * previousShot.getYCoordinate());
								double newOutput = slope * currentY + b;
								// System.out.println(slope + " " + currentY + "
								// + " + b);
								shotToBeSet.setGoalHoodAngle(newOutput);
							}
						} else {
							if (i != shots.length - 1) {
								Shot upperShot = shots[i + 1];

								double slope = (upperShot.getGoalHoodAngle() - shot.getGoalHoodAngle())

										/ (upperShot.getYCoordinate() - shot.getYCoordinate());
								double b = upperShot.getGoalHoodAngle() - (slope * upperShot.getYCoordinate());
								double newOutput = slope * currentY + b;
								// System.out.println(slope + " " + currentY + "
								// + " + b);
								shotToBeSet.setGoalHoodAngle(newOutput);
							}
						}
					}
				}
				shotToBeSet.setYCoordinate(currentY);
				shotToBeSet.setGoalHoodAngle(shotToBeSet.getGoalHoodAngle() - 1);
				currentShotToAimTowards = shotToBeSet;
				// System.out.println(shotToBeSet + " \n");
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<Goal> getGoals() {
		return VisionClient.getInstance().getGoals();
	}
}