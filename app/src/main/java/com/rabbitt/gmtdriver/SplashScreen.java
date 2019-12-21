package com.rabbitt.gmtdriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.rabbitt.gmtdriver.Preferences.prefsManager;


public class SplashScreen extends AppCompatActivity {

    public static final String LOG_TAG = "MainRk";

    private Handler splash = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    prefsManager prefsManager = new prefsManager(getApplicationContext());
                    if (!prefsManager.isFirstTimeLaunch()) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception ex) {
                    Log.i(LOG_TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }, 2000);

    }

}
