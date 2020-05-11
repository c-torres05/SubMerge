package com.example.submerge.interfaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.submerge.R;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;
import com.mongodb.stitch.core.StitchServiceException;

import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;

public class LoginHandler extends AppCompatActivity {
    Button anonymousLogin;
    Button userLogin;

    EditText username;
    EditText password;
    EditText passwordConfirm;

    DatabaseHandler handler;
    boolean logging_in = false;

    String loginType;
    boolean newUser = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.anonymousLogin = findViewById(R.id.anon_login_button);
        this.userLogin = findViewById(R.id.user_login_button);

        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.password);
        this.passwordConfirm = findViewById(R.id.passwordConfirm);

        this.anonymousLogin.setOnClickListener((View view) -> login(view, "anon"));
        this.userLogin.setOnClickListener((View view) -> login(view, "user-pass"));
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
                    userLogin();
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
                loginType = "login-anon";
                gotoMainScreen();
            }
        });
    }

    private void userLogin() {
        if (username.getText().toString().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{3}")) {
            if (password.getText().toString().equals(passwordConfirm.getText().toString())) {
                handler.loginEmail(result -> {
                    Log.d("SubMerge", "Logged in as a user!");
                    if (result.isSuccessful()) {
                        loginType = "login-user";
                        newUser = result.getResult();
                        gotoMainScreen();
                    } else {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), result.getFailure(), duration);
                        toast.show();
                        logging_in = false;
                    }
                }, username.getText().toString(), password.getText().toString());
            } else {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), "Passwords do not match!", duration);
                toast.show();
                logging_in = false;
            }
        } else {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid email!", duration);
            toast.show();
            logging_in = false;
        }
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
        if (newUser) {
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
                    main.putExtra("from", loginType);
                    startActivity(main);
                }
            });
        } else {
            User user = new User(null, handler.getUserId(), handler.getName(), User.ANON_TYPE);
            handler.findUser(new Request(user, null), result -> {
                if (result.isSuccessful()) {
                    Log.i("SubMerge", "Added user!");
                    User.encode_intent(main, result.getResult());
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    main.putExtra("from", loginType);
                    startActivity(main);
                }
            });
        }
    }
}
