package com.example.lab09;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private GeoDataDao dao;
    private FusedLocationProviderClient fused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = AppDatabase.get(this).geoDataDao();
        fused = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabFromLocation = findViewById(R.id.fabFormLocation);

        fabAdd.setOnClickListener(v -> showAddDialog());
        fabFromLocation.setOnClickListener(v -> addCurrentLocationAsPlace());

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        refreshMarkers();

        map.setOnMapLongClickListener(latLng -> {
            var lat = latLng.latitude;
            var lng = latLng.longitude;
            var deviceLocale = getResources().getConfiguration().getLocales().get(0);

            String markerName = String.format(deviceLocale, "%.2f, %.2f", lat, lng);

            addPlace(markerName, lat, lng, "");
        });
    }

    private void refreshMarkers() {
        map.clear();
        for (GeoData gd : dao.getAll()) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(gd.lat, gd.lng))
                    .title(gd.name)
                    .snippet(gd.address));
        }
    }

    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private void addCurrentLocationAsPlace() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fused.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        // Save to DB + add marker
                        addPlace("My Location", lat, lng, null);
                    } else {
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPlace(String name, double lat, double lng, String address) {
        GeoData place = new GeoData();
        place.name = (name == null || name.isEmpty()) ? "Place" : name;
        place.lat = lat;
        place.lng = lng;
        place.address = address;
        place.createdAt = System.currentTimeMillis();
        dao.insertAll(place);
        refreshMarkers();
    }


    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter address");

        new AlertDialog.Builder(this)
                .setTitle("Add marker")
                .setView(input)
                .setPositiveButton("OK", (d, w) -> {
                    String address = input.getText().toString();
                    if (address.isEmpty()) {
                        return;
                    }
                    LatLng coords = forwardGeocode(address);
                    if (coords != null) {
                        addPlace(address, coords.latitude, coords.longitude, address);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f));
                    } else {
                        Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private LatLng forwardGeocode(String address) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocationName(address, 1);
            if (list != null && !list.isEmpty()) {
                Address a = list.get(0);
                return new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}