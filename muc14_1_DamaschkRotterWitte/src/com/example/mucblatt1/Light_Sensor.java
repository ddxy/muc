package com.example.mucblatt1;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Light_Sensor extends Activity implements SensorEventListener {

	private SensorManager mgr;
	private Sensor light;
	private TextView textLight;
	private GraphView graphView;
	GraphViewSeries exampleSeries;
	private int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light__sensor);

		mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		light = mgr.getDefaultSensor(Sensor.TYPE_LIGHT);
		textLight = (TextView) findViewById(R.id.textLight);

		if (light == null) {
			textLight.setText(String.valueOf("Kein Lichtsensor vorhanden"));
		}

		exampleSeries = new GraphViewSeries(new GraphViewData[] {});
		i = 0;
		graphView = new LineGraphView(this, "Lichtgraph");
		graphView.addSeries(exampleSeries);
		
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			public String formatLabel(double value, boolean isValueX) {
				if(isValueX) return String.valueOf(value);
				
				return String.valueOf((int) Math.pow(10, value));
			}
		});
		graphView.setManualYAxisBounds(4, 0);
		//graphView.setLegendWidth(150);

		LinearLayout layout = (LinearLayout) findViewById(R.id.mainLL);
		layout.addView(graphView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.light__sensor, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mgr.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mgr.unregisterListener(this, light);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		float lightLevel = arg0.values[0];
		textLight.setText(String.valueOf(lightLevel));
		exampleSeries.appendData(new GraphViewData(i, (lightLevel <= 0 ? 0 : Math.log10(lightLevel))), false, 200);
		i++;
		graphView.redrawAll();

	}

}
