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
import example.com.eldareini.eldareinifinalprogect.adapters.FavoriteAdapter;
import example.com.eldareini.eldareinifinalprogect.objects.Place;
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;
import example.com.eldareini.eldareinifinalprogect.services.SearchIntentService;



//fragment that holds the Favorites recyclerView
public class FavoriteFragment extends Fragment {
    FavoriteAdapter adapter;


    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_favorite, container, false);

        adapter = new FavoriteAdapter(getContext());
        RecyclerView favoriteList = (RecyclerView) v.findViewById(R.id.favoriteList);
        favoriteList.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteList.setAdapter(adapter);
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

            Cursor placeCursor = getContext().getContentResolver().query(PlaceProvider.FAVORITE_URI, null, null, null, null);

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
        }catch (NullPointerException e){

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
