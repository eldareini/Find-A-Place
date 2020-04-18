package example.com.eldareini.eldareinifinalprogect.fragments;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceAdapter;


//the fragment that contain the Fragments of the map and the detailes of the place
public class PlaceContainerFragment extends Fragment implements View.OnClickListener {
    private Button btnDetails;
    private boolean isEnable;
    private String text;
    private float scale[] = new float[2];
    private boolean isAttached;
    View v;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    public PlaceContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            isEnable = savedInstanceState.getBoolean("isEnable");
            text = savedInstanceState.getString("text");
            scale[0] = savedInstanceState.getFloatArray("scale")[0];
            scale[1] = savedInstanceState.getFloatArray("scale")[1];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contanier, container, false);

        if (savedInstanceState == null) {

            MapsFragment mapsFragment = new MapsFragment();
            PlaceFragment placeFragment = new PlaceFragment();

            getChildFragmentManager().beginTransaction()
                    .add(R.id.container_fragment, mapsFragment, "mapsFragment")
                    .show(mapsFragment)
                    .add(R.id.container_fragment, placeFragment, "placeFragment")
                    .hide(placeFragment)
                    .commit();
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new ItemClickedReceiver(), new IntentFilter(PlaceAdapter.ACTION_CLICKED));

        btnDetails = (Button) v.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(this);

        if (text != null){
            btnDetails.setEnabled(isEnable);
            btnDetails.setText(text);
            btnDetails.setScaleX(scale[0]);
            btnDetails.setScaleY(scale[1]);
        } else {
            btnDetails.setEnabled(false);
        }

        return v;
    }
//on click
    @Override
    public void onClick(View v) {
        ObjectAnimator animator;

        Fragment mapsFragment = getChildFragmentManager().findFragmentByTag("mapsFragment");
        Fragment placeFragment = getChildFragmentManager().findFragmentByTag("placeFragment");

        if (btnDetails.getText().toString().equalsIgnoreCase("tap for more")) {

            getChildFragmentManager().beginTransaction()
                    .hide(mapsFragment)
                    .show(placeFragment)
                    .commit();

            int jump;
            if (v.findViewById(R.id.textIsLand) != null)
                jump = -320;
            else
                jump = -1750;


            animator = ObjectAnimator.ofFloat(btnDetails, "translationY", jump);
            animator.setDuration(500);


            btnDetails.setText("tap to close");
        } else {
            getChildFragmentManager().beginTransaction()
                    .hide(placeFragment)
                    .show(mapsFragment)
                    .commit();

            animator = ObjectAnimator.ofFloat(btnDetails, "translationY", 0);
            animator.setDuration(500);
            btnDetails.setText("tap for more");
        }

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.start();
    }

    private class ItemClickedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            btnDetails.setEnabled(true);

            try {
                if (!btnDetails.getText().toString().equalsIgnoreCase("tap for more")) {
                    ObjectAnimator animator;
                    Fragment mapsFragment = getChildFragmentManager().findFragmentByTag("mapsFragment");
                    Fragment placeFragment = getChildFragmentManager().findFragmentByTag("placeFragment");

                    getChildFragmentManager().beginTransaction()
                            .hide(placeFragment)
                            .show(mapsFragment)
                            .commit();

                    animator = ObjectAnimator.ofFloat(btnDetails, "translationY", 0);
                    animator.setDuration(0);
                    btnDetails.setText("tap for more");
                    AnimatorSet set = new AnimatorSet();
                    set.play(animator);
                    set.start();
                }
            }catch (IllegalStateException e){

            }

        }
    }
    //save on orientation change
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isEnable", btnDetails.isEnabled() );
        outState.putString("text", btnDetails.getText().toString());
        float scale[] = new float[2];
        scale[0] = btnDetails.getScaleX();
        scale[1] = btnDetails.getScaleY();
        outState.putFloatArray("scale", scale);
    }
}
