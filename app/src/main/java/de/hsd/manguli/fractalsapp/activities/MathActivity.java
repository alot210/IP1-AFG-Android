package de.hsd.manguli.fractalsapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import de.hsd.manguli.fractalsapp.R;


/**
 * Klasse MathActivity zeigt den MatheInfoScreen
 *
 * Erbt von: AppCompat Activity - Library, um die ActionBar einzubinden
 *
 * XML-Layout: activity_math.xml (included content_math.xml)
 */
public class MathActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_math);
        //Toolbar aus XML holen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar als ActionBar einbinden für die Activity
        setSupportActionBar(toolbar);

        //zurück Pfeil erstellen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }// end onCreate()

}//end MathActivity
