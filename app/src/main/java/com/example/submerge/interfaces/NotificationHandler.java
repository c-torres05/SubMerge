package com.example.submerge.interfaces;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.submerge.R;
import com.example.submerge.models.NotificationData;
import com.example.submerge.models.Subscription;

import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NotificationHandler{
    private static final int NOTIFICATION_CHANNEL_ID = 888;
    public static final int NOTIFICATION_ID = 887;
    public static final String CHANNEL_ID = "SubMergeNotifications";
    private static final String CHANNEL_NAME = "Subscription Deadlines";
    private static final String CHANNEL_DESCRIPTION = "Recieve notifications about Subscription and Trial expiration and due dates!";
    private static final int CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;
    private static final boolean CHANNEL_VIBRATION = true;
    private static final int CHANNEL_VISIBILITY = NotificationCompat.VISIBILITY_PRIVATE;

    NotificationManagerCompat mNotificationManagerCompat;
    NotificationCompat.Builder notificationCompatBuilder;

    private int seconds = 1000;
    private int minutes = seconds * 60;
    private int hours = minutes * minutes;
    private int days = hours * 24;
    private int weeks = days * 7;



    class DueDate {
        private long time; //Time in seconds
        private String amount;
        private String denomination;

        public DueDate() {}

        public DueDate (String amount, String denomination) {
            this.amount = amount;
            this.denomination = denomination;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDenomination() {
            return denomination;
        }

        public void setDenomination(String denomination) {
            this.denomination = denomination;
        }
    }
    public DueDate getTimeDifference(Date renewal, Date current) {
        long diff = renewal.getTime() - current.getTime();
        DueDate date = new DueDate();
        date.setTime(diff / minutes);

        if (diff / weeks < 1) {
            if (diff / days < 1) {
                if (diff / hours < 1) {
                    date.setAmount("under an hour");
                    date.setDenomination("");
                } else {
                    date.setAmount(Long.toString(diff / hours));
                    date.setDenomination("hours");
                }
            } else {
                date.setAmount(Long.toString(diff / days));
                date.setDenomination("days");
            }
        } else {
            date.setAmount(Long.toString(diff / weeks));
            date.setDenomination("weeks");
        }
        return date;
    }

    private NotificationData createNotification(Subscription subscription) {
        DueDate whenDue = getTimeDifference(new Date(subscription.accessRenewal()), new Date());

        String notificationName = subscription.getTitle() + " due in " + whenDue.getAmount() + " " + whenDue.getDenomination();
        String notificationDescription = "Your " + subscription.getTitle() + " subscription is due in " + whenDue.getAmount() + " " + whenDue.getDenomination() + "."
                + "You will be charged " + subscription.getCost() + ".";

        return new NotificationData(whenDue.getTime(), notificationName, notificationDescription,
                CHANNEL_IMPORTANCE, CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION,
                CHANNEL_IMPORTANCE, CHANNEL_VIBRATION, CHANNEL_VISIBILITY);
    }

    public void sendNotification(Subscription subscription) {
        NotificationData notificationData = createNotification(subscription);

//        String notificationChannelId = createNotificationChannel(this, notificationData);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                // Overrides ContentText in the big form of the template.
                .bigText(notificationData.getChannelDescription())
                // Overrides ContentTitle in the big form of the template.
                .setBigContentTitle(notificationData.getContentTitle());

//        Intent notifyIntent = new Intent(this, MainInterface.class);
////        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
////        stackBuilder.addParentStack(MainInterface.class);
////        stackBuilder.addNextIntent(notifyIntent);
//        PendingIntent mainPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        notifyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
////         Dismiss Action.
//        Intent dismissIntent = new Intent(this, BigTextIntentService.class);
//        dismissIntent.setAction(BigTextIntentService.ACTION_DISMISS);
//
//        PendingIntent dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0);
//        NotificationCompat.Action dismissAction =
//                new NotificationCompat.Action.Builder(
//                        R.drawable.netflix,
//                        "Dismiss",
//                        dismissPendingIntent)
//                        .build();

//        NotificationManagerCompat mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(
//                getApplicationContext(), NotificationHandler.CHANNEL_ID);
//        Intent intent = new Intent(this, MainInterface.class);


        Notification notification = notificationCompatBuilder
                // BIG_TEXT_STYLE sets title and content for API 16 (4.1 and after).
                .setStyle(bigTextStyle)
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(notificationData.getContentTitle())
                // Content for API <24 (7.0 and below) devices.
                .setContentText(notificationData.getContentText())
                .setSmallIcon(R.drawable.icon)
//                .setContentIntent(mainPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // Set primary color (important for Wear 2.0 Notifications).
//                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))

                // SIDE NOTE: Auto-bundling is enabled for 4 or more notifications on API 24+ (N+)
                // devices and all Wear devices. If you have more than one notification and
                // you prefer a different summary notification, set a group key and create a
                // summary notification via
                // .setGroupSummary(true)
                // .setGroup(GROUP_KEY_YOUR_NAME_HERE)

                .setCategory(Notification.CATEGORY_REMINDER)

                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(notificationData.getPriority())

                // Sets lock-screen visibility for 25 and below. For 26 and above, lock screen
                // visibility is set in the NotificationChannel.
                .setVisibility(notificationData.getChannelLockscreenVisibility())

                // Adds additional actions specified above.
//                .addAction(dismissAction)

                .build();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mNotificationManagerCompat.notify(NOTIFICATION_ID, notification);
            }
        }, notificationData.getTime());
    }

    public static String createNotificationChannel(Context context, NotificationData data) {

        // NotificationChannels are required for Notifications on O (API 26) and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // The id of the channel.
            String channelId = data.getChannelId();

            // The user-visible name of the channel.
            CharSequence channelName = data.getChannelName();
            // The user-visible description of the channel.
            String channelDescription = data.getChannelDescription();
            int channelImportance = data.getChannelImportance();
            boolean channelEnableVibrate = data.isChannelEnableVibrate();
            int channelLockscreenVisibility = data.getChannelLockscreenVisibility();

            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            // Adds NotificationChannel to system. Attempting to create an existing notification
            // channel with its original values performs no operation, so it's safe to perform the
            // below sequence.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            // Returns null for pre-O (26) devices.
            return null;
        }
    }
}
