package de.hsd.manguli.fractalsapp.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import de.hsd.manguli.fractalsapp.fragments.JuliaFragment;
import de.hsd.manguli.fractalsapp.fragments.MandelbrotFragment;
import de.hsd.manguli.fractalsapp.models.math.Algorithm;
import de.hsd.manguli.fractalsapp.models.math.Complex;
import de.hsd.manguli.fractalsapp.models.math.Julia;
import de.hsd.manguli.fractalsapp.models.math.Mandelbrot;


import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;



/**
 * View zur Darstellung der Mengen
 * und zum zoomen und drangen innerhalb der Menge
 */
public class FractalView extends View{
    private Paint point;
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    Boolean juliaPush = JuliaFragment.juliaPush;
    Boolean mandelPush = MandelbrotFragment.mandelPush;

    private List<Thread> currentThreads = new ArrayList<>();
    private volatile boolean terminateThreads;

    /**
     * statische Variablen für die minimale und maximale Zoom-Frequenz
     */
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    //Skalierungsfaktor
    private float scaleFactor = 1.f;

    //erstellen eines ScaleGestureDetectors
    private ScaleGestureDetector gestureDetector;

    //wenn kein Finger den Bildschirm berührt
    private static int NONE = 0;
    //wenn 1 Finger den Bildschirm berührt
    private static int DRAG = 1;
    //wenn 2 Finger den Bildschirm berühren
    private static int ZOOM = 2;

    private int mode;

    //x- und y-Koordinaten des ersten Fingers der gesetzt wird
    private float startX = 0f;
    private float startY = 0f;

    //x- und y-Koordinaten des zweiten Fingers der gesetzt wird
    private float translateX = 0f;
    private float translateY = 0f;

    //Koordinaten geben an wo zuletzt hingedragged wurde
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    /**
     *
     * @param context
     */
    //Überschreiben der drei Constructor
    public FractalView(Context context) {
        super(context);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        init();
    }

    public FractalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        init();
    }

    public FractalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
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
            if (bitmap == null) {
                //Bitmap erzeugen mit den Canvas Maßen
                bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                //Canvas mit Bitmap erzeugen
                bitmapCanvas = new Canvas(bitmap);
                bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }

            canvas.save();
            canvas.scale(scaleFactor,scaleFactor);

            //translate soll nicht außerhalb des Canvas stattfinden
            if((translateX*-1)< 0){
                translateX = 0;
            }
            else if((translateX*-1)> (scaleFactor -1)* canvas.getWidth()){
                translateX = (1- scaleFactor)* canvas.getWidth();
            }

            if((translateY*-1)< 0){
                translateY = 0;
            }
            else if((translateY*-1)> (scaleFactor -1)* canvas.getHeight()){
                translateY = (1- scaleFactor)* canvas.getHeight();
            }


            canvas.translate(translateX/scaleFactor,translateY/scaleFactor);
            //Methode zum Mandelbrot zeichnen aufrufen und in Canvas speichern
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            //Bitmap auf Canvas ausgeben
            drawFractal(bitmapCanvas);
            canvas.drawBitmap(bitmap, 0, 0, point);
            System.out.println("onDraw");

            canvas.restore();
            /*onCall = false;
            return;

        }
        onCall = true;*/

        }

        public void drawFractal(Canvas canvas){
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

            int mbItm = Integer.parseInt(MandelbrotFragment.iteration);
            int jItm = Integer.parseInt(JuliaFragment.iteration);
            double _real = Math.cos((double) JuliaFragment.real+3.257);
            double _imag = Math.sin((double) JuliaFragment.imag);
            //nur einmal initialisieren
            int height = canvas.getHeight();
            int width = canvas.getWidth();
            System.out.println("drawFractal");

            //Threads beenden

            terminateThreads();
            Thread t;
            int numberOfThreads = 32;
            int startY = 0, stopY = height/numberOfThreads, startX = width;
            Algorithm am;


            if(!juliaPush) {
                am = new Mandelbrot(width,height, mbItm ,new Complex(2.0,2.0),new Complex(3.0,4.0));
                if(mandelPush) {
                    am.setColor1(MandelbrotFragment.color1);
                    am.setColor2(MandelbrotFragment.color2);
                    am.setColor3(MandelbrotFragment.color3);
                    am.setColor4(MandelbrotFragment.color4);
                }
                MandelbrotFragment.mandelPush = false;
            }
            else {
                am = new Julia(width, height, jItm, new Complex(1.5,2.0),new Complex(3.0,4.0),new Complex(_real,_imag));
                am.setColor1(JuliaFragment.color1);
                am.setColor2(JuliaFragment.color2);
                am.setColor3(JuliaFragment.color3);
                am.setColor4(JuliaFragment.color4);
                JuliaFragment.juliaPush = false;
            }

            for(int i = 0; i < numberOfThreads; i++){
                //Thread initialisieren
                t = new SetThread(am, canvas, startY, startX, stopY);
                //Run-Methode starten
                t.start();
                //Threads zur Liste hinzufügen
                currentThreads.add(t);
                //Nächster Abschnitt vom Mandelbrot
                startY = stopY;
                stopY += height/numberOfThreads;
            }

        }

        private class SetThread extends Thread
        {

            private int startY, stopX, stopY;
            private Canvas canvas;
            private Paint paint = new Paint();
            private Algorithm m;
            final Handler myHandler = new Handler();

            SetThread (Algorithm m, Canvas canvas, int startY, int stopX, int stopY) {
                this.canvas = canvas;
                this.startY = startY;
                this.stopX = stopX;
                this.stopY = stopY;
                this.m = m;
            }

            @Override
            public void run(){
                //An den Punkten in der View zeichnen
                for (int i=0; i< stopX;i++){
                    for(int j=startY; j< stopY; j++){
                        //Punkt zeichnen
                        paint.setColor(m.setColor(i, j));
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPoint(i,j,paint);
                    }
                }

            }
        }

        public void terminateThreads() {
            try {
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



    /**
     * handelt das Touch Event
     * @param event Touch Event dass beim Berühren des Screens übergeben wird
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                //die aktuellen Koordinaten des Fingers beim Berühren des Screens
                startX = event.getX()- previousTranslateX;
                startY = event.getY()- previousTranslateY;
                break;
            case MotionEvent.ACTION_MOVE:
                //wird bei jeder Bewegung des zweiten Fingers geupdatet
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //Ist der zweite Finger gesetzt kann gezoomt werden
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = DRAG;
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                break;
        }

        gestureDetector.onTouchEvent(event);
        if((mode == DRAG && scaleFactor != 1f) || mode == ZOOM){
            invalidate();
        }
        return true;
    }//end onTouchEvent
    /**
     * Klasse wird im Konstruktor von FractalView aufgerufen
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        /**
         * aktueller Skalierungsfaktor wird hier übergeben
         * es wird überprüft ob dieser innerhalb unseres ausgewählten Fensters
         * liegt, wenn ja wird der Zoom ausgeführt
         * @param gestureDetector
         * @return
         */
        public boolean onScale(ScaleGestureDetector gestureDetector){
            scaleFactor *= gestureDetector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            return true;
        }//end onScale
    }//end class ScaleListener()

}//end class()
