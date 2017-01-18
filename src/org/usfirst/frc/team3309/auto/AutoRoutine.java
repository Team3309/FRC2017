package org.usfirst.frc.team3309.auto;

public abstract class AutoRoutine extends RoutineBased implements Runnable {

	/**
	 * Start auto timer and routine
	 * @throws TimedOutException
	 * @throws InterruptedException
	 */
	public void start() throws TimedOutException, InterruptedException {
		autoTimer.start();
		routine();
	}
	
	/**
	 * The entire auto routine.
	 * 
	 * @throws InterruptedException
	 * @throws TimedOutException
	 */
	public abstract void routine() throws TimedOutException, InterruptedException;

	public void run() {
		try {
			start();
		} catch (TimedOutException | InterruptedException e) {
			e.printStackTrace();
		}
    }
	
}
