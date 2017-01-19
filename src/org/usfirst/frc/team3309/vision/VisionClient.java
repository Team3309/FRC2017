/*
 * Copyright 2016 Vinnie Magro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.usfirst.frc.team3309.vision;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VisionClient implements Runnable {
	
	public enum GoalVisibility {
		NOT_VISIBLE, 
		VISIBLE,
		LOCKED
	}

	private static final long TIMEOUT = 500;
	private static VisionClient instance;

	protected static VisionClient getInstance() {
		if (instance == null) {
			instance = new VisionClient();
		}
		return instance;
	}

	private final Thread thread;
	private final Lock lock;
	private final Condition condition;
	private List<Goal> latestGoals;
	private long lastUpdate = 0;
	private long lastTimeoutTime = 0;

	private VisionClient() {
		this.thread = new Thread(this);
		this.lock = new ReentrantLock();
		this.condition = this.lock.newCondition();
	}

	protected void start() {
		thread.start();
	}

	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(5809);
			System.out.println("Vision client started.");
			byte[] ackBuf = "{\"ack\": true}".getBytes();
			while (true) {
				byte[] buf = new byte[2048];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				
				socket.receive(packet);
				//System.out.println("About to recieve");
				DatagramPacket ack = new DatagramPacket(ackBuf, ackBuf.length, packet.getAddress(), 9033);
				socket.send(ack);

				String messageString = new String(packet.getData(), 0, packet.getLength());
				JSONArray goalsJson = new JSONArray(messageString);
				List<Goal> goals = new LinkedList<Goal>();
				//System.out.println("JSON Sending");
				for (int i = 0; i < goalsJson.length(); i++) {
					JSONObject goalJson = goalsJson.getJSONObject(i);
					JSONObject pos = goalJson.getJSONObject("pos");
					JSONObject size = goalJson.getJSONObject("size");
					goals.add(new Goal(pos.getDouble("x"), pos.getDouble("y"), size.getDouble("width"),
							size.getDouble("height"), goalJson.getDouble("distance"),
							goalJson.getDouble("elevation_angle"), goalJson.getDouble("azimuth")));
				}
				this.lock.lock();
				this.lastUpdate = System.currentTimeMillis();
				this.latestGoals = goals;
				this.condition.signalAll();
				this.lock.unlock();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected List<Goal> getGoals() {
		this.lock.lock();
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdate > TIMEOUT && lastUpdate != lastTimeoutTime) {
			this.latestGoals = null;
			this.lastTimeoutTime = lastUpdate;
			System.out.println("Vision timed out");
		}else {
		
		}
		List<Goal> goals = this.latestGoals;
		this.lock.unlock();
		if (goals == null) {
			return new LinkedList<Goal>();
		}
		return goals;
	}

	protected List<Goal> waitForGoals() {
		this.lock.lock();
		try {
			this.condition.wait();
			System.out.println("WAITING");
			return getGoals();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.lock.unlock();
		}
		return new LinkedList<>();
	}

}
