package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.submerge.R;
import com.example.submerge.models.requests.SearchRequest;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.User;

import org.bson.types.ObjectId;

public class LoginHandler extends AppCompatActivity {
    Button anonymousLogin;
    DatabaseHandler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.anonymousLogin = findViewById(R.id.anon_login_button);
        this.anonymousLogin.setOnClickListener(this::anonLogin);
    }

    private void anonLogin(View view) {
        handler = new DatabaseHandler();
        SearchInterface.setHandler(handler);

        handler.loginAnon();
        handler.finishInit();

        User user = new User(new ObjectId(), handler.getUserId(), handler.getName(), User.ANON_TYPE);
        handler.addUser(new SearchRequest(user, null), new Callback<User, String>() {
            @Override
            public void onComplete(Result<User, String> result) {
                Log.i("SubMerge", "Added user!");
            }
        });

        Intent main = new Intent(this, SearchInterface.class);
        main.putExtra("user_object_id", user.get_id().toString());
        main.putExtra("user_owner_id", user.getOwner_id());
        main.putExtra("user_user_id", user.getUser_Id());
        main.putExtra("user_type", user.getType());
        startActivity(main);
    }
}
