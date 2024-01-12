package com.example.safa2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView tvLatitude;
    private TextView tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize locationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize locationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle the updated location here
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Update TextViews with the new values
                tvLatitude.setText("Latitude: " + latitude);
                tvLongitude.setText("Longitude: " + longitude);

                // Calculate and display the new coordinates with a radius of 200 meters
                calculateAndDisplayRadius(latitude, longitude, 200);
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
        };

        // Initialize TextViews
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
    }

    // This method is triggered when the "Request Location Updates" button is clicked
    public void onRequestLocationClick(View view) {
        // Check and request location updates here
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is enabled, proceed with location updates
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                );
            } else {
                // Handle the case when permission is not granted
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1
                );
            }
        } else {
            // GPS is not enabled, prompt the user to enable it
            Toast.makeText(this, "Please enable GPS for accurate location updates.", Toast.LENGTH_LONG).show();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can now request location updates
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
            );
        } else {
            // Permission not granted, handle accordingly
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve latitude and longitude from the snapshot
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);


                    // Handle the retrieved data as needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors when data retrieval is canceled
                // For example, you can log the error or show a toast
                Toast.makeText(MainActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }); // <-- Corrected closing parenthesis
    }


    // Calculate new coordinates with a given radius and display them
    private void calculateAndDisplayRadius(double latitude, double longitude, double radiusInMeters) {
        // Earth radius in meters
        double earthRadius = 6371000;

        // Conversion factor for latitude and longitude to radians
        double degToRad = Math.PI / 180.0;

        // Calculate new coordinates based on a distance and bearing
        double lat2 = latitude + (radiusInMeters / earthRadius) * (180.0 / Math.PI);
        double lon2 = longitude + (radiusInMeters / earthRadius) * (180.0 / Math.PI) / Math.cos(latitude * degToRad);

        // Display the new coordinates
        Toast.makeText(this, "New Coordinates with 200m Radius:\nLatitude: " + lat2 + "\nLongitude: " + lon2, Toast.LENGTH_LONG).show();
    }
}