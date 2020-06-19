package com.example.akshiban.everything;

import android.content.Context;
import android.net.ConnectivityManager;

public class AppUtils {


    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
        }
        return false;

    }


    public static boolean walletCheck(Context context) {
        long now = System.currentTimeMillis();
        long stored = SharedPreferencesManager.getInstance(context).getWalletMetedataFetched();
        return Math.abs(stored - now) < 84000000;
    }


}
