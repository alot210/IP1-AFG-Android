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
import java.text.AttributedCharacterIterator;

public class FractalView extends View {
    private Paint point;

    public FractalView(Context context) {
        super(context);
        init();
    }

    public FractalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FractalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        point = new Paint(Paint.ANTI_ALIAS_FLAG);
        point.setColor(0xff101010);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        point.setColor(Color.BLUE);
        point.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,canvas.getWidth()/2,canvas.getHeight()/2,point);
    }

}
