package com.rabbitt.gmtdriver.MapActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rabbitt.gmtdriver.CurrentRide.CurrentRide;
import com.rabbitt.gmtdriver.MainActivity;
import com.rabbitt.gmtdriver.MapAnimator.DataParser;
import com.rabbitt.gmtdriver.MapAnimator.MapAnimator;
import com.rabbitt.gmtdriver.Odometer.OdometerService;
import com.rabbitt.gmtdriver.R;
import com.rabbitt.gmtdriver.RideAlert;
import com.rabbitt.gmtdriver.Utils.Config;
import com.rabbitt.gmtdriver.VolleySingleton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRideMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //global variables goes on
    final static int REQUEST_LOCATION = 199;
    private static final String MY_API_KEY = "AIzaSyDWbQJxOw1BHAqQqUWL2DekIsYar5vUzxc";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "RkMap";

    GoogleApiClient mGoogleApiClient;

    //map utils
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    MarkerOptions options = new MarkerOptions();
    private Marker oriMarker, destMarker, userMarker;
    LocationRequest mLocationRequest;

    LatLng oriLatlng, desLatlng, userLatlng;

    String oriLati,oriLngi,desLati,desLngi;

    String book_id;

    String type_, package_, vehicle_;

    TextView distance;

    ProgressBar progressBar;

    //track distance code starts here
    OdometerService odo;
    boolean bound;
    //timer
//    private Button btn_start, btn_cancel;
    private TextView tv_timer;
//    EditText et_hours;

    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    LinearLayout after_layout;
    ProgressDialog p;

    Button cancel_btn, finish_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkLocationPermission();

        // Initializing
        distance = findViewById(R.id.dist);
        MarkerPoints = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_cyclic);
        cancel_btn = findViewById(R.id.decline);
        finish_btn = findViewById(R.id.finished);

        //distance tracking code
        Intent intento = new Intent(this, OdometerService.class);

        bindService(intento, connection, Context.BIND_AUTO_CREATE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        assert mapFragment != null;
        View mapView = mapFragment.getView();

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 100);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(10, 10, 10, 10);
        }
        mapFragment.getMapAsync(this);

        //get Current Location on app launch

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        turnOnGPS();

        //getting intent values
        Intent intent = getIntent();
        book_id = intent.getStringExtra("book_id");
        type_ = intent.getStringExtra("type");



        init();
    }

    private void init() {

        tv_timer = findViewById(R.id.timer);
        after_layout = findViewById(R.id.after_taken);
        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        p = new ProgressDialog(this);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p = new ProgressDialog(this);

        try {
            String str_value = mpref.getString("data", "");
            if (str_value.matches("")) {
                tv_timer.setText("");

            } else {

                if (mpref.getBoolean("finish", false)) {
                    tv_timer.setText("");
                } else {
                    tv_timer.setText(str_value);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "init: " + e.getMessage());
        }

        new getRideTask().execute();

    }
   ///////////////////////////////////////////////////////////////////////////////////////////

    public void startTimer() {
        Intent intent_service = new Intent(getApplicationContext(), TimerService.class);
        startService(intent_service);
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str_time = intent.getStringExtra("time");
            tv_timer.setText(str_time);
        }
    };

    public void stoptimer() {
        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        stopService(intent);
        mEditor.clear().commit();
    }

