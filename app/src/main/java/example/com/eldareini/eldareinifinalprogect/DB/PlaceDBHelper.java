package example.com.eldareini.eldareinifinalprogect.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import example.com.eldareini.eldareinifinalprogect.objects.Place;

/**
 * Created by Eldar on 9/19/2017.
 */


//creating a database
public class PlaceDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_PLACES = "places";
    public static final String TABLE_FAVORITE = "favorite";
    public static final String COL_ID = "id";
    public static final String COl_NAME = "name";
    public static final String COL_ADDRESS = "address";
    public static final String COL_LAT = "lat";
    public static final String COL_LON = "lon";
    public static final String COL_IMAGE = "image";
    public static final String COL_PICTURES_URL = "picturesURL";

    public PlaceDBHelper(Context context) {
        super(context, "places.db", null, 1);
    }

    //creating a Table for SearchPlaces and for FavoritePlaces
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format( " CREATE TABLE %s ( %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s INTEGER, %s TEXT )",
                TABLE_PLACES, COL_ID, COl_NAME, COL_ADDRESS, COL_LAT, COL_LON, COL_IMAGE, COL_PICTURES_URL));

        db.execSQL(String.format( " CREATE TABLE %s ( %s TEXT, %s TEXT, %s TEXT, %s REAL, %s REAL, %s INTEGER, %s TEXT )",
                TABLE_FAVORITE, COL_ID, COl_NAME, COL_ADDRESS, COL_LAT, COL_LON, COL_IMAGE, COL_PICTURES_URL));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPlace (Place place) throws SQLException {
    }
}
