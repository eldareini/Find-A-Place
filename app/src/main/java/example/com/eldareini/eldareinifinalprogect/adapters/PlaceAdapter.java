package example.com.eldareini.eldareinifinalprogect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import example.com.eldareini.eldareinifinalprogect.DB.PlaceDBHelper;
import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.activities.MainActivity;
import example.com.eldareini.eldareinifinalprogect.objects.Place;
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;
import example.com.eldareini.eldareinifinalprogect.services.SearchIntentService;

/**
 * Created by Eldar on 9/17/2017.
 */
//the recycler Adapter for Places
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    public static final String ACTION_CLICKED = "example.com.eldareini.eldareinifinalprogect.adapters.ACTION_CLICKED";
    private Context context;
    private ArrayList<Place> places = new ArrayList<>();

    public PlaceAdapter(Context context) {
        this.context = context;
    }

    public void add(Place place){
        places.add(0, place);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Place> places){
        this.places.addAll(places);
        notifyDataSetChanged();
    }

    public void remove(Place place){
        places.remove(place);
        notifyDataSetChanged();
    }

    public void clear(){
        places.clear();
        notifyDataSetChanged();
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.place_layout, parent, false);
        return new PlaceHolder(v);
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        holder.bind(places.get(position));

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
        ImageView imageMenu, imagePlace, imageStar;
        TextView textName, textAddress, textDistance;
        boolean isFavorite;
        Place currentPlace;
        MenuItem contextFavorite, contextShare;


        public PlaceHolder(View itemView) {
            super(itemView);

            imageMenu = (ImageView) itemView.findViewById(R.id.imageMenu);
            imagePlace = (ImageView) itemView.findViewById(R.id.imagePlace);

            textName = (TextView) itemView.findViewById(R.id.textName);
            textDistance = (TextView) itemView.findViewById(R.id.textDistance);
            textAddress = (TextView) itemView.findViewById(R.id.textAddress);
            imageStar = (ImageView) itemView.findViewById(R.id.imageStar);
            imageMenu.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);

        }

        public void bind(Place place){
            currentPlace = place;
            textName.setText(place.getName().toString());
            textAddress.setText(place.getAddress().toString());
            double distance = SphericalUtil.computeDistanceBetween(new LatLng(place.getLat(), place.getLon()), new LatLng(MainActivity.getUserLat(), MainActivity.getUserLon()));
            if (MainActivity.getDistance().equalsIgnoreCase("KM")) {
                if (distance >= 1000) {
                    textDistance.setText(String.format("%1$,.1f", (distance / 1000)) + "km");
                } else {
                    textDistance.setText(((int) distance) + "m");
                }
            } else {
                textDistance.setText(String.format("%1$.1f", (distance * 0.000621371)) + "Miles");
            }

            imagePlace.setImageResource(place.getImage());

            Cursor placeCursor = context.getContentResolver().query(PlaceProvider.FAVORITE_URI, null, PlaceDBHelper.COL_ID, null, null );

            while (placeCursor.moveToNext()) {
                if (placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_ID)).toString().equals(currentPlace.getId())){
                    imageStar.setImageResource(R.drawable.favorite);
                    isFavorite = true;
                    break;

                }
            }
        }



        //creating context menu

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            contextFavorite = menu.add(R.menu.place_context_menu, R.id.contextFavorite, 0, "Add Favorite");
            contextShare = menu.add(R.menu.place_context_menu, R.id.contextShare, 0, "Share");

            contextFavorite.setOnMenuItemClickListener(this);
            contextShare.setOnMenuItemClickListener(this);

        }


        //what to do when item selected
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()){
                case R.id.contextFavorite:

                    Cursor placeCursor = context.getContentResolver().query(PlaceProvider.FAVORITE_URI, null, null, null, null );
                    boolean isFavorite = false;

                    while (placeCursor.moveToNext()) {
                        String id = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_ID));
                        if (id.equals(currentPlace.getId())){
                            Toast.makeText(context, "Place already in favorites", Toast.LENGTH_SHORT).show();
                            isFavorite = true;
                            break;
                        }
                    }

                    if (isFavorite ){
                        break;
                    }

                        ContentValues values = new ContentValues();
                        values.put(PlaceDBHelper.COL_ID, currentPlace.getId());
                        values.put(PlaceDBHelper.COl_NAME, currentPlace.getName());
                        values.put(PlaceDBHelper.COL_ADDRESS, currentPlace.getAddress());
                        values.put(PlaceDBHelper.COL_LAT, currentPlace.getLat());
                        values.put(PlaceDBHelper.COL_LON, currentPlace.getLon());
                        values.put(PlaceDBHelper.COL_IMAGE, currentPlace.getImage());
                        values.put(PlaceDBHelper.COL_PICTURES_URL, currentPlace.getPicturesURL());


                        context.getContentResolver().insert(PlaceProvider.FAVORITE_URI, values);
                        Toast.makeText(context, "Place added to favorites", Toast.LENGTH_SHORT).show();

                    //opening Broadcast to refresh the adapters

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SearchIntentService.ACTION_FINISHED));

                    break;

                case R.id.contextShare:

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                    intent.putExtra(Intent.EXTRA_SUBJECT, "Look at this cool place!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Look at this cool place:\n" + currentPlace.getName() + "\nin " + currentPlace.getAddress());
                    context.startActivity(Intent.createChooser(intent, "How do you want to share?"));

                    break;
            }

            return false;
        }

        //opening a broadcast every time that an item was clicked
        @Override
        public void onClick(View v) {

            Intent clickedIntent = new Intent(ACTION_CLICKED);
            clickedIntent.putExtra("place", currentPlace);
            LocalBroadcastManager.getInstance(context).sendBroadcast(clickedIntent);

        }
    }
}
