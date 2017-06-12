package de.hsd.manguli.fractalsapp;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import de.hsd.manguli.fractalsapp.EditorActivityFragment;

import de.hsd.manguli.fractalsapp.Complex;

import java.lang.reflect.Array;
import java.text.AttributedCharacterIterator;



/**
 * View zur Darstellung der Mengen
 */
public class FractalView extends View {
    private Paint point;
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    EditorActivityFragment eaf = new EditorActivityFragment();
    Julia_Fragment jf = new Julia_Fragment();

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
        //Methode zum Mandelbrot zeichnen aufrufen und in Canvas speichern
        drawOnCanvas(bitmapCanvas);
        //Bitmap auf Canvas ausgeben
        canvas.drawBitmap(bitmap, 0, 0, point);

    }

    public void drawOnCanvas(Canvas canvas){
        //Mandelbrot Objekt erstellen

        Boolean juliaPush = jf.getJuliaPush();

        if(!juliaPush){
            int itm = Integer.parseInt(eaf.getIteration());
            Mandelbrot mb = new Mandelbrot(canvas.getWidth(),canvas.getHeight(),itm,new Complex(2.0,2.0),new Complex(3.0,4.0));

            //An den Punkten in der View zeichnen
            for (int i=0; i< canvas.getWidth();i++){
                for(int j=0; j< canvas.getHeight(); j++){
                    //Farbe und Style setzen
                    point.setColor(mb.setColor(i,j));
                    point.setStyle(Paint.Style.FILL);
                    //Punkt zeichnen
                    canvas.drawPoint(i,j,point);
                }
            }
        }
        else{
            int itj = Integer.parseInt(jf.getIteration());
            double _real = Math.cos((double)jf.getReal()+3.257);
            double _imag = Math.sin((double)jf.getImag());
            Julia ju = new Julia(canvas.getWidth(),canvas.getHeight(),itj,new Complex(1.5,2.0),new Complex(3.0,4.0),new Complex(_real,_imag));

            //An den Punkten in der View zeichnen
            for (int i=0; i< canvas.getWidth();i++){
                for(int j=0; j< canvas.getHeight(); j++){
                    //Farbe und Style setzen
                    point.setColor(ju.setColor(i,j));
                    point.setStyle(Paint.Style.FILL);
                    //Punkt zeichnen
                    canvas.drawPoint(i,j,point);
                }
            }

        }


    }//end drawOnCanvas()

}//end class()
