package com.example.submerge.models;

import javax.annotation.Nonnull;

public interface Callback<T, U> {
    void onComplete(Result<T, U> result);
}
