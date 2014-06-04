package com.example.mensafinder;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Orientation extends Thread implements SensorEventListener{
	
	private SensorManager mgr;
	private Sensor orientation;
	private TextView textAzimuth;
	private TextView textPitch;
	private TextView textRoll;
	public static int curr_orientation = 0;
	

	@Override
	public void run() {
		super.run();
//		Context context = MainActivity.instance.getApplicationContext(); 
		mgr = (SensorManager) MainActivity.ctx.getSystemService(Context.SENSOR_SERVICE);
		orientation = mgr.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mgr.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
		
		
		
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

    
	@Override
	public void onSensorChanged(SensorEvent event) {
		MainActivity.azimut = (int)event.values[0];
		float pitch = event.values[1];
		float roll = event.values[2];
		
//		System.out.println(String.valueOf(x));
		curr_orientation = (int) MainActivity.azimut;

//		textAzimuth.setText(String.valueOf(x));
//		textPitch.setText(String.valueOf(y));
//		textRoll.setText(String.valueOf(z));
		//MainActivity.go();
		
		
		
	}

}
