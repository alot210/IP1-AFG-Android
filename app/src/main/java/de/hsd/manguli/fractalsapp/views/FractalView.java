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
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    int i = 0;
    volatile boolean zoomingZyklusFinished = false;
    double animationScaleXValue = 0, animationScaleYValue = 0;
    double animationReal = 0.55, animationImag = 0.83, animationRealStart = 2,animationImagStart = 2;
    boolean animationIsRunning = true;
    private Paint paint;
    private Bitmap bitmap = null;
    private List<Thread> currentThreads = new ArrayList<>();
    private volatile boolean terminateThreads;
    private static Boolean onCall = false;
    private int[] pixels;
    Boolean juliaPush = JuliaFragment.juliaPush;
    Boolean mandelPush = MandelbrotFragment.mandelPush;
    Algorithm am;

    //Transformationsvariablen
    private double scaleX;
    private double scaleY;
    private double factor = 1.0;
    private Complex translate = new Complex(2.0*factor, 2.0*factor);
    private Boolean zooming = false;

    //Auflösung wird mit 16x16, 8x8, 4x4 und 2x2 berechnet
    private int granulation = 16;
    private int endOfGranulation = 16;

    /**
     * statische Variablen für die minimale und maximale Zoom-Frequenz
     */
    private static float MIN_ZOOM = 0.2f;
    private static float MAX_ZOOM = 2f;

    //Skalierungsfaktor
    private float scaleFactor = 1.0f;

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

    //Koordinaten geben an wo zuletzt hingedragged wurde
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    //Koordinaten der letzten Geste
    private float lastGestureX = 0f;
    private float lastGestureY = 0f;

    private static final int INVALID_POINTER_ID = -1;
    private int  activePointerId = INVALID_POINTER_ID;


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
            scaleX = 3.0;
            scaleY = 4.0;
            //translate = new Complex(scaleY*0.5, scaleY*0.5);
            terminateThreads();
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            drawFractal();

            //Soll direkt in ein Seepferdchen gezoomt werden?
            if (MandelbrotFragment.seahorse){
                scaleX = 0.10211656607523975;
                scaleY = 0.13615542143365297;
                translate = new Complex(0.8086260179507412, 0.24773066371129954);
                drawFractal();
            }

            if(MandelbrotFragment.animation) {
                mandelsetAnimation(MandelbrotFragment.speed * 100);
            }

            onCall = false;
            Log.d("LOGGING", "drawFractal initial call");
            return;
        }
        if (bitmap != null) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            canvas.save();

            //canvas.translate(startX/scaleFactor,startY/scaleFactor);

            if(gestureDetector.isInProgress()){
                canvas.scale(this.scaleFactor,this.scaleFactor,gestureDetector.getFocusX(),gestureDetector.getFocusY());
                //translate soll nicht außerhalb des Canvas stattfinden
                //scaleWindow(canvas);
            }
            else{
                canvas.scale(this.scaleFactor,this.scaleFactor, lastGestureX,lastGestureY);
                //translate soll nicht außerhalb des Canvas stattfinden
                //scaleWindow(canvas);

            }

            //Methode zum Mandelbrot zeichnen aufrufen und in Canvas speichern
            canvas.drawBitmap(bitmap, 0, 0, paint);

            canvas.restore();
            Log.d("LOGGING", "drawBitmap()");
            return;
        }
        onCall = true;
    }

    /**
     * erstellt Bitmap mit Mandelbrot/Juliamenge
     */
    public void drawFractal() {
        terminateThreads();
        //canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        Log.d("LOGGING", "drawFractal()");

        //final, da innerhalb von Thread verwendet wird
        final int width = this.getWidth();
        final int height = this.getHeight();
        Log.w("WIDTH", ""+width);
        Log.w("HEIGHT", ""+height);

        int mandelbrotIteration = Integer.parseInt(MandelbrotFragment.iteration);
        int juliaIteration = Integer.parseInt(JuliaFragment.iteration);
        double _real = Math.cos((double) JuliaFragment.real+3.257);
        double _imag = Math.sin((double) JuliaFragment.imag);

        if(!juliaPush) {

            Log.w("TRANSLATE", translate.complexToString());
            am = new Mandelbrot(width,height, mandelbrotIteration ,translate ,new Complex(scaleX, scaleY));

            //new Complex(2.0,2.0) Translation; new Complex(3.0/4.0/ Skalierung;
            //Zahlen < 1 damit das Mandelbrot größer wird
            //Verschiebung von Pixel in das Koordinatensystem

            if(mandelPush) {
                am.setColor1(MandelbrotFragment.color1);
                am.setColor2(MandelbrotFragment.color2);
                am.setColor3(MandelbrotFragment.color3);
                am.setColor4(MandelbrotFragment.color4);
            }
            MandelbrotFragment.mandelPush = false;
        }
        else {
            am = new Julia(width, height, juliaIteration, translate,new Complex(scaleX,scaleY),new Complex(_real,_imag));
            am.setColor1(JuliaFragment.color1);
            am.setColor2(JuliaFragment.color2);
            am.setColor3(JuliaFragment.color3);
            am.setColor4(JuliaFragment.color4);
            JuliaFragment.juliaPush = false;
        }

        //Pixelarray initialiseren
        setPixels(new int[height * width]);



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
                        Log.w("GRANULATION", "End of Granulation");
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

    /**
     * handelt das Touch Event
     * @param event Touch Event dass beim Berühren des Screens übergeben wird
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        gestureDetector.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                animationIsRunning = false;
                if(!gestureDetector.isInProgress()) {
                    mode = DRAG;
                    //Koordinaten des ersten Fingers
                    final float x = event.getX();
                    final float y = event.getY();
                    previousTranslateX = x;
                    previousTranslateY = y;
                    activePointerId = event.getPointerId(0);
                    Log.w("ONTOUCH", "ACTION_DOWN");
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if(!gestureDetector.isInProgress()) {
                    //wird bei jeder Bewegung des Fingers geupdatet
                    final float x = event.getX();
                    final float y = event.getY();

                    //Koordinaten des neuen Punktes
                    final float dx = x - previousTranslateX;
                    final float dy = y - previousTranslateY;

                    //Berechnung der Bewegung
                    startX += dx;
                    startY += dy;


                    //drawFractal();
                    previousTranslateX = x;
                    previousTranslateY = y;
                    invalidate();
                }
                else{
                    zooming = true;

                    final float gx = gestureDetector.getFocusX();
                    final float gy = gestureDetector.getFocusY();

                    final float gdx = gx - lastGestureX;
                    final float gdy = gy - lastGestureY;

                    startX += gdx;
                    startY += gdy;

                    //drawFractal();
                    lastGestureX = gx;
                    lastGestureY = gy;
                    invalidate();
                }
                Log.w("ONTOUCH", "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(gestureDetector.isInProgress()) {
                    //Ist der zweite Finger gesetzt kann gezoomt werden
                    mode = ZOOM;
                    zooming = true;
                    final float gx = gestureDetector.getFocusX();
                    final float gy = gestureDetector.getFocusY();
                    //Koordinate der letzen Geste
                    lastGestureX = gx;
                    lastGestureY = gy;
                }
                Log.w("ONTOUCH", "ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_UP:

                endOfGranulation = 16;
                Log.w("ONTOUCH", "ACTION_UP");

                if(zooming){
                    Log.w("ZOOM", "zooming");

                    factor = 1.0/scaleFactor;

                    scaleX *= factor;
                    scaleY *= factor;

                    double dX = scaleX*(lastGestureX-(lastGestureX*scaleFactor))/this.getWidth();
                    double dY = scaleY*(lastGestureY-(lastGestureY*scaleFactor))/this.getHeight();


                    translate = new Complex(translate.getReal()+dX, translate.getImag()+dY);

                    zooming = false;
                }else{

                    translate = translate.add(new Complex((scaleX*(startX))/this.getWidth(), (scaleY*(startY)/this.getHeight())));
                }
                mode = NONE;

                Log.w("TRANSLATE", "startX: "+(startX)+", "+(startY));
                Log.w("TRANSLATE", "previousTranslateX: "+(previousTranslateX)+", "+(previousTranslateY));
                Log.w("TRANSLATE", "lastGestureX: "+(lastGestureX)+", "+(lastGestureY));
                Log.w("TRANSLATE", "lastGestureX/factor: "+lastGestureX/factor+", "+lastGestureY/factor);
                Log.w("TRANSLATE", "lastGestureX/scalefactor: "+lastGestureX/scaleFactor+", "+lastGestureY/scaleFactor);
                Log.w("TRANSLATE", "lastGestureX-lastGestureX/factor: "+(lastGestureX-lastGestureX/factor)+", "+(lastGestureY-lastGestureY/factor));
                Log.w("TRANSLATE", "scaleFactor: "+(scaleFactor+""));
                Log.w("COMPLEX","scaleX: "+scaleX+", "+scaleY);

                startX = 0;
                startY = 0;
                previousTranslateY = 0;
                previousTranslateX = 0;
                drawFractal();
                scaleFactor = 1.0f;
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.w("ONTOUCH", "ACTION_POINTER_UP");
                break;
        }
        return true;
    }//end onTouchEvent
    /**
     * Methode überprüft, dass nicht aus dem Canvas herausgezoomt werden kann
     *  das übergebene Canvas-Objekt auf das gezeichnet wird
     */

    public void scaleWindow(Canvas canvas){
        if((startX*-1)< 0){
            //startX = 0;
        }
        else if((startX*-1)> (scaleFactor -1)* canvas.getWidth()){
            startX = (1- scaleFactor)* canvas.getWidth();
        }

        if((startY*-1)< 0){
           // startY = 0;
        }
        else if((startY*-1)> (scaleFactor -1)* canvas.getHeight()){
            startY = (1- scaleFactor)* canvas.getHeight();
        }
    }//end scaleWindow
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

    public void mandelsetAnimation(final long speed) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (animationIsRunning) {
                    if(zoomingZyklusFinished) {
                        if(i==1) {
                            zoomingZyklusFinished = false;
                        }
                        scaleX += 0.29;
                        scaleY += 0.39;
                        translate = new Complex(animationRealStart+=0.12,animationImagStart+=0.175);
                        i--;
                    }
                    else {
                        if(i == 22) {
                            zoomingZyklusFinished = true;
                        }
                        scaleX -=0.29;
                        scaleY -=0.39;
                        translate = new Complex(animationRealStart-=0.12,animationImagStart-=0.175);
                        i++;
                    }
                    drawFractal();
                    handler.postDelayed(this, speed);
                }
            }
        });
    }
}//end class()