package com.example.mensafinder;

import java.util.HashMap;
import java.util.Map;

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
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	public static Button discoverBtn;
	public static Button searchBtn;
	public static Button settingsBtn;
	public static Context ctx;
	public static String userName = "David";

	public static int azimut;
	public static HashMap<String, Integer> user = new HashMap<String, Integer>();

	public class CustomDrawableView extends View {
		Paint paint = new Paint();

		public CustomDrawableView(Context context) {
			super(context);
			paint.setColor(0xff00ff00);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			paint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int width = getWidth();
			int height = getHeight();
			int centerx = width / 2;
			int centery = height / 2;
			canvas.drawLine(centerx, 0, centerx, height, paint);
			canvas.drawLine(0, centery, width, centery, paint);

			paint.setTextSize(28f);
			paint.setColor(Color.GRAY);
			canvas.drawText("0° N", centerx, centery - 250, paint);
			canvas.drawText("180° S", centerx, centery + 250, paint);
			canvas.drawText("90° O", centerx + 250, centery, paint);
			canvas.drawText("270° W", centerx - 250, centery, paint);
			canvas.rotate(-90, centerx, centery);

			for (Map.Entry<String, Integer> entry : user.entrySet()) {
				double angle = entry.getValue() * Math.PI / 180;
				int x2 = (int) (centerx + 200 * Math.cos(angle));
				int y2 = (int) (centerx + 200 * Math.sin(angle));
				System.out.println(x2);
				String name = entry.getKey();
				if (name.equals(userName) ){
					paint.setColor(Color.RED);
					canvas.drawLine(centerx, centery, x2, y2, paint);
					canvas.drawText("le moi", x2, y2, paint);
				}else {
					paint.setColor(Color.BLUE);
					canvas.drawLine(centerx, centery, x2, y2, paint);
					canvas.drawText(name, x2, y2, paint);
					
				}


			}
			paint.setColor(0xff00ff00);
		}
	}

	public static CustomDrawableView mCustomDrawableView;

	public void discover(View view) {

		// todo: azimuth to deg, Get Name from Somewhere
		int degrees = Orientation.curr_orientation;
		HttpQueries.sendMyOrientation(degrees, userName);
		System.out.println(userName);

		// show window
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle("Message");
		// set dialog message
		alertDialogBuilder
				.setMessage("What do you want to do?")
				.setCancelable(false)
				.setPositiveButton("Recalibrate",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// MainActivity.this.finish();
								int degrees = Orientation.curr_orientation;
								HttpQueries
										.sendMyOrientation(degrees, userName);
								mCustomDrawableView.postInvalidate();
							}
						})
				.setNegativeButton("Undiscover",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								HttpQueries.logout(userName);
								user.remove(userName);
								mCustomDrawableView.postInvalidate();
								dialog.cancel();

							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();

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

						// add to map and update compass
						user.put(nameOfPersonOfInterest,
								orientationOfPersonOfInterest);
						mCustomDrawableView.postInvalidate();
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

		// never enable it back, since Perseus can't stop the Krakens in a safe
		// way
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

		LinearLayout commentsLayout = (LinearLayout) findViewById(R.id.compasscontainer);

		mCustomDrawableView = new CustomDrawableView(this);
		commentsLayout.addView(mCustomDrawableView);

		// ImageView imageView = new ImageView(this) {
		// @Override
		// public void draw(Canvas canvas) {
		// canvas.scale((float)1.0, (float)1.5);
		// canvas.translate(0, -100);
		// super.draw(canvas);
		// // Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		// // canvas.drawCircle(0, 0, 5, p);
		// }
		// };
		ImageView imageView = (ImageView) findViewById(R.id.imageview);

		imageView.setBackgroundColor(Color.WHITE);
		// svg doesnt work
		// imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.mensa);
		imageView.setImageResource((R.raw.mensa1));

		// LinearLayout rl = (LinearLayout) findViewById(R.id.image);
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// rl.addView(imageView);

		Orientation orientThread = new Orientation();
		orientThread.start();

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

}
