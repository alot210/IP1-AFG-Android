package de.hsd.manguli.fractalsapp.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import de.hsd.manguli.fractalsapp.R;
import de.hsd.manguli.fractalsapp.activities.MainActivity;
import de.hsd.manguli.fractalsapp.views.FractalView;

/**
 * Fragment für den Julia Editor,
 * wird in der EditorActivity dargestellt
 */
public class JuliaFragment extends Fragment implements View.OnClickListener {

    //Seekbar für Iterationen im Editor
    SeekBar sb_iter;
    //Seekbar für Geschwindigkeit Animation im Editor
    SeekBar sb_speed;
    //Seekbar für den Realteil im Editor
    SeekBar sb_real;
    //Seekbar für den Imaginärteil im Editor
    SeekBar sb_imag;


    //Textfeld für Iterationen im Editor
    TextView tv_iter;
    //Textfeld für Geschwindigkeit im Editor
    TextView tv_speed;
    //Textfeld Realteil
    TextView tv_real;
    //Textfeld Imaginaerteil
    TextView tv_imag;
    //Button erste Farbe;
    Button bt_color1;
    //Button zweite Farbe
    Button bt_color2;
    //Button dritte Farbe
    Button bt_color3;
    //Button vierte Farbe
    Button bt_color4;

    //Boolean zur Prüfung ob die jeweilige Farbe gewählt wurde
    boolean bt_color1_pressed;
    boolean bt_color2_pressed;
    boolean bt_color3_pressed;
    boolean bt_color4_pressed;

    //Benötigte Variablen für Übergabeparameter an Fractalview --> static, da immer auf die Klassenvariablen zugegriffen werden muss
    //Julia Iteration, Real/Imag Anteil
    public static String iteration = "20";
    public static int real = 0;
    public static int imag = 0;
    //Farben
    public static int color1 = 0;
    public static int color2 = 0;
    public static int color3 = 0;
    public static int color4 = 0;
    //Geschwindigkeit der Animation
    public static int speed = 1;
    //Variable zum prüfen ob Julia gemalt werden soll
    public static Boolean juliaPush = false;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View erstellen, um darüber die XML Elemente ansprechen zu können
        View editor_j = inflater.inflate(R.layout.fragment_julia, container, false);

        //Layout Elemente über ID ansprechen
        //Seekbar für Iterationen ansprechen, Progress auf den aktuellen Wert setzen
        sb_iter = (SeekBar) editor_j.findViewById(R.id.seekBar_j_Iteration);
        sb_iter.incrementProgressBy(5);
        sb_iter.setMax(100);
        sb_iter.setProgress(Integer.parseInt(iteration));

        //Seekbar für Realteil initialisieren
        //Seekbar für Realteil ansprechen, Progress auf den aktuellen Wert setzen
        sb_real = (SeekBar) editor_j.findViewById(R.id.seekBar_j_real);
        sb_real.incrementProgressBy(1);
        sb_real.setMax(360);
        sb_real.setProgress(real);
        //Seekbar für Imaginärteil initialisieren
        //Seekbar für Imaginärteil ansprechen, Progress auf den aktuellen Wert setzen
        sb_imag = (SeekBar) editor_j.findViewById(R.id.seekBar_j_imaginaer);
        sb_imag.incrementProgressBy(1);
        sb_imag.setMax(360);
        sb_imag.setProgress(imag);
        //Textviews für Iteration und Speed initialisieren und auf den aktuellen Wert setzen
        tv_iter = (TextView) editor_j.findViewById(R.id.text_j_Iteration_value);
        tv_iter.setText(String.valueOf(iteration));


        //Werte setzen in die Textviews für Real und Imaginaerteil
        tv_real = (TextView) editor_j.findViewById(R.id.text_j_real_value);
        tv_real.setText(""+real);

        tv_imag = (TextView) editor_j.findViewById(R.id.text_j_imaginaer_value);
        tv_imag.setText(""+imag);

