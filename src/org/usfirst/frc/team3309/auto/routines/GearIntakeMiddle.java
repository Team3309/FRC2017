package org.usfirst.frc.team3309.auto.routines;

import org.usfirst.frc.team3309.auto.AutoRoutine;
import org.usfirst.frc.team3309.auto.TimedOutException;

public class GearIntakeMiddle extends AutoRoutine {

	@Override
	public void routine() throws TimedOutException, InterruptedException {
		this.driveEncoder(3000, 10);
	}

}
