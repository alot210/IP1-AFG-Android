package de.hsd.manguli.fractalsapp.views.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import de.hsd.manguli.fractalsapp.fragments.MandelbrotFragment;
import de.hsd.manguli.fractalsapp.fragments.JuliaFragment;

/**
 * Created by Thomas on 29.05.2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

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
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


