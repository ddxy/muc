package com.example.mucblatt1;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class GPS extends Activity implements LocationListener{
	
	private LocationManager mgr;
	private TextView textLongitude;
	private TextView textLatitude;
	private TextView textAltitude;
	private TextView textAccuracyPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps);
		
		mgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		textLongitude = (TextView) findViewById(R.id.textLongitude);
		textLatitude = (TextView) findViewById(R.id.textLatitude);
		textAltitude = (TextView) findViewById(R.id.textAltitude);
		textAccuracyPos = (TextView) findViewById(R.id.textAccuracyPos);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.g, menu);
		return true;
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		textLongitude.setText(String.valueOf(arg0.getLongitude()));
		textLatitude.setText(String.valueOf(arg0.getLatitude()));
		textAltitude.setText(String.valueOf(arg0.getAltitude()));
		textAccuracyPos.setText(String.valueOf(arg0.getAccuracy()));
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		textLongitude.setText(String.valueOf("Kein GPS vorhanden"));
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		mgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
	}
}
