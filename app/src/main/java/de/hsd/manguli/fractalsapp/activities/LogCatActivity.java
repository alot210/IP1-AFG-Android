package de.hsd.manguli.fractalsapp.activities;

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

import de.hsd.manguli.fractalsapp.R;

/**
 * Klasse LogCatActivity zeigt den Screen für das Log
 *
 * Erbt von: AppCompat Activity - Library, um die ActionBar einzubinden
 *
 * XML-Layout: activity_log_cat.xml (included content_log_cat.xml)
 */
public class LogCatActivity extends AppCompatActivity {

    /**
     * Hier werden Basisfunktionen implementiert, die nur einmal beim Erstellen
     * aufgerufen werden sollen
     *
     * Lifecycle: wird beim Erstellen der Activity aufgerufen
     * Zustand: Created
     *
     * @param savedInstanceState Bundle Objekt, enthält den zuletzt gespeicherten Zustand der Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreate() der Superklasse aurufen, um Erstellung der Activity zu vollenden
        super.onCreate(savedInstanceState);

        //XML-Layout über die Ressourcen Variable holen
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
