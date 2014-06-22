package com.example.mucblatt4;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ParachuteActivity extends Activity implements SensorEventListener {

	private SensorManager sensor_manager = null;
	private Sensor pressure_sensor = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parachute);
		
		sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		pressure_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		sensor_manager.registerListener(this, pressure_sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onDestroy() {
				
		sensor_manager.unregisterListener(this);
		super.onDestroy();
	}
	
	public void sendMail(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_SUBJECT, "letzte Grüße");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "Kein Emailprogramm gefunden!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView height_view = (TextView) findViewById(R.id.height_text);
		double pressure = event.values[0];
		int height = (int) (7990 * Math.log(pressure / 1013));
		height_view.setText("" + height + "m");		
	}
}
