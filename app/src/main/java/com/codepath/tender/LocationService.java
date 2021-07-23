package com.codepath.tender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseUser;

import static com.codepath.tender.Constants.LATITUDE_KEY;
import static com.codepath.tender.Constants.LONGITUDE_KEY;
import static com.codepath.tender.MainActivity.PERMISSION_ID;

public class LocationService {

    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private RestaurantViewModel model;

    public LocationService(Context context, FusedLocationProviderClient fusedLocationProviderClient, RestaurantViewModel model) {
        this.context = context;
        this.mFusedLocationClient = fusedLocationProviderClient;
        this.model = model;
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            //update view model
                            model.setLatitude(location.getLatitude());
                            model.setLongitude(location.getLongitude());

                            //update parse database
                            ParseUser.getCurrentUser().put(LATITUDE_KEY, location.getLatitude());
                            ParseUser.getCurrentUser().put(LONGITUDE_KEY, location.getLongitude());
                            ParseUser.getCurrentUser().saveInBackground();
                        }
                    }
                });
            } else {
                Toast.makeText(context, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    // method to check for permissions
    private boolean checkPermissions() {
        return context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions((Activity)context, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            model.setLatitude(mLastLocation.getLatitude());
            model.setLongitude(mLastLocation.getLongitude());
            ParseUser.getCurrentUser().put(LATITUDE_KEY, mLastLocation.getLatitude());
            ParseUser.getCurrentUser().put(LONGITUDE_KEY, mLastLocation.getLongitude());
            ParseUser.getCurrentUser().saveInBackground();
        }
    };

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    // If everything is alright then get location
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    public void onResume() {
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}
