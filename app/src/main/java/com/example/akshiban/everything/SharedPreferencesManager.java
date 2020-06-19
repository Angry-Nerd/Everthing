package com.example.akshiban.everything;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static SharedPreferencesManager instance;
    private static SharedPreferences preferences;

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager();
            preferences = context.getSharedPreferences("APP_SHARED_PREFS", Context.MODE_PRIVATE);
        }
        return instance;
    }

    public void setWalletMetedataFetched(long time) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(AppConstants.WALLET_METADATA_FETCHED, time);
        editor.apply();
    }

    public long getWalletMetedataFetched() {
        return preferences.getLong(AppConstants.WALLET_METADATA_FETCHED, 0);
    }

    public void setTransactionsFetched(String wid, boolean b) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppConstants.TRANSACTIONS_FETCHED, b);
        editor.apply();
    }

    public boolean getTransactionsFetched(String wid) {
        return preferences.getBoolean(AppConstants.TRANSACTIONS_FETCHED + wid, false);
    }

    public void setLoginEmail(String email) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstants.USER_EMAIL, email);
        editor.apply();
    }

    public String getLoginEmail() {
        return preferences.getString(AppConstants.USER_EMAIL, null);
    }


}
