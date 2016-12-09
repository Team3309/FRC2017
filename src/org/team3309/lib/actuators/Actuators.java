package org.team3309.lib.actuators;

import java.util.LinkedList;
import java.util.List;

public class Actuators {
	private static List<Actuator> actuators = new LinkedList<Actuator>();

	public static void actuate() {
		for (Actuator x : actuators)
			x.actuate();
	}

	public static void addActuator(Actuator act) {
		Actuators.actuators.add(act);
	}
}
