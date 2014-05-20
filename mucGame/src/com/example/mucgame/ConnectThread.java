package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread{
	private BluetoothSocket mSocket;
	private BluetoothDevice mServerDevice;
	private  UUID uuid= new UUID(0x4080ad8d8ba24846L, 0x8803a3206a8975beL);
	private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();;
	
	
	public ConnectThread(BluetoothDevice serverDevice){
		System.out.println("socket client konstruktor");
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
		System.out.println("socket client threadrun");
		//mAdapter.cancelDiscovery();
		
		try {
			System.out.println("socket client connect");
			mSocket.connect();
			System.out.println("socket client connected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (InputStream input = mSocket.getInputStream(); OutputStream output = mSocket.getOutputStream()) {
			mSocket.getOutputStream();
			System.out.println("INPUT OUTPUT");
			output.write("Hallo Server\n".getBytes());
			output.flush();
			byte[] arg = new byte[100];
			
			int bytesToRead = 13;
			while (bytesToRead > 0) {
				bytesToRead -= input.read(arg, 0, bytesToRead);
			}
			System.out.println(new String(arg));

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
