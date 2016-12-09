package org.team3309.lib.controllers;

import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

/**
 * The basis of any Controller. A Controller can be made to calculate values
 * that can be then applied in a ControllerSubsystem.
 * 
 * @author TheMkrage
 * 
 */
public abstract class Controller {

	private String name = "Default";

	/**
	 * Resets the Controller. For example, reseting the integral term back to
	 * zero in a PID Loop
	 */
	public abstract void reset();

	/**
	 * Should be ran one time each loop, tells the subsystem what to do based
	 * off of the controller.
	 * 
	 * @param inputState
	 *            The state of the ControlledSubsystem
	 * @return The signal sent to the ControlledSubsystem
	 */
	public abstract OutputSignal getOutputSignal(InputState inputState);

	/**
	 * Tells if the controller is done executed its specified task.
	 * 
	 * @return boolean telling if controller is done or not
	 */
	public abstract boolean isCompleted();

	/**
	 * Sends info of controller to smartdash for looks on data and tuning
	 */
	public void sendToSmartDash() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void print(String print) {
		System.out.println(this.getName() + " " + print);
	}
}
