package example.com.eldareini.eldareinifinalprogect.adapters;

import android.content.Context;
import android.content.Intent;
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
 * Created by Eldar on 9/24/2017.
 */
//the recycler Adapter for favorites
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {

    private Context context;
    private ArrayList<Place> places = new ArrayList<>();

    public FavoriteAdapter(Context context) {
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
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.place_layout, parent, false);
        return new FavoriteHolder(v);
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {
        holder.bind(places.get(position));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    //the holder

    public class FavoriteHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
        ImageView imageMenu, imagePlace, imageStar;
        TextView textName, textAddress, textDistance;
        Place currentPlace;
        MenuItem contextRemoveFavorite, contextShare;

        public FavoriteHolder(View itemView) {
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
                    textDistance.setText(String.format("%1$.1fkm", (distance / 1000)));
                } else {
                    textDistance.setText(((int) distance) + "m");
                }
            } else {
                textDistance.setText(String.format("%1$.1fMiles", (distance * 0.000621371)));
            }
            imagePlace.setImageResource(place.getImage());
            imageStar.setImageResource(R.drawable.favorite);
        }

        //creating context menu

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            contextRemoveFavorite = menu.add(R.menu.favorite_context_menu, R.id.contextRemoveFavorite, 0, "Remove Favorite");
            contextShare = menu.add(R.menu.favorite_context_menu,R.id.contextFavoriteShare,0, "Share");

            contextRemoveFavorite.setOnMenuItemClickListener(this);
            contextShare.setOnMenuItemClickListener(this);

        }

        //what to do when item selected

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){

                case R.id.contextRemoveFavorite:

                    //opening Broadcast to refresh the adapters
                    context.getContentResolver().delete(PlaceProvider.FAVORITE_URI, PlaceDBHelper.COL_ID + "='" + currentPlace.getId() + "'", null);
                    Toast.makeText(context, "Place deleted from favorites", Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SearchIntentService.ACTION_FINISHED));

                    return true;

                case R.id.contextFavoriteShare:
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                    intent.putExtra(Intent.EXTRA_SUBJECT, "Look at this cool place!");
                    intent.putExtra(Intent.EXTRA_TEXT, "Look at this cool place:\n" + currentPlace.getName() + "\nin " + currentPlace.getAddress());
                    context.startActivity(Intent.createChooser(intent, "How do you want to share?"));

                    return true;
            }
            return false;
        }
        //opening a broadcast every time that an item was clicked
        @Override
        public void onClick(View v) {
            Intent clickedIntent = new Intent(PlaceAdapter.ACTION_CLICKED);
            clickedIntent.putExtra("place", currentPlace);
            LocalBroadcastManager.getInstance(context).sendBroadcast(clickedIntent);
        }
    }
}
