package com.example.mucblatt1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class Acceleration extends Activity implements SensorEventListener{
	
	private SensorManager mgr;
	private Sensor acc;
	private TextView textXAxis;
	private TextView textYAxis;
	private TextView textZAxis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acceleration);
		
		mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		acc = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		textXAxis = (TextView) findViewById(R.id.textAzimuth);
		textYAxis = (TextView) findViewById(R.id.textYAxis);
		textZAxis = (TextView) findViewById(R.id.textRoll);

		if (acc == null) {
			textXAxis.setText(String.valueOf("Kein Accelerator vorhanden"));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.acceleration, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void onResume() {
		super.onResume();
        mgr.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
      
    }
	

    @Override
    protected void onPause() {
    	super.onPause();
        mgr.unregisterListener(this, acc);
    }
    
	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		textXAxis.setText(String.valueOf(x));
		textYAxis.setText(String.valueOf(y));
		textZAxis.setText(String.valueOf(z));
		
	}

}
