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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import de.hsd.manguli.fractalsapp.activities.MainActivity;
import de.hsd.manguli.fractalsapp.views.FractalView;
import de.hsd.manguli.fractalsapp.R;

/**
 * Fragment als Teil des Editor
 */
public class MandelbrotFragment extends Fragment implements View.OnClickListener {

    //Seekbar Iterationen
    SeekBar sb_iter;
    //Seekbar Geschwindigkeit Animation
    SeekBar sb_speed;

    //Textfeld Iterationen
    TextView tv_iter;
    //Textfeld Geschwindigkeit
    TextView tv_speed;

    //Button erste Farbe
    Button bt_color1;
    //Button zweite Farbe
    Button bt_color2;
    //Button dritte Farbe
    Button bt_color3;
    //Button vierte Farbe
    Button bt_color4;




    //Boolean zur Prüfung ob eine Farbe gewählt wurde
    boolean bt_color1_pressed;
    boolean bt_color2_pressed;
    boolean bt_color3_pressed;
    boolean bt_color4_pressed;


    //Switch Button für Seepferdchen
    Switch sw_seahorses;
    //Switch Button für Animation
    Switch sw_animation;


    //Variablen für Übergabeparameter an Fractalview
    public static String iteration = "20";
    public static int color1 = 0;
    public static int color2 = 0;
    public static int color3 = 0;
    public static int color4 = 0;
    public static Boolean mandelPush = false;

    //Geschwindigkeit der Animation
    public static int speed = 1;

    //Soll zu Seepferdchen gesprungen werden oder nicht
    public static Boolean seahorse = false;
    //Soll animiert werden oder nicht
    public static Boolean animation = false;


    public MandelbrotFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //View erstellen, um darüber die XML Elemente ansprechen zu können
        View editor_m = inflater.inflate(R.layout.fragment_editor, container, false);

        //Layout Elemente über ID ansprechen
        //Iteration
        sb_iter=(SeekBar) editor_m.findViewById(R.id.seekBar_m_Iteration);
        sb_iter.setProgress(Integer.parseInt(iteration));
        sb_iter.incrementProgressBy(5);
        sb_iter.setMax(100);

        //Alle Buttons initialisieren und ClickListener hinzufügen
        bt_color1 = (Button) editor_m.findViewById(R.id.button_m_color1_select);
        bt_color1.setOnClickListener(this);
        bt_color1_pressed = false;
        bt_color2 = (Button) editor_m.findViewById(R.id.button_m_color2_select);
        bt_color2.setOnClickListener(this);
        bt_color2_pressed = false;
        bt_color3 = (Button) editor_m.findViewById(R.id.button_m_color3_select);
        bt_color3.setOnClickListener(this);
        bt_color3_pressed = false;
        bt_color4 = (Button) editor_m.findViewById(R.id.button_m_color4_select);
        bt_color4.setOnClickListener(this);
        bt_color4_pressed = false;

        sb_speed=(SeekBar) editor_m.findViewById(R.id.seekBar_m_speed);

        tv_iter=(TextView) editor_m.findViewById(R.id.text_m_Iteration_value);
        tv_iter.setText(String.valueOf(iteration));
        tv_speed=(TextView) editor_m.findViewById(R.id.text_m_Speed_value);
        tv_speed.setText(speed+"");

        sb_speed.setProgress(speed);
        sb_speed.incrementProgressBy(1);
        sb_speed.setMax(10);


        if(color1!=0 && color2!= 0 &&color3!=0 && color4!=0){
            bt_color1.setBackgroundColor(color1);
            bt_color2.setBackgroundColor(color2);
            bt_color3.setBackgroundColor(color3);
            bt_color4.setBackgroundColor(color4);
        }

