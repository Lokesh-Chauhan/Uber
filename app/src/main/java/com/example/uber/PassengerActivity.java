package com.example.uber;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btnRequestCar,btnLogoutPassenger;
    private Boolean isUberCanceled=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnRequestCar=findViewById(R.id.btnRequestCar);
        btnLogoutPassenger=findViewById(R.id.btnLogoutPassenger);
        btnRequestCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isUberCanceled) {

                    if (ContextCompat.checkSelfPermission(PassengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location passengerLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (passengerLocation != null) {
                            ParseObject parseObject = new ParseObject("RequestCar");
                            parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(passengerLocation.getLatitude(), passengerLocation.getLongitude());
                            parseObject.put("passengerLocation", parseGeoPoint);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), "Car Request is send ", Toast.LENGTH_LONG).show();
                                        btnRequestCar.setText("cancel Request");
                                        isUberCanceled = false;
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Somthing is Wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    ParseQuery<ParseObject> carRequestQuary=ParseQuery.getQuery("RequestCar");
                    carRequestQuary.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
                    carRequestQuary.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (objects.size() > 0 && e==null){
                                btnRequestCar.setText("Request New Car");
                                isUberCanceled=true;
                                for (ParseObject user:objects){
                                    user.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                }
        });

        btnLogoutPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(PassengerActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCamera(location);
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

        if (Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(PassengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(PassengerActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location currentLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateCamera(currentLocation);
            }

        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1000 && grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(PassengerActivity.this,Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateCamera(currentLocation);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void updateCamera(Location pLocation){
        LatLng passangerLatLng=new LatLng(pLocation.getLatitude(),pLocation.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passangerLatLng,10));
        mMap.addMarker(new MarkerOptions().position(passangerLatLng).title("You"));
    }
}