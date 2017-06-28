package de.hsd.manguli.fractalsapp;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
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
import java.util.Set;

import static android.content.ContentValues.TAG;


/**
 * View zur Darstellung der Mengen
 */
public class FractalView extends View {
    ProgressDialog pd;
    private Paint point;
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    Boolean juliaPush = Julia_Fragment.juliaPush;
    Boolean mandelPush = EditorActivityFragment.mandelPush;
    Boolean onCall = false;
    int numberOfThreads = 4;

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
            //super.onDraw(canvas);
            //Existiert ein Bitmap?
            //damit onDraw-Methode nur einmal ausgeführt wird (ansonsten wird sie zweimal mit onCreate und onResume aufgerufen)
            if(Build.VERSION.SDK_INT > 23) {
                onCall = true;
            }
            if(onCall) {
                showProgressDialog(getContext());
                if (bitmap == null) {
                    //Bitmap erzeugen mit den Canvas Maßen
                    bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                    //Canvas mit Bitmap erzeugen
                    bitmapCanvas = new Canvas(bitmap);
                    bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                //Methode zum Mandelbrot zeichnen aufrufen und in Canvas speichern
                drawFractal(bitmapCanvas);
                //Bitmap auf Canvas ausgeben
                canvas.drawBitmap(bitmap, 0, 0, point);
                System.out.println("onDraw");
                onCall = false;
            }
            onCall = true;
        }

        public void drawFractal(Canvas canvas){
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

            int mbItm = Integer.parseInt(EditorActivityFragment.iterartion);
            int jItm = Integer.parseInt(Julia_Fragment.iterartion);
            double _real = Math.cos((double)Julia_Fragment.real+3.257);
            double _imag = Math.sin((double)Julia_Fragment.imag);
            int color1, color2, color3, color4;
            System.out.println("drawFractal");

            SetThread t, t1, t2, t3, t4;
            int startY = 0, stopY = canvas.getHeight()/numberOfThreads, startX = canvas.getWidth();

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
            Algorithm am;


            if(!juliaPush) {
                am = new Mandelbrot(canvas.getWidth(),canvas.getHeight(), mbItm ,new Complex(2.0,2.0),new Complex(3.0,4.0));
                if(mandelPush) {
                    am.setColor1(EditorActivityFragment.color1);
                    am.setColor2(EditorActivityFragment.color2);
                    am.setColor3(EditorActivityFragment.color3);
                    am.setColor4(EditorActivityFragment.color4);
                }
                EditorActivityFragment.mandelPush = false;
            }
            else {
                am = new Julia(canvas.getWidth(), canvas.getHeight(), jItm, new Complex(1.5,2.0),new Complex(3.0,4.0),new Complex(-0.7,-0.3));
                am.setColor1(Julia_Fragment.color1);
                am.setColor2(Julia_Fragment.color2);
                am.setColor3(Julia_Fragment.color3);
                am.setColor4(Julia_Fragment.color4);
                Julia_Fragment.juliaPush = false;
            }

            for(int i = 0; i < numberOfThreads; i++){
                //Thread initialisieren
                t = new SetThread(am, canvas, startY, startX, stopY);
                //Run-Methode starten
                t.start();
                //Nächster Abschnitt vom Mandelbrot
                startY = stopY;
                stopY += canvas.getHeight()/numberOfThreads;
            }
        }

        private class SetThread extends Thread
        {

            private int startY, stopX, stopY, color1, color2, color3, color4;
            private Canvas canvas;
            private Paint paint = new Paint();
            private Algorithm m;

            SetThread (Algorithm m, Canvas canvas, int startY, int stopX, int stopY) {
                this.canvas = canvas;
                this.startY = startY;
                this.stopX = stopX;
                this.stopY = stopY;
                this.m = m;
            }

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    pd.incrementProgressBy(100/numberOfThreads);
                    if(pd.getProgress() == pd.getMax()) pd.dismiss();
                }
            };

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
                handler.sendMessage(handler.obtainMessage());
                try {
                    this.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    public void showProgressDialog(Context context) {
        pd = new ProgressDialog(context);
        pd.setMax(100);
        pd.setTitle("Die Menge wird gezeichnet");
        pd.setMessage("Bitte haben Sie etwas Geduld");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
    }
}//end class()