        //Wird bei Veränderung der Seekbar aufgerufen
        sb_iter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Wenn Wert verändert wird
            @Override
            public void onProgressChanged(SeekBar sb_iter, int progress, boolean fromUser) {

                progress = progress / 5;
                progress = progress * 5;
                //in Textfeld den Wert schreiben
                tv_iter.setText(String.valueOf(progress));
                Log.d("LOGGING","Iteration veraendert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_iter) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_iter) {

            }
        });

        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb_speed, int progress, boolean fromUser) {
                tv_speed.setText(String.valueOf(progress));
                Log.d("LOGGING","Geschwindigkeit veraendert");
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_speed) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_speed) {

            }
        });

        sw_animation = (Switch) editor_m.findViewById(R.id.switch_m_Animation);
        sw_animation.setChecked(false);
        animation = false;
        sw_animation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        animation = true;
                    }
                    else {
                        animation = false;
                    }
                }
        });
        if(sw_animation.isChecked()) {
            animation = true;
        }
        else {
            animation = false;
        }

        //Setze den Seepferdchen Button auf false standardmäßig
        sw_seahorses = (Switch) editor_m.findViewById(R.id.switch_j_seepferdchen);
        sw_seahorses.setChecked(false);
        seahorse = false;
        //Listener für den Switch Button
        sw_seahorses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    seahorse = true;
                }
                else{
                    seahorse = false;
                }

            }
        });
        //Setze den Boolean jenachdem ob in Seepferdchen gezoomt werden soll oder nicht
        if(sw_seahorses.isChecked()){
            seahorse = true;
        }
        else{
            seahorse = false;
        }

        final TextView iteration = (TextView) editor_m.findViewById(R.id.text_m_Iteration_value);
        Button drawIt = (Button) editor_m.findViewById(R.id.button_m_draw);


        drawIt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Wenn alle Farben gesetzt sind wird gezeichnet, ansonsten Fehlermeldung
                //Es müssen alle Farben selber gesetzt sein
                //Oder alle Farben vorhanden sein
                if((bt_color1_pressed && bt_color2_pressed && bt_color3_pressed && bt_color4_pressed) ||
                        (color1 != 0 && color2 != 0 && color3 != 0 && color4 != 0)) {
                    if(seahorse && animation) {
                        Toast.makeText(getContext(), "Bitte nur Seepferdchen oder Animation auswählen und nicht beides.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Mandelbrot wird nun gezeichnet.", Toast.LENGTH_LONG).show();
                        MandelbrotFragment.iteration = iteration.getText().toString();
                        mandelPush = true;

                        color1 = ((ColorDrawable)bt_color1.getBackground()).getColor();
                        color2 = ((ColorDrawable)bt_color2.getBackground()).getColor();
                        color3 = ((ColorDrawable)bt_color3.getBackground()).getColor();
                        color4 = ((ColorDrawable)bt_color4.getBackground()).getColor();

                        speed = Integer.parseInt(tv_speed.getText().toString());
                        Log.d("LOGGING SPEED",speed+"");

                        //Snackbar.make(view,  i, Snackbar.LENGTH_LONG)
                        //        .setAction("Action", null).show();


                        Log.d("LOGGING","Mandelbrot Draw gedrueckt");
                        //Intent mandelbrotIntent = new Intent(this,MainActivity.class);
                        Intent mandelbrotIntent = new Intent(getActivity(),MainActivity.class);
                        startActivity(mandelbrotIntent);

                        //String i = iteration.getText().toString();
                    }

                }
                else {
                    Toast.makeText(getContext(),"Bitte wählen Sie Farben aus.", Toast.LENGTH_LONG).show();
                    Log.d("LOGGING","Mandelbrot Draw gedrueckt, Farben fehlen");
                }



            }
        });

        return editor_m;
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
            case R.id.button_m_color1_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color1.setBackgroundColor(i);
                                //color1 = i;
                                bt_color1_pressed = true;
                                Log.d("LOGGING","Mandelbrot Farbe 1 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_m_color2_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color2.setBackgroundColor(i);
                                //color2 = i;
                                bt_color2_pressed = true;
                                Log.d("LOGGING","Mandelbrot Farbe 2 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_m_color3_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color3.setBackgroundColor(i);
                                //color3 = i;
                                bt_color3_pressed = true;
                                Log.d("LOGGING","Mandelbrot Farbe 3 gesetzt");
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.button_m_color4_select:
                cpdb
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                                bt_color4.setBackgroundColor(i);
                                //color4 = i;
                                bt_color4_pressed = true;
                                Log.d("LOGGING","Mandelbrot Farbe 4 gesetzt");
                            }
                        })
                        .build()
                        .show();
        }
    }
}
