package com.dut2.memorise.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.dut2.memorise.R;


public final class ThemeLoader {

    public static void LoadTheme(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Load theme 0 => Light, 1 => Dark
        boolean theme = sharedPreferences.getBoolean("themedark", false);
        if(theme){
            activity.setTheme(R.style.NeumorphicDark);
        } else {
            activity.setTheme(R.style.NeumorphicLight);
        }
    }

    public static void ChangeTheme(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        boolean theme = sharedPreferences.getBoolean("themedark", false);
        SaveTheme(activity,!theme);
        LoadTheme(activity);
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.finish();
    }

    public static void SaveTheme(Activity activity, boolean themedark){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("themedark", themedark);
        editor.commit();
    }
}
