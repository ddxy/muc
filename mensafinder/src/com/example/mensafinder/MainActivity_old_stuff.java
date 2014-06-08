package com.example.mensafinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.os.Build;

public class MainActivity_old_stuff extends ActionBarActivity {

	public static Button discoverBtn;
	public static Button searchBtn;
	public static Button settingsBtn;
	public static Context ctx;

	public void discover(View view) {

		// todo: azimuth to deg, Get Name from Somewhere
		int degrees = Orientation.curr_orientation;
		String name = "david";
		HttpQueries.sendMyOrientation(degrees, name);

	}

	public void search(View view) {
		// request list from persons of interest
		Thread kraken1 = new Thread(new Runnable() {
			@Override
			public void run() {
				String result = HttpQueries.requestListOfPersonsOfInterest();
				if (result != null) {
					// Json, lets have fun
					updateMyPic(result);
				} else {
					System.out.println("result: null");
				}

			}

			private void updateMyPic(String result) {
				try {
					JSONObject jObject = new JSONObject(result);
					JSONArray listOfPersonsOfInterest = jObject
							.getJSONArray("list");
					for (int i = 0; i < listOfPersonsOfInterest.length(); i++) {
						JSONObject rowPersonOfInterest = listOfPersonsOfInterest
								.getJSONObject(i);
						String nameOfPersonOfInterest = rowPersonOfInterest
								.getString("user");
						int orientationOfPersonOfInterest = rowPersonOfInterest
								.getInt("orientation");
						System.out.println(nameOfPersonOfInterest + ": "
								+ orientationOfPersonOfInterest);

						// todo: update mensaplan
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// update me when i press discover/changes arrives
		Thread kraken2 = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpQueries.pushUpdates();
			}
		});

		// RELEASE THE KRAKENS
		kraken1.start();
		kraken2.start();

		// someday someone wants to enable it back
		searchBtn.setEnabled(false);
	}

	public void settings(View view) {

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {

		}
		ctx = this;
		discoverBtn = (Button) findViewById(R.id.discover);
		searchBtn = (Button) findViewById(R.id.search);
		settingsBtn = (Button) findViewById(R.id.settings);
		
		Orientation orientThread = new Orientation();
		orientThread.start();

//		LinearLayout to add Images to View
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearlayout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		


/*
 * 		Preferable way to create/draw images. Maybe CompassView and MensaView
 * 		When we create a new Object DrawView, it calls onDraw (dunno why)
 * 		when we call invalidate(), we can call onDraw manually
 */
		Resources res4 = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res4, R.raw.mensa1);
		// Canvas canvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888,
		// true));
		DrawView i2 = new DrawView(this);
		i2.invalidate();
		i2.setImageBitmap(bitmap);
		i2.setAdjustViewBounds(true); // set the ImageView bounds to match the
										// Drawable's dimensions
		i2.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		rl.addView(i2, lp);
		
/*
 * 		another way to create/draw images but with inner methods. i dont like. lets keep it clear.
 */
		ImageView imageView = new ImageView(this) {
			Paint paint = new Paint();
			
			@Override
			public void draw(Canvas canvas) {
				paint.setColor(Color.BLUE);
				super.draw(canvas);
				canvas.drawLine(10, 50, 90, 10, paint);

			}
		};

		imageView.setBackgroundColor(Color.WHITE);
		// svg doesnt work
		// imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.mensa);
		imageView.setImageResource((R.raw.mensa1));

		imageView.invalidate();
		rl.addView(imageView, lp);
		
		
		// testimage without anything
		// ImageView i = new ImageView(this);
		// i.setImageResource(R.raw.mensa1);
		// i.setAdjustViewBounds(true); // set the ImageView bounds to match the
		// Drawable's dimensions
		// i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		// rl.addView(i, lp);

		// myImage.draw(canvas)

		// testimage with drawable
		// ImageView i3 = new ImageView(this);
		// Resources res = this.getResources();
		// Drawable myImage = res.getDrawable(R.raw.mensa1);
		// i3.setImageDrawable(myImage);
		// i3.setAdjustViewBounds(true); // set the ImageView bounds to match
		// the Drawable's dimensions
		// i3.setLayoutParams(new
		// Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		// rl.addView(i3, lp);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

}
