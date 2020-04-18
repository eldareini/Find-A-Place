package example.com.eldareini.eldareinifinalprogect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.activities.MainActivity;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceAdapter;
import example.com.eldareini.eldareinifinalprogect.objects.Place;
//the Fragment that contain the map
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Place place;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            place = savedInstanceState.getParcelable("place");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Broadcast for updating data on the map

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new ItemClickedReceiver(), new IntentFilter(PlaceAdapter.ACTION_CLICKED));


        return v;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (place == null) {

            mMap.addMarker(new MarkerOptions().position(new LatLng(MainActivity.getUserLat(), MainActivity.getUserLon())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.getUserLat(), MainActivity.getUserLon()), 15));
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLon())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLat(), place.getLon()), 15));
        }
    }

    //the receiver

    private class ItemClickedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            place = intent.getParcelableExtra("place");
            mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLon())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLat(), place.getLon()), 15));
        }
    }
//save on orientation change
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("place", place);
    }
}
