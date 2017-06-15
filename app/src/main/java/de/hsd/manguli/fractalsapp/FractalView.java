package de.hsd.manguli.fractalsapp;



import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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
}//end class()
