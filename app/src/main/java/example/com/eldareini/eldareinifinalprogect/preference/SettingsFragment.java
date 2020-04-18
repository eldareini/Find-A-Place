package example.com.eldareini.eldareinifinalprogect.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


import androidx.annotation.Nullable;

import example.com.eldareini.eldareinifinalprogect.R;

/**
 * Created by Eldar on 9/24/2017.
 */
//the settings fragments
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_fragment);

        ListPreference distance = (ListPreference) findPreference("preference_distance");

        Preference radius = findPreference("preference_radius");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        switch (preference.getKey()){
            case "preference_distance":
                preference.setSummary("Current: " + newValue);
                break;

        }
        return true;
    }
}
