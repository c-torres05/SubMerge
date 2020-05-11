package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.submerge.R;

import androidx.appcompat.app.AppCompatActivity;

public class Unsubscribe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsubscribe);
    }

    public void loadWebPage(View v) {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
    }
}
