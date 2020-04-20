package com.example.submerge.interfaces;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

public class BigTextIntentService extends IntentService {

    private static final String TAG = "SubMerge-Notificaitons";

    public static final String ACTION_DISMISS = "com.example.submerge.interfaces.action.DISMISS";

    public BigTextIntentService() {
        super("SubMerge-Notifications");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent(): " + intent);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DISMISS.equals(action)) {
                handleActionDismiss();
            }
        }
    }

    /**
     * Handles action Dismiss in the provided background thread.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleActionDismiss() {
        Log.d(TAG, "Dismiss Notification");

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(NotificationHandler.NOTIFICATION_ID);
    }

}