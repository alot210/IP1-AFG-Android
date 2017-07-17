package de.hsd.manguli.fractalsapp.activities;

import android.Manifest;
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
import de.hsd.manguli.fractalsapp.R;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse MainActivity ist die Launching Activity
 * -> wird beim Start der App aufgerufen
 *
 * sie beinhaltet die View in der XML zur Darstellung und Ausgabe der Fraktale
 *
 * Erbt von: AppCompat Activity - Library, um die ActionBar einzubinden
 *
 * XML-Layout: activity_main.xml (included content_main.xml ->View)
 */
public class MainActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_main);

        //Toolbar aus XML holen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar als ActionBar einbinden für die Activity
        setSupportActionBar(toolbar);

        // Logo in die Action Bar hinzufügen
        getSupportActionBar().setLogo(R.drawable.hsd_logo_weiss);
        // Logo anzeigen - true für individuelles Logo
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


        bewusst auskommentiert, da View in XML für den Aufruf sorgt

        //View Objekt erstellen und mit invalidate() onDraw aufrufen
        FractalView fw = new FractalView(this);
        //fw.invalidate();
        Log.d("LOGGING","View wurde initialisiert, Menge wird gezeichnet");
        */

        //Aktuelles Logcat in Datei schreiben
        writeLogFile();
    }// end onCreate()

    /**
     * Wird aufgerufen, wenn App nach onCreate für den Vordergrund vorbereitet wird
     *
     * Screenshot muss hier gespeichert werden, weil View initialisiert worden sein muss
     *
     * Lifecycle: wird nach dem Erstellen aufgerufen
     * Zustand: immer noch Created, kurz danach im Vordergrund Resumed
     */
    @Override
    public void onStart(){
        // Superklasse muss aufgerufen werden
        super.onStart();

        //Screenshot Button als FAB (=Floating Action Button) integrieren
        //Button aus XML holen
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Snackbar Meldung bei Klick auf FAB
        fab.setOnClickListener(new View.OnClickListener() {
            /**
             * wird beim Klick auf den FAB aufgerufen
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Dein Fractal wurde gespeichert.", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();

                //Permission abfragen ab Android 6 nötig
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    //Bild speichern mit aktuellem Bitmap
                    saveImage(getBitmap());
                }
                //bei Android 5 reicht die Permission im Manifest
                else {
                    //Bild speichern mit aktuellem Bitmap
                    saveImage(getBitmap());
                }
            }
        });
    }// end onStart()

    /**
     * speicher das übergebene Bitmap auf dem Handy
     *
     * @param finalBitmap das zu speichernde Bitmap
     */
    private void saveImage(Bitmap finalBitmap) {

        //den Pfad des Storage speichern
        String root = Environment.getExternalStorageDirectory().toString();
        //Datei im Ordner Pictures speichern
        File myDir = new File(root + "/Pictures");
        //Dateipfad erstellen, falls nicht vorhanden
        myDir.mkdirs();

        // das aktuelle Datum mit Uhrzeit speichern
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        //den Dateinamen festlegen
        String fname = "Image-"+ timeStamp +".jpg";

        //File erstellen mit Eltern und Kinderpfad
        File file = new File (myDir, fname);

        //Falls Datei bereits exisiert
        if (file.exists ()){
            //Datei löschen
            file.delete ();
        }

        try {
            // Dateien in die Datei schreiben, OutputStream zur Datei
            FileOutputStream out = new FileOutputStream(file);
            //Bitmap
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            //Galerie aktualisieren
            notifyMediaStoreScanner(file);
            //alles bisher gebufferte in Datei schreiben
            out.flush();
            //Stream beenden und freigeben
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// end saveImage()

    /**
     * Methode informiert Gallerie und aktualisiert diese
     *
     * @param file die neu gespeicherte Datei
     */
    public final void notifyMediaStoreScanner(final File file) {
        try {
            //Bild einfügen und Thumbnail erstellen
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            //Übertragung mittels Intent und Permission, Media Scanner fügt Media hinzu
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }//end notifyMediaScanner()

    /**
     * aktuelle Bitmap aus der View holen
     *
     * @return das aktuelle Bitmap
     */
    public Bitmap getBitmap() {
        //View aus XML holen
        View myView = findViewById(R.id.view);
        //drawingCache erstellen, falls er nicht exisitiert
        myView.buildDrawingCache();
        //Bitmap aus Cache holen
        Bitmap b1 = myView.getDrawingCache();
        //Bitmap kopieren und jedes Pixel in 4 Bytes speichern
        Bitmap b = b1.copy(Bitmap.Config.ARGB_8888, false);
        //drawinCache löschen (Pflicht)
        myView.destroyDrawingCache();

        //Bitmap zurückgeben
        return b;
    }// getBitmap()

    /**
     * Callback Methode für das Ergebnis der Permission Abfrage
     *
     * @param requestCode der RequestCode von requestPermission()
     * @param permissions die angefragten Permissions, niemals null
     * @param grantResults die Antwort entweder granted oder denied, niemals null
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Abfrage nach dem Requestcode (nur Screenshot Permission)
        switch (requestCode) {
            //Permission für Screenshot /Speicherung auf Storage
            case 1: {

                // Anfrage wird nicht beendet, Ergebnis Array ist nicht leer
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast, damit user sieht, dass das Bild gespeichert wird
                    Toast.makeText(MainActivity.this, "Bild wurde gespeichert.", Toast.LENGTH_LONG).show();
                } else {

                    // Permission wurde abgelehnt oder beendet
                    Toast.makeText(MainActivity.this, "Zugriff verweigert. Rechte nicht vorhanden.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }// end onRequestPermissionResult()

    /**
     * erstellt das Menü in der Actionbar
     * @param menu Das Optionenmenü, in welchem die Menü Items platziert werden
     * @return true, um Menü anzuzeigen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menü der ActionBar hinzufügen, menu über XML holen
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("LOGGING","Menue geoeffnet");
        return true;
    }// end onCreateOptionsMenu()

    /**
     * Callback Methode
     * wird aufgerufen, wenn der User das Menü Item bzw.
     * ein Item der Action Bar anklickt
     *
     * @param item das ausgewählte Menü item
     * @return false, um normale Menübearbeitung fortzusetzen, true um weiterzuleiten
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // speichert die ID des ausgwähltem Items
        // Zurück Button in den anderen Activitys wird automatisch ausgeführt,
        // da Parent Activity im Manifest angegeben (die MainActivity)
        // ID=ID des Items in XML
        int id = item.getItemId();

        //vergleicht die Ids und leitet weiter
        if (id == R.id.action_settings) {
            Intent editorIntent = new Intent(this,EditorActivity.class);
            startActivity(editorIntent);
            Log.d("LOGGING","EDITOR ausgewaehlt");
            // true zurückgeben, um weiterzuleiten
            return true;
        }
       else if (id == R.id.action_math) {
            Intent mathIntent = new Intent(this,MathActivity.class);
            startActivity(mathIntent);
            Log.d("LOGGING","Mathe ausgewaehlt");
            // true zurückgeben, um weiterzuleiten
            return true;
        }
        else if(id == R.id.action_developer) {
            Intent developerIntent = new Intent(this,DeveloperActivity.class);
            startActivity(developerIntent);
            Log.d("LOGGING","Developer ausgewaehlt");
            // true zurückgeben, um weiterzuleiten
            return true;
        }

        // Aktion des Users konnte nicht festgestellt werden
        // Methode der Superklasse wird aufgerufen, um dies zu handeln
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected()

    /**
     * Methode wird beim Klick auf den Android Zurück Button aufgerufen und führt zum Home Screen
     *
     */
    @Override
    public void onBackPressed(){
        //ACTION_MAIN und CATEGORY_HOME führen zusammen zum Home Screen
        //neuen Intent erzeugen, einfacher Intent ->erwartet nix
        Intent a = new Intent(Intent.ACTION_MAIN);
        //Home Screen Intent
        a.addCategory(Intent.CATEGORY_HOME);
        //BackStack anpassen, MainActivity ist neuer StartTask
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Inten ausführen
        startActivity(a);
    }

    /**
     * Methode schreibt das aktuelle Logfile in eine txt
     */
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
    }// end writeLogFile()

}// end class MainActivity