//////////////////////////////////////////////////////////////////////////////////////////////
    private void checkLocationPermission() {
        Log.i(TAG, "checklocationpermission");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is neededx
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            OdometerService.OdomterBinder odomterBinder = (OdometerService.OdomterBinder) service;
            odo = odomterBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    private void turnOnGPS() {
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();

        }
        if (!hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();

            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(GetRideMapActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(TAG, "onMapReady: " + Thread.currentThread().getName() + " ID:" + Thread.currentThread().getId());
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getRideTask extends AsyncTask<Void, Void, Void> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.show();
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected Void doInBackground(Void... params) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GETRIDE_DETAILS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    //Adding the parameters to the request
                    params.put("TYPE", type_);
                    params.put("BOOK_ID", book_id);
                    return params;
                }
            };

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            return null;

        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(Void result) {
            p.dismiss();
            typeFinding(oriLati, oriLngi, desLati, desLngi, type_);
            // Do things like hide the progress bar or change a TextView
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "connected");
        progressBar.setVisibility(View.GONE);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "locationchanged");
        //Place current location marker
        userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.i(TAG, "onLocationChanged: " + userLatlng);
    }

    public void getRide(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Did you pick up the customer ?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        after_layout.setVisibility(View.VISIBLE);
                        cancel_btn.setEnabled(false);
                        finish_btn.setEnabled(true);
                        displayDistance();
                        startTimer();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void decline(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    //user defined functions//--------------------------------------------------------------------------------------------
    private void outstationAnimator(final LatLng oriLatlng, final LatLng desLatlng) {
        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        oriMarker = mMap.addMarker(markerOptionsOri);

        MarkerOptions markerOptionsDes = new MarkerOptions();
        markerOptionsDes.position(desLatlng);
        markerOptionsDes.title("Destination point");
        markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        destMarker = mMap.addMarker(markerOptionsDes);

        //calling polyline
        animatePath(oriLatlng, desLatlng);
        //calling zoomfuntion
        zoomout(oriMarker, destMarker);
//         try {
//            new Thread() {
//                public void run() {
//                    //calling zoomfuntion
//                    zoomout(oriMarker, destMarker);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //calling polyline
//                            animatePath(oriLatlng, desLatlng);
//
//                        }
//                    });
//                }
//            }.start();
//        }
//        catch (Exception ex)
//        {
//            Log.i(TAG, "animatePath Catch: "+ex.getMessage());
//        }

    }

    private void cityAnimator(final LatLng oriLatlng, final LatLng desLatlng) {
        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        oriMarker = mMap.addMarker(markerOptionsOri);

        MarkerOptions markerOptionsDes = new MarkerOptions();
        markerOptionsDes.position(desLatlng);
        markerOptionsDes.title("Destination point");
        markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        destMarker = mMap.addMarker(markerOptionsDes);

        //calling polyline
        animatePath(oriLatlng, desLatlng);
        //calling zoomfuntion
        zoomout(oriMarker, destMarker);
//        try {
//            new Thread() {
//                public void run() {
//                    //calling zoomfuntion
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //calling polyline
//                            animatePath(oriLatlng, desLatlng);
//                            zoomout(oriMarker, destMarker);
//                        }
//                    });
//                }
//            }.start();
//        }
//        catch (Exception ex)
//        {
//            Log.i(TAG, "animatePath Catch: "+ex.getMessage());
//        }
    }

    public void rentalAnimator(LatLng oriLatlng) {

        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//        oriMarker = mMap.addMarker(markerOptionsOri);
        mMap.addMarker(markerOptionsOri);

        Log.i(TAG, "   " + oriLatlng.toString());

        //calling polyline
        animatePath(userLatlng, oriLatlng);
        //calling zoomfuntion
    }

    public void typeFinding(String oriLati, String oriLngi, String desLati, String desLngi, String type_) {
        //converting string to double
        double oriLat = Double.parseDouble(oriLati);
        double oriLng = Double.parseDouble(oriLngi);
        double desLat = Double.parseDouble(desLati);
        double desLng = Double.parseDouble(desLngi);

        //converting double to latlng
        oriLatlng = new LatLng(oriLat, oriLng);
        desLatlng = new LatLng(desLat, desLng);

        switch (type_) {
            case "RNT":
                rentalAnimator(oriLatlng);
                break;
            case "CTY":
                cityAnimator(oriLatlng, desLatlng);
                break;
            case "OST":
                outstationAnimator(oriLatlng, desLatlng);
                break;
            default:
                Toast.makeText(this, "Can't get the Travel type", Toast.LENGTH_SHORT).show();
        }
        //distance calculation
//        displayDistance();
    }

    private void displayDistance() {
        try {
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    double dist = 0.0;
                    if (odo != null) {
                        dist = odo.getDistance();
                    }
                    String distanceStr = String.format("%1$,.2f Km", dist);
                    distance.setText(distanceStr);
                    handler.postDelayed(this, 1000);
                }
            });
        } catch (Exception ex) {
            Log.i(TAG, "animatePath Catch: " + ex.getMessage());
        }

    }

    private void zoomout(Marker oriMarker, Marker destMarker) {
        Log.i(TAG, "zooomout");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //the include method will calculate the min and max bound.
        builder.include(oriMarker.getPosition());
        builder.include(destMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    public void animatePath(final LatLng origin, final LatLng dest) {
        Log.i(TAG, "iam animate path");

//        try {
//            new Thread() {
//                public void run() {
//                    Looper.prepare();
//                    String url = getUrl(origin, dest);
//                    Log.d("onMapClick", url);
//                    FetchUrl FetchUrl = new FetchUrl();
//
//                    // Start downloading json data from Google Directions API
//                    FetchUrl.execute(url);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
//                            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//                        }
//                    });
//                }
//            }.start();
//        }
//        catch (Exception ex)
//        {
//            Log.i(TAG, "animatePath Catch: "+ex.getMessage());
//        }


        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        Log.i(TAG, "getUrl: Parameteres: " + parameters);
//        Toast.makeText(MapsActivity.this, "parameters " + parameters, Toast.LENGTH_SHORT).show();
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + MY_API_KEY;

        Log.i(TAG, "getUrl: Url: " + url);
//        Toast.makeText(MapsActivity.this, "url " + url, Toast.LENGTH_SHORT).show();

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void CalculateBill() {
        //fetch value from the database

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RATE_CALCULATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i(TAG, "Responce.............." + response);

                        try {
                            if (response.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Status updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in status update", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Cant connect to the server", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("TYPE", type_);
                params.put("BOOK_ID", book_id);
                params.put("TIME", tv_timer.getText().toString());
                params.put("DISTANCE", distance.getText().toString());
                return params;
            }
        };

        //Adding request the the queue
//        requestQueue.add(stringRequest);

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected void onPreExecute() {
            p.show();
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    Toast.makeText(GetRideMapActivity.this, "latitude " + lat + "....longitude " + lng, Toast.LENGTH_SHORT).show();
//RkDk
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    Log.i(TAG, "Points....." + points.toString());
                }

                // Adding all the points in the route to LineOptions

                if (mMap != null) {
                    MapAnimator.getInstance().animateRoute(mMap, points);
                } else {
                    Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
                }

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
                p.dismiss();
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    public void finishRide(View view) {

        stoptimer();
        Intent billIntent = new Intent(this, BillActivity.class);
        startActivity(billIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(timeReceiver, new IntentFilter(TimerService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timeReceiver);
    }
}
