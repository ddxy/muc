package com.example.mucblatt1;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class Orientation extends Activity implements SensorEventListener{
	
	private SensorManager mgr;
	private Sensor orientation;
	private TextView textAzimuth;
	private TextView textPitch;
	private TextView textRoll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orientation);
		
		mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		orientation = mgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		textAzimuth = (TextView) findViewById(R.id.textAzimuth);
		textPitch = (TextView) findViewById(R.id.textPitch);
		textRoll = (TextView) findViewById(R.id.textRoll);

		if (orientation == null) {
			textAzimuth.setText(String.valueOf("Kein Orientation vorhanden"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.orientation, menu);
		return true;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void onResume() {
		super.onResume();
        mgr.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
      
    }
	

    @Override
    protected void onPause() {
    	super.onPause();
        mgr.unregisterListener(this, orientation);
    }
    
	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		textAzimuth.setText(String.valueOf(x));
		textPitch.setText(String.valueOf(y));
		textRoll.setText(String.valueOf(z));
		
	}

}
