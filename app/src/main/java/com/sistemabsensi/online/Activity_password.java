package com.sistemabsensi.online;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sistemabsensi.online.connection.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class  Activity_password extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String nip,nama, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "nip";
    public final static String TAG_NAMA = "nama";
    EditText Edpassword,Edconfirm;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        SharedPreferences sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        nama = sharedpreferences.getString(TAG_NAMA, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);
        Edpassword = findViewById(R.id.password);
        Edconfirm = findViewById(R.id.passwordconfirm);
        session = sharedpreferences.getBoolean(session_status, false);
        nama = sharedpreferences.getString(TAG_NAMA, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);

        final String pass = Edpassword.getText().toString();
        final String confirm = Edconfirm.getText().toString();

        Toast.makeText(Activity_password.this, "https://dailyperpus.com/nitipagam/skripsi/api/update.php?nip="+username+"&pass="+pass, Toast.LENGTH_SHORT).show();







    }

    public void btn_updateData(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        nama = sharedpreferences.getString(TAG_NAMA, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);

        final String pass = Edpassword.getText().toString();
        final String confirm = Edconfirm.getText().toString();
        Log.d("User", pass+username);


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating....");
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, "https://api.absensionlinediskominfotik.com/update.php?nip="+username+"&pass="+pass,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(Activity_password.this, response, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Activity_password.class));
                            finish();
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(Activity_password.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }){

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<String,String>();

                    params.put("nip",username);
                    params.put("password",pass);


                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(Activity_password.this);
            requestQueue.add(request);











    }
}