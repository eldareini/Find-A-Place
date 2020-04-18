package example.com.eldareini.eldareinifinalprogect.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import example.com.eldareini.eldareinifinalprogect.DB.PlaceDBHelper;
import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceAdapter;
import example.com.eldareini.eldareinifinalprogect.objects.Place;
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;
import example.com.eldareini.eldareinifinalprogect.services.SearchIntentService;



//fragment that holds the Search Places recyclerView
public class MainFragment extends Fragment {
    private PlaceAdapter adapter;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        adapter = new PlaceAdapter(getContext());
        RecyclerView listPlaces = (RecyclerView) v.findViewById(R.id.listPlaces);
        listPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        listPlaces.setAdapter(adapter);
        //Broadcast for updating the adapter
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new SearchFinishedReceiver(), new IntentFilter(SearchIntentService.ACTION_FINISHED));


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();

    }
    //method for updating the data
    private void updateData(){
        adapter.clear();
        try {
            Cursor placeCursor = getContext().getContentResolver().query(PlaceProvider.PLACE_URI, null, null, null, null);

            while (placeCursor.moveToNext()) {
                String id = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_ID));
                String name = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COl_NAME));
                String address = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_ADDRESS));
                double lat = placeCursor.getDouble(placeCursor.getColumnIndex(PlaceDBHelper.COL_LAT));
                double lon = placeCursor.getDouble(placeCursor.getColumnIndex(PlaceDBHelper.COL_LON));
                int image = placeCursor.getInt(placeCursor.getColumnIndex(PlaceDBHelper.COL_IMAGE));
                String picturesURL = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_PICTURES_URL));
                adapter.add(new Place(id, name, address, lat, lon, image, picturesURL));
            }
        } catch (NullPointerException e){

        }
    }

    //the receiver of the broadcast
    private class SearchFinishedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           updateData();

            }

        }
    }

