package com.example.submerge.models;

public interface Callback<T, U> {
    void onComplete(Result<T, U> result);
}
