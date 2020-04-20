package com.example.submerge.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.submerge.R;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainInterface extends AppCompatActivity {
    private static final String TAG = "SubMerge";

    MainAdapter adapter;
    static DatabaseHandler databaseHandler;
    static NotificationHandler notificationHandler;
    User user;

    RecyclerView recyclerView;
    Button add_button;
    Button notification_button;
    TextView current_month;
    TextView total_cost;
    TextView login;

    double cost;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); //Set the view of this interface here

        this.add_button = findViewById(R.id.add_item);
        this.notification_button = findViewById(R.id.test_notification);
        this.recyclerView = findViewById(R.id.list_items);
        this.current_month = findViewById(R.id.current_month);
        this.total_cost = findViewById(R.id.current_cost);
        this.login = findViewById(R.id.txt_user_id);

        makeListeners();
        setDefaults();

        decodeIntent(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeListeners() {
        add_button.setOnClickListener(this::goToSearchPage);

        notification_button.setOnClickListener(this::sendNotification);

        notificationHandler = new NotificationHandler();
        notificationHandler.mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationHandler.notificationCompatBuilder = new NotificationCompat.Builder( getApplicationContext(), NotificationHandler.CHANNEL_ID);

        RecyclerItemClickListener.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> gotoDetail(v, position));

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                databaseHandler.log_out(result -> {
                    Log.d(TAG, "Logged out the user!");
                    goBackToLogin();
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void setDefaults() {
        this.login.setText(String.format(Locale.ENGLISH, "Logged in with ID: %s", databaseHandler.getName()));

        List<Subscription> items = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        String month = Subscription.MONTHS[cal.get(Calendar.MONTH)];
        this.current_month.setText(month);

        this.adapter = new MainAdapter(items);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.cost = 0.00;
        for (Subscription sub : items) {
            this.cost += sub.accessCost();
        }

        this.total_cost.setText(String.format(Locale.ENGLISH, "$%.2f", this.cost));
    }

    public void decodeIntent(Intent intent) {
        switch (Objects.requireNonNull(intent.getStringExtra("from"))) {
            case "login":
                user = User.decode_intent(intent);
                break;
            case "edit-add":
                Subscription sub = Subscription.decode_intent(intent);
                user = User.decode_intent(intent);
                addItem(sub);
                break;
            case "edit-edit":
                break;
        }
    }

    public void goBackToLogin() {
        Intent login = new Intent(this, LoginHandler.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void goToSearchPage(View v) {
        Log.i("SubMerge-Info", "Going to Search Page");

        SearchInterface.setDatabaseHandler(databaseHandler);
        //SearchInterface.setNotificationHandler(notificationHandler);

        Intent search = new Intent(this, SearchInterface.class);
        //User.encode_intent(search, user);

        startActivityForResult(search, 1);
    }

    public void gotoDetail(View v, int position) {
        Log.i("SubMerge-Info", "Going to Search Page");

//        DetailInterface.setDatabaseHandler(databaseHandler);
//        DetailInterface.setNotificationHandler(notificationHandler);
//
//        Intent detail = new Intent(this, DetailInterface.class);
//        User.encode_intent(detail, user);
//        Subscription.encode_intent(detail, adapter.getList().get(position));
//
//        startActivityForResult(detail, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //handle result
            assert data != null;
            decodeIntent(data);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //do nothing
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendNotification(View v) {
        Log.i("SubMerge-Info", "Sending a notification!");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, 1);
        Subscription sub = new Subscription(R.drawable.spotify, "Spotify - Student", false, c.getTime(), Subscription.Recurrences.MONTHLY, 4.99, 0.00);
        notificationHandler.sendNotification(sub);
    }

    public static void setDatabaseHandler(DatabaseHandler new_handler) {
        databaseHandler = new_handler;
    }

    public void addItem(Subscription sub) {
        Request add = new Request(user, sub);
        databaseHandler.addSubscription(add, result -> {
            if (result.isSuccessful()) {
                Log.i(TAG, String.format("Added -> %s - %s", sub.getTitle(), sub.getCost()));
                adapter.addItem(sub);
                cost += sub.accessCost();
                total_cost.setText(String.format(Locale.ENGLISH, "$%.2f", cost));
            }
        });
    }

    public void removeItem(Subscription sub) {
        adapter.removeItem(sub);

        this.cost -= sub.accessCost();
        this.total_cost.setText(String.format(Locale.ENGLISH, "$%.2f", cost));
    }
}