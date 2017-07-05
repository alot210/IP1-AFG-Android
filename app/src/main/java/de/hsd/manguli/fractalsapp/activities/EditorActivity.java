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

public class EditorActivity extends AppCompatActivity {

    private static final String INDEX_KEY_STRING ="" ;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Mandelbrot"));
        tabLayout.addTab(tabLayout.newTab().setText("Juliamenge"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        final SharedPreferences.Editor ed = getSharedPreferences("name",
                android.content.Context.MODE_PRIVATE).edit();
        ed.putInt(INDEX_KEY_STRING, viewPager.getCurrentItem());
        ed.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences sp = getSharedPreferences("name",
                android.content.Context.MODE_PRIVATE);
        viewPager.setCurrentItem(sp.getInt(INDEX_KEY_STRING, 0));
    }

}

