package de.hsd.manguli.fractalsapp.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import de.hsd.manguli.fractalsapp.views.adapter.PagerAdapter;
import de.hsd.manguli.fractalsapp.R;

/**
 * Klasse EditorActivity zeigt die beiden Fragmente
 *
 * Erbt von: AppCompat Activity - Library, um die ActionBar einzubinden
 *
 * XML-Layout: activity_developer.xml (included content_developer.xml)
 */
public class EditorActivity extends AppCompatActivity {

    private static final String INDEX_KEY_STRING ="" ;
    ViewPager viewPager;
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
        setContentView(R.layout.activity_editor);

        //Toolbar aus XML holen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar als ActionBar einbinden für die Activity
        setSupportActionBar(toolbar);

        //zurück Pfeil erstellen, führt zu Parent Activity aus Manifest
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TabLayout über XML holen
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        //Tabs hinzufügen
        tabLayout.addTab(tabLayout.newTab().setText("Mandelbrot"));
        tabLayout.addTab(tabLayout.newTab().setText("Juliamenge"));

        //tabLayout.setTabMode(1);
        //Tab Position festlegen, Schrift nimmt so viel Platz wie möglich ein
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ViewPager über XML holen, welcher Swipe zwischen Tabs ermöglicht
        viewPager = (ViewPager) findViewById(R.id.pager);
        //PagerAdapter erzeugen mit Anzahl der Tabs, er fügt die Views zum Viewpager hinzu
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        //Adpater setzen
        viewPager.setAdapter(adapter);
        //Aufruf der verschiedenen Tabs
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /**
             * Wird aufgerufen wenn ein Tab angeklickt wird
             *
             * @param tab der gewählte Tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //aktuelle TabPosition aufrufen
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0) {
                    Log.d("LOGGING", "Mandelbrot wird bearbeitet");
                }
                else Log.d("LOGGING", "Julia wird bearbeitet");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }//onCreate()

    /**
     * wird aufgerufen, when der Fokus verloren geht und speichert den Stand des Tab
     */
    @Override
    public void onPause() {
        //Methode der SUperklasse aufrufen.
        super.onPause();
        final SharedPreferences.Editor ed = getSharedPreferences("name",
                android.content.Context.MODE_PRIVATE).edit();
        ed.putInt(INDEX_KEY_STRING, viewPager.getCurrentItem());
        ed.commit();
    }// end onPause()

    /**
     * wird aufgerufen wenn die Activity in den Fokus gerät-
     * ruft den letzten Stand der Activity auf
     */
    @Override
    public void onResume() {
        //Methode der Siperklasse aufrufen
        super.onResume();
        final SharedPreferences sp = getSharedPreferences("name",
                android.content.Context.MODE_PRIVATE);
        viewPager.setCurrentItem(sp.getInt(INDEX_KEY_STRING, 0));
    }// end onResume()

}// end EditorActivity

