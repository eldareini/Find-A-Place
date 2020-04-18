package example.com.eldareini.eldareinifinalprogect.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceFragmentsAdapter;


//the fragment that contain the fragments of search and favorite
public class TabsFragment extends Fragment {


    public TabsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabs, container, false);

        PlaceFragmentsAdapter adapter = new PlaceFragmentsAdapter(getFragmentManager());
        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        return v;
    }

}
