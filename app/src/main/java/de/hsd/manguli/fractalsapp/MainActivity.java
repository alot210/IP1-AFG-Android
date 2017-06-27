package de.hsd.manguli.fractalsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MainActivity - wird beim Start der App aufgerufen
 * hier befindet sich die View --> Ausgabe der Mengen
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //XML-Layout über die Ressourcen Variable holen
        setContentView(R.layout.activity_main);

        //Toolbar als ActionBar hinzufügen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Logo in Action Bar integrieren
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.hsd_logo_weiss);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Screenshot Button als FAB (=Floating Action Button) integrieren
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Snackbar Meldung bei Klick auf FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hier wird im finalen Release ein Screenshot gespeichert.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //LogCat anzeigen lassen
                //Intent logCatIntent = new Intent(MainActivity.this,LogCatActivity.class);
                //startActivity(logCatIntent);


            }
        });

        FloatingActionButton lcb = (FloatingActionButton) findViewById(R.id.button_logcat);
        //Snackbar Meldung bei Klick auf FAB
        lcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Hier wird im finalen Release ein Screenshot gespeichert.", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                //LogCat anzeigen lassen
                Intent logCatIntent = new Intent(MainActivity.this,LogCatActivity.class);
                startActivity(logCatIntent);


            }
        });

        //View initialisieren und mit invalidate() onDraw aufrufen
        FractalView fw = new FractalView(this);
        fw.invalidate();

        //Aktuelles Logcat in Datei schreiben
        writeLogFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        //MenüPunkte und die Intents zu den Activities
        if (id == R.id.action_settings) {
            Intent editorIntent = new Intent(this,EditorActivity.class);
            startActivity(editorIntent);
            return true;
        }
       else if (id == R.id.action_math) {
            Intent mathIntent = new Intent(this,MathActivity.class);
            startActivity(mathIntent);
            return true;
        }
        else if(id == R.id.action_developer) {
            Intent developerIntent = new Intent(this,DeveloperActivity.class);
            startActivity(developerIntent);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    //Methode die das aktuelle Logcat in eine txt Datei schreibt
    private void writeLogFile(){
        //Speichern der Logcat Daten in eine log.txt
        //Toast.makeText(this, "SDCard Found", Toast.LENGTH_LONG).show();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                log.append(line);
                log.append("\n");
            }

            //Aktuelles Datum(Timestamp)
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm"); // Format für 24-Stunden-Anzeige
            String date = dateFormat.format(new Date());


            //Log in einen String schreiben + Timestamp
            final String logString = new String(date + " " +log.toString());
            //Log.v("loggingString",logString);

            File dir = new File(this.getFilesDir(), "/LOG");

            //Toast.makeText(this, dir.toString(), Toast.LENGTH_LONG).show();

            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dir, "logcat.txt");
            //Toast.makeText(this, "Logcat.txt erstellt", Toast.LENGTH_LONG).show();
            //Log.v("FILEDIR", dir.toString());

            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            //Writing the string to file
            osw.write(logString);
            osw.flush();
            osw.close();

            //Toast.makeText(this, "sollte durchgelaufen sein", Toast.LENGTH_LONG).show();

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, "Fehler 1", Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Fehler 2", Toast.LENGTH_LONG).show();
        }
    }
}
