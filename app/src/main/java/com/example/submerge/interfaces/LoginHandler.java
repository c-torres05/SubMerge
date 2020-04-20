package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.submerge.R;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;

import org.bson.types.ObjectId;

public class LoginHandler extends AppCompatActivity {
    Button anonymousLogin;

    DatabaseHandler handler;
    boolean logging_in = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.anonymousLogin = findViewById(R.id.anon_login_button);
        this.anonymousLogin.setOnClickListener((View view) -> login(view, "anon"));
    }

    private void login(View view, String type) {
        if (!logging_in) {
            logging_in = true;

            handler = new DatabaseHandler();
            MainInterface.setDatabaseHandler(handler);

            switch (type) {
                case "anon":
                    anonLogin();
                    break;
                case "user-pass":
                    break;
                case "google":
                    break;
            }
        } else {
            Log.e("SubMerge", "Already logging in!");
        }
    }

    private void anonLogin() {
        handler.loginAnon(result -> {
            Log.d("SubMerger", "Logged in anonymously!");
            if (result.isSuccessful()) {
                gotoMainScreen();
            }
        });
    }

    private void addUser(Callback<User, String> callback) {
        User user = new User(new ObjectId(), handler.getUserId(), handler.getName(), User.ANON_TYPE);

        handler.addUser(new Request(user, null), result -> {
            Log.i("SubMerge", "Added user!");
            callback.onComplete(new Result<>(user, "", result.isSuccessful()));
        });
    }

    private void gotoMainScreen() {
        Intent main = new Intent(this, MainInterface.class);
        handler.finishInit();

        addUser(result -> {
            if (result.isSuccessful()) {
                User.encode_intent(main, result.getResult());
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                main.putExtra("from", "login");
                startActivity(main);
            }
        });
    }
}
