package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.submerge.R;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView web =  findViewById(R.id.webView);

        Intent from_intent = getIntent();
        String url = from_intent.getStringExtra("from");

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.loadUrl(url);
    }
}
