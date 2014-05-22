package com.example.mucgame;

import java.util.ArrayList;
import java.util.Set;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	ArrayAdapter<BluetoothDevice> mArrayAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private static int REQUEST_ENABLE_BT = 1;
	ListView serverlist;
	ProgressDialog serverDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		serverlist = (ListView) findViewById(R.id.serverlist);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (mBluetoothAdapter == null) {
			// no bluetooth
		}
		// lets start Bluetooth
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtInten = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(enableBtInten, REQUEST_ENABLE_BT);
		}

		mArrayAdapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				new ArrayList<BluetoothDevice>());
		System.out.println("onstart");
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices

		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				System.out.println("bluetoothgeräte paired: "
						+ device.getAddress() + device.getName());
				// du solltest nur die devices hinzufügen, die auch wirklich
				// aktiv sind.
				mArrayAdapter.add(device);
			}
		}

		// this part AFTER all devices have been put to the mArrayAdapter!!!!
		if (mArrayAdapter != null)
			serverlist.setAdapter(mArrayAdapter);
		serverlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item index
				// int itemPosition = position;

				// ListView Clicked item value/device
				BluetoothDevice serverDevice = (BluetoothDevice) serverlist
						.getItemAtPosition(position);

				// starts client with selected item as server

//				ConnectThread clientThread = new ConnectThread(serverDevice);
//				clientThread.start();


				// wenn verbindung steht intent aufrufen:
				Intent intent = new Intent(view.getContext(), Game.class);
				intent.putExtra("who", String.valueOf("Client"));
				intent.putExtra("serverDevice", serverDevice);
				startActivity(intent);

			}

		});

		// registerReceiver(mReceiver, filter); // Don't forget to unregister
		// during onDestroy
		// mBluetoothAdapter.startDiscovery();

	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a
				// ListView
				// mArrayAdapter.add(device.getName() + "\n" +
				// device.getAddress());
			}
		}
	};
	// Register the BroadcastReceiver
	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Log.d(BLUETOOTH_SERVICE, "Bluetooth Callback");
		super.onActivityResult(arg0, arg1, arg2);

	}

	public void startServer(View view) {
		System.out.println("start server thread");
		// show wait box
		serverDialog = ProgressDialog.show(this, "Waiting for Opponent",
				"Please wait...", true);
//		AccepThread serverThread = new AccepThread();
//		serverThread.start();

		// noch an richtige stelle einfügen
		if (serverDialog.isShowing())
			serverDialog.dismiss();
		Intent intent = new Intent(this, Game.class);
		intent.putExtra("who", String.valueOf("Server"));
		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
