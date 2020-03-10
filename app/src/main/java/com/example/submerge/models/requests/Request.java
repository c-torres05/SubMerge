package com.example.submerge.models.requests;

import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;

public abstract class Request {
    public static final int UNKOWN_TYPE = 0;
    public static final int USER_TYPE = 1;
    public static final int SUBSCRIPTION_TYPE = 2;
    //User
    //Type (User | Subscription)
    //Values JsonObject
    protected User user;
    protected Subscription subscription;
    protected int type;

    public Request(User user, Subscription subscription) {
        this.user = user;
        this.subscription = subscription;
        this.type = UNKOWN_TYPE;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public int getType() {
        return type;
    }
}
