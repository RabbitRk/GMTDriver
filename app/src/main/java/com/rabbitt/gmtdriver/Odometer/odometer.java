package com.rabbitt.gmtdriver.Odometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

public class odometer extends Service{

    private LocationListener listener;
    private LocationManager locManager;
    private static double distanceInMeters;
    private static Location lastLocation = null;
    public static final String PERMISSION_STRING = android.Manifest.permission.ACCESS_FINE_LOCATION;

    //  create a binder object to bind the service the activity
    private final IBinder binder = new OdometerBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class OdometerBinder extends Binder {
        public odometer getOdometer() {
            return odometer.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

//API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        locManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
            String provider = locManager.getBestProvider(criteria, true);
            if (provider != null) {
                locManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (lastLocation == null) {
                            lastLocation = location;
                        }
                        distanceInMeters += location.distanceTo(lastLocation);
                        lastLocation = location;
                        Toast.makeText(odometer.this, "Iam service location change "+distanceInMeters, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String arg0) {
                    }

                    @Override
                    public void onProviderEnabled(String arg0) {
                    }

                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
                    }
                });
            }
        }
    }
    public double getDistance(){
//        double distMiles=this.distanceInMeters / 1609.344;
//        double distKm=distMiles*0.0006213711;
//        Toast.makeText(getApplicationContext(),""+distKm,Toast.LENGTH_SHORT).show();

//        convert the distance travelled to miles and return it
//        Toast.makeText(this, "Distance..."+ distanceInMeters/1000, Toast.LENGTH_SHORT).show();
        return distanceInMeters/1000;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager != null && listener != null) {
            if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                    == PackageManager.PERMISSION_GRANTED) {
                locManager.removeUpdates(listener);
            }
            locManager = null;
            listener = null;
        }
    }
}
