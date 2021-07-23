package com.codepath.tender;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import static com.codepath.tender.Constants.MAPVIEW_BUNDLE_KEY;

public class MapService implements OnMapReadyCallback {

    private Context context;
    private MapView mapView;
    private double latitude;
    private double longitude;
    private LatLngBounds boundaries;
    private GoogleMap map;

    public MapService(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //initialize map view
    public void createMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    //save instance of map
    public void onSaveInstance(Bundle outState) {
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    //called when map is ready to be used
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        setMapView();
    }

    public void setMarker() {
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
    }

    //sets the view boundaries of the map
    public void setMapView() {
        double user_latitude = ParseUser.getCurrentUser().getDouble("latitude");
        double user_longitude = ParseUser.getCurrentUser().getDouble("longitude");

        //creating a window around the current user location
        double bottom_boundary = user_latitude - .005;
        double left_boundary = user_longitude - .005;
        double top_boundary = user_latitude + .005;
        double right_boundary = user_longitude + .005;

        boundaries = new LatLngBounds(new LatLng(bottom_boundary, left_boundary), new LatLng(top_boundary, right_boundary));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundaries, 200, 200, 0));
    }

    public void onResume() {
        mapView.onResume();
    }

    public void onStart() {
        mapView.onStart();
    }

    public void onStop() {
        mapView.onStop();
    }

    public void onPause() {
        mapView.onPause();
    }

    public void onDestroy() {
        mapView.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
    }
}
