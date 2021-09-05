package com.sistemabsensi.online;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.sistemabsensi.Constants;
import com.sistemabsensi.online.services.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener{
    String latweb,longweb;
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private TextView txtlat, txtlong;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    ArrayList<HashMap<String, String>> list_data;

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "nip";
    public final static String TAG_NAMA = "nama";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1 ;

    String tag_json_obj = "json_obj_req";
    TextView headertext;
    SharedPreferences sharedpreferences;
    Boolean session = false;
    String nip,nama, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private static final int REQUEST_MULTIPLE_PERMISSIONS = 117;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        startService(new Intent(MainActivity.this, LocationService.class));
        Toast.makeText(MainActivity.this ,"Mendapatkan Lokasi User",Toast.LENGTH_SHORT).show();
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        SharedPreferences sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        nama = sharedpreferences.getString(TAG_NAMA, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.selamat);
        TextView text2 = (TextView) header.findViewById(R.id.userheader);
        text.setText(username);
        txtlat = (TextView)findViewById(R.id.txtlatitude);
        txtlong = (TextView)findViewById(R.id.txtlong);
        statusCheck();

        boolean perms = checkPermissions();
        if (perms) {


            if (savedInstanceState != null) {

            }
        }
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading Sedang Mengambil Data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        String JSON_URL = "https://api.absensionlinediskominfotik.com/lokasi.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    final JSONArray array = new JSONArray(object.getString("lokasi"));
                    for (int i = 0; i < array.length(); i++) {


                      txtlat.setText(array.getJSONObject(i).get("latitude").toString());

                        txtlong.setText(array.getJSONObject(i).get("longitude").toString());



                    }

                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "KONEKSI INTERNET BERMASALAH", Toast.LENGTH_LONG);
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "Some error occurred", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this.getApplicationContext());
        requestQueue.add(stringRequest);
        txtlong.setVisibility(View.INVISIBLE);
        txtlat.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();





        if (id == R.id.nav_absensi) {
            SharedPreferences sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
            session = sharedpreferences.getBoolean(session_status, false);
            nama = sharedpreferences.getString(TAG_NAMA, null);
            username = sharedpreferences.getString(TAG_USERNAME, null);
            String latweb = txtlat.getText().toString();
            String longweb = txtlong.getText().toString();
            if (session) {


                Intent intent = new Intent(MainActivity.this, Absensi.class);
                intent.putExtra(TAG_NAMA, nama);
                intent.putExtra(TAG_USERNAME, nip);
                intent.putExtra(KEY_LAT, latweb);
                intent.putExtra(KEY_LONG, longweb);
                finish();
                startActivity(intent);
            }
        } else if (id == R.id.riwayat) {
            Intent intent1 = new Intent(MainActivity.this ,ActivityRiwayat.class);
            startActivity(intent1);





        } else if (id == R.id.nav_akun) {
            Intent intent1 = new Intent(MainActivity.this ,Activity_password.class);
            startActivity(intent1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS Tidak diaktifkan , ingin mengaktifkan sekarang ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        statusCheck();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
    private void loadData() {

    }
}

