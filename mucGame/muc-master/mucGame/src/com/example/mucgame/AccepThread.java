package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AccepThread extends Thread {
	private BluetoothServerSocket mServerSocket;
	private final String NAME = "MUCubigame";
	private UUID uuid = new UUID(0x4080ad8d8ba24846L, 0x8803a3206a8975beL);
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();;
	private static boolean socketCreated = false;

	public AccepThread() {
		
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
		System.out.println("socket server null");
		if (!socketCreated) {
			while (true) {
				try {
					System.out.println("socket server acceptednull");
					socket = mServerSocket.accept();
					System.out.println("socket server accepted");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (socket != null) {
					try (InputStream input = socket.getInputStream();
							OutputStream output = socket.getOutputStream()) {
						System.out.println("INPUT OUTPUT");
						socket.getOutputStream();
						output.write("Hallo Client\n".getBytes());
						output.flush();
						byte[] arg = new byte[100];

						int bytesToRead = 13;
						while (bytesToRead > 0) {
							bytesToRead -= input.read(arg, 0, bytesToRead);
						}
						System.out.println(new String(arg));

						mServerSocket.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

}
