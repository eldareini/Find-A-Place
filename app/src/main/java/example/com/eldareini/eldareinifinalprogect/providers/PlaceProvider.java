package example.com.eldareini.eldareinifinalprogect.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import example.com.eldareini.eldareinifinalprogect.DB.PlaceDBHelper;

// Provider to change data on the dataBase
public class PlaceProvider extends ContentProvider {

    private PlaceDBHelper helper;
    private static final String AUTHORIZATION = "com.eldareini.eldareinifinalprogect.PLACE";
    public static final  Uri PLACE_URI = Uri.parse("content://" + AUTHORIZATION + "/" + PlaceDBHelper.TABLE_PLACES);
    public static final  Uri FAVORITE_URI = Uri.parse("content://" + AUTHORIZATION + "/" + PlaceDBHelper.TABLE_FAVORITE);

    public PlaceProvider() {
    }

    @Override
    public boolean onCreate() {
        helper = new PlaceDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(uri.getLastPathSegment(), projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        db.close();
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowNum = db.insert(uri.getLastPathSegment(), null, values);
        db.close();
        return Uri.withAppendedPath(uri, "" + rowNum);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        db.close();
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
