package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

		if (!socketCreated) {
			while (true) {
				try {
					byte[] byteMsgReceived = new byte[100];
					int bytelength = input.read(byteMsgReceived);
					String msgReceived = new String(byteMsgReceived, 0,
							bytelength);
					System.out.println(msgReceived);
					if (msgReceived.startsWith("connected")) {
						mHandler.obtainMessage(Game.INT_CONNECTED).sendToTarget();
						System.out.println("Player connected. Lets play.");
						// send Random gesture:
						Game.gesture = "";
						Game.gestureNeededToSend = true;

						while(Game.gesture == ""){
							sleep(1000);
						}

						String gesture = Game.gesture;
						
						// asynch. is allowed.
						mHandler.obtainMessage(Game.INT_GESTURE_TO_SEND, gesture).sendToTarget();
						Game.gesture = "";
						output.write(("gesture:" + gesture).getBytes());
						output.flush();
						
					} else if (msgReceived.startsWith("gesture:")) {
						String gesture = msgReceived.substring(8, msgReceived.length());
						//do sth. call game methods.
						System.out.println(gesture);
						mHandler.obtainMessage(Game.INT_GESTURE_TO_CREATE, gesture).sendToTarget();
						synchronized (mHandler) {
						    try {
						    	mHandler.wait();
						    } catch (InterruptedException e) {
						    	
						    }
						}
						//give him about 3sec to perform zee figure
						sleep(10000);

						//unbind listener
						if(Game.winner){
							mHandler.obtainMessage(Game.INT_WON, gesture).sendToTarget();
							output.write("win".getBytes());
							output.flush();
							

							Game.gesture = "";
							Game.gestureNeededToSend = true;
							while(Game.gesture == ""){
								sleep(1000);
							}

							gesture = Game.gesture;
							
							// asynch. is allowed.
							mHandler.obtainMessage(Game.INT_GESTURE_TO_SEND, gesture).sendToTarget();
							Game.gesture = "";
							output.write(("gesture:" + gesture).getBytes());
							output.flush();
							//we won, therefore bind listener, get gesture with timeout and send it to the server.
						}else {
							mHandler.obtainMessage(Game.INT_LOST, gesture).sendToTarget();
							output.write("lose".getBytes());
							output.flush();
						}

					} else if (msgReceived.startsWith("win")) {
						mHandler.obtainMessage(Game.INT_LOST).sendToTarget();

						// we lost therefore the other won must send a gesture
						// to us

					} else if (msgReceived.startsWith("lose")) {
						//we won, enemy lost.
						mHandler.obtainMessage(Game.INT_WON).sendToTarget();
						Game.gesture = "";
						Game.gestureNeededToSend = true;
						while(Game.gesture == ""){
							sleep(1000);
						}

						String gesture = Game.gesture;
						
						// asynch. is allowed.
						mHandler.obtainMessage(Game.INT_GESTURE_TO_SEND, gesture).sendToTarget();
						Game.gesture = "";
						output.write(("gesture:" + gesture).getBytes());
						output.flush();

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