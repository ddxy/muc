package com.example.mucblatt4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class ParachuteService extends Service implements SensorEventListener {
	private static final String tag = "ParachuteService";
	
	// minimale Zeit im freien Fall (ms) bis die Aktivität gestartet wird; zum testen eignen sich
	// Werte um 300-500 ms (freier Fall aus 0.5-1.25m), real sollte der Wert bei > 3s (45m) liegen,
	// sonst wird das ganze evtl. noch während einer Achterbahnfahrt ausgelöst!
	private static final int MIN_FALLING_TIME = 400;
	
	// Luftdruck messen um auszuschließen, dass man sich im Orbit o.Ä. befindet. Bei freiem Fall
	// innerhalb der Atmosphäre sollte sich der Luftdruck stetig erhöhen; zum Testen ungeeignet,
	// deshalb abschaltbar.
	private static final boolean USE_PRESSURE = false;


	private WakeLock wake_lock = null;
	private SensorManager sensor_manager = null;
	private Sensor acc_sensor = null;
	private Sensor pressure_sensor = null;
	boolean is_falling = false;
	
	private double current_pressure = 0;
	private double height_start = 0;
	private long falling_time_start = 0;
	
	public ParachuteService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(tag, "onStartCommand");

		acc_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		pressure_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		
		sensor_manager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);
		sensor_manager.registerListener(this, pressure_sensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		return Service.START_REDELIVER_INTENT;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(tag, "onCreate");
		
		PowerManager power_manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wake_lock = power_manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
		wake_lock.acquire();
		
		sensor_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(tag, "onDestroy");
		
		sensor_manager.unregisterListener(this, acc_sensor);
		sensor_manager.unregisterListener(this, pressure_sensor);
		
		wake_lock.release();
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.equals(pressure_sensor)) {
			current_pressure = event.values[0];
		} else if(event.sensor.equals(acc_sensor)) {
			double current_acc = Math.sqrt(event.values[0]*event.values[0]
										 + event.values[1]*event.values[1]
										 + event.values[2]*event.values[2]);
			//Log.i(tag, "current_acc: " + current_acc);
			
			if(current_acc < 3) {
				if(!is_falling) {
					is_falling = true;
					falling_time_start = System.currentTimeMillis();
					height_start = 7990 * Math.log((current_pressure == 0 ? 1000 : current_pressure) / 1013);
				}
				
				double current_falling_time = System.currentTimeMillis() - falling_time_start;
				if(current_falling_time > MIN_FALLING_TIME) {
					if(USE_PRESSURE) {
						double current_height = 7990*Math.log(current_pressure/1013);
						double falling_height = 0.5*9.81*current_falling_time*current_falling_time;
						if(Math.abs(current_height + falling_height - height_start) / falling_height > 0.25) {
							// Abweichung der mit der barometrischen Höhenformel errechneten Fallhöhe zur
							// theoretischen Fallhöhe im Vakuum > 25%
							// => kein freier Fall!
							is_falling = false;
							return;
						}
					}
					
					Intent intent = new Intent(this, ParachuteActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			} else {
				is_falling = false;
			}
		}
	}
}
