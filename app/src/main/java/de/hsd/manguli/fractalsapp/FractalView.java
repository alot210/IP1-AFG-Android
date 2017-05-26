package de.hsd.manguli.fractalsapp;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.view.View;

import de.hsd.manguli.fractalsapp.Complex;

import java.lang.reflect.Array;

public class FractalView extends View {
    private Paint point;
    public FractalView(Context context) {
        super(context);
        init();
    }

    private void init() {
        point = new Paint(Paint.ANTI_ALIAS_FLAG);
        point.setColor(0xff101010);

    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        point.setColor(Color.BLUE);
        point.setStyle(Paint.Style.FILL);
        canvas.drawRect(2f,3f,4f,4f,point);
    }

}
