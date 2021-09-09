package com.hle.knowyourgovernment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //API key: AIzaSyD9GR0qBE-T08g2A4xJsDFGnKLuzhQjco8

    private static final String TAG = "MainActivity";
    private final List<Official> officialList = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private OfficialAdapter officialAdapter;
    private TextView Location;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location = findViewById(R.id.location);
        if (savedInstanceState != null) Location.setText(savedInstanceState.getString("LOCATION"));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        //use GPS for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        //check location service permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( //request permission
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            if (savedInstanceState == null) setLocation();
            if (!checkNetworkConnection()) Location.setText("No Data For Location");
        }

        recyclerView = findViewById(R.id.recyclerView);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.smoothScrollToPosition(0);

        downloadDataBegin();

        swiper = findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!checkNetworkConnection()) {
                    swiper.setRefreshing(false);
                    return;
                } else {
                    if (Location.getText().toString().equals("No Data For Location")) setLocation();
                    downloadDataBegin();
                    officialAdapter.notifyDataSetChanged();
                    swiper.setRefreshing(false); //stop busy circle
                }

            }
        });
    }

    //ask location service permission
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        ((TextView) findViewById(R.id.location)).setText("Unavailable Location Service");
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true); //get provide

        android.location.Location currentLocation = null;
        if (bestProvider != null) { //check gps or network
            currentLocation = locationManager.getLastKnownLocation(bestProvider); //update location
        }
        if (currentLocation != null) { //
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String a = "";
                for (Address ad : addresses) {
                    a = String.format("%s, %s %s",
                            //(ad.getSubThoroughfare() == null ? "" : ad.getSubThoroughfare()),
                            //(ad.getThoroughfare() == null ? "" : ad.getThoroughfare()), //This is Street
                            (ad.getLocality() == null ? "" : ad.getLocality()), //City
                            (ad.getAdminArea() == null ? "" : ad.getAdminArea()), //State
                            (ad.getPostalCode() == null ? "" : ad.getPostalCode())); //Zipcode
                            //(ad.getCountryName() == null ? "" : ad.getCountryName())); //Country
                }
                ((TextView) findViewById(R.id.location)).setText(a);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ((TextView) findViewById(R.id.location)).setText("No location set");
        }
    }

    public void downloadDataBegin() {
        officialList.clear();
        String searchLocation = Location.getText().toString();
        ProfileDownloader pd = new ProfileDownloader(this, searchLocation); ////need to fix this
        new Thread(pd).start();
    }

    public void downloadDataSearch(String searchString) {
        officialList.clear();
        ProfileDownloader pd = new ProfileDownloader(this, searchString); ////need to fix this
        new Thread(pd).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.knowyourgovernment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.question:
                Toast.makeText(this, "About this app", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(MainActivity.this, AboutThisApp.class);
                startActivity(intent1);
                return true;
            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                makeSearchDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeSearchDialog() {
        if (!checkNetworkConnection()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                searchString = et.getText().toString().trim();
                downloadDataSearch(searchString);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a City, State or Zip Code:");
        builder.setTitle("Location Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        checkNetworkConnection();

        int pos = recyclerView.getChildLayoutPosition(v);
        Official official = officialList.get(pos);
        Intent intent = new Intent(MainActivity.this, OfficialProfileActivity.class);

        intent.putExtra("location", Location.getText().toString());
        intent.putExtra("name", official.getName());
        intent.putExtra("position", official.getPosition());
        intent.putExtra("party", official.getParty());
        intent.putExtra("address", official.getAddress());
        intent.putExtra("phones", official.getPhones());
        intent.putExtra("urls", official.getUrls());
        intent.putExtra("emails", official.getEmails());
        intent.putExtra("photoUrl", official.getPhotoUrl());
        intent.putExtra("FacebookID", official.getFacebookID());
        intent.putExtra("Twitter", official.getTwitterID());
        intent.putExtra("YouTube", official.getYouTubeID());

        startActivity(intent);
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }
    }

    public void addOfficial(Official official) {
        officialList.add(official);
        //Collections.sort(officialList);
        officialAdapter.notifyDataSetChanged();
    }

    public void updateLocation(String newlocation) {
        Location.setText(newlocation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("LOCATION", Location.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveState) {
        super.onRestoreInstanceState(saveState);
        Location.setText(saveState.getString("LOCATION"));
        officialAdapter.notifyDataSetChanged();
    }
}