package com.example.mensafinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

class DrawView extends ImageView {
	  Paint paint = new Paint();

	  public DrawView(Context context) {
	    super(context);
	    paint.setColor(Color.BLUE);
	  }
	  @Override
	  public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    canvas.drawLine(10, 50, 90, 10, paint);
	    canvas.drawLine(10, 80, 90, 10, paint);
	    canvas.drawText("That's the Way (I like it) ", 80, 50, paint);

	  }
	}