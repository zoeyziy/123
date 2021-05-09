package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(this, "Google_Map_API_Key");
        placesClient = Places.createClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMarkerClickListener(this);
        enableMyLocation();

        setMarkers();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

//    private void drawLocationMarkers() {
//        HashMap<String, LatLng> results = processResults(getLocations());
//        for (Map.Entry<String, LatLng> result : results.entrySet()) {
//            map.addMarker(new MarkerOptions().title(result.getKey()).position(result.getValue()));
//        }
//    }

    private HashMap<String, LatLng> processResults(List<AutocompletePrediction> results) {
        HashMap<String, LatLng> locations = new HashMap<>();
        CharacterStyle s = new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint tp) {
                // Unnecessary
            }
        };

        for (AutocompletePrediction pred : results) {
            LatLng latLng = getLocationLatLng(pred.getPlaceId());
            locations.put(pred.getPrimaryText(s).toString(), latLng);
        }

        return locations;
    }

    private LatLng getLocationLatLng(String placeId) {
        List<Place.Field> options = new ArrayList<>();
        options.add(Place.Field.LAT_LNG);

        FetchPlaceRequest req = FetchPlaceRequest.builder(placeId, options).build();
        FetchPlaceResponse res = placesClient.fetchPlace(req).getResult();
        return res.getPlace().getLatLng();
    }

    private void getLocations() {
        FindAutocompletePredictionsRequest req =
                FindAutocompletePredictionsRequest.builder()
                .setQuery("car maintenance")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build();
        Task<FindAutocompletePredictionsResponse> res =
                placesClient.findAutocompletePredictions(req);

        res.addOnCompleteListener(task -> {
            task.getResult().getAutocompletePredictions().forEach(x -> {
                Log.d("", "getLocations: " + x.toString());
            });
        });
    }

    private void setMarkers() {
        for (Map.Entry<String, LatLng> entry : SpoofData.hashMap.entrySet()) {
            map.addMarker(new MarkerOptions().title(entry.getKey()).position(entry.getValue()));
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent returnIntent = new Intent(this, ScheduleApptActivity.class);
        returnIntent.putExtra("title", marker.getTitle());
        returnIntent.putExtra("latlng", marker.getPosition());
        Log.d("map", "fuckin a");
        startActivity(returnIntent);
        return true;
    }
}