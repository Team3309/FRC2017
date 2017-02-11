package org.usfirst.frc.team3309.robot;

import java.util.LinkedList;
import java.util.List;

import org.team3309.lib.KragerSystem;

import edu.wpi.first.wpilibj.DriverStation;

public class Systems {
	private static List<KragerSystem> systems = new LinkedList<KragerSystem>();

	public static void addSystem(KragerSystem e) {
		systems.add(e);
	}

	public static void update() {
		for (KragerSystem e : systems) {
			if (DriverStation.getInstance().isAutonomous())
				e.updateAuto();
			else
				e.updateTeleop();
		}
	}

	public static void init() {
		for (KragerSystem e : systems) {
			if (DriverStation.getInstance().isAutonomous())
				e.initAuto();
			else
				e.initAuto();
		}
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
