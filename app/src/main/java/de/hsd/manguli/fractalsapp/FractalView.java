package de.hsd.manguli.fractalsapp;



import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;



/**
 * View zur Darstellung der Mengen
 * und zum zoomen und drangen innerhalb der Menge
 */
public class FractalView extends View{
    private Paint point;
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    EditorActivityFragment eaf = new EditorActivityFragment();
    Julia_Fragment jf = new Julia_Fragment();

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
        drawOnCanvas(bitmapCanvas);
        //Bitmap auf Canvas ausgeben
        canvas.drawBitmap(bitmap, 0, 0, point);
        //Canvas drawBitmap = new Canvas(bitmap);
        canvas.restore();

    }

    public void drawOnCanvas(Canvas canvas){
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
        //Mandelbrot Objekt erstellen

        Boolean juliaPush = Julia_Fragment.juliaPush;
        Boolean mandelPush = EditorActivityFragment.mandelPush;
        int mbItm = Integer.parseInt(EditorActivityFragment.iterartion);

        if(!juliaPush & !mandelPush){
            Mandelbrot mb = new Mandelbrot(canvas.getWidth(),canvas.getHeight(),mbItm,new Complex(2.0,2.0),new Complex(3.0,4.0));

            //An den Punkten in der View zeichnen
            for (int i=0; i< canvas.getWidth();i++){
                for(int j=0; j< canvas.getHeight(); j++){
                    //Farbe und Style setzen
                    point.setColor(mb.setColor(i,j));
                    //point.setColor(mb.setColor(i,j));
                    point.setStyle(Paint.Style.FILL);
                    //Punkt zeichnen
                    canvas.drawPoint(i,j,point);
                }
            }
        }
        else if(mandelPush & !juliaPush) {
            int color1 = EditorActivityFragment.color1;
            int color2 = EditorActivityFragment.color2;
            int color3 = EditorActivityFragment.color3;
            int color4 = EditorActivityFragment.color4;
            Mandelbrot mb = new Mandelbrot(canvas.getWidth(),canvas.getHeight(),mbItm,new Complex(2.0,2.0),new Complex(3.0,4.0));

            //An den Punkten in der View zeichnen
            for (int i=0; i< canvas.getWidth();i++){
                for(int j=0; j< canvas.getHeight(); j++){
                    //Farbe und Style setzen
                    point.setColor(mb.setColor(i,j,color1,color2/*,color3,color4*/));
                    point.setStyle(Paint.Style.FILL);
                    //Punkt zeichnen
                    canvas.drawPoint(i,j,point);
                }
            }
        }
        else {
            int jItm = Integer.parseInt(Julia_Fragment.iterartion);
            double _real = Math.cos((double)Julia_Fragment.real+3.257);
            double _imag = Math.sin((double)Julia_Fragment.imag);
            int color1 = Julia_Fragment.color1;
            int color2 = Julia_Fragment.color2;
            int color3 = Julia_Fragment.color3;
            int color4 = Julia_Fragment.color4;
            Julia ju = new Julia(canvas.getWidth(),canvas.getHeight(),jItm,new Complex(1.5,2.0),new Complex(3.0,4.0),new Complex(-0.7,-0.3));

            //An den Punkten in der View zeichnen
            for (int i=0; i< canvas.getWidth();i++){
                for(int j=0; j< canvas.getHeight(); j++){
                    //Farbe und Style setzen
                    point.setColor(ju.setColor(i,j,color1,color2/*,color3,color4*/));
                    point.setStyle(Paint.Style.FILL);
                    //Punkt zeichnen
                    canvas.drawPoint(i,j,point);
                }
            }
            Julia_Fragment.juliaPush = false;
        }


    }//end drawOnCanvas()

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
