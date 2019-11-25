package com.rabbitt.gmtdriver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rabbitt.gmtdriver.DBHelper.dbHelper;
import com.rabbitt.gmtdriver.MapActivity.MapsActivity;

import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.BOOK_ID;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DEST_LAT;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DEST_LNG;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.DROP;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.ORI_LAT;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.ORI_LNG;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.PACKAGE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.PICKUP;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.TIME;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.TYPE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.VEHICLE;
import static com.rabbitt.gmtdriver.Firebase.FirebaseMessengingService.NOTIFY_SHARED_PREFS;

public class RideAlert extends AppCompatActivity {
    private static final String TAG = "RideAlert";
    //Global declaration
    dbHelper dbHelpar;
    Ringtone ringtone;
    SharedPreferences shrp;

    String book_id;
    String type;
    String vehicle;
    String pickup;
    String drop;
    String time;
    String package_type;
    String ori_lat;
    String ori_lng;
    String dest_lat;
    String dest_lng;
    Toolbar toolbar;

    TextView book_idTxt, typeTxt, vehicleTxt, package_idTxt, pickupTxt, dropTxt, timeTxt;

    public static final String oriLata = "orilat", oriLnga = "orilng";
    public static final String desLata = "deslat", desLnga = "deslng";

    public static final String typeI = "type";
    public static final String vehicleI = "vehicle";
    public static final String packageI = "package";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_alert);

        toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        init();

    }

    private void init() {
        dbHelpar =  new dbHelper(this);

        toolbar.setTitle("Ride");

        //Textview initialization
        book_idTxt = findViewById(R.id.book_id);
        typeTxt = findViewById(R.id.type);
        vehicleTxt = findViewById(R.id.vehcile);
        package_idTxt = findViewById(R.id.package_id);
        pickupTxt = findViewById(R.id.pickup);
        dropTxt = findViewById(R.id.drop);
        timeTxt = findViewById(R.id.timeat);

        //getting values from the sharedprefs
        shrp = getSharedPreferences(NOTIFY_SHARED_PREFS, MODE_PRIVATE);
        book_id = shrp.getString(BOOK_ID, "0");
        type = shrp.getString(TYPE, "0");
        vehicle = shrp.getString(VEHICLE, "0");
        pickup = shrp.getString(PICKUP, "0");
        drop = shrp.getString(DROP, "0");
        time = shrp.getString(TIME, "0");
        package_type = shrp.getString(PACKAGE, "0");
        ori_lat = shrp.getString(ORI_LAT, "0");
        ori_lng = shrp.getString(ORI_LNG, "0");
        dest_lat = shrp.getString(DEST_LAT, "0");
        dest_lng = shrp.getString(DEST_LNG, "0");

        //setting the info to the job alert page
        book_idTxt.setText(book_id);
        typeTxt.setText(type);
        vehicleTxt.setText(vehicle);
        pickupTxt.setText(pickup);
        dropTxt.setText(drop);
        timeTxt.setText(time);
        package_idTxt.setText(package_type);

        //visiblity controller
        if (type.equals("rental")) {
            dropTxt.setVisibility(View.GONE);
        }
        else {
            package_idTxt.setVisibility(View.GONE);
        }

        Log.i(TAG, book_id+" "+type+" "+vehicle+" "+pickup+" "+drop+" "+time);

        //ringing alarm to alert the driver
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

    }

    public void gotoNavigation(View view) {
        ringtone.stop();
//        dbHelpar.insertdata(book_id, time, type, vehicle, pickup, drop, package_type);

        //close the notification jon the notificaiton bar
        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
        catch (Exception ex){
            Log.i(TAG, "gotoNavigation: "+ex.getMessage());
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(oriLata, ori_lat);
        intent.putExtra(oriLnga, ori_lng);
        intent.putExtra(desLata, dest_lat);
        intent.putExtra(desLnga, dest_lng);
        intent.putExtra(typeI, type);
        intent.putExtra(vehicleI, vehicle);
        intent.putExtra(packageI, package_type);

        startActivity(intent);
    }

    public void stopAlarm(View view) {
        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
        catch (Exception ex){
            Log.i(TAG, "gotoNavigation: "+ex.getMessage());
        }
        ringtone.stop();
        SharedPreferences.Editor editor = shrp.edit();
        editor.clear();
        editor.apply();
        finish();
    }
}
