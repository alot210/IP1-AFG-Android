package de.hsd.manguli.fractalsapp.fragments;


import android.content.DialogInterface;
import android.content.Intent;
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
 * A placeholder fragment containing a simple view.
 */
public class JuliaFragment extends Fragment implements View.OnClickListener {

    //Seekbar Iterationen
    SeekBar sb_iter;
    //Seekbar Geschwindigkeit Animation
    SeekBar sb_speed;

    //Textfeld Iterationen
    TextView tv_iter;
    //Textfeld Geschwindigkeit
    TextView tv_speed;
    //Button erste Farbe;
    Button bt_color1;
    //Button zweite Farbe
    Button bt_color2;
    //Button dritte Farbe
    Button bt_color3;
    //Button vierte Farbe
    Button bt_color4;

    //Variablen für Übergabeparameter an Fractalview
    public static String iteration = "20";
    public static int real;
    public static int imag;
    public static int color1;
    public static int color2;
    public static int color3;
    public static int color4;
    public static Boolean juliaPush = false;

    public JuliaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Farbwerte auf 0 gesetzt für Fehlerbehandlung
        color1 = 0;
        color2 = 0;
        color3 = 0;
        color4 = 0;
        //View erstellen, um darüber die XML Elemente ansprechen zu können
        View editor_j = inflater.inflate(R.layout.fragment_julia, container, false);

        //Layout Elemente über ID ansprechen
        //Iteration
        sb_iter = (SeekBar) editor_j.findViewById(R.id.seekBar_j_Iteration);
        sb_iter.setProgress(0);
        sb_iter.incrementProgressBy(10);
        sb_iter.setMax(100);

        sb_speed = (SeekBar) editor_j.findViewById(R.id.seekBar_j_speed);

        tv_iter = (TextView) editor_j.findViewById(R.id.text_j_Iteration_value);
        tv_speed = (TextView) editor_j.findViewById(R.id.text_j_Speed_value);

        //Wird bei Veränderung der Seekbar aufgerufen
        sb_iter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Wenn Wert verändert wird
            @Override
            public void onProgressChanged(SeekBar sb_iter, int progress, boolean fromUser) {

                progress = progress / 10;
                progress = progress * 10;
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

        //Alle Buttons initialisieren und ClickListener hinzufügen
        bt_color1 = (Button) editor_j.findViewById(R.id.button_j_color1_select);
        bt_color1.setOnClickListener(this);
        bt_color2 = (Button) editor_j.findViewById(R.id.button_j_color2_select);
        bt_color2.setOnClickListener(this);
        bt_color3 = (Button) editor_j.findViewById(R.id.button_j_color3_select);
        bt_color3.setOnClickListener(this);
        bt_color4 = (Button) editor_j.findViewById(R.id.button_j_color4_select);
        bt_color4.setOnClickListener(this);

        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb_speed, int progress, boolean fromUser) {
                tv_speed.setText(String.valueOf(progress));
                Log.d("LOGGING","Geschwindigkeit verändert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_speed) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_speed) {

            }
        });

        final TextView iteration = (TextView) editor_j.findViewById(R.id.text_j_Iteration_value);
        final SeekBar realValue = (SeekBar) editor_j.findViewById(R.id.seekBar_j_real);

        realValue.setProgress(0);
        realValue.incrementProgressBy(1);
        realValue.setMax(360);

        final SeekBar imagValue = (SeekBar) editor_j.findViewById(R.id.seekBar_j_imaginaer);

        imagValue.setProgress(0);
        imagValue.incrementProgressBy(1);
        imagValue.setMax(360);

        Button drawIt = (Button) editor_j.findViewById(R.id.button_j_draw);


        drawIt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Wenn alle Farben gesetzt sind wird gezeichnet, ansonsten Fehlermeldung
                if(color1 != 0 && color2 != 0 && color3 != 0 && color4 != 0) {
                    Toast.makeText(getContext(), "Juliamenge wird nun gezeichnet.", Toast.LENGTH_LONG).show();
                    JuliaFragment.iteration = iteration.getText().toString();
                    real = realValue.getProgress();
                    imag = imagValue.getProgress();
                    juliaPush = true;

                    Log.d("LOGGING","Julia Draw gedrueckt");
                    Intent juliaIntent = new Intent(getActivity(),MainActivity.class);
                    startActivity(juliaIntent);
                }
                else {
                    Toast.makeText(getContext(),"Bitte wählen Sie Farben aus.", Toast.LENGTH_LONG).show();
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
                                bt_color1.setBackgroundColor(i);
                                color1 = i;
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
                                color2 = i;
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
                                color3 = i;
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
                                color4 = i;
                                Log.d("LOGGING","Juliamenge Farbe 4 gesetzt");
                            }
                        })
                        .build()
                        .show();
        }
    }
}
