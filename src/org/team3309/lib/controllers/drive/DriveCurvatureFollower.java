package org.team3309.lib.controllers.drive;

import org.team3309.lib.KragerMath;
import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

// This class is not done
/**
 * Give this class an array of Poses and it will drive the robot to each one by
 * finding the curvatures
 * 
 * @author Krager
 *
 */
public class DriveCurvatureFollower extends Controller {

	// private FeedForwardWithPIDController translationalController = new
	// FeedForwardWithPIDController(double kV, double kA, double kP, double kI,
	// double kD, double kILimit);
	// private FeedForwardWithPIDController angularController = new
	// FeedForwardWithPIDController(double kV, double kA, double kP, double kI,
	// double kD, double kILimit);
	private Pose[] path;
	private int currentGoalIndex = 0;

	public DriveCurvatureFollower(Pose[] path) {
		this.path = path;
	}

	@Override
	public void reset() {

	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		OutputSignal output = new OutputSignal();
		double a = path[currentGoalIndex].x - inputState.getX();
		double b = path[currentGoalIndex].y - inputState.getY();
		double l = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		double theta = inputState.getAngularPos();
		// otherSide = 90 - theta
		double otherSide = 90 - theta;
		double displacement = theta - otherSide;
		double xRobotDelta = l * KragerMath.sinDeg(displacement);
		double curvature = (2 * xRobotDelta) / (Math.pow(l, 2));
		double aimTransVel;
		if (b < 0) { // default throttle aims
			aimTransVel = -10;
		} else {
			aimTransVel = 10;
		}
		double aimAngVel = curvature * aimTransVel;
		// double rightSide =
		// translationalController.getOutputSignal(inputState).getMotor() +
		// angularController.getOutputSignal(inputState).getMotor();
		// double leftSide =
		// translationalController.getOutputSignal(inputState).getMotor() -
		// angularController.getOutputSignal(inputState).getMotor();
		// output.setLeftRightMotor(leftSide, rightSide);
		return output;
	}

	@Override
	public boolean isCompleted() {
		return false;
	}

	public Pose[] getPath() {
		return path;
	}

	public void setPath(Pose[] path) {
		this.path = path;
	}

}
