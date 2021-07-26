package com.codepath.tender;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codepath.tender.models.PolylineData;
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
    private Marker marker;

    private ArrayList<PolylineData> polylines_list;

    //constructor
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

    //sets a marker at the restaurant position
    public void setMarker(double latitude, double longitude, String title) {
        marker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(title));
        calculateDirections(marker);
    }

    //calculates the routes from the user location to the restaurant using google directions api
    private void calculateDirections(Marker marker){
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

        //call to google directions api
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                //add this route to the map
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
                //clearing any past data
                if(polylines_list.size() > 0){
                    for(PolylineData polylineData: polylines_list){
                        polylineData.getPolyline().remove();
                    }
                    polylines_list.clear();
                    polylines_list = new ArrayList<>();
                }

                double duration = 999999999;
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
                    polylines_list.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                    }
                }
            }
        });
    }

    //called when a polyline is clicked; changes its color to blue and shows trip duration
    @Override
    public void onPolylineClick(Polyline polyline) {
        //find which polyline in our list is the one that was clicked
        for(PolylineData polylineData: polylines_list){
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                //this polyline was clicked; set color to blue and index to 1
                polylineData.getPolyline().setColor(ContextCompat.getColor(context, R.color.main_color));
                polylineData.getPolyline().setZIndex(1);

                //set marker dialog to show distance in time
                marker.setSnippet("" + polylineData.getLeg().duration);

                //show info window above marker
                marker.showInfoWindow();

                //zoom view to correct boundary
                zoomRoute(polyline.getPoints());
            }
            else{
                //this polyline was not clicked; set color to grey and index to 0
                polylineData.getPolyline().setColor(ContextCompat.getColor(context, R.color.grey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    //animates the camera view to zoom in on routes
    public void zoomRoute(List<LatLng> lstLatLngRoute) {
        //check whether map is not completely set
        if (map == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        //set bounds to include all points in route
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 80;
        LatLngBounds latLngBounds = boundsBuilder.build();

        //animate a zoom to the center of the view
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    public void openGoogleMapsDialog() {
        //creating alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //prompting user whether to opem google maps
        builder.setMessage("Open Google Maps?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() { //yes
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //creating google maps route uri
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                        //passing in the uri to google maps
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        try{
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(mapIntent);
                            }
                        }catch (NullPointerException e){
                            Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() { //no
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //close dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(context.getResources().getColor(R.color.transparent));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(context.getResources().getColor(R.color.transparent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.dark_grey));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.dark_grey));
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
