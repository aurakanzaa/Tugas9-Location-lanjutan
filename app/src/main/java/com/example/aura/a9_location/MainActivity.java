package com.example.aura.a9_location;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements DapatkanAlamatTask.onTaskSelesai{

    // btn location
    public Button btnLoc;

    // variable dg tipe Location
    private Location mLastLocation;

    // obj location callback
    private LocationCallback mLocationCallback;

    // var dg tipe FusedLocationProviderClient
    private FusedLocationProviderClient mFusedLocationClient;
    public TextView mLocationTextView;

    // Constant digunakan untuk mmengidentifikasi req permission
    // dari method onRequestPermissionResult()
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;

    private boolean mTrackingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationTextView = (TextView) findViewById(R.id.textMap);
        btnLoc = (Button) findViewById(R.id.btnLocation);
        mAndroidImageView = (ImageView) findViewById(R.id.imgMap);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);

                // jika tracking aktif, proses reverse geocode manjadi data alamat
                if(mTrackingLocation){
                    new DapatkanAlamatTask(MainActivity.this, MainActivity.this)
                            .execute(locationResult.getLastLocation());
                }
            }
        };

        // Animasi
        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate );
        mRotateAnim.setTarget(mAndroidImageView);

        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getLocation();
//                mulaiTrackingLokasi();
                if (!mTrackingLocation){
                    mulaiTrackingLokasi();
                } else {
                    stopTrackingLokasi();
                }
            }
        });
    }

//    private void getLocation(){
//        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,
//                    REQUEST_LOCATION_PERMISSION );
//        } else {
////            Log.d("GETPERMISSION", "getLocation : permission granted");
//            mFusedLocationClient.getLastLocation().addOnSuccessListener(
//                    new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if(location != null){
////                                get lang long
//
////                                mLastLocation = location;
////                                mLocationTextView. setText(
////                                        getString(R.string.location_text,
////                                                mLastLocation.getLatitude(),
////                                                mLastLocation.getLongitude(),
////                                                mLastLocation.getTime())
////                                );
//                                new DapatkanAlamatTask(MainActivity.this, MainActivity.this).execute(location);
//                            } else {
//                                mLocationTextView.setText("Lokasi tidak tersedia");
//                            }
//                        }
//                    }
//            );
//        }
//        mLocationTextView.setText(getString(R.string.alamat_text, "sedang mencari alamat",
//                System.currentTimeMillis()));
//
//    }




    private void mulaiTrackingLokasi(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,
                    REQUEST_LOCATION_PERMISSION );
        } else {
//            Log.d("GETPERMISSION", "getLocation : permission granted");

            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback,null );


            mLocationTextView.setText(getString(R.string.alamat_text, "sedang mencari alamat",
                    System.currentTimeMillis()));
            mTrackingLocation = true;
            btnLoc.setText("Stop Tracking Lokasi");
            mRotateAnim.start();
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
//                    getLocation();
                    mulaiTrackingLokasi();
                } else {
                    Toast.makeText(this, "tidak dapat permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        // update UI dengan tampilan hasil alamat
//        mLocationTextView.setText(getString(R.string.alamat_text, result, System.currentTimeMillis()));


//        untuk mengecek mTrackingLocatin aktif atau tidak
        if(mTrackingLocation){
            mLocationTextView.setText(getString(R.string.alamat_text, result, System.currentTimeMillis()));
        }
    }

    private void stopTrackingLokasi(){
        if(mTrackingLocation){
            mTrackingLocation = false;

            // menghapus request update lokasi
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);

            btnLoc.setText("Mulai Tracking Lokasi");
            mLocationTextView.setText("Tracking sedang dihentikan");
            mRotateAnim.end();
        }

    }

    // digunakan untuk menentukan frekuensi req dan tingkat akurasi dari update lokasi
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();

        // digunakan untuk seberapa sering update lokasi yg diinginkan
        locationRequest.setInterval(10000);

        // adalah seberapa sering update lokasi dari app lain yg meminta req lokasi
        locationRequest.setFastestInterval(5000);

        // parameter untuk memilih akurasi dan akurasi tinggi menggunakan GPS
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}
