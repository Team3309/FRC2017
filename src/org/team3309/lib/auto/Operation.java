package org.team3309.lib.auto;

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
