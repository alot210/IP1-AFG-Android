package de.hsd.manguli.fractalsapp.views.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import de.hsd.manguli.fractalsapp.fragments.MandelbrotFragment;
import de.hsd.manguli.fractalsapp.fragments.JuliaFragment;

/**
 * PagerAdapter fügt dem tabLayout die Views hinzu
 *
 * erweitert FragmentStatePagerAdapter um Ressourcen zu sparen,
 * da dieser nicht sichtbare Tabs nicht berücksichtigt
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Anzahl der Tabs
     */
    int mNumOfTabs;

    /**
     * Constructor
     * @param fm
     * @param numOfTabs Anzahl der Tabs
     */
    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }// end PagerAdapter()

    /**
     * Gibt das aktuelle sichtbare Fragment im Tab zurück
     *
     * @param position TabPosition
     * @return das sichtbare Fragment
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MandelbrotFragment tab1 = new MandelbrotFragment();
                Log.d("LOGGING","Mandelbrot Editor Tab wird angezeigt");
                return tab1;
            case 1:
                JuliaFragment tab2 = new JuliaFragment();
                Log.d("LOGGING","JuliaMenge Editor Tab wird angezeigt");
                return tab2;

            default:
                return null;
        }
    }// end getItem()

    /**
     * Gibt die aktuelle Position zurück
     * @return die aktuelle Position
     */
    @Override
    public int getCount() {
        return mNumOfTabs;
    }//end getCount()
}//end PagerAdapter


