package org.team3309.lib.controllers.drive;

import java.util.concurrent.TimeoutException;

public abstract class EncoderSwitchOperation {
	public double encoder = 0;

	public EncoderSwitchOperation() {
		this.encoder = 0;
	}

	public EncoderSwitchOperation(double counts) {
		this.encoder = counts;
	}

	public abstract void perform() throws InterruptedException, TimeoutException;
}
