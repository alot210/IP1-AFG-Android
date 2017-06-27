package de.hsd.manguli.fractalsapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogCatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_cat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView t = (TextView) findViewById(R.id.logCatText);


        t.setText(getLogFile());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Funktion um den Inhalt der logcat.txt zurück zu geben
    public String getLogFile(){
        BufferedReader br;
        StringBuilder text = new StringBuilder("");
        try {
            br = new BufferedReader(new FileReader(new File(getFilesDir()+ "/LOG/logcat.txt")));
            String read;


            while((read = br.readLine()) != null){
                text.append(read);
            }
            //Log.v("OUTPUT", text.toString());

            br.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
}
