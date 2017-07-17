package de.hsd.manguli.fractalsapp.views;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

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

    //Größe des Screens
    int screenWidth;
    int screenHeight;
    //Zustand des Zyklus
    int animationCycleState = 0;
    //Ist Zyklus abgeschlossen
    volatile boolean animationCycleFinished = false;
    //Real und Imaginärteil für die Animation
    double animationReal = 2, animationImag = 2;
    //Start und Stop der Animation
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
    private int endOfGranulation = 1;

    //statische Variablen für die minimale und maximale Zoom-Frequenz
    private static float MIN_ZOOM = 0.2f;
    private static float MAX_ZOOM = 2f;

    //Skalierungsfaktor
    private float scaleFactor = 1.0f;

    //erstellen eines ScaleGestureDetectors
    private ScaleGestureDetector gestureDetector;

    //x- und y-Koordinaten des ersten Fingers der gesetzt wird
    private float startX = 0f;
    private float startY = 0f;

    //Koordinaten geben an wo zuletzt hingedragged wurde
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    //Koordinaten der letzten Geste
    private float lastGestureX = 0f;
    private float lastGestureY = 0f;

    /**
     * setzen der Bitmap
     * @param bitmap die gezeichnete Bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        System.out.println("new Bitmap");
        this.bitmap = bitmap;
    }

    /**
     * setzten der Pixel im Pixel-Array
     * @param pixels
     */
    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    /**
     *
     * @return aktuelle granulation der Bitmap
     */
    public int getGranulation() {
        return granulation;
    }

    /**
     *
     * @return endOfGranulation die kleinste Granulation, die erreicht wird
     */
    public int getEndOfGranulation() {
        return endOfGranulation;
    }


    /**
     *
     * @param context
     */
    public FractalView(Context context) {
        super(context);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public FractalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        init();
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FractalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new ScaleGestureDetector(context,new ScaleListener());
        init();
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
            if ((screenWidth != this.getWidth()) || (screenHeight != this.getHeight())) {
                screenWidth = this.getWidth();
                screenHeight = this.getHeight();
                if ((screenWidth == 0) || (screenHeight == 0)) {
                    return;
                }
                scaleX = 3.0;
                scaleY = 4.0;
                terminateThreads();
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                drawFractal();

                //Soll direkt in ein Seepferdchen gezoomt werden?
                if (MandelbrotFragment.seahorse) {
                    scaleX = 0.10211656607523975;
                    scaleY = 0.13615542143365297;
                    translate = new Complex(0.8086260179507412, 0.24773066371129954);
                    drawFractal();
                }

                //Wurde die Animation im Editor aktiviert?
                if (MandelbrotFragment.animation) {
                    //Toast, um anzuzeigen, wie ANimation gestoppt wird
                    Toast.makeText(getContext(), "Berühre den Bildschirm, um die Animation zu stoppen.", Toast.LENGTH_LONG).show();

                    mandelSetAnimation(1000 / MandelbrotFragment.speed);
                }

                onCall = false;
                Log.d("LOGGING", "drawFractal initial call");
                return;
            }
        }
        if (bitmap != null) {
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
            canvas.save();

            //canvas.translate(startX/scaleFactor,startY/scaleFactor);

            if(gestureDetector.isInProgress()){
                canvas.scale(this.scaleFactor,this.scaleFactor,gestureDetector.getFocusX(),gestureDetector.getFocusY());
            }
            else{
                canvas.scale(this.scaleFactor,this.scaleFactor, lastGestureX,lastGestureY);

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
        Log.d("LOGGING", "drawFractal()");

        //Speichert Werte aus dem Editor in Variablen ab
        int mandelbrotIteration = Integer.parseInt(MandelbrotFragment.iteration);
        int juliaIteration = Integer.parseInt(JuliaFragment.iteration);
        double _real = Math.cos((double) JuliaFragment.real+3.257);
        double _imag = Math.sin((double) JuliaFragment.imag);

        //Wurde der DrawButton im Editor der Juliamenge oder der Mandelbrotmenge geklickt?
        if(!juliaPush) {
            //Setze den Algorithmus auf die Mandelbrotmenge
            am = new Mandelbrot(screenWidth, screenHeight, mandelbrotIteration ,translate ,new Complex(scaleX, scaleY));

            //Speichert die Farben aus dem Editor ab
            if(mandelPush) {
                am.setColor1(MandelbrotFragment.color1);
                am.setColor2(MandelbrotFragment.color2);
                am.setColor3(MandelbrotFragment.color3);
                am.setColor4(MandelbrotFragment.color4);
            }

            MandelbrotFragment.mandelPush = false;
        }
        else {
            //Zentriere die Juliamenge, initialisiere sie und setze die Farben
            translate = new Complex(1.5,2.0);
            am = new Julia(screenWidth, screenHeight, juliaIteration, translate,new Complex(scaleX,scaleY),new Complex(_real,_imag));
            am.setColor1(JuliaFragment.color1);
            am.setColor2(JuliaFragment.color2);
            am.setColor3(JuliaFragment.color3);
            am.setColor4(JuliaFragment.color4);
            JuliaFragment.juliaPush = false;
        }

        //Pixelarray initialiseren
        setPixels(new int[screenWidth * screenHeight]);

        //WorkerThread für die Berechnung der Mengen und setzen der Bitmap
        Thread t = new Thread(new Runnable() {
            int granulationProgress = 0;

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    granulationProgress ++;
                    if(!animationIsRunning) {
                        Toast.makeText(getContext(),"Detailgrad: "+granulationProgress+"/"+3, Toast.LENGTH_SHORT).show();
                    }
                }
            };

            @Override
            public void run() {
                int granulation = getGranulation();
                int endOfGranulation = getEndOfGranulation();
                int width = screenWidth;
                int height = screenHeight;

                //solange Thread lebt oder höchste Auflösung (granulation) erreicht ist
                while (true) {

                    pixels = am.fillPixels(granulation);
                    //wenn Thread beendet ist => thread.join()
                    if (terminateThreads) {
                        break;
                    }
                    //neue Bitmap setzen und Pixelarray übergeben
                    setBitmap(Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888));
                    //View soll neu gerendert werden
                    FractalView.this.postInvalidate();
                    handler.sendMessage(handler.obtainMessage());
                    //wenn Auflösung erreich ist
                    if (granulation <= endOfGranulation) {
                        Log.w("GRANULATION", "End of Granulation");
                        break;
                    }
                    //Auflösung verfeinern
                    granulation = granulation / 4;
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
                    t.join();
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

    /**
     * Setze die Daten für den ColorPicker bis auf Cancel Button
     * @return Gibt einen ColorPicker zurück
     */
    public ColorPickerDialogBuilder getColorPickerDialogBuilder() {
        ColorPickerDialogBuilder cpdb = ColorPickerDialogBuilder
                //Initialisierung
                .with(getContext())
                .setTitle("Farbe auswählen")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .lightnessSliderOnly()
                //CancelButton
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        return cpdb;
    }//end getColorPickerDialogBuilder

    /**
     * handelt das Touch Event
     * @param event Touch Event dass beim Berühren des Screens übergeben wird
     * @return true wenn eine gültiges Touch-Event ausgeführt wurde
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){

        //rekursiver Aufruf über den gestureDetector
        gestureDetector.onTouchEvent(event);
        //abfangen der verschieden MotionEvents auf dem Screen
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            //der erste Finger ist gesetzt
            case MotionEvent.ACTION_DOWN:
                //Wenn der Finger das Display berührt, wird die Animation gestoppt
                animationIsRunning = false;
                granulation = 16;
                endOfGranulation = 1;
                if(!gestureDetector.isInProgress()) {
                    //Koordinaten des ersten Fingers
                    final float x = event.getX();
                    final float y = event.getY();
                    previousTranslateX = x;
                    previousTranslateY = y;
                    Log.w("ONTOUCH", "ACTION_DOWN");
                }
                break;
            //der erste Finger bewegt sich
            case MotionEvent.ACTION_MOVE:
                //Abfragen ob gestureDetector bereits verwendet wird
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

                    lastGestureX = gx;
                    lastGestureY = gy;
                    invalidate();
                }
                Log.w("ONTOUCH", "ACTION_MOVE");
                break;
            //der zweite Finger ist gesetzt
            case MotionEvent.ACTION_POINTER_DOWN:
                if(gestureDetector.isInProgress()) {
                    //Ist der zweite Finger gesetzt kann gezoomt werden
                    zooming = true;
                    final float gx = gestureDetector.getFocusX();
                    final float gy = gestureDetector.getFocusY();
                    //Koordinate der letzen Geste
                    lastGestureX = gx;
                    lastGestureY = gy;
                }
                Log.w("ONTOUCH", "ACTION_POINTER_DOWN");
                break;
            //der erste Finger verlässt den Screen
            case MotionEvent.ACTION_UP:
                Log.w("ONTOUCH", "ACTION_UP");

                //die Bitmap muss an die Position der letzten Geste angepasst werden
                //und wird beim Verlassen der Finger vom Screen neu gezeichnet
                //dabei muss zwischen der Zoom-Geste und der Drag-Geste unterschieden werden
                if(zooming){
                    Log.w("ZOOM", "zooming");

                    factor = 1.0/scaleFactor;

                    scaleX *= factor;
                    scaleY *= factor;

                    double dX = scaleX*(lastGestureX-(lastGestureX*scaleFactor))/screenWidth;
                    double dY = scaleY*(lastGestureY-(lastGestureY*scaleFactor))/screenHeight;


                    translate = new Complex(translate.getReal()+dX, translate.getImag()+dY);

                    zooming = false;
                }else{

                    translate = translate.add(new Complex((scaleX*(startX))/screenWidth, (scaleY*(startY)/screenHeight)));
                }

                Log.w("TRANSLATE", "startX: "+(startX)+", "+(startY));
                Log.w("TRANSLATE", "previousTranslateX: "+(previousTranslateX)+", "+(previousTranslateY));
                Log.w("TRANSLATE", "lastGestureX: "+(lastGestureX)+", "+(lastGestureY));
                Log.w("TRANSLATE", "lastGestureX/factor: "+lastGestureX/factor+", "+lastGestureY/factor);
                Log.w("TRANSLATE", "lastGestureX/scalefactor: "+lastGestureX/scaleFactor+", "+lastGestureY/scaleFactor);
                Log.w("TRANSLATE", "lastGestureX-lastGestureX/factor: "+(lastGestureX-lastGestureX/factor)+", "+(lastGestureY-lastGestureY/factor));
                Log.w("TRANSLATE", "scaleFactor: "+(scaleFactor+""));
                Log.w("COMPLEX","scaleX: "+scaleX+", "+scaleY);

                //Werte werden auf null gesetzt, da eine neue Bitmap gezeichnet wurde
                startX = 0;
                startY = 0;
                previousTranslateY = 0;
                previousTranslateX = 0;
                drawFractal();
                scaleFactor = 1.0f;
                break;
            //der zweite Finger verlässt den Screen
            case MotionEvent.ACTION_POINTER_UP:
                Log.w("ONTOUCH", "ACTION_POINTER_UP");
                break;
        }
        return true;
    }//end onTouchEvent

    /**
     * Definition des Listeners, der im Konstruktor von FractalView übergeben wird
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        /**
         * aktueller Skalierungsfaktor wird hier übergeben
         * es wird überprüft ob dieser innerhalb unseres ausgewählten Fensters
         * liegt, wenn ja wird der Zoom ausgeführt
         * @param gestureDetector
         * @return true wenn die Skalierung ausgeführt werden kann
         */
        public boolean onScale(ScaleGestureDetector gestureDetector){
            scaleFactor *= gestureDetector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            return true;
        }//end onScale
    }//end class ScaleListener()

    /**
     * Animation der Mandelbrotmenge; automatischer Zoom
     * @param speed Schnelligkeit mit der die Animation stattfindet
     */
    public void mandelSetAnimation(final long speed) {
        granulation = 8;
        endOfGranulation = 8;
        final Handler handler = new Handler();
        //Runnable wird der Message Queue angehangen
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Zeichne die Mandelbrotmenge solange neu, bis ACTION_DOWN Event ausgeführt wird
                if (animationIsRunning) {
                    //Wird der erste oder zweite Zyklus ausgeführt?
                    if(animationCycleFinished) {
                        //Führe den ersten Zyklus der Animation aus
                        //nachdem der erste Zyklus durchlaufen wurde
                        if(animationCycleState ==1) {
                            animationCycleFinished = false;
                        }
                        //Inkrementiere die Skalierung, sowie den Real- und Imaginärteil
                        scaleX += 0.29;
                        scaleY += 0.39;
                        translate = new Complex(animationReal +=0.12, animationImag +=0.175);
                        animationCycleState--;
                    }
                    else {
                        //Führe den zweiten Zyklus der Animation aus
                        //nachdem der erste Zyklus durchlaufen wurde
                        if(animationCycleState == 22) {
                            animationCycleFinished = true;
                        }
                        //Dekrementiere die Skalierung, sowie den Real- und Imaginärteil
                        scaleX -=0.29;
                        scaleY -=0.39;
                        translate = new Complex(animationReal -=0.12, animationImag -=0.175);
                        animationCycleState++;
                    }
                    //Zeichne das Fraktal neu
                    drawFractal();
                    //Runnable wird je nach Wert der Seekbar der Message Queue angehangen
                    handler.postDelayed(this, speed);
                }
            }
        });
    }//end mandelSetAnimation
}//end class()