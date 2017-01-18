package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.actuators.Actuator;

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
