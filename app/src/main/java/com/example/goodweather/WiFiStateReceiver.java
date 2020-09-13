package com.example.goodweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;


public class WiFiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Toast.makeText(context, "Wi-Fi: " + context.getString(R.string.connected), Toast.LENGTH_LONG).show();
        }
        if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            Toast.makeText(context, "Wi-Fi: " + context.getString(R.string.disconnected), Toast.LENGTH_LONG).show();
        }
    }
}
