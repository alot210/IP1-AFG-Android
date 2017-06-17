package de.hsd.manguli.fractalsapp;



import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
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
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    EditorActivityFragment eaf = new EditorActivityFragment();
    Julia_Fragment jf = new Julia_Fragment();

    private List<Thread> currentThreads = new ArrayList<>();
    private volatile boolean terminateThreads;
    private static Boolean onCall = false;

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

    /**
     * Methode zur Initialisierung der View, wird beim Erstellen aufgerufen
     */
    private void init() {
        //Paint Objekt initialisieren
        point = new Paint();
        point.setColor(0xff101010);

        //Default-Menge beim Start

    }

    /**
     * Überschreiben der onDraw Methode
     * Hier wird angegeben, was in der View gezeichnet wird
     * @param canvas das übergebene Canvas Objekt, mit dem gezeichnet wird
     */


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //Existiert ein Bitmap?
            //damit onDraw-Methode nur einmal ausgeführt wird (ansonsten wird sie zweimal mit onCreate und onResume aufgerufen)
            //if(onCall) {
            if (bitmap == null) {
                //Bitmap erzeugen mit den Canvas Maßen
                bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                //Canvas mit Bitmap erzeugen
                bitmapCanvas = new Canvas(bitmap);
                bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            //Methode zum Mandelbrot zeichnen aufrufen und in Canvas speichern
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            //Bitmap auf Canvas ausgeben
            drawFractal(bitmapCanvas);
            canvas.drawBitmap(bitmap, 0, 0, point);
            System.out.println("onDraw");
            /*onCall = false;
            return;

        }
        onCall = true;*/
        }

        public void drawFractal(Canvas canvas){
            System.out.println("drawFractal");
            Mandelbrot mb = new Mandelbrot(canvas.getWidth(),canvas.getHeight(), 20 ,new Complex(2.0,2.0),new Complex(3.0,4.0));
            terminateThreads();
            Thread t, t1, t2, t3, t4;

        /*t1 = new MandelThread(mb, canvas, 0, canvas.getWidth(), canvas.getHeight()/4, Color.RED);
        t2 = new MandelThread(mb, canvas, canvas.getHeight()/4, canvas.getWidth(), canvas.getHeight()/2, Color.BLUE);
        t3 = new MandelThread(mb, canvas, canvas.getHeight()/2, canvas.getWidth(), (canvas.getHeight()*3)/4, Color.GREEN);
        t4 = new MandelThread(mb, canvas, (canvas.getHeight()*3)/4, canvas.getWidth(), canvas.getHeight(),Color.YELLOW);

        t2.start();
        t1.start();
        t3.start();
        t4.start();
        currentThreads.add(t1);
        currentThreads.add(t2);
        currentThreads.add(t3);
        currentThreads.add(t4);*/

            int numberOfThreads = 8;
            int startY = 0, stopY = canvas.getHeight()/numberOfThreads, startX = canvas.getWidth();
            for(int i = 0; i < numberOfThreads; i++){

                //Thread initialisieren
                t = new MandelThread(mb, canvas, startY, startX, stopY);
                //Run-Methode starten
                t.start();
                //Threads zur Liste hinzufügen
                currentThreads.add(t);
                //Nächster Abschnitt vom Mandelbrot
                startY = stopY;
                stopY += canvas.getHeight()/numberOfThreads;
            }
        }

        private class MandelThread extends Thread
        {

            private int startY, stopX, stopY;
            private Canvas canvas;
            private Paint paint = new Paint();
            private Algorithm m;

            MandelThread (Algorithm m, Canvas canvas, int startY, int stopX, int stopY) {
                this.canvas = canvas;
                this.startY = startY;
                this.stopX = stopX;
                this.stopY = stopY;
                this.m = m;
            }

            @Override
            public void run(){

                //canvas.drawRect(0, startY , stopX, stopY, point);

                //An den Punkten in der View zeichnen
                for (int i=0; i< stopX;i++){
                    for(int j=startY; j< stopY; j++){
                        //Punkt zeichnen
                        paint.setColor(m.setColor(i, j));
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPoint(i,j,paint);
                        //System.out.println("[ID"+this.getId()+"]"+i+", "+j);
                    }
                }

            }
        }
        public void terminateThreads() {
            try {
                // mandelnative.SignalTerminate(paramIndex);
                terminateThreads = true;

                for(Thread t : currentThreads){
                    if (t.isAlive()) {
                        t.join(DateUtils.SECOND_IN_MILLIS);
                        System.out.println("thread terminated");
                    }
                    if (t.isAlive()) {
                        Log.w(TAG, "Thread is still alive after 1sec...");
                    }
                }

                terminateThreads = false;
                currentThreads.clear();
            } catch(Exception ex) {
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
                    public void onClick(DialogInterface dialog, int which) {}
                });
        return cpdb;
    }//end getColorPickerDialogBuilder
}//end class()
