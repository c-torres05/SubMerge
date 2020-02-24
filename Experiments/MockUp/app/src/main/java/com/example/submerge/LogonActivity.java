package com.example.submerge;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

public class LogonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logon);
        setupLogin();
    }

    private void setupLogin() {
        setContentView(R.layout.logon);
        enableAnonymousAuth();
    }

    private void enableAnonymousAuth() {
        findViewById(R.id.anon_login_button).setOnClickListener(ignored ->
                SubListActivity.client.getAuth().loginWithCredential(new AnonymousCredential())
                        .addOnSuccessListener(user -> {
                            Toast.makeText(LogonActivity.this,
                                    "Logged in Anonymously. ID: " + user.getId(),
                                    Toast.LENGTH_LONG).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Stitch Auth", "error logging in", e);
                            Toast.makeText(LogonActivity.this, "Failed to log in Anonymously. " +
                                            "Did you enable Anonymous Auth in your Stitch backend and copy " +
                                            "your Stitch App ID to strings.xml?",
                                    Toast.LENGTH_LONG).show();
                        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
