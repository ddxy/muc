package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

public class AccepThread extends Thread {
	private BluetoothServerSocket mServerSocket;
	private final String NAME = "MUCubigame";
	private UUID uuid = new UUID(0x4080ad8d8ba24846L, 0x8803a3206a8975beL);
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();;
	private boolean socketCreated = false;
	private Handler mHandler;

	public AccepThread(Handler mHandler) {

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
		BluetoothSocket socket = null;
		try {
			System.out.println("socket server acceptbefore");
			socket = mServerSocket.accept();
			System.out.println("socket server acceptafter");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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
			mHandler.obtainMessage(Game.INT_CONNECTED).sendToTarget();
			while (true) {
				try {
					
					Random rand = new Random();
					int randomNum = rand.nextInt(10001 - 1000) + 1000;
					sleep(randomNum);
					rand = new Random();
					int gesture = rand.nextInt(8 - 0) + 0;
					String[] gestures = { "square_angle", "square",
							"right", "left", "up", "down", "circle_right",
							"circle_left"

					};

					output.write(("gesture:" + gestures[gesture]).getBytes());
					output.flush();
					
					mHandler.obtainMessage(Game.INT_GESTURE_TO_CREATE,
							gestures[0]).sendToTarget();
					
					synchronized (mHandler) {
						try {
							mHandler.wait();
						} catch (InterruptedException e) {

						}
					}
					// give him about 5sec to perform zee figure
					sleep(5000);
					Game.gameOnGoing = false;
					
					byte[] byteMsgReceived = new byte[100];
					int bytelength = input.read(byteMsgReceived);
					String msgReceived = new String(byteMsgReceived, 0,
							bytelength);

					if (msgReceived.startsWith("time:")) {
						String s_timeClient = msgReceived.substring(5,
								msgReceived.length());

						Long client_time = Long.valueOf(s_timeClient).longValue();
						
						Long server_time = Game.time_finished- Game.time_started;
						System.out.println(server_time);
						System.out.println(Game.time_finished);
						System.out.println(Game.time_started);
						if (Game.gestureNeededToWin == false) {

							//we also lose, update game ui
							mHandler.obtainMessage(Game.INT_LOST)
							.sendToTarget();
							if (client_time < 10000) {

								output.write(("result:win").getBytes());
								output.flush();
							}else {
								output.write(("result:loss").getBytes());
								output.flush();
							}
						}else if (client_time - server_time > 0) {
							output.write(("result:loss").getBytes());
							output.flush();
							mHandler.obtainMessage(Game.INT_WON)
							.sendToTarget();
						}else {
							output.write(("result:loss").getBytes());
							output.flush();
							mHandler.obtainMessage(Game.INT_LOST)
							.sendToTarget();

						}
						//begin again

					}
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
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