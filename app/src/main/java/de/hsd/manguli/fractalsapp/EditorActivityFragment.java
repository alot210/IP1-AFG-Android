package de.hsd.manguli.fractalsapp;


import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Fragment als Teil des Editor
 */
public class EditorActivityFragment extends Fragment {

    //Seekbar Iterationen
    SeekBar sb_iter;
    //Seekbar Geschwindigkeit Animation
    SeekBar sb_speed;

    //Textfeld Iterationen
    TextView tv_iter;
    //Textfeld Geschwindigkeit
    TextView tv_speed;

    static String i = "20";

    public EditorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View erstellen, um darüber die XML Elemente ansprechen zu können
        View editor_m = inflater.inflate(R.layout.fragment_editor, container, false);

        //Layout Elemente über ID ansprechen
        //Iteration
        sb_iter=(SeekBar) editor_m.findViewById(R.id.seekBar_m_Iteration);
        sb_iter.setProgress(0);
        sb_iter.incrementProgressBy(10);
        sb_iter.setMax(100);

        sb_speed=(SeekBar) editor_m.findViewById(R.id.seekBar_m_speed);

        tv_iter=(TextView) editor_m.findViewById(R.id.text_m_Iteration_value);
        tv_speed=(TextView) editor_m.findViewById(R.id.text_m_Speed_value);

        //Wird bei Veränderung der Seekbar aufgerufen
        sb_iter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Wenn Wert verändert wird
            @Override
            public void onProgressChanged(SeekBar sb_iter, int progress, boolean fromUser) {

                progress = progress / 10;
                progress = progress * 10;
                //in Textfeld den Wert schreiben
                tv_iter.setText(String.valueOf(progress));
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb_speed) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar sb_speed) {

            }
        });

        final TextView iteration = (TextView) editor_m.findViewById(R.id.text_m_Iteration_value);
        Button drawIt = (Button) editor_m.findViewById(R.id.button_m_draw);


        drawIt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Julia_Fragment jf = new Julia_Fragment();
                jf.setJuliaPush(false);
                i = iteration.getText().toString();

                //Snackbar.make(view,  i, Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


                //Intent mandelbrotIntent = new Intent(this,MainActivity.class);
                Intent mandelbrotIntent = new Intent(getActivity(),MainActivity.class);
                startActivity(mandelbrotIntent);

                //String i = iteration.getText().toString();


            }
        });

        return editor_m;
    }

    public String getIteration(){
        return i;
    }
}
