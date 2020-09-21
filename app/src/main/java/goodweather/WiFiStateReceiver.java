package goodweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.goodweather.R;


public class WiFiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.d(MainActivity.TAG, "Wi-Fi: " + context.getString(R.string.connected));
        }
        if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            Log.d(MainActivity.TAG, "Wi-Fi: " + context.getString(R.string.disconnected));
        }
    }
}
