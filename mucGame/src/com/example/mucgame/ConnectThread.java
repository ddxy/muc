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
			mHandler.obtainMessage(Game.INT_CONNECTED).sendToTarget();
			byte[] byteMsgReceived = new byte[100];

			while (true) {
				int bytelength = input.read(byteMsgReceived);
				String msgReceived = new String(byteMsgReceived, 0, bytelength);
				System.out.println("hello:" + msgReceived);
				if (msgReceived.startsWith("gesture:")) {
					String gesture = msgReceived.substring(8,
							msgReceived.length());

					mHandler.obtainMessage(Game.INT_GESTURE_TO_CREATE,
							gesture).sendToTarget();
					
					synchronized (mHandler) {
						try {
							mHandler.wait();
						} catch (InterruptedException e) {

						}
					}
					// give him about 5sec to perform zee figure
					sleep(5000);
					Game.gameOnGoing = false;
					if (Game.gestureNeededToWin == false) {
						//client lost
						output.write(("time:10000").getBytes());
						output.flush();
						
					}else {
						Long client_time = Game.time_finished- Game.time_started;
						output.write(("time:" + Long.toString(client_time)).getBytes());
						output.flush();
					}
	

				} else if (msgReceived.startsWith("result:win")) {
					mHandler.obtainMessage(Game.INT_WON).sendToTarget();

				} else if (msgReceived.startsWith("result:loss")) {
					mHandler.obtainMessage(Game.INT_LOST).sendToTarget();
				}
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}