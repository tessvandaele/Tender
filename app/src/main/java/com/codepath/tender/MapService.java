package com.codepath.tender;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.tender.Constants.MAPVIEW_BUNDLE_KEY;
import static com.codepath.tender.Constants.MAP_API_KEY;

public class MapService implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    final private static String TAG = "MapService";

    private Context context;
    private MapView mapView;
    private LatLngBounds boundaries;
    private GoogleMap map;
    private GeoApiContext geoApiContext;

    private ArrayList<Polyline> polylines_list;

    public MapService(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    //initialize map view
    public void createMap(Bundle savedInstanceState) {
        polylines_list = new ArrayList<>();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if(geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(MAP_API_KEY)
                    .build();
        }
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
        this.map.setOnPolylineClickListener(this);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        setMapView();
    }

    public void setMarker(double latitude, double longitude) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));
        calculateDirections(marker);
    }

    //calculates the routes from the user location to the restaurant using google directions api
    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        //set destination
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        //provides user with multiple routes
        directions.alternatives(true);

        //set origin
        directions.origin(
                new com.google.maps.model.LatLng(
                        ParseUser.getCurrentUser().getDouble("latitude"),
                        ParseUser.getCurrentUser().getDouble("longitude")
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    //draws route lines on map
    private void addPolylinesToMap(final DirectionsResult result){
        //must be on main thread because map is currently on main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(!polylines_list.isEmpty()) {
                    polylines_list.clear();
                }

                //iterate through routes
                for(DirectionsRoute route: result.routes){
                    //split up path into parts
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    //iterates through all the coordinates of a polyline and add them to path list
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    //add and set polyline
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(context, R.color.grey));
                    polyline.setClickable(true);
                    polylines_list.add(polyline);
                }
            }
        });
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

    @Override
    public void onPolylineClick(Polyline polyline) {
        for(Polyline polylineData: polylines_list){
            if(polyline.getId().equals(polylineData.getId())){
                polylineData.setColor(ContextCompat.getColor(context, R.color.main_color));
                polylineData.setZIndex(1);
            }
            else{
                polylineData.setColor(ContextCompat.getColor(context, R.color.grey));
                polylineData.setZIndex(0);
            }
        }
    }
}
