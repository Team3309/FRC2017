package org.usfirst.frc.team3309.subsystems;

import org.team3309.lib.KragerSystem;
import org.team3309.lib.actuators.TalonSRXMC;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.RobotMap;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends KragerSystem {

	private static Climber instance;
	private TalonSRXMC climberMC = new TalonSRXMC(RobotMap.CLIMBER_ID);
	private final double CURRENT_LIMIT = 8;
	private final double UP_POWER = 1;
	private final double HOLD_POWER = .3;
	private int loopsAboveCurrentLimit = 0;
	private boolean hasStartedClimbing = false;

	public static Climber getInstance() {
		if (instance == null) {
			instance = new Climber();
		}
		return instance;
	}

	private Climber() {
		super("Climber");
	}

	@Override
	public void updateTeleop() {
		boolean operatorStartButton = Controls.operatorController.getStartButton();
		boolean operatorBackButton = Controls.operatorController.getBackButton();
		if (operatorStartButton) {
			if (hasHitTop())
				setClimber(UP_POWER);
			else
				setClimber(HOLD_POWER);
		} else {
			setClimber(0);
		}
		// check if a cancel on turret auto correction has occurred
		if (operatorBackButton)
			this.hasStartedClimbing = false;
	}

	@Override
	public void updateAuto() {
		// NO CLIMBING IN AUTO
	}

	@Override
	public void initTeleop() {
		setClimber(0);
	}

	@Override
	public void initAuto() {
		// NO CLIMBING IN AUTO
		setClimber(0);
	}

	@Override
	public void sendToSmartDash() {
		NetworkTable.getTable("Climber").putNumber("current", climberMC.getTalon().getOutputCurrent());
		SmartDashboard.putNumber("Climber Curret", climberMC.getTalon().getOutputCurrent());
	}

	@Override
	public void manualControl() {
		updateTeleop();
	}

	public boolean hasHitTop() {
		if (climberMC.getTalon().getOutputCurrent() > CURRENT_LIMIT) {
			loopsAboveCurrentLimit++;
		} else {
			loopsAboveCurrentLimit = 0;
		}
		return loopsAboveCurrentLimit > 10;
	}

	public void setClimber(double power) {
		if (power != 0) {
			hasStartedClimbing = true;
		}
		climberMC.setDesiredOutput(power);
	}

	public boolean isClimbing() {
		return hasStartedClimbing;
	}

}
