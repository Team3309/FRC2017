package org.usfirst.frc.team3309.auto;

import org.usfirst.frc.team3309.auto.RoutineBased;
import org.usfirst.frc.team3309.auto.TimedOutException;

public abstract class Operation extends RoutineBased {

	public double encoder = 0;

	public Operation() {
		this.encoder = 0;
			
	}

	public Operation(double counts) {
		this.encoder = counts;
	}

	public abstract void perform() throws InterruptedException, TimedOutException;
}
