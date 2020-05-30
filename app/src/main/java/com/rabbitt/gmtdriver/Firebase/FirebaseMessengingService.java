package com.rabbitt.gmtdriver.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rabbitt.gmtdriver.DBHelper.dbHelper;
import com.rabbitt.gmtdriver.R;
import com.rabbitt.gmtdriver.RideAlert;
import com.rabbitt.gmtdriver.Utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class FirebaseMessengingService extends FirebaseMessagingService {

    String TAG = "rkfirebase";
    Bitmap bitmap;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static String NOTIFY_SHARED_PREFS= "SHARED_PREFS";
    public static String BOOK_ID = "BOOK_ID";
    public static String TYPE = "TYPE";
    public static String VEHICLE = "VEHICLE";
    public static String VEHICLE_ID = "VEHICLE_ID";
    public static String PICKUP = "PICKUP";
    public static String DROP = "DROP";
    public static String PACKAGE = "PACKAGE";
    public static String TIME = "TIME";
    public static String ORI_LAT = "ORI_LAT";
    public static String ORI_LNG = "ORI_LNG";
    public static String DEST_LAT = "DEST_LAT";
    public static String DEST_LNG = "DEST_LNG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.i("remote", "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e("remote", "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.i("remote", "Notification JSON " + json.toString());

        try
        {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String book_id = data.getString("book_id");
            String type = data.getString("type");
            String vehicle = data.getString("vehicle");
            String pickup = data.getString("pickup");
            String drop = data.getString("drop");
            String time = data.getString("time");
            String package_type = data.getString("package");
            String ori_lat = data.getString("ori_lat");
            String ori_lng = data.getString("ori_lng");
            String dest_lat = data.getString("dest_lat");
            String dest_lng = data.getString("dest_lng");
            String vehicle_id = data.getString("v_no");

            Log.i("remote", "title..." + book_id);
            Log.i("remote", "body1..." + type);

            //setting shared prefs
            sharedPreferences = getSharedPreferences(NOTIFY_SHARED_PREFS, MODE_PRIVATE);
            editor = sharedPreferences.edit();

            editor.putString(BOOK_ID, book_id);
            editor.putString(TYPE, type);
            editor.putString(VEHICLE, vehicle);
            editor.putString(VEHICLE_ID, vehicle_id);
            editor.putString(PICKUP, pickup);
            editor.putString(DROP, drop);
            editor.putString(PACKAGE, package_type);
            editor.putString(TIME, time);
            editor.putString(ORI_LAT, ori_lat);
            editor.putString(ORI_LNG, ori_lng);
            editor.putString(DEST_LAT, dest_lat);
            editor.putString(DEST_LNG, dest_lng);
            editor.apply();

            Intent i = new Intent(this, RideAlert.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Flash Sale");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);

            // assuming your main activity
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            notificationBuilder.setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_CALL)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.newlogo)
                    .setTicker("Hearty365")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle("GMT driver")
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentText(book_id + " " + type + " " + pickup + " " + drop + " " + time)
                    .setFullScreenIntent(pendingIntent, true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentInfo("Info");

            notificationManager.notify(/*notification id*/1, notificationBuilder.build());

//            dbHelper database;
//            database = new dbHelper(this);
//
//            database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "Pondy","04-12-2019");

        } catch (JSONException e) {//maluKanna15:-*02Kk
            Log.e("remote", "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e("remote", "Exception: " + e.getMessage());
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.apply();
        editor.commit();
    }
}