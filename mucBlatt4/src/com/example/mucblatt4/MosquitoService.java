package com.example.mucblatt4;

import java.util.Calendar;
import java.util.Date;






import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class MosquitoService extends Service implements SensorEventListener {

	private static final String TAG = "MosquitoService";
	WakeLock wakeLock;
	private SensorManager mSensorManager = null;
	Sensor tempSensor, humSensor;
	float temperature = 0f;
	float humidity = 0f;
	Thread mosquito = null;
	AudioTrack audioTrack;
	boolean playMosquitoSound;
	Context ctx;
	Notification notifMosquito;
	NotificationManager mNMgr;
	
	@Override
	  public void onCreate() {
		super.onCreate();
		Log.v(TAG, "Service onCreate");
		ctx = this;
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		        TAG);
		wakeLock.acquire();
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // This must be in onCreate since it needs the Context to be created.
		
		//get notification manager
		mNMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId); // If this is not written then onHandleIntent is not called.
		 
		Log.v(TAG, "Service onStartCommand");
		
		Log.v(TAG, "Service running");
		
	    if (mSensorManager != null) {
	    	registerListeners();
	    }
		 
	    String mosquitosound = intent.getStringExtra("mosquitosound");
	    playMosquitoSound = (mosquitosound.equals("1"))?true:false;
	    
	    return Service.START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.v(TAG, "Service onBind");
		return null;
	}
	
	@Override
	  public void onDestroy() {
		Log.v(TAG, "Service onDestroy");
		super.onDestroy();
        if (mSensorManager != null) {
        	mSensorManager.unregisterListener(this, tempSensor);
        	mSensorManager.unregisterListener(this, humSensor);
        }
		wakeLock.release();
		mNMgr.cancelAll();
		if(audioTrack!=null) audioTrack.stop();
	}
	
	private void registerListeners() {
        Log.v(TAG, "Registering sensors listeners");
        tempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, humSensor, SensorManager.SENSOR_DELAY_NORMAL);
                
       
	}

	@Override
	public void onSensorChanged(SensorEvent currentEvent) {

        synchronized (this) {

            int sensor = currentEvent.sensor.getType();
            switch (sensor) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            	temperature = currentEvent.values[0];
                Log.v(TAG, "Temperature: " + temperature + " �C");
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            	humidity = currentEvent.values[0];
                Log.v(TAG, "Humidity: " + humidity + " %");
                break;
            }
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            int time = Integer.parseInt(today.format("%k"));
            
            if(playMosquitoSound && (time<4 || time>19) && temperature>20 && humidity>40) {
            	Log.v(TAG, "Anti Mosquito Service executed");
            	if(mosquito==null) {
            		Runnable r = new Runnable() {
            		       public void run() {
            		     		synchronized (this) {
            		     			//create Notification und set default properties (sound, vibrate,...)
            		     	    	notifMosquito = new Notification();
            		     	    	notifMosquito.defaults = Notification.DEFAULT_ALL;
            		     	    	//create "pending intent" to start activity
            		     	    	//when notifiation is selected
            		     	    	Intent notifIntent = new Intent(ctx, MosquitoActivity.class);
            		     	    	PendingIntent pendIntent = 
            		     	    	 PendingIntent.getActivity(ctx, 0, notifIntent, 0);
            		     	    	//add notification infos for panel view and add pending intent
            		     	    	notifMosquito.icon = R.drawable.laser;
            		     	    	notifMosquito.setLatestEventInfo(ctx, "Anti Mosquito Sound", "IMMA CHARGIN MAH LAZER", pendIntent);          		     	    	
            		     	    	//add notification
            		     	    	mNMgr.notify(1337, notifMosquito);
            		     	    	
            		     	    	AudioManager a = (AudioManager)ctx.getSystemService(ctx.AUDIO_SERVICE);
            		     	    	if(!a.isWiredHeadsetOn()) {
            		     	    		Log.v(TAG, "Start playing Sound");
            		     	    		//generate tone
            		     	    		int duration = 3; // seconds
            		     	    	    int sampleRate = 8000;
            		     	    	    int numSamples = duration * sampleRate;
            		     	    	    double sample[] = new double[numSamples];
            		     	    	    double freqOfTone = 20000; // hz
           		     	    	    	byte generatedSnd[] = new byte[2 * numSamples];
            		     	    	    // fill out the array
            		     	           for (int i = 0; i < numSamples; ++i) {
            		     	               sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
            		     	           }

            		     	           // convert to 16 bit pcm sound array
            		     	           // assumes the sample buffer is normalised.
            		     	           int idx = 0;
            		     	           for (final double dVal : sample) {
            		     	               // scale to maximum amplitude
            		     	               final short val = (short) ((dVal * 32767));
            		     	               // in 16 bit wav PCM, first byte is the low order byte
            		     	               generatedSnd[idx++] = (byte) (val & 0x00ff);
            		     	               generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

            		     	           }
            		     	           
            		     	           //play sound
            		     	          audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            		     	                 sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
            		     	                 AudioFormat.ENCODING_PCM_16BIT, numSamples,
            		     	                 AudioTrack.MODE_STATIC);
            		     	         audioTrack.write(generatedSnd, 0, generatedSnd.length);
            		     	         audioTrack.setLoopPoints(0, generatedSnd.length/4, -1); //loop it
            		     	         audioTrack.play();
            		     	    	}
				        	    }
            		           	
            		           Log.v(TAG, "Sound playing");
            		       }
            	    };
            	    mosquito = new Thread(r);
            		mosquito.start();
            	}
            } else { // turn off notification
            	if(mosquito!=null) {
            		try {
						mosquito.join();
					} catch (InterruptedException e) {
					}
            		mosquito = null;
            		mNMgr.cancel(1337);
            		if(audioTrack!=null) audioTrack.stop();
            		Log.v(TAG, "Turn off mosquito sound");
            	}
            }
        }		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	
}
