package org.usfirst.frc.team3309.vision;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.team3309.lib.KragerMath;
import org.usfirst.frc.team3309.driverstation.Controls;
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.shooter.Flywheel;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class VisionServer implements Runnable {

	public AdbBridge adb = new AdbBridge();
	private ServerSocket serverSocket;
	private Socket socket;
	private int PORT = 8254;
	public List<TargetInfo> targets = new LinkedList<TargetInfo>();
	private Shot currentShotToAimTowards = new Shot();
	private static VisionServer instance;
	public static double FIELD_OF_VIEW_DEGREES = 45;
	private static double k_predRPS = .001;

	// private static Shot[] shots = { new Shot(98, .01, .23),
	// new Shot(99, .01, .161),
	// new Shot(99, .05, .16),
	// new Shot(102, .05, .101),
	// new Shot(100, .4, .1),
	// new Shot(102, .4, .071),
	// new Shot(104, .6, .07),

	// new Shot(106, .6, .051),
	// new Shot(106, 1, .05),
	// new Shot(113, 1, .031) // to here)
	// };
	// ross
	private static Shot[] shots = { new Shot(125, .01, .23),
			new Shot(130, .01, .161),
			new Shot(120, .05, .16),
			new Shot(134, .05, .101),
			new Shot(128, .25, .1),
			new Shot(131, .25, .07),
			new Shot(134, .25, .051),
			new Shot(137, .25, .031) // to here)
	};

	public static VisionServer getInstance() {
		if (instance == null)
			instance = new VisionServer();
		return instance;
	}

	private VisionServer() {
		try {
			adb = new AdbBridge();
			serverSocket = new ServerSocket(PORT);
			adb.start();
			adb.runCommand("devices");
			adb.reversePortForward(PORT, PORT);
			adb.runCommand("devices");
		} catch (Exception e) {
			e.printStackTrace();
			(new Thread(this)).stop();
		}
	}

	public void generateFromJsonString(String updateString) {
		try {
			JSONObject j = new JSONObject(updateString);
			JSONArray targetsArray = (JSONArray) j.get("targets");
			ArrayList<TargetInfo> targetInfos = new ArrayList<>();
			for (Object targetObj : targetsArray) {
				JSONObject target = (JSONObject) targetObj;
				double y = target.getDouble("y");
				double z = target.getDouble("z");

				targetInfos.add(new TargetInfo(y, z));
			}
			targets = targetInfos;
			Turret.getInstance().resetAngVelocityCounts();
		} catch (ClassCastException e) {
			System.err.println("Data type error: " + e);
			System.err.println(updateString);
		}
	}

	public void send(VisionMessage message) {
		String toSend = message.toJson() + "\n";
		if (socket != null && socket.isConnected()) {
			try {
				OutputStream os = socket.getOutputStream();
				os.write(toSend.getBytes());
			} catch (IOException e) {
				System.err.println("VisionServer: Could not send data to socket");
			}
		}
	}

	public void handleMessage(VisionMessage message) {
		if ("targets".equals(message.getType())) {
			generateFromJsonString(message.getMessage());
		}
		if ("heartbeat".equals(message.getType())) {
			send(HeartbeatMessage.getInstance());
		}
	}

	public void run() {
		while (true) {
			if (serverSocket == null) {
				return;
			}
			try {
				socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int read;
				//
				while (socket.isConnected() && (read = is.read(buffer)) != -1) {
					String messageRaw = new String(buffer, 0, read);
					String[] messages = messageRaw.split("\n");
					for (String message : messages) {
						// System.out.println("message " + message);
						OffWireMessage parsedMessage = new OffWireMessage(message);
						if (parsedMessage.isValid()) {
							handleMessage(parsedMessage);
						}
					}
					findClosestGoal();
				}
				System.out.println("Socket disconnected");

			} catch (IOException e) {
				System.err.println("Could not talk to socket");
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private void findClosestGoal() {
		// System.out.println("FIND CLOSEST GOAL");
		List<TargetInfo> currentTargets = this.getTargets();
		// Find the closest preset value to the vision shot
		if (!this.hasTargetsToAimAt()) {
			currentShotToAimTowards = null;
			return;
		}
		double closestShot = Integer.MAX_VALUE;
		double currentHyp = this.getTarget().getHyp();
		Shot shotToBeSet = new Shot();
		for (int i = 0; i < shots.length; i++) {
			Shot shot = shots[i];
			if (Math.abs(currentHyp - shot.getHyp()) < closestShot) {
				closestShot = Math.abs(currentHyp - shot.getHyp());
				shotToBeSet.setGoalHoodAngle(shot.getGoalHoodAngle());
				shotToBeSet.setGoalRPS(shot.getGoalRPS());
				shotToBeSet.setHyp(shot.getHyp());
				// Find out which of the other shots need to be found to
				// make an equation
				if (currentHyp > shot.getHyp()) {
					if (i != 0) {
						Shot previousShot = shots[i - 1];
						// double slope = (previousShot.getGoalHoodAngle() -
						// shot.getGoalHoodAngle())
						// / (previousShot.getHyp() - shot.getHyp());
						// double b = previousShot.getGoalHoodAngle() - (slope *
						// previousShot.getHyp());
						// double newOutput = slope * currentHyp + b;
						// RPS stuff
						double slopeRPS = (previousShot.getGoalRPS() - shot.getGoalRPS())
								/ (previousShot.getHyp() - shot.getHyp());
						double bRPS = previousShot.getGoalRPS() - (slopeRPS * previousShot.getHyp());
						double newOutputRPS = slopeRPS * currentHyp + bRPS;
						shotToBeSet.setGoalRPS(newOutputRPS);
						// shotToBeSet.setGoalHoodAngle(newOutput);
						double predRPS = KragerMath.sign(slopeRPS) * k_predRPS + newOutputRPS;
					}
				} else {
					if (i != shots.length - 1) {
						Shot upperShot = shots[i + 1];
						// double slope = (upperShot.getGoalHoodAngle() -
						// shot.getGoalHoodAngle())
						// / (upperShot.getHyp() - shot.getHyp());
						/// double b = upperShot.getGoalHoodAngle() - (slope *
						// upperShot.getHyp());
						// double newOutput = slope * currentHyp + b;
						// RPS stuff

						double slopeRPS = (upperShot.getGoalRPS() - shot.getGoalRPS())
								/ (upperShot.getHyp() - shot.getHyp());
						double bRPS = upperShot.getGoalRPS() - (slopeRPS * upperShot.getHyp());
						double newOutputRPS = slopeRPS * currentHyp + bRPS;
						shotToBeSet.setGoalRPS(newOutputRPS);
						// shotToBeSet.setGoalHoodAngle(newOutput);
					}
				}

			}
			shotToBeSet.setHyp(currentHyp);
			// if (!DriverStation.getInstance().isDisabled())
			// System.out.println(
			// "shotTobe Set " + shotToBeSet.getGoalHoodAngle() + " flywheel " +
			// shotToBeSet.getGoalRPS()
			// + " Hyp "
			// + shotToBeSet.getHyp());
			// shotToBeSet.setGoalHoodAngle(shotToBeSet.getGoalHoodAngle() - 1);
			// System.out.println("Predictive RPS: " + predRPS);
			currentShotToAimTowards = shotToBeSet;
		}

	}

	public void restartAdb() {
		adb.restartAdb();
		adb.reversePortForward(PORT, PORT);
	}

	public List<TargetInfo> getTargets() {
		return targets;
	}

	public TargetInfo getTarget() {
		try {
			if (this.hasTargetsToAimAt())
				return targets.get(0);
		} catch (Exception e) {

		}
		return new TargetInfo(0, 0);
	}

	public void setTargets(List<TargetInfo> targets) {
		this.targets = targets;
	}

	public double getHoodAngle() {
		if (currentShotToAimTowards != null)
			return currentShotToAimTowards.getGoalHoodAngle();
		return Hood.getInstance().getAngle();
	}

	public double getRPS() {
		if (currentShotToAimTowards != null)
			return currentShotToAimTowards.getGoalRPS();
		return Sensors.getFlywheelRPS();
	}

	public boolean hasTargetsToAimAt() {
		return !this.targets.isEmpty();
	}

}
