package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

public class AccepThread extends Thread {
	private BluetoothServerSocket mServerSocket;
	private final String NAME = "MUCubigame";
	private UUID uuid = new UUID(0x4080ad8d8ba24846L, 0x8803a3206a8975beL);
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();;
	private boolean socketCreated = false;
	private Handler mHandler;
	private Activity context;
	private ProgressDialog serverDialog;
	
	public AccepThread(Handler mHandler, Activity context) {
		this.context = context;
		this.mHandler = mHandler;
		try {
			mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME,
					uuid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void run() {
		super.run();

		context.runOnUiThread(new Runnable() {
			public void run() {
				serverDialog = ProgressDialog.show(context, "Waiting for Opponent",
						"Please wait...", true);
			}
		});
		
		
		BluetoothSocket socket = null;
		try {
			System.out.println("socket server acceptbefore");
			socket = mServerSocket.accept();
			System.out.println("socket server acceptafter");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		context.runOnUiThread(new Runnable() {
			public void run() {
				if (serverDialog.isShowing())
					serverDialog.dismiss();
			}
		});
		
		
		InputStream input = null;
		OutputStream output = null;
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (socket != null) {
			while (true) {
				try {
					// start new round
					output.write(("game:new_round").getBytes());
					output.flush();
					mHandler.obtainMessage(Game.INT_CONNECTED).sendToTarget();
					
					// wait for 1 to 10s
					Random rand = new Random();
					int randomNum = rand.nextInt(10001 - 1000) + 1000;
					sleep(randomNum);
					
					// choose a gesture
					rand = new Random();
					int gesture = rand.nextInt(8 - 0) + 0;
					String[] gestures = { "square_angle", "square",
							"right", "left", "up", "down", "circle_right",
							"circle_left"
					};
					output.write(("gesture:" + gestures[gesture]).getBytes());
					output.flush();
					
					// save timestamp and display gesture
					mHandler.obtainMessage(Game.INT_GESTURE_TO_CREATE,
							gestures[gesture]).sendToTarget();
					synchronized (mHandler) {
						try {
							mHandler.wait();
						} catch (InterruptedException e) {

						}
					}
					
					// give him about 5sec to perform zee figure
					//sleep(5000);
					//Game.gameOnGoing = false;
					
					// wait for client to send time needed to perform the gesture
					byte[] byteMsgReceived = new byte[100];
					long client_time = 0;
					
					while(true) {
						int bytelength = input.read(byteMsgReceived);
						String msgReceived = new String(byteMsgReceived, 0,
								bytelength);
	
						if (msgReceived.startsWith("time:")) {
							String s_timeClient = msgReceived.substring(5,
									msgReceived.length());
	
							client_time = Long.valueOf(s_timeClient).longValue();
							break;
						} else if (msgReceived.startsWith("game:quit")) {
							mHandler.obtainMessage(Game.INT_QUIT).sendToTarget();
						}
					}
					
					// compare the times needed to perform the gestures
					// wait for server player to perform the gesture
					while (!Game.gestureNeededToWin) {
						sleep(1000);
					}
					long server_time = Game.time_finished - Game.time_started;
					System.out.println("Server time: " + server_time);
					System.out.println("Client time: " + client_time);
					
					if (client_time < server_time) {
						//client wins
						mHandler.obtainMessage(Game.INT_LOST)
						.sendToTarget();
						output.write(("result:win").getBytes());
						output.flush();
						
					} else {
						//server wins
						mHandler.obtainMessage(Game.INT_WON)
						.sendToTarget();
						output.write(("result:loss").getBytes());
						output.flush();
					}
					
					// wait 5 seconds until the next round starts
					sleep(5000);
					
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						output.write(("game:quit").getBytes());
						output.close();
						input.close();
						mServerSocket.close();
						break;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						break;
					}
				}

			}

		}
	}

}