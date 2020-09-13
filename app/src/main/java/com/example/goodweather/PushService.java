package com.example.goodweather;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.goodweather.settings.Settings;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushService extends FirebaseMessagingService {
    private int messageId = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        if (title == null){
            title = "Push Message";
        }
        String text = remoteMessage.getNotification().getBody();
        // создать нотификацию
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());

    }

    @Override
    public void onNewToken(String s) {
        Settings.getInstance(getBaseContext()).setToken(s);
        Looper.prepare();
        Toast.makeText(getBaseContext(), getString(R.string.new_token_msg), Toast.LENGTH_LONG).show();
    }
}
