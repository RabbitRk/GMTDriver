package com.rabbitt.gmtdriver;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class InternetBroadcast extends BroadcastReceiver {

    public boolean isConnected = false;
    String LOG_TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.v(LOG_TAG, "Receieved notification about network status");
        final boolean status = isNetworkAvailable(context);

        if (!status) {

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.activity_network_dialog);
            dialog.setTitle("No Internet Connection...");
            dialog.setCancelable(false);

            Button dialogButton = dialog.findViewById(R.id.ok_button);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable(context)) {
                        dialog.dismiss();
                    }
                }
            });

            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }

        }
    }


    private boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}

