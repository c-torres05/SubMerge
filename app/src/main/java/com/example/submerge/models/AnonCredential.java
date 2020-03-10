package com.example.submerge.Test.models;

import com.mongodb.stitch.core.auth.ProviderCapabilities;
import com.mongodb.stitch.core.auth.StitchCredential;

import org.bson.Document;

/**
 * The credential used for custom auth log ins.
 */
public final class AnonCredential implements StitchCredential {

    private final String providerName;
    private final String username;

    public AnonCredential(final String username) {
        this("custom-function", username);
    }

    private AnonCredential(final String providerName, final String username) {
        this.providerName = providerName;
        this.username = username;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public String getProviderType() {
        return "custom-function";
    }

    @Override
    public Document getMaterial() {
        return new Document(Fields.USERNAME, username);
    }

    @Override
    public ProviderCapabilities getProviderCapabilities() {
        return new ProviderCapabilities(false);
    }

    private static class Fields {
        static final String USERNAME = "username";
    }
}
