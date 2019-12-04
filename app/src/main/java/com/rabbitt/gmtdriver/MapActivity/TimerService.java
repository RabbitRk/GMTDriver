package com.rabbitt.gmtdriver.MapActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerService extends Service {
    public static String str_receiver = "com.countdowntimerservice.receiver";

    private Handler mHandler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    int min = 0, sec = 0, hrs = 0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(str_receiver);
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
//                    calendar = Calendar.getInstance();
//                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//                    strDate = simpleDateFormat.format(calendar.getTime());
//                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();
                }

            });
        }

    }

    public void twoDatesBetweenTime() {

        try
        {
//            long int_timer = TimeUnit.HOURS.toMillis(int_hours);
//            long long_hours = int_timer - diff;
//            long diffSeconds2 = long_hours / 1000 % 60;
//            long diffMinutes2 = long_hours / (60 * 1000) % 60;
//            long diffHours2 = long_hours / (60 * 60 * 1000) % 24;

            sec++;
            if (sec>59)
            {
                min++;
                sec = 0;
                if (min > 59)
                {
                    hrs++;
                    min = 0;
                }
            }

            @SuppressLint("DefaultLocale") String str_testing = String.format("%02d:%02d:%02d",hrs,min,sec);

            Log.e("TIME", str_testing);

            fn_update(str_testing);
//            if (long_hours > 0) {
//
//            } else {
//                mEditor.putBoolean("finish", true).commit();
//                mTimer.cancel();
//            }
        } catch (Exception e) {
            mTimer.cancel();
            mTimer.purge();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service finish", "Finish");
    }

    private void fn_update(String str_time) {

        intent.putExtra("time", str_time);
        sendBroadcast(intent);
    }
}