package com.rabbitt.gmtdriver;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rabbitt.gmtdriver.CurrentRide.CurrentRide;
import com.rabbitt.gmtdriver.Earning.Earning;
import com.rabbitt.gmtdriver.PastRide.PastRide;
import com.rabbitt.gmtdriver.Preferences.prefsManager;
import com.rabbitt.gmtdriver.Utils.Config;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, CurrentRide.OnFragmentInteractionListener, Earning.OnFragmentInteractionListener, LocationListener {

    private static final String TAG = "MainRk";

    prefsManager prefsManager;
    String driver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting preferences
        prefsManager = new prefsManager(getApplicationContext());
        prefsManager.setFirstTimeLaunch(true);

        SharedPreferences sharedPreferences = this.getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");
        Log.i(TAG, "onCreate: Token value: "+token);
        init();

    }

    private void init() {
        loadFragment(new CurrentRide());
        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    public boolean loadFragment(final Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            Log.i(TAG, "Current thread: " + Thread.currentThread().getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new CurrentRide();
                break;
            case R.id.navigation_service:
                fragment = new Earning();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double user_lat = location.getLatitude();
        double user_lng = location.getLongitude();
        Log.i(TAG, "onLocationChanged: "+user_lat+user_lng);
        updateDriverLocation(user_lat, user_lng);
    }

    private void updateDriverLocation(final double user_lat, final double user_lng) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_LOC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: "+error.toString());
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("DRI_ID",driver_id);
                params.put("DRI_LAT", String.valueOf(user_lat));
                params.put("DRI_LNG", String.valueOf(user_lng));
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
}
