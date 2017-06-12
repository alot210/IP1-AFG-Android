package de.hsd.manguli.fractalsapp;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Julia_Fragment extends Fragment {

    //Seekbar Iterationen
    SeekBar sb_iter;
    //Seekbar Geschwindigkeit Animation
    SeekBar sb_speed;

    //Textfeld Iterationen
    TextView tv_iter;
    //Textfeld Geschwindigkeit
    TextView tv_speed;

    static String i = "20";
    static int real;
    static int imag;
    static Boolean juliaPush = false;

    public Julia_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                i = iteration.getText().toString();
                real = realValue.getProgress();
                imag = imagValue.getProgress();
                juliaPush = true;

                Intent juliaIntent = new Intent(getActivity(),MainActivity.class);
                startActivity(juliaIntent);

            }
        });


        return editor_j;
    }

    public String getIteration(){
        return i;
    }
    public int getReal(){
        return real;
    }
    public int getImag(){
        return imag;
    }
    public Boolean getJuliaPush(){
        return juliaPush;
    }
    public void setJuliaPush(Boolean value){juliaPush=value;}

}

