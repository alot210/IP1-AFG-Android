package de.hsd.manguli.fractalsapp.views;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import de.hsd.manguli.fractalsapp.fragments.JuliaFragment;
import de.hsd.manguli.fractalsapp.fragments.MandelbrotFragment;
import de.hsd.manguli.fractalsapp.models.math.Algorithm;
import de.hsd.manguli.fractalsapp.models.math.Complex;
import de.hsd.manguli.fractalsapp.models.math.Julia;
import de.hsd.manguli.fractalsapp.models.math.Mandelbrot;

import static android.content.ContentValues.TAG;


/**
 * View zur Darstellung der Mengen
 */
public class FractalView extends View {
    private Paint paint;
    private Bitmap bitmap = null;
    private List<Thread> currentThreads = new ArrayList<>();
    private volatile boolean terminateThreads;
    private static Boolean onCall = false;
    private int[] pixels;
    Boolean juliaPush = JuliaFragment.juliaPush;
    Boolean mandelPush = MandelbrotFragment.mandelPush;
    Algorithm am;

    //Auflösung wird mit 16x16, 8x8, 4x4 und 2x2 berechnet
    private int granulation = 16;
    private int endOfGranulation = 2;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        System.out.println("new Bitmap");
        this.bitmap = bitmap;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int getGranulation() {
        return granulation;
    }

    public void setGranulation(int granulation) {
        this.granulation = granulation;
    }

    public int getEndOfGranulation() {
        return endOfGranulation;
    }

    public void setEndOfGranulation(int endOfGranulation) {
        this.endOfGranulation = endOfGranulation;
    }


    //Überschreiben der drei Constructor
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

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * Methode zur Initialisierung der View, wird beim Erstellen aufgerufen
     */
    private void init() {
        paint = new Paint();
        paint.setColor(0xff101010);
        //Androidversion ueberprufen => ab Marshmallow
        if (Build.VERSION.SDK_INT > 23) {
            onCall = true;
        }
    }

    /**
     * onDraw() wird im LifeCycle aufgerufen
     * bei Lollipop in onCreate und onResume
     * ab Nougat nur
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Nur einmal drawFractal aufrufen
        if (onCall) {
            terminateThreads();
            drawFractal(canvas);
            onCall = false;
            Log.d("LOGGING", "drawFractal initial call");
            return;
        }
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Log.d("LOGGING", "drawBitmap()");
            return;
        }
        onCall = true;
    }

    /**
     * erstellt Bitmap mit Mandelbrot/Juliamenge
     * @param canvas
     */
    public void drawFractal(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        Log.d("LOGGING", "drawFractal()");

        //final, da innerhalb von Thread verwendet wird
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        int mandelbrotIteration = Integer.parseInt(MandelbrotFragment.iteration);
        int juliaIteration = Integer.parseInt(JuliaFragment.iteration);

        if(!juliaPush) {
            am = new Mandelbrot(width,height, mandelbrotIteration ,new Complex(2.0,2.0),new Complex(3.0,4.0));
            if(mandelPush) {
                am.setColor1(MandelbrotFragment.color1);
                am.setColor2(MandelbrotFragment.color2);
                am.setColor3(MandelbrotFragment.color3);
                am.setColor4(MandelbrotFragment.color4);
            }
            MandelbrotFragment.mandelPush = false;
        }
        else {
            am = new Julia(width, height, juliaIteration, new Complex(1.5,2.0),new Complex(3.0,4.0),new Complex(-0.7,-0.3));
            am.setColor1(JuliaFragment.color1);
            am.setColor2(JuliaFragment.color2);
            am.setColor3(JuliaFragment.color3);
            am.setColor4(JuliaFragment.color4);
            JuliaFragment.juliaPush = false;
        }

        //Pixelarray initialiseren
        setPixels(new int[height * width]);

        terminateThreads();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int granulation = getGranulation();
                int endOfGranulation = getEndOfGranulation();

                //solange Thread lebt oder höchste Auflösung (granulation) erreicht ist
                while (true) {

                    pixels = am.fillPixels(granulation);
                    //neue Bitmap setzen und Pixelarray übergeben
                    setBitmap(Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888));
                    //View soll neu gerendert werden
                    FractalView.this.postInvalidate();

                    //wenn Thread beendet ist => thread.join()
                    if (terminateThreads) {
                        break;
                    }
                    //wenn Auflösung erreich ist
                    if (granulation <= endOfGranulation) {
                        break;
                    }
                    //Auflösung verfeinern
                    granulation = granulation / 2;
                }
            }
        });
        t.start();
        currentThreads.add(t);
        Log.d("LOGGING", "drawFractal() beendet");
    }

    /**
     * Methode um Threads zu beenden
     */
    public void terminateThreads() {
        int counter = 0;
        try {
            terminateThreads = true;

            for (Thread t : currentThreads) {
                if (t.isAlive()) {
                    t.join(DateUtils.SECOND_IN_MILLIS);
                    counter++;
                    System.out.println(counter + ". thread terminated");
                } else {
                    System.out.println(counter + ". thread dead");
                }
                if (t.isAlive()) {
                    Log.w(TAG, counter + ". Thread is still alive after 1sec...");
                }
            }

            terminateThreads = false;
            currentThreads.clear();
        } catch (Exception ex) {
            Log.w(TAG, "Exception while terminating threads: " + ex.getMessage());
        }
    }


    //Methode gibt ein Teil des Color Picker Dialogfensters zurück
    public ColorPickerDialogBuilder getColorPickerDialogBuilder() {
        ColorPickerDialogBuilder cpdb = ColorPickerDialogBuilder
                //Initialisierung
                .with(getContext())
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()
                //CancelButton
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return cpdb;
    }//end getColorPickerDialogBuilder
}//end class()