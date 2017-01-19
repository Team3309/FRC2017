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

public class VisionServer implements Runnable {

	private AdbBridge adb = new AdbBridge();
	private ServerSocket serverSocket;
	private Socket socket;
	private int PORT = 8254;
	public List<TargetInfo> targets = new LinkedList<TargetInfo>();
	private static VisionServer instance;

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

		} catch (IOException e) {
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
		System.out.println("Starting Run fmd,ahfkjlx");
		while (true) {
			if (serverSocket == null) {
				System.out.println("BREAK");
				return;
			}
			try {
				System.out.println("before serverSocket");
				socket = serverSocket.accept();
				System.out.println("ACCEPT");
				InputStream is = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int read;
				while (socket.isConnected() && (read = is.read(buffer)) != -1) {
					// System.out.println("Got Connection");
					String messageRaw = new String(buffer, 0, read);
					String[] messages = messageRaw.split("\n");
					for (String message : messages) {
						OffWireMessage parsedMessage = new OffWireMessage(message);
						if (parsedMessage.isValid()) {

							handleMessage(parsedMessage);
						}
					}
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

	public void restartAdb() {
		adb.restartAdb();
		adb.reversePortForward(PORT, PORT);
	}
}
