package com.example.mucblatt4;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class MosquitoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mosquito);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mosquito, menu);
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
	
	public void sendStart(View view) {
		Log.v("MosquitoActivity", "Service started");
		Intent intent = new Intent(this, MosquitoService.class);
		intent.putExtra("mosquitosound", "1");
		startService(intent);
		Toast.makeText(getApplicationContext(), "Sound playing", Toast.LENGTH_LONG).show();
	}
	
	public void sendStop(View view) {
		Log.v("MosquitoActivity", "Service stopped");
		Intent intent = new Intent(this, MosquitoService.class);
		intent.putExtra("mosquitosound", "0");
		stopService(intent);
		Toast.makeText(getApplicationContext(), "Sound stopped", Toast.LENGTH_LONG).show();
	     
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_mosquito,
					container, false);
			return rootView;
		}
	}

}
