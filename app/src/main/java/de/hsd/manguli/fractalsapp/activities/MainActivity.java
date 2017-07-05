package de.hsd.manguli.fractalsapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import de.hsd.manguli.fractalsapp.views.FractalView;
import de.hsd.manguli.fractalsapp.R;
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
import java.util.Random;

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

        //Button für das auslesen der LOGCAT.txt innerhalb der App
        //mit Absicht Auskommentiert!!!
        /*
        FloatingActionButton lcb = (FloatingActionButton) findViewById(R.id.button_logcat);
        //Snackbar Meldung bei Klick auf FAB
        lcb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LogCat anzeigen lassen
                Intent logCatIntent = new Intent(MainActivity.this,LogCatActivity.class);
                startActivity(logCatIntent);
            }
        });
        */

        //View initialisieren und mit invalidate() onDraw aufrufen
        FractalView fw = new FractalView(this);
        fw.invalidate();
        Log.d("LOGGING","View wurde initialisiert, Menge wird gezeichnet");

        //Aktuelles Logcat in Datei schreiben
        writeLogFile();
    }

    @Override
    public void onResume(){
        super.onResume();
        //Screenshot Button als FAB (=Floating Action Button) integrieren
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Snackbar Meldung bei Klick auf FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Dein Fractal wurde gespeichert.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                    saveImage(getBitmap());
                }
                else {
                    saveImage(getBitmap());
                }
            }
        });
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Pictures");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            notifyMediaStoreScanner(file);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        View myView = findViewById(R.id.view);
        myView.buildDrawingCache();
        Bitmap b1 = myView.getDrawingCache();
        Bitmap b = b1.copy(Bitmap.Config.ARGB_8888, false);
        myView.destroyDrawingCache();

        return b;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Bild gespeichert", Toast.LENGTH_LONG).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("LOGGING","Menue geoeffnet");
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
            Log.d("LOGGING","EDITOR ausgewaehlt");
            return true;
        }
       else if (id == R.id.action_math) {
            Intent mathIntent = new Intent(this,MathActivity.class);
            startActivity(mathIntent);
            Log.d("LOGGING","Mathe ausgewaehlt");
            return true;
        }
        else if(id == R.id.action_developer) {
            Intent developerIntent = new Intent(this,DeveloperActivity.class);
            startActivity(developerIntent);
            Log.d("LOGGING","Developer ausgewaehlt");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Methode die das aktuelle Logcat in eine txt Datei schreibt
    private void writeLogFile(){
        //Speichern der Logcat Daten in eine LOGCAT.txt
        try {
            //Aktuelles Logcat Debug wird Zeile für Zeile eingelesen
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

            File path;
            File dir;
            File file;

            //Wenn der Zugriff auf den externen Speicher erlaubt ist...
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //In Download einen neuen Ordner und die txt erstellen
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                dir = new File(path, "/FRACTALICIOUS");
                if(!dir.exists()) dir.mkdirs();
                file = new File(dir, "LOGCAT.txt");
                Log.d("LOGGING",dir.toString());
            }
            //Falls der Zugriff nicht erlaubt ist, den Zugriff abfragen
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                //Siehe oben
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                dir = new File(path, "/FRACTALICIOUS");
                if(!dir.exists()) dir.mkdirs();
                file = new File(dir, "LOGCAT.txt");
                Log.d("LOGGING",dir.toString());

            }

            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            //Writing the string to file
            osw.write(logString);
            Log.d("LOGGING","Schreibe in LogFile");
            osw.flush();
            osw.close();

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            //Toast.makeText(this, "Fehler 1", Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Fehler 2", Toast.LENGTH_LONG).show();
        }
    }
}
