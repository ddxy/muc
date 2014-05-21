package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

public class ConnectThread extends Thread {
	private BluetoothSocket mSocket;
	private BluetoothDevice mServerDevice;
	private UUID uuid = new UUID(0x4080ad8d8ba24846L, 0x8803a3206a8975beL);
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();;
	private Handler mHandler;

	public ConnectThread(BluetoothDevice serverDevice, Handler mHandler) {

		this.mHandler = mHandler;
		mServerDevice = serverDevice;
		try {
			System.out.println("socket client serverdevicecreatebefore");
			mSocket = serverDevice.createRfcommSocketToServiceRecord(uuid);
			System.out.println("socket client serverdevicecreateafter");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void run() {

		// TODO Auto-generated method stub
		super.run();
		// mAdapter.cancelDiscovery();

		try {
			System.out.println("socket client connect");
			mSocket.connect();
			System.out.println("socket client connected");
		} catch (IOException e) {
			System.out.println("error");
			e.printStackTrace();
		}
		
		try (InputStream input = mSocket.getInputStream();
				OutputStream output = mSocket.getOutputStream()) {
			mSocket.getOutputStream();
			System.out.println("INPUT OUTPUT");
			output.write("connected\n".getBytes());
			output.flush();
			byte[] byteMsgReceived = new byte[100];

			while (true) {
				int bytelength = input.read(byteMsgReceived);
				String msgReceived = new String(byteMsgReceived, 0, bytelength);
				System.out.println(msgReceived);
				if (msgReceived.startsWith("gesture:")) {
					String gesture = msgReceived.substring(8,
							msgReceived.length());
					// do sth. call game methods.
					System.out.println(gesture);
					mHandler.obtainMessage(Game.INT_GESTURE_TO_CREATE, gesture)
							.sendToTarget();
					synchronized (mHandler) {
						try {
							mHandler.wait();
						} catch (InterruptedException e) {

						}
					}
					// give him about 3sec to perform zee figure
					sleep(10000);

					// unbind listener
					if (Game.winner) {
						mHandler.obtainMessage(Game.INT_WON, gesture)
								.sendToTarget();
						output.write("win".getBytes());
						output.flush();

						Game.gesture = "";
						Game.gestureNeededToSend = true;
						while (Game.gesture == "") {
							sleep(1000);
						}

						gesture = Game.gesture;

						// asynch. is allowed.
						mHandler.obtainMessage(Game.INT_GESTURE_TO_SEND,
								gesture).sendToTarget();
						Game.gesture = "";
						output.write(("gesture:" + gesture).getBytes());
						output.flush();
						// we won, therefore bind listener, get gesture with
						// timeout and send it to the server.
					} else {
						mHandler.obtainMessage(Game.INT_LOST, gesture)
								.sendToTarget();
						output.write("lose".getBytes());
						output.flush();
					}

				} else if (msgReceived.startsWith("win")) {
					mHandler.obtainMessage(Game.INT_LOST).sendToTarget();

					// we lost therefore the other won must send a gesture
					// to us

				} else if (msgReceived.startsWith("lose")) {
					// we won, enemy lost.
					mHandler.obtainMessage(Game.INT_WON).sendToTarget();
					Game.gesture = "";
					Game.gestureNeededToSend = true;
					while (Game.gesture == "") {
						sleep(1000);
					}

					String gesture = Game.gesture;

					// asynch. is allowed.
					mHandler.obtainMessage(Game.INT_GESTURE_TO_SEND, gesture)
							.sendToTarget();
					Game.gesture = "";
					output.write(("gesture:" + gesture).getBytes());
					output.flush();

				}
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}