package example.com.eldareini.eldareinifinalprogect.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import example.com.eldareini.eldareinifinalprogect.R;
import example.com.eldareini.eldareinifinalprogect.adapters.PlaceAdapter;
import example.com.eldareini.eldareinifinalprogect.fragments.PlaceContainerFragment;
import example.com.eldareini.eldareinifinalprogect.fragments.TabsFragment;
import example.com.eldareini.eldareinifinalprogect.providers.PlaceProvider;
import example.com.eldareini.eldareinifinalprogect.services.SearchIntentService;

//The Main Activity


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LocationListener, AlertDialog.OnClickListener {
    private SharedPreferences sp;
    private static String distance;
    private static int radius;
    private static double userLat, userLon;
    private LocationManager locationManager;
    private static boolean isTablet, isResume;
    private AlertDialog clearFavoritesDialog, exitDialog;
    private BatteryReceiver receiver = new BatteryReceiver();

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check if it the first time the app running
        if (savedInstanceState == null){
            TabsFragment tabsFragment = new TabsFragment();
            PlaceContainerFragment placeContainerFragment = new PlaceContainerFragment();
            //Check if it run on a tablet - in land view
            if (findViewById(R.id.contanier_tablet) == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contanier, tabsFragment, "tabsFragment")
                        .show(tabsFragment)
                        .add(R.id.contanier, placeContainerFragment, "placeContainerFragment")
                        .hide(placeContainerFragment)
                        .commit();
            } else {
                isTablet = true;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contanier, tabsFragment, "tabsFragment")
                        .show(tabsFragment)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.contanier_tablet, placeContainerFragment, "placeContainerFragment")
                        .show(placeContainerFragment)
                        .commit();
            }

        }

        Toolbar myToolbar= (Toolbar) findViewById(R.id.myToolbar);
        SearchView toolbarSearch = (SearchView) myToolbar.findViewById(R.id.toolbarSearch);
        toolbarSearch.setOnQueryTextListener(this);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

       LocalBroadcastManager.getInstance(this).registerReceiver(new ItemClickedReceiver(), new IntentFilter(PlaceAdapter.ACTION_CLICKED));
       registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    protected void onStart() {
        super.onStart();
        distance = sp.getString("preference_distance", "KM");
        radius = (sp.getInt("preference_radius", 15)) * 1000;
        getLocation();

    }
    //get the Location Permission from user
    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, getMainLooper());
    }
    // when permission granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
               getLocation();
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Allow access to your GPS!")
                        .setMessage("Please Allow us to use your GPS, if you want the application to work")
                        .create();
                dialog.show();
                getLocation();
            }
        }
    }
    //create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.menuClearFavorits:
                clearFavoritesDialog = new AlertDialog.Builder(this)
                        .setTitle("Are You Sure?")
                        .setMessage("Are you sure you want to delete all the favorits?")
                        .setPositiveButton("yes", this)
                        .setNegativeButton("no", this)
                        .create();
                clearFavoritesDialog.show();
                break;

            case R.id.menuExit:
                exitDialog = new AlertDialog.Builder(this)
                        .setTitle("Are You Sure?")
                        .setMessage("Are you sure you want to Exit")
                        .setPositiveButton("yes", this)
                        .setNegativeButton("no", this)
                        .create();
                exitDialog.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //whate to do when searching
    @Override
    public boolean onQueryTextSubmit(String query) {
        getLocation();
        getContentResolver().delete(PlaceProvider.PLACE_URI, null, null);
        Intent searchService = new Intent(this, SearchIntentService.class);
        searchService.putExtra("LAT", userLat);
        searchService.putExtra("LON", userLon);
        searchService.putExtra("RADIUS", radius);
        searchService.putExtra("SEARCH", query);
        startService(searchService);

        Toast.makeText(this, "Searching...\nPlease waite", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static String getDistance() {
        return distance;
    }

    public static int getRadius() {
        return radius;
    }

    public static double getUserLat() {
        return userLat;
    }

    public static double getUserLon() {
        return userLon;
    }

    //get the Latitude and Longitude
    @Override
    public void onLocationChanged(Location location) {
        userLat = location.getLatitude();
        userLon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
       if (dialog == clearFavoritesDialog){
           switch (which){
               case DialogInterface.BUTTON_POSITIVE:
                   getContentResolver().delete(PlaceProvider.FAVORITE_URI, null, null);
                   LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SearchIntentService.ACTION_FINISHED));
                   break;
           }

       } else if (dialog == exitDialog){
           switch (which) {
               case DialogInterface.BUTTON_POSITIVE:
                   finish();
                break;
           }

       }
    }


    private class ItemClickedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Fragment placeContainerFragment = getSupportFragmentManager().findFragmentByTag("placeContainerFragment");
            Fragment tabsFragment = getSupportFragmentManager().findFragmentByTag("tabsFragment");

            try {

                if (!isTablet && isResume) {
                    getSupportFragmentManager().beginTransaction()
                            .show(placeContainerFragment)
                            .hide(tabsFragment)
                            .addToBackStack("")
                            .commitAllowingStateLoss();
                }
            } catch (IllegalStateException e){

            }

        }
    }

    private class BatteryReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int charging = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            switch (charging){
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    Toast.makeText(MainActivity.this, "You Are Charging", Toast.LENGTH_SHORT).show();
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    Toast.makeText(MainActivity.this, "You are Not Charging", Toast.LENGTH_SHORT).show();
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    Toast.makeText(MainActivity.this, "Battery is full", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
