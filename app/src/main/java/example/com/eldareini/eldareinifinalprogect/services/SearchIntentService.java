package example.com.eldareini.eldareinifinalprogect.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;

//a service for searching places near by
public class SearchIntentService extends IntentService {
    public static final String ACTION_FINISHED = "example.com.eldareini.eldareinifinalprogect.services.ACTION_FINISHED";
    private String error;

      public SearchIntentService() {
        super("SearchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        double userLat = intent.getDoubleExtra("LAT", 0);
        double userLon = intent.getDoubleExtra("LON", 0);
        int radius = intent.getIntExtra("RADIUS", 25000);
        String search = intent.getStringExtra("SEARCH");

        String searchURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyA4gYLvR0QW8tsz5556x9pznFIQg2EpF6k&" +
                "location=" + userLat + "," + userLon + "&radius=" + radius;

        if (!(search.isEmpty())){
            searchURL += ("&keyword=" + search);
        }

        try {
            URL url = new URL(searchURL);
            connection = (HttpsURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                error = "Error from Server";
                return;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();
            while (line != null){
                builder.append(line);
                line = reader.readLine();
            }

            JSONObject object = new JSONObject(builder.toString());
            JSONArray resultArray = object.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject resultObject = resultArray.getJSONObject(i);
                String id = resultObject.getString("place_id");
                String name = resultObject.getString("name");
                String address = resultObject.getString("vicinity");

                JSONObject geometryObject = resultObject.getJSONObject("geometry");
                JSONObject locationObject = geometryObject.getJSONObject("location");
                double lat = locationObject.getDouble("lat");
                double lon = locationObject.getDouble("lng");

                JSONArray typeArray = resultObject.getJSONArray("types");
                String type = typeArray.getString(0);

                int image;

                if (type.equalsIgnoreCase("airport") || type.equalsIgnoreCase("travel_agency"))
                    image = R.mipmap.airport;
                else if (type.equalsIgnoreCase("amusement_park"))
                    image = R.mipmap.amusement_park;
                else if (type.equalsIgnoreCase("aquarium"))
                    image = R.mipmap.sea;
                else if (type.equalsIgnoreCase("art_gallery") || type.equalsIgnoreCase("museum"))
                    image = R.mipmap.museum;
                else if (type.equalsIgnoreCase("atm") || type.equalsIgnoreCase("bank") || type.equalsIgnoreCase("casino"))
                    image = R.mipmap.bank;
                else if (type.equalsIgnoreCase("bakery"))
                    image = R.mipmap.bread;
                else if (type.equalsIgnoreCase("beauty_salon") || type.equalsIgnoreCase("hair_care"))
                    image = R.mipmap.makeup;
                else if (type.equalsIgnoreCase("bicycle_store"))
                    image = R.mipmap.bicycle;
                else if (type.equalsIgnoreCase("book_store") || type.equalsIgnoreCase("library"))
                    image = R.mipmap.reading;
                else if (type.equalsIgnoreCase("bowling_alley"))
                    image = R.mipmap.bowling;
                else if (type.equalsIgnoreCase("bus_station"))
                    image = R.mipmap.bus_stop;
                else if (type.equalsIgnoreCase("cafe"))
                    image = R.mipmap.coffee;
                else if (type.equalsIgnoreCase("car_dealer") || type.equalsIgnoreCase("car_rental") || type.equalsIgnoreCase("car_repair") || type.equalsIgnoreCase("car_wash"))
                    image = R.mipmap.car;
                else if (type.equalsIgnoreCase("shopping_mall") || type.equalsIgnoreCase("jewelry_store") || type.equalsIgnoreCase("shoe_store") || type.equalsIgnoreCase("store") || type.equalsIgnoreCase("clothing_store") || type.equalsIgnoreCase("convenience_store") || type.equalsIgnoreCase("department_store") || type.equalsIgnoreCase("electronics_store") || type.equalsIgnoreCase("furniture_store") || type.equalsIgnoreCase("hardware_store") || type.equalsIgnoreCase("home_goods_store"))
                    image = R.mipmap.cart;
                else if (type.equalsIgnoreCase("dentist") || type.equalsIgnoreCase("hospital") || type.equalsIgnoreCase("doctor"))
                    image = R.mipmap.nurse;
                else if (type.equalsIgnoreCase("fire_station") || type.equalsIgnoreCase("police"))
                    image = R.mipmap.police;
                else if (type.equalsIgnoreCase("gym"))
                    image = R.mipmap.gym;
                else if (type.equalsIgnoreCase("zoo") || type.equalsIgnoreCase("pet_store"))
                    image = R.mipmap.zoo;
                else if (type.equalsIgnoreCase("university") || type.equalsIgnoreCase("school"))
                    image = R.mipmap.diploma;
                else if (type.equalsIgnoreCase("transit_station") || type.equalsIgnoreCase("train_station") || type.equalsIgnoreCase("subway_station"))
                    image = R.mipmap.train;
                else if (type.equalsIgnoreCase("taxi_stand"))
                    image = R.mipmap.taxi;
                else if (type.equalsIgnoreCase("synagogue"))
                    image = R.mipmap.jew;
                else if (type.equalsIgnoreCase("spa"))
                    image = R.mipmap.spa;
                else if (type.equalsIgnoreCase("stadium"))
                    image = R.mipmap.stadium;
                else if (type.equalsIgnoreCase("rv_park") || type.equalsIgnoreCase("parking"))
                    image = R.mipmap.parking;
                else if (type.equalsIgnoreCase("restaurant") || type.equalsIgnoreCase("meal_delivery") || type.equalsIgnoreCase("meal_takeaway"))
                    image = R.mipmap.restaurant;
                else if (type.equalsIgnoreCase("post_office"))
                    image = R.mipmap.postbox;
                else if (type.equalsIgnoreCase("bar") || type.equalsIgnoreCase("liquor_store") || type.equalsIgnoreCase("night_club"))
                    image = R.mipmap.beer;
                else if (type.equalsIgnoreCase("movie_theater") || type.equalsIgnoreCase("movie_rental"))
                    image = R.mipmap.movie;
                else
                    image = R.mipmap.place;

                JSONArray photoArray = resultObject.getJSONArray("photos");
                JSONObject photoObject = photoArray.getJSONObject(0);
                String picturesURL = photoObject.getString("photo_reference");

                ContentValues values = new ContentValues();
                values.put(PlaceDBHelper.COL_ID, id);
                values.put(PlaceDBHelper.COl_NAME, name);
                values.put(PlaceDBHelper.COL_ADDRESS, address);
                values.put(PlaceDBHelper.COL_LAT, lat);
                values.put(PlaceDBHelper.COL_LON, lon);
                values.put(PlaceDBHelper.COL_IMAGE, image);
                values.put(PlaceDBHelper.COL_PICTURES_URL, picturesURL);
                getContentResolver().insert(PlaceProvider.PLACE_URI, values);


            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //open a Broadcast to update data on the adapter
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FINISHED));


    }

}
