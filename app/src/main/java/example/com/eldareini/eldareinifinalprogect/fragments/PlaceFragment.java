package example.com.eldareini.eldareinifinalprogect.fragments;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import example.com.eldareini.eldareinifinalprogect.DB.PlaceDBHelper;
import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceAdapter;
import example.com.eldareini.eldareinifinalprogect.objects.Place;
import example.com.eldareini.eldareinifinalprogect.objects.PlaceDetails;
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;
import example.com.eldareini.eldareinifinalprogect.services.SearchIntentService;


//the fragments with all the data on the place
public class PlaceFragment extends Fragment implements View.OnClickListener {
    private PlaceDetails place;
    TextView textPlaceName, textPlaceAddress, textRate, textIsOpen, textOpeningHours;
    ImageView imagePlace;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            place = savedInstanceState.getParcelable("placeDetail");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_place, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new ItemClickedReceiver(), new IntentFilter(PlaceAdapter.ACTION_CLICKED));


        imagePlace = (ImageView) v.findViewById(R.id.imagePlace);
        textPlaceName = (TextView) v.findViewById(R.id.textPlaceName);
        textPlaceAddress = (TextView) v.findViewById(R.id.textPlaceAddress);
        textRate = (TextView) v.findViewById(R.id.textRate);
        textIsOpen = (TextView) v.findViewById(R.id.textIsOpen);
        textOpeningHours = (TextView) v.findViewById(R.id.textOpeningHours);

        if (place != null){
            Picasso.get()
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&key=AIzaSyA4gYLvR0QW8tsz5556x9pznFIQg2EpF6k&photoreference=" + place.getPicturesURL())
                    .into(imagePlace);
            textPlaceName.setText(place.getName());
            textPlaceAddress.setText(place.getAddress());
            textRate.setText(place.getRating() + "/5");
            if (place.isOpenNow())
                textIsOpen.setText("Open Now");
            else
                textIsOpen.setText("Close");

            textOpeningHours.setText(place.getOpeningHours());
        }

        v.findViewById(R.id.btnFavorite).setOnClickListener(this);
        v.findViewById(R.id.btnShare).setOnClickListener(this);
        v.findViewById(R.id.btnWeb).setOnClickListener(this);
        v.findViewById(R.id.btnWaze).setOnClickListener(this);
        v.findViewById(R.id.btnCall).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFavorite:
                try {
                    Cursor placeCursor = getContext().getContentResolver().query(PlaceProvider.FAVORITE_URI, null, null, null, null);
                    boolean isFavorite = false;

                    while (placeCursor.moveToNext()) {
                        String id = placeCursor.getString(placeCursor.getColumnIndex(PlaceDBHelper.COL_ID));
                        if (id.equals(place.getId())) {
                            Toast.makeText(getContext(), "Place already in favorites", Toast.LENGTH_SHORT).show();
                            isFavorite = true;
                            break;
                        }
                    }

                    if (isFavorite) {
                        break;
                    }
                    ContentValues values = new ContentValues();
                    values.put(PlaceDBHelper.COL_ID, place.getId());
                    values.put(PlaceDBHelper.COl_NAME, place.getName());
                    values.put(PlaceDBHelper.COL_ADDRESS, place.getAddress());
                    values.put(PlaceDBHelper.COL_LAT, place.getLat());
                    values.put(PlaceDBHelper.COL_LON, place.getLon());
                    values.put(PlaceDBHelper.COL_IMAGE, place.getImage());
                    values.put(PlaceDBHelper.COL_PICTURES_URL, place.getPicturesURL());

                    getContext().getContentResolver().insert(PlaceProvider.FAVORITE_URI, values);

                    Toast.makeText(getContext(), "Place added to favorites", Toast.LENGTH_SHORT).show();
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(SearchIntentService.ACTION_FINISHED));
                } catch (NullPointerException e){

                }

                break;

            case R.id.btnShare:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

                intent.putExtra(Intent.EXTRA_SUBJECT, "Look at this cool place!");
                intent.putExtra(Intent.EXTRA_TEXT, "Look at this cool place:\n" + place.getName() + "\nin " + place.getAddress()
                + "\n" + place.getWebsite());
                startActivity(Intent.createChooser(intent, "How do you want to share?"));

                break;

            case R.id.btnWeb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(place.getWebsite())));

                break;

            case R.id.btnWaze:
                String uri = "waze://?ll=" + place.getLat() + "," + place.getLon() + "&navigate=yes";
                try {

                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }catch (ActivityNotFoundException e){
                    uri = "google.navigation:q=" + place.getLat() + "," + place.getLon();
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }

                break;

            case R.id.btnCall:
                try {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.getPhone())));
                } catch (ActivityNotFoundException e){
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle("You Don't have a Phone!")
                            .setMessage("You can't call from this device, but here is the number:\n" + place.getPhone() + "\nHave Fun :)")
                            .create();
                    dialog.show();
                }
                break;
        }
    }
//updating new data
    private class ItemClickedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Place place = intent.getParcelableExtra("place");
            new DetailsTask().execute(place);
        }
    }
//AsyncTask for downloading all the data from the internet
    class DetailsTask extends AsyncTask<Place, Void, PlaceDetails> {

        @Override
        protected PlaceDetails doInBackground(Place... params) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyA4gYLvR0QW8tsz5556x9pznFIQg2EpF6k&placeid=" + params[0].getId());
                connection = (HttpsURLConnection) url.openConnection();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    AlertDialog dialogWeb = new AlertDialog.Builder(getContext())
                            .setTitle("Connection Problem")
                            .setMessage("Sorry, there was a connection problem :( \nYou can't see data on the place\nGet WiFi and try again" )
                            .create();
                    dialogWeb.show();
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null){
                    builder.append(line);
                    line = reader.readLine();
                }

                JSONObject placeObject = new JSONObject(builder.toString());
                JSONObject resultObject = placeObject.getJSONObject("result");
                String phone = resultObject.getString("international_phone_number");
                String website = resultObject.getString("website");
                float rate = (float) resultObject.getDouble("rating");
                JSONObject openObject = resultObject.getJSONObject("opening_hours");
                boolean isOpen = openObject.getBoolean("open_now");

                JSONArray openArray = openObject.getJSONArray("weekday_text");
                String openingHours = "";
                for (int i = 0; i < openArray.length() ; i++) {
                    openingHours += openArray.getString(i) + "\n";
                }

                return new PlaceDetails(params[0].getId(),params[0].getName(),params[0].getAddress(),params[0].getLat(), params[0].getLon(),params[0].getImage(),params[0].getPicturesURL(),website,openingHours,phone,rate,isOpen);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PlaceDetails placeDetails) {
            super.onPostExecute(placeDetails);
            place = placeDetails;
            if (getContext() != null) {
                try {



                    Picasso.get()
                            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&key=AIzaSyA4gYLvR0QW8tsz5556x9pznFIQg2EpF6k&photoreference=" + place.getPicturesURL())
                            .into(imagePlace);
                    textPlaceName.setText(place.getName());
                    textPlaceAddress.setText(place.getAddress());
                    textRate.setText(place.getRating() + "/5");
                    if (place.isOpenNow())
                        textIsOpen.setText("Open Now");
                    else
                        textIsOpen.setText("Close");

                    textOpeningHours.setText(place.getOpeningHours());
                } catch (NullPointerException e) {

                }
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("placeDetail", place);
    }
}
