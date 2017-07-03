package de.hsd.manguli.fractalsapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import de.hsd.manguli.fractalsapp.EditorActivityFragment;

import de.hsd.manguli.fractalsapp.Complex;

import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * View zur Darstellung der Mengen
 */
public class FractalView extends View {
    private Paint point;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        System.out.println("new Bitmap");
        this.bitmap = bitmap;
    }

    private Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    Boolean juliaPush = Julia_Fragment.juliaPush;
    Boolean mandelPush = EditorActivityFragment.mandelPush;

    int counter = 0;
    int VIEW_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    int VIEW_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    DateUtils d = new DateUtils();
    private List<Thread> currentThreads = new ArrayList<>();
    private volatile boolean terminateThreads;
    private static Boolean onCall = false;

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    private int[] pixels;

    public int getGranulation() {
        return granulation;
    }

    public void setGranulation(int granulation) {
        this.granulation = granulation;
    }

    private int granulation = 32;

    public int getEndOfGranulation() {
        return endOfGranulation;
    }

    public void setEndOfGranulation(int endOfGranulation) {
        this.endOfGranulation = endOfGranulation;
    }

    private int endOfGranulation = 2;


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
        //Paint Objekt initialisieren
        point = new Paint();
        point.setColor(0xff101010);
        if(Build.VERSION.SDK_INT > 23) {
            onCall = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        counter = 0;

        if (onCall) {
            //pixels = new int[canvas.getHeight() * canvas.getWidth()];
            terminateThreads();
            //pixels = drawFractal(canvas);
            drawFractal(canvas);
            //bitmap = Bitmap.createBitmap(pixels, canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            onCall = false;
            return;
        }
        if(bitmap != null) {
            System.out.println("drawBitmap");
            canvas.drawBitmap(bitmap, 0, 0, point);
            return;
        }
        onCall = true;
    }

    public int[] drawFractal(Canvas canvas) {
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        System.out.println("drawFractal");

        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        setPixels(new int[height*width]);

        terminateThreads();
        final Algorithm am;
        am = new Mandelbrot(width, height, 100, new Complex(2.0, 2.0), new Complex(3.0, 4.0));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int c;
                int loc;
                int granulation = getGranulation();
                int endOfGranulation = getEndOfGranulation();
                while (true) {

                    for (int i = 0; i < width - 1; i += granulation) {
                        for (int j = 0; j < height - granulation; j += granulation) {
                            loc = i + j * width;
                            c = am.setColor(i, j);
                            pixels[loc] = c;
                            int n = granulation*granulation-1;

                            while(n > 0){
                                if (n % granulation == 0) {
                                    loc = loc + width;
                                    pixels[loc] = c;
                                }
                                pixels[loc + (n % granulation)] = c;
                                n--;
                            }
                        }
                    }
                FractalView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("post");
                            setBitmap(Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888));
                        }
                    });
                    FractalView.this.postInvalidate();
                    if(terminateThreads){
                        break;
                    }
                    if (granulation <= endOfGranulation) {
                        break;
                    }
                    granulation = granulation/2;
            }}
        });
        t.start();
        currentThreads.add(t);
        /*try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.println("drawFractal finished");
        return pixels;
    }

    public void terminateThreads() {
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
