package de.hsd.manguli.fractalsapp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

        Log.v("EXAMPLE READ",Environment.getExternalStorageDirectory().toString()+"/Download");

        t.setText(getLogFile());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Funktion um den Inhalt der logcat.txt zurück zu geben
    public String getLogFile(){
        BufferedReader br;
        StringBuilder text = new StringBuilder("");
        try {
            br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory() + "/Download/FRACTALICIOUS/LOGCAT.txt")));

            String read;


            while((read = br.readLine()) != null){
                text.append(read);
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Log.v("Fehler 1", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }
}
