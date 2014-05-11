package com.example.mucblatt1;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage(View view) {
	    // Do something in response to button
		//binding between two seperate components (such as two activities)
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		startActivity(intent);

	}
	
	public void sendLight(View view) {
	    // Do something in response to button
		//binding between two seperate components (such as two activities)
		Intent intent = new Intent(this, Light_Sensor.class);
		startActivity(intent);

	}
	
	public void sendAcc(View view) {
	    // Do something in response to button
		//binding between two seperate components (such as two activities)
		Intent intent = new Intent(this, Acceleration.class);
		startActivity(intent);

	}
	
	public void sendOrientation(View view) {
	    // Do something in response to button
		//binding between two seperate components (such as two activities)
		Intent intent = new Intent(this, Orientation.class);
		startActivity(intent);

	}

	public void sendGPS(View view) {
	    // Do something in response to button
		//binding between two seperate components (such as two activities)
		Intent intent = new Intent(this, GPS.class);
		startActivity(intent);

	}

}
