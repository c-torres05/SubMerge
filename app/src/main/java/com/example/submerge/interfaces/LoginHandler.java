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
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;

import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;

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

            handler = DatabaseHandler.getInstance();
//            MainInterface.setDatabaseHandler(handler);

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
//                Calendar cal = Calendar.getInstance();
//                Request add = new Request(result.getResult(), new Subscription("netflix", "Netflix - Basic", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00));
//                Request add1 = new Request(result.getResult(), new Subscription("netflix", "Netflix - Basic", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 12.99, 0.00));
//                Request add2 = new Request(result.getResult(), new Subscription("netflix", "Netflix - Basic", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 15.99, 0.00));
//                Request add3 = new Request(result.getResult(), new Subscription("hulu", "Hulu", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00));
//                handler.insertSearchSubscription(add, r -> {});
//                handler.insertSearchSubscription(add1, r -> {});
//                handler.insertSearchSubscription(add2, r -> {});
//                handler.insertSearchSubscription(add3, r -> {});
                User.encode_intent(main, result.getResult());
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                main.putExtra("from", "login");
                startActivity(main);
            }
        });
    }
}
