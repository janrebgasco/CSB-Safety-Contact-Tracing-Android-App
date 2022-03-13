package com.ucc.csbsafety.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public PreferenceUtils(){

    }
    public static boolean isFirstTimeUser(boolean token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(Constants.FTU, token);
        prefsEditor.apply();
        return true;
    }

    public static boolean getFTU(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(Constants.FTU, true);
    }
    public static void removeToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(Constants.FTU);
        prefsEditor.apply();
    }
    public static boolean saveUserStatusCheckDate(String token, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.savedDate, token);
        prefsEditor.apply();
        return true;
    }

    public static String getUserStatusCheckDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.savedDate, null);
    }
    public static void removeUserStatusCheckDate(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(Constants.savedDate);
        prefsEditor.apply();
    }


}
