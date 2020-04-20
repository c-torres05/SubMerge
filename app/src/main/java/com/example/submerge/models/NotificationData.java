package com.example.submerge.models;

public class NotificationData {
    //Data
    private long time;
    // Standard notification values:
    private String mContentTitle;
    private String mContentText;
    private int mPriority;

    // Notification channel values (O and above):
    private String mChannelId;
    private CharSequence mChannelName;
    private String mChannelDescription;
    private int mChannelImportance;
    private boolean mChannelEnableVibrate;
    private int mChannelLockscreenVisibility;

    public NotificationData(long time, String mContentTitle, String mContentText, int mPriority) {
        this.time = time;
        this.mContentTitle = mContentTitle;
        this.mContentText = mContentText;
        this.mPriority = mPriority;
    }

    public NotificationData(long time, String mContentTitle, String mContentText, int mPriority, String mChannelId, CharSequence mChannelName, String mChannelDescription,
                            int mChannelImportance, boolean mChannelEnableVibrate, int mChannelLockscreenVisibility) {
        this.time = time;
        this.mContentTitle = mContentTitle;
        this.mContentText = mContentText;
        this.mPriority = mPriority;
        this.mChannelId = mChannelId;
        this.mChannelName = mChannelName;
        this.mChannelDescription = mChannelDescription;
        this.mChannelImportance = mChannelImportance;
        this.mChannelEnableVibrate = mChannelEnableVibrate;
        this.mChannelLockscreenVisibility = mChannelLockscreenVisibility;
    }


    // Notification Standard notification get methods:
    public String getContentTitle() {
        return mContentTitle;
    }

    public String getContentText() {
        return mContentText;
    }

    public int getPriority() {
        return mPriority;
    }

    // Channel values (O and above) get methods:
    public String getChannelId() {
        return mChannelId;
    }

    public CharSequence getChannelName() {
        return mChannelName;
    }

    public String getChannelDescription() {
        return mChannelDescription;
    }

    public int getChannelImportance() {
        return mChannelImportance;
    }

    public boolean isChannelEnableVibrate() {
        return mChannelEnableVibrate;
    }

    public int getChannelLockscreenVisibility() {
        return mChannelLockscreenVisibility;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
