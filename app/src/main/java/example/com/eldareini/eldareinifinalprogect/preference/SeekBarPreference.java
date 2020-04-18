package example.com.eldareini.eldareinifinalprogect.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import example.com.eldareini.eldareinifinalprogect.R;

/**
 * Created by Eldar on 9/24/2017.
 */
//a seekBar for settings
public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private static final int DEFAULT_VALUE = 15;
    private int maxValue = 50;
    private int minValue = 1;
    private int interval = 1;
    private int currentValue;
    private SeekBar seekBar;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPreference(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPreference(context, attrs);
    }

    private void initPreference(Context context, AttributeSet attrs) {
        setValuesFromXml(attrs);
        /*
        seekBar = new SeekBar(context, attrs);
        seekBar.setMax(maxValue);
        seekBar.setOnSeekBarChangeListener(this);
        */
        setWidgetLayoutResource(R.layout.seekbar_preference);
    }

    private void setValuesFromXml(AttributeSet attrs) {

        maxValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "max", maxValue);
        minValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "min", minValue);


        try {
            String newInterval = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "interval");
            if (newInterval != null)
                interval = Integer.parseInt(newInterval);
        } catch (Exception e) {
            Log.e("SeekBar Preference", "Invalid interval value", e);
        }

    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        // The basic preference layout puts the widget frame to the right of the title and summary,
        // so we need to change it a bit - the seekbar should be under them.
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.VERTICAL);

        seekBar = (SeekBar) view.findViewById(R.id.seekBarPrefSeekBar);
        seekBar.setMax(maxValue);
        seekBar.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        updateView(view);

    }

    /**
     * Update a SeekBarPreference view with our current state
     *
     * @param view
     */
    protected void updateView(View view) {

        try {

            seekBar.setProgress(currentValue);

            TextView seekMin = (TextView) view.findViewById(R.id.seekbarMinLabel);
            seekMin.setText(String.valueOf(minValue));

            TextView seekMax = (TextView) view.findViewById(R.id.seekbarMaxLabel);
            seekMax.setText(String.valueOf(maxValue));

        } catch (Exception e) {
            Log.e("SeekBar Preference", "Error updating seek bar preference", e);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int newValue = progress + minValue;

        if (newValue > maxValue)
            newValue = maxValue;
        else if (newValue < minValue)
            newValue = minValue;
        else if (interval != 1 && newValue % interval != 0)
            newValue = Math.round(((float) newValue) / interval) * interval;

        // change rejected, revert to the previous value
        if (!callChangeListener(newValue)) {
            seekBar.setProgress(currentValue - minValue);
            return;
        }

        // change accepted, store it
        currentValue = newValue;
        persistInt(newValue);

        // show the current value as the summary. We can add a new TextView to the layout if we think this is ugly...
        //setSummary(newValue +"");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index) {

        int defaultValue = ta.getInt(index, DEFAULT_VALUE);
        return defaultValue;

    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        if (restoreValue) {
            currentValue = getPersistedInt(currentValue);
        } else {
            int temp = 0;
            try {
                temp = (Integer) defaultValue;
            } catch (Exception ex) {
                Log.e("SeekBar Preference", "Invalid default value: " + defaultValue.toString());
            }

            persistInt(temp);
            currentValue = temp;
        }

    }

    /**
     * make sure that the seekbar is disabled if the preference is disabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        seekBar.setEnabled(enabled);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        super.onDependencyChanged(dependency, disableDependent);

        //Disable movement of seek bar when dependency is false
        if (seekBar != null) {
            seekBar.setEnabled(!disableDependent);
        }
    }}
