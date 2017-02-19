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
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

public class VisionServer implements Runnable {

	public AdbBridge adb = new AdbBridge();
	private ServerSocket serverSocket;
	private Socket socket;
	private int PORT = 8254;
	public List<TargetInfo> targets = new LinkedList<TargetInfo>();
	private TargetInfo currentTargetToAimTowards = new TargetInfo(0, 0);
	private Shot currentShotToAimTowards = new Shot();
	private static VisionServer instance;
	public static double FIELD_OF_VIEW_DEGREES = 45;

	private static Shot[] shots = { new Shot(120, 24.85, .59), new Shot(120, 27.85, .346), new Shot(120, 30.85, .227),
			new Shot(120, 31.95, .09), new Shot(120, 32.95, -.037), new Shot(120, 34.05, -.148),
			new Shot(120, 35.05, -.25), new Shot(120, 35.55, -.329), new Shot(120, 35.5, -.4),
			new Shot(130, 36.4, -.401), new Shot(130, 39.8, -.479), new Shot(130, 40.5, -.562),
			new Shot(130, 41.2, -.615), new Shot(130, 41.7, -.735), new Shot(130, 42.1, -.812),
			new Shot(140, 45.7, -.813), new Shot(140, 46.1, -.98) };

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
			System.out.println("RUN");
		} catch (Exception e) {
			e.printStackTrace();
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
		List<TargetInfo> currentTargets = this.getTargets();
		// Find the closest preset value to the vision shot
		if (currentTargets.size() != 0) {
			for (TargetInfo x : currentTargets) {
			}
		} else {
			currentTargetToAimTowards = null;
		}
		// Find Shot To Aim At
		if (currentTargetToAimTowards == null) {
			currentShotToAimTowards = null;
		} else {
			double closestShot = Integer.MAX_VALUE;
			double currentHyp = currentTargetToAimTowards.getHyp();
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
							double slope = (previousShot.getGoalHoodAngle() - shot.getGoalHoodAngle())
									/ (previousShot.getHyp() - shot.getHyp());
							double b = previousShot.getGoalHoodAngle() - (slope * previousShot.getHyp());
							double newOutput = slope * currentHyp + b;
							shotToBeSet.setGoalHoodAngle(newOutput);
						}
					} else {
						if (i != shots.length - 1) {
							Shot upperShot = shots[i + 1];
							double slope = (upperShot.getGoalHoodAngle() - shot.getGoalHoodAngle())
									/ (upperShot.getHyp() - shot.getHyp());
							double b = upperShot.getGoalHoodAngle() - (slope * upperShot.getHyp());
							double newOutput = slope * currentHyp + b;
							shotToBeSet.setGoalHoodAngle(newOutput);
						}
					}
				}
			}
			shotToBeSet.setHyp(currentHyp);
			shotToBeSet.setGoalHoodAngle(shotToBeSet.getGoalHoodAngle() - 1);
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
		if (this.hasTargetsToAimAt())
			return targets.get(0);
		return null;
	}

	public void setTargets(List<TargetInfo> targets) {
		this.targets = targets;
	}

	public double getHoodAngle() {
		return currentShotToAimTowards.getGoalHoodAngle();
	}

	public double getRPS() {
		return currentShotToAimTowards.getGoalRPS();
	}

	public boolean hasTargetsToAimAt() {
		return !this.targets.isEmpty();
	}

}
