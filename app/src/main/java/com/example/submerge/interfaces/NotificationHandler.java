package com.example.submerge.interfaces;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.Calendar;
import java.util.Random;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

    public NotificationData createNotificationData(Subscription subscription) {
        DueDate whenDue = getTimeDifference(new Date(subscription.accessRenewal()), new Date());

        String notificationName = subscription.getTitle() + " due in " + whenDue.getAmount() + " " + whenDue.getDenomination();
        String notificationDescription = "Your " + subscription.getTitle() + " subscription is due in " + whenDue.getAmount() + " " + whenDue.getDenomination() + "."
                + "You will be charged " + subscription.getCost() + ".";


        return new NotificationData(whenDue.getTime(), notificationName, notificationDescription,
                CHANNEL_IMPORTANCE, CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION,
                CHANNEL_IMPORTANCE, CHANNEL_VIBRATION, CHANNEL_VISIBILITY);
    }

    public Notification createNotification(NotificationData notificationData) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(notificationData.getChannelDescription())
                .setBigContentTitle(notificationData.getContentTitle());
        Notification notification = notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(notificationData.getContentTitle())
                .setContentText(notificationData.getContentText())
                .setSmallIcon(R.drawable.icon)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setPriority(notificationData.getPriority())
                .setVisibility(notificationData.getChannelLockscreenVisibility())
                .build();
        return notification;
    }

    public void sendNotification(Notification notification, NotificationData notificationData) {
        Calendar now = Calendar.getInstance();
        Calendar when = Calendar.getInstance();
        when.add(Calendar.SECOND, 4);
        Handler handler = new Handler();
        Random rand = new Random();
        handler.postDelayed(() -> mNotificationManagerCompat.notify(NOTIFICATION_ID + rand.nextInt(51), notification), when.getTime().getTime() - now.getTime().getTime());
    }

    public static String createNotificationChannel(Context context, NotificationData data) {
        String channelId = data.getChannelId();
        CharSequence channelName = data.getChannelName();
        String channelDescription = data.getChannelDescription();
        int channelImportance = data.getChannelImportance();
        boolean channelEnableVibrate = data.isChannelEnableVibrate();
        int channelLockscreenVisibility = data.getChannelLockscreenVisibility();
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableVibration(channelEnableVibrate);
        notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        return channelId;
    }
}
