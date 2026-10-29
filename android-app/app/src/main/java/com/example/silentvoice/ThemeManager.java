package com.example.silentvoice;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeManager {

    private static final String PREF_NAME = "APP_THEME";
    private static final String KEY_DARK_MODE = "isDarkMode";

    public static boolean isDarkMode(Context context) {

        return context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_DARK_MODE, true);
    }

    public static void saveTheme(
            Context context,
            boolean isDarkMode
    ) {

        context
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK_MODE, isDarkMode)
                .apply();
    }
}