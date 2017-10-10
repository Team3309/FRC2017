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
import org.usfirst.frc.team3309.robot.Sensors;
import org.usfirst.frc.team3309.subsystems.shooter.Hood;
import org.usfirst.frc.team3309.subsystems.shooter.Turret;

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
	// private static Shot[] shots = { new Shot(126, .31, .23),
	// new Shot(131, .31, .161),
	// new Shot(127, .35, .16),
	// new Shot(137, .35, .101),
	// new Shot(131, .8, .1),
	// new Shot(134, .8, .06157),
	// new Shot(136, .8, .04471),
	// new Shot(137, .8, .031),
	// new Shot(144, .8, .018)// to here)
	// };

	// .25 is the new .8

	// double goalRPS double goalHoodAngle, double hyp-raw

	/*
	 * private static Shot[] shots = { new Shot(124, 0, .23), new Shot(129, 0,
	 * .161), new Shot(107, .4, .16), new Shot(109, .4, .101), new Shot(111, .4,
	 * .1), new Shot(113, .4, .06157), new Shot(115, .4, .04471), new Shot(117,
	 * .4, .031), new Shot(119, .4, .018), new Shot(121, .4, .01), new Shot(123,
	 * .4, .008), new Shot(125, .4, .0031), new Shot(128, .4, .000001), new
	 * Shot(145, .9, 0), new Shot(148, .9, -.2), new Shot(151, .9, -.4), new
	 * Shot(154, .9, -.53), new Shot(157, .9, -2), };
	 */

	/*
	 * private static Shot[] shots = { new Shot(108, 0.1, 0.26), new Shot(109,
	 * 0.2, .21), new Shot(109, 0.3, 0.18), new Shot(109, 0.4, 0.16), new
	 * Shot(110, 0.5, 0.14), new Shot(108, 0.6, 0.12), new Shot(108, 0.6, 0.10),
	 * new Shot(110, 0.8, 0.09), new Shot(109, 1, 0.06), new Shot(110.5, 1,
	 * 0.05), new Shot(111.25, 1, 0.04), new Shot(111.75, 1, 0.03), new
	 * Shot(116.5, 1, 0.02), new Shot(120, 1, 0.01), new Shot(129, 1, 0) };
	 */

	private static Shot[] shots = {
			new Shot(113.2, 0, .26),
			new Shot(115.2, 0, .25),
			new Shot(116.2, 0, .24),
			new Shot(117.2, 0, .23),
			new Shot(118.2, 0, .22),
			new Shot(117.2, 0, .21),
			new Shot(116.2, 0, .2),
			new Shot(115.2, 0, .19),
			new Shot(116.2, 0, .18),
			new Shot(114.2, 0, .17),
			new Shot(114.2, 0.05, .16),
			new Shot(114.7, 0.05, .15),
			new Shot(115.2, .05, .14),
			new Shot(114.7, .1, .13),
			new Shot(115.2, .15, .12),
			new Shot(115.7, .2, .11),
			new Shot(115.7, .3, .1),
			new Shot(116.2, .3, .09),
			new Shot(116.7, .3, .08), // new Shot(114, .3, .08),
			new Shot(116.7, .4, .07), // new Shot(114.5, .4, .07),
			new Shot(117.2, .4, .06), // new Shot(112.1, .45, .06),  
			new Shot(117.4, .5, .05),  // new Shot(113.1, .5, .05),
			new Shot(117.4, .7, .04), // new Shot(112.6, .5, .04), 
			new Shot(119.2, .7, .03), // new Shot(114.5, .65, .03)
			new Shot(121.2, .9, .02), // new Shot(118, .9, .02)
			new Shot(124.2, 1, .01),  // new Shot(121.5, 1, .01), 
			new Shot(134.2, 1, 0)
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

	@Override
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
