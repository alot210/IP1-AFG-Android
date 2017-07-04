package de.hsd.manguli.fractalsapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Attr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



/**
 * MainActivity - wird beim Start der App aufgerufen
 * hier befindet sich die View --> Ausgabe der Mengen
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    int viewWidth;
    int viewHeight;
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

                View myView = findViewById(R.id.view);
                myView.buildDrawingCache();
                Bitmap b1 = myView.getDrawingCache();
                // copy this bitmap otherwise distroying the cache will destroy
                // the bitmap for the referencing drawable and you'll not
                // get the captured view
                Bitmap b = b1.copy(Bitmap.Config.ARGB_8888, false);
                //BitmapDrawable d = new BitmapDrawable(b);
                //canvasView.setBackgroundDrawable(d);
                myView.destroyDrawingCache();
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);

                    saveImage(b);

                }
                else {
                    saveImage(b);
                }
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                // RootView
                //Bitmap bitmap_rootview = ScreenShott.getInstance().takeScreenShotOfRootView(view);
                //ScreenShott.getInstance().saveScreenshotToPicturesFolder(MainActivity.this, bitmap_rootview, "my_screenshot_filename");
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

    public final void notifyMediaStoreScanner(final File file) {
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Log.d(TAG,"Hallo");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    View myView = findViewById(R.id.view);
                    myView.buildDrawingCache();
                    Bitmap b1 = myView.getDrawingCache();
                    // copy this bitmap otherwise distroying the cache will destroy
                    // the bitmap for the referencing drawable and you'll not
                    // get the captured view
                    Bitmap b = b1.copy(Bitmap.Config.ARGB_8888, false);
                    //BitmapDrawable d = new BitmapDrawable(b);
                    //canvasView.setBackgroundDrawable(d);
                    myView.destroyDrawingCache();
                    saveImage(b);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
