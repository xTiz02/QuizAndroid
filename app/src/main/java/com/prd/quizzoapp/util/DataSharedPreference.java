package com.prd.quizzoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class DataSharedPreference {

    private static final String SHARED_PREFERENCE_NAME = "QuizzoApp";

    public static void saveData(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getData(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
