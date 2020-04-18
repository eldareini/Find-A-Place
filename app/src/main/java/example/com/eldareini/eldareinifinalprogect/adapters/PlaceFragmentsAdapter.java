package example.com.eldareini.eldareinifinalprogect.adapters;


import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import example.com.eldareini.eldareinifinalprogect.fragments.FavoriteFragment;
import example.com.eldareini.eldareinifinalprogect.fragments.MainFragment;

/**
 * Created by Eldar on 10/5/2017.
 */
//Fragments adapter for the tabs to work with
public class PlaceFragmentsAdapter extends FragmentPagerAdapter {
    public PlaceFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.e("****", "Main fragment created");
                return new MainFragment();
            case 1:
                Log.e("****", "Favorite fragment created");
                return new FavoriteFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Places";
            case 1:
                return "Favorites";
        }
        return null;
    }
}
