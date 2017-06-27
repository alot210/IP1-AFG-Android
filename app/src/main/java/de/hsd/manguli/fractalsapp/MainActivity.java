package de.hsd.manguli.fractalsapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import github.nisrulz.screenshott.ScreenShott;

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
        //View initialisieren und mit invalidate() onDraw aufrufen
        final FractalView fw = new FractalView(this);
        fw.invalidate();
        //Screenshot Button als FAB (=Floating Action Button) integrieren
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Snackbar Meldung bei Klick auf FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hier wird im finalen Release ein Screenshot gespeichert.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // RootView
                Bitmap bitmap_rootview = ScreenShott.getInstance().takeScreenShotOfRootView(view);
                ScreenShott.getInstance().saveScreenshotToPicturesFolder(MainActivity.this, bitmap_rootview, "my_screenshot_filename");
            }
        });


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

}
