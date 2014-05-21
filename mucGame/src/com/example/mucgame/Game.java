package com.example.mucgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EventListener;
import java.util.UUID;

import de.dfki.ccaal.gestures.Distribution;
import de.dfki.ccaal.gestures.IGestureRecognitionListener;
import de.dfki.ccaal.gestures.IGestureRecognitionService;

import android.R.bool;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class Game extends ActionBarActivity {
	public static boolean winner;
	public static boolean gameOnGoing = false;
	public static boolean gestureNeededToWin = false;
	public static String gesture = "";
	private static ImageView  image;
	private TextView roundnumber, youpoints, opponentpoints;
	private String who;
	private Button start;
	private Context context;
	public static final int INT_CONNECTED = 1;
	public  static int INT_LOST = 3;
	public  static int INT_WON = 4;
	public  static int INT_UNBIND = 5;
	public  static int INT_BIND = 6;
	public  static int INT_GESTURE_TO_CREATE = 7;
	public static long time_started;
	public static long time_finished;
	
	
	private IGestureRecognitionService mRecService;
	
	private IBinder mGestureListenerStub = new IGestureRecognitionListener.Stub() {

		@Override
		public void onGestureRecognized(Distribution distribution)
				throws RemoteException {
			 String gesture_created = distribution.getBestMatch();
			 System.out.println("desired:" + gesture + " and received:" + gesture_created);
			 System.out.println(gesture.equals(gesture_created));
			 if(gameOnGoing && !gestureNeededToWin && (gesture.equals(gesture_created))){
				 System.out.println("set:" + time_finished);
				 time_finished = System.currentTimeMillis();
				 System.out.println("after:" + time_finished);
				 gestureNeededToWin = true;
			 }
			 System.out.println(gesture_created);
			
		}

		@Override
		public void onGestureLearned(String gestureName) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTrainingSetDeleted(String trainingSet)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		
	};
	private ServiceConnection mGestureConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRecService = IGestureRecognitionService.Stub.asInterface(service);
			try {
				mRecService.registerListener(IGestureRecognitionListener.Stub.asInterface(mGestureListenerStub));
				mRecService.startClassificationMode("muc");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRecService = null;
		}
		
	};
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what == INT_CONNECTED){
				 Toast.makeText(getApplicationContext(),
	                     "GET READY" ,3000)
	                     .show();
				//both player connected. start game
				
			//show pic which enemy must create
			}else if (msg.what == INT_GESTURE_TO_CREATE) {
				 gesture =  (String) msg.obj;

				 Toast.makeText(getApplicationContext(),
	                     "Do a " + gesture , Toast.LENGTH_LONG)
	                     .show();
					switch(gesture) {
					case "square_angle": image.setImageResource(R.drawable.square_angle); break;
					case "square": image.setImageResource(R.drawable.square); break;
					case "right": image.setImageResource(R.drawable.right); break;
					case "left": image.setImageResource(R.drawable.left); break;
					case "up": image.setImageResource(R.drawable.up); break;
					case "down": image.setImageResource(R.drawable.down); break;
					case "circle_right": image.setImageResource(R.drawable.circle_right); break;
					case "circle_left": image.setImageResource(R.drawable.circle_left); break;
					}
					 gestureNeededToWin = false;
					 gameOnGoing = true;
					 time_started = System.currentTimeMillis();
					
					synchronized (this) {
						notifyAll();
					}
				 
			}
			else if (msg.what == INT_LOST) {
				int currPoints = Integer.parseInt(opponentpoints.getText().toString());
				opponentpoints.setText(String.valueOf(currPoints+1));
				 Toast.makeText(getApplicationContext(),
	                     "Your opponent won this round. Get Ready" , 3000)
	                     .show();
			}else if (msg.what == INT_WON) {
				int currPoints = Integer.parseInt(youpoints.getText().toString());
				youpoints.setText(String.valueOf(currPoints+1));
				 Toast.makeText(getApplicationContext(),
	                     "You won this round. Get Ready" , 3000)
	                     .show();
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		image = (ImageView) findViewById(R.id.image);
		roundnumber = (TextView) findViewById(R.id.roundnumber);
		roundnumber.setText("0");
		youpoints = (TextView) findViewById(R.id.youpoints);
		youpoints.setText("0");
		opponentpoints = (TextView) findViewById(R.id.opponentpoints);
		opponentpoints.setText("0");
		context = this;
		
		who = getIntent().getStringExtra("who");
		

		
		//start clientThread
		if(who.equals("Client")) {
			BluetoothDevice serverDevice = getIntent().getExtras().getParcelable("serverDevice");
			ConnectThread clientThread = new ConnectThread(serverDevice, mHandler);
			clientThread.start();
		}
		
		//start ServerThread
		if(who.equals("Server")) {
			AccepThread serverThread = new AccepThread(mHandler);
			serverThread.start();

		}

	}
	
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		bind();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
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
	
	public void bind(){
			Intent gestureBindIntent = new Intent("de.dfki.ccaal.gestures.GESTURE_RECOGNIZER");
			bindService(gestureBindIntent, mGestureConn, Context.BIND_AUTO_CREATE);
	}
	public void unbind(){
		try {
			mRecService.unregisterListener(IGestureRecognitionListener.Stub.asInterface(mGestureListenerStub));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRecService = null;
		unbindService(mGestureConn);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unbind();
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bind();
	}
	

}
