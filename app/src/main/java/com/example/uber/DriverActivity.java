package com.example.uber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Button btnGetrequests;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private ListView listView;
    private ArrayList<Double> passengerLongitude;
    private ArrayList<Double> passengerLattitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        passengerLattitude = new ArrayList<>();
        passengerLongitude = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        btnGetrequests = findViewById(R.id.btnGetRequests);
        btnGetrequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


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
                }

                if (Build.VERSION.SDK_INT < 23) {

                } else if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(DriverActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DriverActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                    } else {
                         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        try {

                            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateCamera(currentLocation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    listView.setOnItemClickListener(DriverActivity.this);

                }
            }
        });
    }

    private void updateCamera(Location currentLocation) {

        if (currentLocation!=null) {

            arrayList.clear();

            final ParseGeoPoint parseGeoPoint=new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
            ParseQuery<ParseObject> parseQuery=ParseQuery.getQuery("RequestCar");
            parseQuery.whereNear("passengerLocation",parseGeoPoint);
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size()>0 && e==null){

                        if (arrayList.size()>0){
                            arrayList.clear();
                        }
                        if (passengerLattitude.size()>0){
                            passengerLattitude.clear();
                        }
                        if (passengerLongitude.size()>0){
                            passengerLongitude.clear();
                        }

                        for (ParseObject obj: objects){

                            ParseGeoPoint pLocation=(ParseGeoPoint) obj.get("passengerLocation");
                            Double milesDistanceToPassenger=parseGeoPoint.distanceInMilesTo(pLocation);
                            float roundDistancevalue=Math.round(milesDistanceToPassenger * 10) /10;
                            arrayList.add("There are "+roundDistancevalue+"Miles To "+ obj.get("username"));

                            passengerLattitude.add(pLocation.getLatitude());
                            passengerLongitude.add(pLocation.getLongitude());
                        }

                        arrayAdapter.notifyDataSetChanged();

                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menuDriverLogout){
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        Toast.makeText(getApplicationContext(),"LoggedOut",Toast.LENGTH_LONG).show();

                    finish();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1000 && grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(DriverActivity.this,Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateCamera(currentLocation);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_LONG).show();
    }
}

