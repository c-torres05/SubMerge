package com.example.submerge.models.requests;

import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;

public class SearchRequest extends com.example.submerge.models.requests.Request {
    public SearchRequest(User user, Subscription subscription) {
        super(user, subscription);
        this.type = com.example.submerge.models.requests.Request.SUBSCRIPTION_TYPE;
    }
}
