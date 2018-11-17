package com.example.aura.a9_location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    // btn location
    public Button btnLoc;

    // variable dg tipe Location
    private Location mLastLocation;


    // var dg tipe FusedLocationProviderClient
    private FusedLocationProviderClient mFusedLocationClient;
    public TextView mLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationTextView = (TextView) findViewById(R.id.textMap);

        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();

            }
        });
    }


    // Constant digunakan untuk mmengidentifikasi req permission
    // dari method onRequestPermissionResult()
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private void getLocation(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,
                    REQUEST_LOCATION_PERMISSION );
        } else {
//            Log.d("GETPERMISSION", "getLocation : permission granted");

            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
//                                get lang long

                                mLastLocation = location;
                                mLocationTextView. setText(
                                        getString(R.string.location_text,
                                                mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude(),
                                                mLastLocation.getTime())
                                );

                            } else {
                                mLocationTextView.setText("Lokasi tidak tersedia");
                            }
                        }
                    }
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                // jika permission diijinkan, getLocation()
                // jika tidak, tampilkan toast
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                } else {
                    Toast.makeText(this, "tidak dapat permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



}