        //Wird bei Veränderung der Seekbar aufgerufen
        sb_iter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Wenn Wert verändert wird
            @Override
            public void onProgressChanged(SeekBar sb_iter, int progress, boolean fromUser) {

                progress = progress / 5;
                progress = progress * 5;
                //in Textfeld den Wert schreiben
                tv_iter.setText(String.valueOf(progress));
                Log.d("LOGGING","Iteration verändert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_iter) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_iter) {

            }
        });

        //Alle Buttons initialisieren und ClickListener hinzufügen, Variable ob der Button gedrückt wurde standartisiert auf false
        bt_color1 = (Button) editor_j.findViewById(R.id.button_j_color1_select);
        bt_color1.setOnClickListener(this);
        bt_color1_pressed = false;
        bt_color2 = (Button) editor_j.findViewById(R.id.button_j_color2_select);
        bt_color2.setOnClickListener(this);
        bt_color2_pressed = false;
        bt_color3 = (Button) editor_j.findViewById(R.id.button_j_color3_select);
        bt_color3.setOnClickListener(this);
        bt_color3_pressed = false;
        bt_color4 = (Button) editor_j.findViewById(R.id.button_j_color4_select);
        bt_color4.setOnClickListener(this);
        bt_color4_pressed = false;
        //Falls bereits Farben ausgewählt wurden, setze die Hintergrund Farbe der Buttons auf die vorher gewählte Farbe
        if(color1!=0 && color2!= 0 &&color3!=0 && color4!=0){
            bt_color1.setBackgroundColor(color1);
            bt_color2.setBackgroundColor(color2);
            bt_color3.setBackgroundColor(color3);
            bt_color4.setBackgroundColor(color4);
        }

        //Wird bei Veränderung der Seekbar aufgerufen
        sb_real.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar sb_real, int progress, boolean fromUser) {
                //Bei Veränderung den Progress in die Textview schreiben
                tv_real.setText(String.valueOf(progress));
                Log.d("LOGGING","Realteil verändert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_speed) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_speed) {

            }
        });

        sb_imag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar sb_imag, int progress, boolean fromUser) {
                tv_imag.setText(String.valueOf(progress));
                Log.d("LOGGING","Imaginärteil verändert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_speed) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_speed) {

            }
        });

        final TextView iteration = (TextView) editor_j.findViewById(R.id.text_j_Iteration_value);


        Button drawIt = (Button) editor_j.findViewById(R.id.button_j_draw);

        //Bei Click auf den drawIt Button -->
        drawIt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Es müssen alle Farben gesetzt werden, ansonsten wird darauf hingewiesen
                //Es müssen alle Farben aktuell gesetzt worden sein ( Erstaufruf des Editors )
                //Oder alle Farben vorhanden sein ( nach erstem Zeichnen eines Mandelbrotes )
                if((bt_color1_pressed && bt_color2_pressed && bt_color3_pressed && bt_color4_pressed) ||
                        (color1 != 0 && color2 != 0 && color3 != 0 && color4 != 0)) {
                    //Toastnachricht ausgeben
                    Toast.makeText(getContext(), "Juliamenge wird nun gezeichnet.", Toast.LENGTH_LONG).show();
                    //Benötigte Variablen final setzen und falls vorhanden, Überschreiben
                    JuliaFragment.iteration = iteration.getText().toString();
                    real = sb_real.getProgress();
                    imag = sb_imag.getProgress();
                    //JuliaMenge soll gemalt werden
                    juliaPush = true;
                    //Farbe setzen anhand der Hintergrundfarbe des Buttons
                    color1 = ((ColorDrawable)bt_color1.getBackground()).getColor();
                    color2 = ((ColorDrawable)bt_color2.getBackground()).getColor();
                    color3 = ((ColorDrawable)bt_color3.getBackground()).getColor();
                    color4 = ((ColorDrawable)bt_color4.getBackground()).getColor();

                    Log.d("LOGGING","Julia Draw gedrueckt");
                    //Die Mainactivity wird aufgerufen, nachdem die wichtigen Parameter gesetzt wurden
                    Intent juliaIntent = new Intent(getActivity(),MainActivity.class);
                    startActivity(juliaIntent);
                }
                else {
                    Toast.makeText(getContext(),"Wählen Sie bitte 4 Farben aus.", Toast.LENGTH_LONG).show();
                    Log.d("LOGGING","Julia Draw gedrueckt, Farben fehlen");
                }


            }
        });


        return editor_j;
    }

    //OnCllick Methode muss für OnClickListener überschrieben werden
    @Override
    public void onClick(View v) {
        //Holen uns den ersten Teil des Dialogfensters aus FractalView
        FractalView fw = new FractalView(getContext());
        ColorPickerDialogBuilder cpdb = fw.getColorPickerDialogBuilder();

        //Überprüfung welcher Button gedrückt worden ist
        switch (v.getId()) {
            //Setze für jedes Dialogfenster den Ok Button, setze Werte zur Übergabe und lasse das Fenster anzeigen
            case R.id.button_j_color1_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                //Setzte die Hintergrundfarbe auf die Farbe die ausgewählt wurde
                                bt_color1.setBackgroundColor(i);
                                //Button für Farbe 1 wurde betätigt
                                bt_color1_pressed = true;
                                Log.d("LOGGING","Juliamenge Farbe 1 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_j_color2_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color2.setBackgroundColor(i);
                                bt_color2_pressed = true;
                                Log.d("LOGGING","Juliamenge Farbe 2 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_j_color3_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color3.setBackgroundColor(i);
                                bt_color3_pressed = true;
                                Log.d("LOGGING","Juliamenge Farbe 3 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_j_color4_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color4.setBackgroundColor(i);
                                bt_color4_pressed = true;
                                Log.d("LOGGING","Juliamenge Farbe 4 gesetzt");
                            }
                        })
                        .build()
                        .show();
        }
    }
}