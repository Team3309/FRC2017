package org.team3309.lib.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.team3309.lib.controllers.statesandsignals.InputState;
import org.team3309.lib.controllers.statesandsignals.OutputSignal;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The basis of any Controller. A Controller can be made to calculate values
 * that can be then applied in a ControllerSubsystem.
 * 
 * @author TheMkrage
 * 
 */
public abstract class Controller {

	private String name = "Default";
	protected String subsystemID = "drivetrain";
	public NetworkTable table = NetworkTable.getTable(subsystemID);

	public Controller() {
		table = NetworkTable.getTable(subsystemID);
	}

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
		JSONArray recievedArray = new JSONArray(table.getString("constants", ""));
		for (int i = 0; i < recievedArray.length(); i++) {
			JSONObject object = (JSONObject) recievedArray.get(i);
			SmartDashboard.putNumber(object.getString("label"), object.getDouble("value"));
		}

		JSONObject x1 = new JSONObject();
		x1.put("kP", kP);
		JSONObject x2 = new JSONObject();
		x2.put("kI", kI);
		JSONObject x3 = new JSONObject();
		x3.put("kD", kD);

		JSONArray array = new JSONArray();
		array.put(x1).put(x2).put(x3);

		table.putString("constants", array.toString());
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

	public String getSubsystemID() {
		return subsystemID;
	}

	public void setSubsystemID(String subsystemID) {
		this.subsystemID = subsystemID;
	}
}
