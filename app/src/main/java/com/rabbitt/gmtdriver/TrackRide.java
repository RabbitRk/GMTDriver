package com.rabbitt.gmtdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import com.rabbitt.gmtdriver.Odometer.OdometerService;

public class TrackRide extends AppCompatActivity {

    TextView txt;
    private double distanceTraveledValue;

    //track distance code starts here
    OdometerService odo;
    boolean bound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_ride);

        txt = findViewById(R.id.text);
        Intent intento = new Intent(this, OdometerService.class);
        bindService(intento, connection, Context.BIND_AUTO_CREATE);

        displayDistance();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            OdometerService.OdomterBinder odometerBinder = (OdometerService.OdomterBinder) binder;
            odo = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private void displayDistance() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double dist = 0.0;
                if (bound && odo != null) {
                    dist = odo.getDistance();
                }
                @SuppressLint("DefaultLocale") String distanceStr = String.format("%.3f Km", dist);
                txt.setText(distanceStr);
            }
        });
    }
}
