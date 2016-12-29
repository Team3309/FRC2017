package org.usfirst.frc.team339.robot;

import org.usfirst.frc.team3309.driverstation.XboxController;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Robot extends IterativeRobot {
	Spark rightDrive = new Spark(RobotMap.RIGHT_DRIVE);
	Spark leftDrive = new Spark(RobotMap.LEFT_DRIVE);
	Solenoid shifter = new Solenoid(RobotMap.SHIFTER);
	Spark feedyWheel = new Spark(RobotMap.FEEDY_WHEEL_MOTOR);

	XboxController driverRemote = new XboxController(0);

	public void robotInit() {
		;
	}

	public void disabledPeriodic() {

	}

	public void autonomousInit() {

	}

	public void autonomousPeriodic() {

	}

	public void teleopInit() {
		shifter.set(true);
	}

	public void teleopPeriodic() {

		double throttle = driverRemote.getLeftY();
		double turn = driverRemote.getLeftX();

		double leftPower = throttle + turn;
		double rightPower = throttle - turn;

		leftDrive.set(leftPower);
		rightDrive.set(-rightPower);

		boolean isAPressed = driverRemote.getA();
		boolean isBPressed = driverRemote.getB();

		if (isAPressed) {
			feedyWheel.set(1);
		} else if (isBPressed) {
			feedyWheel.set(-1);
		} else {
			feedyWheel.set(0);
		}

	}
}
