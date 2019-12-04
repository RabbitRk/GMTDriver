package com.rabbitt.gmtdriver.Preferences;

/**
 * Created by Rabbitt on 07,February,2019
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class prefsManager {

    private SharedPreferences pref, userpref;
    private SharedPreferences.Editor editor, user_editor;

    // Shared preferences file name
    private static final String PREF_NAME = "USER_PREFS";
    private static final String LOGIN = "IsFirstTimeLaunch";

    //user details
    public static final String USER_PREFS = "USER_DETAILS";
    private static final String ID_KEY = "ID_KEY";
    private static final String TYPE = "USER_KEY";
    private static final String USER_PHONE = "USER_PHONE";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_NAME = "USER_NAME";

    private static final String LOG_STATUS = "LOG_STATUS";

    @SuppressLint("CommitPrefEdits")
    public prefsManager(Context context) {
        // shared pref mode
        int PRIVATE_MODE = 0;

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        userpref = context.getSharedPreferences(USER_PREFS, PRIVATE_MODE);
        user_editor = userpref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(LOGIN, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(LOGIN, false);
    }

    public void userPreferences(String id, String username, String phonenumber) {
        user_editor.putString(ID_KEY,id);
        user_editor.putString(USER_NAME,username);
        user_editor.putString(USER_PHONE,phonenumber);
        user_editor.commit();
    }

    public void status(String stat)
    {
        user_editor.putString(LOG_STATUS,stat);
        user_editor.commit();
    }

}
