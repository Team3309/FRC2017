
package org.team3309.lib;

import org.team3309.lib.controllers.Controller;
import org.team3309.lib.controllers.generic.BlankController;
import org.team3309.lib.controllers.statesandsignals.InputState;

/**
 * Subsystem that contains a Controller
 * 
 * @author TheMkrage
 * 
 */
public abstract class ControlledSubsystem extends KragerSystem {
	/**
	 * Controller of Subsystem
	 */
	private Controller controller;

	public ControlledSubsystem(String name) {
		super(name);
		controller = new BlankController();
	}

	/*
	 * @see org.team3309.lib.KragerSubsystem#update()
	 */
	public abstract void updateTeleop();

	public abstract void updateAuto();

	/**
	 * returns Input State
	 * 
	 * @return The current state the subsystem is in, this is then sent to the
	 *         Controller object.
	 */
	public abstract InputState getInputState();

	public void setController(Controller mController) {
		this.controller = mController;
		this.controller.setSubsystemID(this.getName());
	}

	public Controller getController() {
		return this.controller;
	}

	/**
	 * Tells if the controller is where it is supposed to be
	 * 
	 * @return
	 */
	public boolean isOnTarget() {
		return controller.isCompleted();
	}

	/**
	 * Use this to send controllers to the smartdash for live tuning
	 */
	public abstract void sendToSmartDash();

	/**
	 * Manual Control for no sensors
	 */
	public abstract void manualControl();
}
