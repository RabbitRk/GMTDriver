package com.rabbitt.gmtdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rabbitt.gmtdriver.Preferences.prefsManager;
import com.rabbitt.gmtdriver.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {


    private static final String LOG_TAG = "LoginActivity";
    EditText password, phone_number;
    String passTxt, phoneTxt, token;
    String PuserTxt, PemailTxt, getId;
    Button lb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        password = findViewById(R.id.confirm_pass);
        phone_number = findViewById(R.id.phonenumber);

        lb = findViewById(R.id.loading_btn);
        lb.setTypeface(Typeface.SERIF);

        //getting token
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
    }

    public void login(View view) {
        passTxt = password.getText().toString().trim();
        phoneTxt = phone_number.getText().toString().trim();

        if (TextUtils.isEmpty(phoneTxt)) {
            phone_number.setError("Enter the phone number");
            phone_number.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passTxt)) {
            password.setError("Enter the password");
            password.requestFocus();
            return;
        }

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Getting Details", "Please wait...", false, false);

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "Responce.............." + response);

                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject jb = arr.getJSONObject(0);
                            getId = jb.getString("id");
                            PuserTxt = jb.getString("name");
                            PemailTxt = jb.getString("email");

                            setPrefsdetails(getId, PuserTxt, PemailTxt);

                        } catch (JSONException e) {
                            Log.i(LOG_TAG, "Json error.............." + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("passWord", passTxt);
                params.put("phoneNumber", phoneTxt);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void loginto() {
        Intent mainA = new Intent(getApplicationContext(), MainActivity.class);
        mainA.putExtra("UserId", getId);
        startActivity(mainA);
        finish();
        Log.i(LOG_TAG, "Hid.............." + getId);
    }

    private void setPrefsdetails(String getId, String puserTxt, String pemailTxt) {
        prefsManager prefsManager = new prefsManager(this);
        prefsManager.userPreferences(getId, puserTxt, pemailTxt);
        Log.i(LOG_TAG, "set preference Hid.............." + this.getId);

        updateTokenServer(getId);
    }

    private void updateTokenServer(final String getId) {
        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Updating your details", "Please wait...", false, false);

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TOKEN_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "Responce.............." + response);
                        if (response.equals("success"))
                        {
                            Toast.makeText(LoginActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            loginto();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("token", token);
                params.put("userId", getId);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}