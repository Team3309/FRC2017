package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.KragerSystem;

public class Systems {
	private static List<KragerSystem> systems = new LinkedList<KragerSystem>();

	public static void addSystem(KragerSystem e) {
		systems.add(e);
	}

	public static void updateTeleop() {
		for (KragerSystem e : systems)
			e.updateTeleop();
	}

	public static void upateAuto() {
		for (KragerSystem e : systems)
			e.updateAuto();
	}

	public static void initTeleop() {
		for (KragerSystem e : systems)
			e.initTeleop();
	}

	public static void initAuto() {
		for (KragerSystem e : systems)
			e.initAuto();
	}

	public static void manualControl() {
		for (KragerSystem e : systems)
			e.manualControl();
	}

	public static void sendToSmartDashboard() {
		for (KragerSystem e : systems)
			e.sendToSmartDash();
	}
}
