package org.team3309.lib.controllers.drive;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;
import org.team3309.lib.sensors.Sensors;
import org.usfirst.frc.team3309.vision.Shot;
import org.usfirst.frc.team3309.vision.Vision;

public class FaceVisionTargetController extends DriveAngleController {

	private boolean sendToDash = true;
	private Shot aimShot;

	public FaceVisionTargetController() {
		super(Sensors.getAngle());
		if (Vision.getInstance().getShotToAimTowards() != null) {
			aimShot = Vision.getInstance().getShotToAimTowards();
			this.setName("Turn To Vision");
			this.goalAngle = (aimShot.getAzimuth() + Sensors.getAngle());// -
																			// (.4
																			// *
																			// (aimShot.getYCoordinate()));
		} else {
			this.sendToDash = false;
			System.out.println("THIS IS VERY BAD");
		}
		this.mIntegral = 0;
		
	}

	@Override
	public OutputSignal getOutputSignal(InputState inputState) {
		System.out.println("ERROR :" + (Math.abs(this.goalAngle) - Math.abs(Sensors.getAngle())));
		if (Math.abs(this.goalAngle - Sensors.getAngle()) < .25) {
			System.out.println("AT RIGHT ANGLE");
			// Controls.operatorController.setRumble((float) 1.0);
			// return new OutputSignal();
		}

		return super.getOutputSignal(inputState);
	}

	@Override
	public void sendToSmartDash() {
		
		if (this.sendToDash) {
			super.sendToSmartDash();
		}
	}

	@Override
	public boolean isCompleted() {
		if (Math.abs(this.goalAngle - Sensors.getAngle()) < .25) {
			return false;
		}
		return false;

	}
	

}