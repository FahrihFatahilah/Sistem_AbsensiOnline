package com.sistemabsensi.online;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.sistemabsensi.online.app.AppController;
import com.sistemabsensi.online.connection.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Absensi extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mLocationClient;
    private Marker mCurrLocationMarker;
    private Location mLastLocation;
    public double latitude;
    Intent intent;
    public double longitude;
    private Button absenmasuk;
    String nip, nama;
    private ImageButton refresh;
    private Button absenspulang;
    ConnectivityManager conMgr;
    String latitude1, longtitude1;
    private String url = Server.URL + "insert.php";
    public final static String TAG_NIPLOGIN = "nip";
    public final static String TAG_NAMA = "nama";
    SharedPreferences sharedpreferences;
    TextView nomor;
    String status;
    private String latweb,longweb;
    String statusabsensi;
    ProgressDialog pDialog;
    private AppBarConfiguration mAppBarConfiguration;
    String tag_json_obj = "json_obj_req";
    int success;
    private String KEY_JARAK = "NAMA";
    private TextView nama1;
    private static final String TAG = Absensi.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    LatLng pegawai = new LatLng(latitude, longitude);
    LatLng kantor = new LatLng(-8.458811, 118.7549641);
    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
    int s = Integer.parseInt(sdf.format(new Date()));
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private static final int REQUEST_MULTIPLE_PERMISSIONS = 117;
    Double latit, longit;
    String lat, longt;
    double distance;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_absensi);

        TextView jarak = findViewById(R.id.jarak);

        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        statusCheck();
        boolean perms = checkPermissions();
        if (perms) {


            if (savedInstanceState != null) {

            }
        }
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
        new DrawerBuilder().withActivity(this).build();

        TextView textView = findViewById(R.id.waktu);

        textView.setText(currentTime);
        nama = sharedpreferences.getString(TAG_NAMA, null);
        absenspulang = findViewById(R.id.absenspulang);
        absenmasuk = findViewById(R.id.absenmasuk);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
        absenmasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusabsensi = "Masuk";
                TextView jarak = findViewById(R.id.jarak);

                if (s <= 73000) {
                    status = "Tepat Waktu";


                } else if (s >= 73100 && s <= 80000) {
                    status = "Terlambat";


                } else if (s >= 80100) {
                    status = "Terlambat";
                    

                } else {
                    status = "Terlambat";


                }

                String nip = sharedpreferences.getString(TAG_NIPLOGIN, null);
                ;
                String keterangan = status;
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String lat = String.valueOf(latitude);
                String longt = String.valueOf(longitude);


                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    cekAbsensi(nip, keterangan, lat, longt, statusabsensi);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

        });
        absenspulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusabsensi = "Pulang";
                TextView jarak = findViewById(R.id.jarak);

                if (s >= 73000) {
                    status = "Tepat Waktu";


                } else if (s <= 80000) {
                    status = "Terlambat";


                } else if (s >= 80100) {
                    status = "telat";


                } else {
                    status = "Telat";


                }

                String nip = sharedpreferences.getString(TAG_NIPLOGIN, null);
                ;
                String keterangan = status;
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String lat = String.valueOf(latitude);
                String longt = String.valueOf(longitude);


                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    cekAbsensi(nip, keterangan, lat, longt, statusabsensi);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

        });


        textView.setText(currentTime);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        nama = sharedpreferences.getString(TAG_NAMA, null);
        nip = sharedpreferences.getString(TAG_NIPLOGIN, null);


        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
      /*  Location loc1 = new Location("");
        loc1.setLatitude(-8.4545115);
        loc1.setLongitude(118.7286805);

        Location loc2 = new Location("");
        loc2.setLatitude(latitude);
        loc2.setLongitude(longitude);
        float[] results = new float[1];
        Location.distanceBetween(loc1.getLatitude(),loc1.getLongitude(),loc2.getLatitude(),loc2.getLongitude(),results);
        float jarak1 =results[0];
        float distanceInMeters = loc1.distanceTo(loc2);*/


        // Memuat SupportMapFragment dan memberi notifikasi saat telah siap.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void cekAbsensi(final String nip, final String keterangan, final String lat, final String longt, final String statusabsensi) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memasukan Data Absensi");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            Log.e(TAG, "Respon absensi: " + response.toString());
            hideDialog();

            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);

                // Check for error node in json
                if (success == 1) {

                    Log.e("Berhasil Absensi", jObj.toString());

                    Toast.makeText(getApplicationContext(),
                            jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    /*txtNama.setText("");
                    txtUsername.setText("");
                    txtPassword.setText("");
                    txtRepassword.setText("");
                    txtNama.setText("");*/


                } else {
                    Toast.makeText(getApplicationContext(),
                            jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url

                Map<String, String> params = new HashMap<String, String>();
                Log.e("Error", keterangan);

                params.put("nip", nip);
                params.put("keterangan", keterangan);

                params.put("lat", lat);
                params.put("long", longt);
                params.put("statusabsensi", statusabsensi);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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

        Bundle extras = getIntent().getExtras();
        latweb = extras.getString(KEY_LAT);
        longweb = extras.getString(KEY_LONG);

        mMap = googleMap;
        LatLng user = new LatLng(latitude,longitude);
        LatLng diskominfo = new LatLng(Double.valueOf(latweb), Double.valueOf(longweb));
        mMap.addMarker(new MarkerOptions().position(user).title("user"));
        mMap.addMarker(new MarkerOptions().position(diskominfo).title("Diskominfo")
                // below line is use to add custom marker on our map.
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_marker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(diskominfo));
        //Memulai Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);


            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(Absensi.this ,"Broadcast receinved", Toast.LENGTH_SHORT).show();

            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));



        }
    };
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

       } catch (Exception e) {
           e.printStackTrace();
       }
        /*@SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)*/;
        try {


        double latit = mLastLocation.getLatitude();
        double longit = mLastLocation.getLongitude();
            Bundle extras = getIntent().getExtras();
            latweb = extras.getString(KEY_LAT);
            longweb = extras.getString(KEY_LONG);
            LatLng diskominfo = new LatLng(-8.4604926, 118.7560354);
        LatLng kantor = new LatLng(Double.valueOf(latweb),Double.valueOf(longweb));
        double kantorlat = Double.valueOf(latweb);
        double kantorlong = Double.valueOf(longweb);
        LatLng User = new LatLng(latit,longit);
      /* distance=SphericalUtil.computeDistanceBetween(User,kantor);*/
            double earthRadius = 3958.75;
            double dLat = Math.toRadians(kantorlat-latit);
            double dLng = Math.toRadians(kantorlong-longit);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(latit)) * Math.cos(Math.toRadians(kantorlat)) *
                            Math.sin(dLng/2) * Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = earthRadius * c;
            int meterConversion = 1609;
            double myjr=dist * meterConversion;
           Math.floor(myjr/1000);

            TextView jarak = findViewById(R.id.jarak);

            jarak.setText("Jarak Dari Kantor "+Math.round(myjr)+ " Meter");
        Log.d(TAG,"Latitude" + latitude  +"Longitude" + longitude);
            if (Math.round(myjr) < 10 ) {
                absenmasuk.setEnabled(true);
                absenspulang.setEnabled(true);
                Log.d("ADebugTag", "Value: " + distance);
                Log.d(TAG, "COba Jarak" + Math.round(distance));
            } else{
                ShowDialogLokasi();
                absenmasuk.setEnabled(false);
                absenspulang.setEnabled(false);
            }




        } finally {
            Log.d(TAG,"Jarak Anda " +distance);
            if (Math.round(distance) < 10 ) {
                absenmasuk.setEnabled(true);

                Log.d("ADebugTag", "Value: " + distance);
                Log.d(TAG, "COba Jarak" + Math.round(distance));
            }
            else {
                absenmasuk.setEnabled(false);

            }

            if (Math.round(distance) > 10 ) {
                absenspulang.setEnabled(false);
                Log.d("ADebugTag", "Value: " + String.valueOf(distance));
                Log.d(TAG, "COba Jarak" + String.valueOf(distance));
            }

        }

        /*float[] results  = new float[1];
        Location.distanceBetween( latit,longit,-8.4524368,118.7287879,results);
        float distance = results[0];
        int meter = (int) (distance/1000);
        TextView jarak = findViewById(R.id.jarak);

        jarak.setText("Jarak Dari Kantor :" + meter+ " Meter");*/



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void onBackPressed()
    {
        startActivity(new Intent(Absensi.this, MainActivity.class));
        super.onBackPressed();  // optional depending on your needs
    }
    @Override
    public void onLocationChanged(Location location) {
      /*  mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latLng.latitude, latLng.longitude)).zoom(16).build();


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latit = location1.getLatitude();
        double longit = location1.getLongitude();
        float[] results  = new float[1];
        Location.distanceBetween(latitude,longitude,-8.4604926,118.7560354,results);
        float distance = results[0];
        int meter = (int) (distance/10000);
        TextView jarak = findViewById(R.id.jarak);*/

       /* jarak.setText("Jarak Dari Kantor :" + meter+ " Meter");
        Log.d(TAG,"Latitude" + String.valueOf(latitude)  +"Longitude" + String.valueOf(longitude));*/
        /*Location loc1 = new Location("");
        loc1.setLatitude(latitude);
        loc1.setLongitude(longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(-8.4545115);
        loc2.setLongitude(118.7286805);

        float distanceInMeters = loc1.distanceTo(loc2);
        TextView jarak = findViewById(R.id.jarak);
        jarak.setText("Jarak Dari Kantor :" + String.valueOf(distanceInMeters) + "Meter");

        Toast.makeText(this, "Location Changed", Toast.LENGTH_LONG).show();*/
        //menghentikan pembaruan lokasi
      /*  if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }*/
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Izin diberikan.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Izin ditolak.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }


    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }
    private void ShowDialogLokasi(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Lokasi Tidak Sesuai")
                .setMessage("Lokasi berada diluar jangkauan absensi")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        startActivity(getIntent());
                    }
                });

        alert.show();
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    protected void onStart(){
        super.onStart();

    }
}