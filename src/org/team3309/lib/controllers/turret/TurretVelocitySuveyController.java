package org.team3309.lib.controllers.turret;

import org.team3309.lib.controllers.generic.FeedForwardWithPIDController;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TurretVelocitySuveyController extends FeedForwardWithPIDController {

	private Turret turret = Turret.getInstance();
	private double desiredVelTarget = -turret.MAX_VEL;
	private double desiredVel = 0;
	private double desiredAngle = turret.RIGHT_LIMIT;

	public double getDesiredAngle() {
		return desiredAngle;
	}

	public void setDesiredAngle(double desiredAngle) {
		this.desiredAngle = desiredAngle;
	}

	public TurretVelocitySuveyController() {
		super(.017, 0, .009, 0, 0);
	}

	public TurretVelocitySuveyController(boolean isLeftFirst) {
		super(.017, 0, .009, 0, 0);
		if (isLeftFirst)
			desiredAngle = turret.LEFT_LIMIT;
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	
	public OutputSignal getOutputSignal(InputState inputState) {
		double curAngle = inputState.getAngularPos();
		Turret.getInstance().survey();
		if (desiredVel < desiredVelTarget) {
			desiredVel += Math.min(desiredVelTarget - desiredVel, turret.MAX_ACC);
		} else if (desiredVel > desiredVelTarget) {
			desiredVel -= Math.max(desiredVelTarget - desiredVel, turret.MAX_ACC);
		}

		if (curAngle < Turret.getInstance().LEFT_LIMIT) {
			desiredAngle = turret.RIGHT_LIMIT;
			desiredVelTarget = -turret.MAX_VEL;
		} else if (curAngle > Turret.getInstance().RIGHT_LIMIT) {
			desiredAngle = turret.LEFT_LIMIT;
			desiredVelTarget = turret.MAX_VEL;
		}
		if (curAngle < Turret.getInstance().LEFT_ABSOLUTE_LIMIT) {
			desiredAngle = turret.RIGHT_LIMIT;
			desiredVel = -5;
		} else if (curAngle > Turret.getInstance().RIGHT_ABSOLUTE_LIMIT) {
			desiredAngle = turret.LEFT_LIMIT;
			desiredVel = 5;
		}
		//System.out.println("Targetting: " + desiredAngle + " curAngle " + curAngle + " goalVel: " + this.desiredVel
			//	+ " veltarget: " + this.desiredVelTarget);

		this.setAimVel(this.desiredVel);
		InputState stateToSendToSuper = new InputState();
		stateToSendToSuper.setError(this.desiredVel - inputState.getAngularVel());
		return super.getOutputSignal(stateToSendToSuper);
	}

	public void testVelControl() {
		this.desiredVel = SmartDashboard.getNumber("aim Turret Vel", 0);
		SmartDashboard.putNumber("aim Turret Vel", this.desiredVel);
	}

	@Override
	public boolean isCompleted() {
		return false;
	}
}