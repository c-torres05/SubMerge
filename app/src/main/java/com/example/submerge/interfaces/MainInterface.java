package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.submerge.interfaces.DatabaseHandler;
import com.example.submerge.interfaces.MainAdapter;
import com.example.submerge.R;
import com.example.submerge.models.requests.SearchRequest;
import com.example.submerge.models.requests.Request;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainInterface extends AppCompatActivity {
    final String[] months = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Button add_button;
    RecyclerView list;
    TextView current_month;
    TextView current_cost;
    TextView login;

    MainAdapter adapter;
    double cost;

    static DatabaseHandler handler;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); //Set the view of this interface here

        this.add_button = findViewById(R.id.add_item);
        this.list = findViewById(R.id.list_items);
        this.current_month = findViewById(R.id.current_month);
        this.current_cost = findViewById(R.id.current_cost);
        this.login = findViewById(R.id.txt_user_id);

        Intent from_login = getIntent();
        ObjectId object_id = new ObjectId(from_login.getStringExtra("user_object_id"));
        String owner_id = from_login.getStringExtra("user_owner_id");
        String user_id = from_login.getStringExtra("user_user_id");
        int type = from_login.getIntExtra("user_type", User.UNKNOWN_TYPE);

        user = new User(object_id, owner_id, user_id, type);

        add_button.setOnClickListener(this::addButton);

        this.login.setText(String.format(Locale.ENGLISH, "Logged in with ID: %s", handler.getName()));

        Calendar cal = Calendar.getInstance();
        String month = months[cal.get(Calendar.MONTH)];
        this.current_month.setText(month);

        List<Subscription> items = new ArrayList<>();
        Calendar netflix_basic_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 2);
        items.add(new Subscription(R.drawable.netflix, "Netflix - Basic", false, netflix_basic_calendar.getTime(), Subscription.Reccurances.WEEKLY, 8.99, 0.00));

        Calendar netflix_standard_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 14);
        items.add(new Subscription(R.drawable.netflix, "Netflix - Standard", false, netflix_standard_calendar.getTime(), Subscription.Reccurances.MONTHLY, 12.99, 1.00));

        Calendar netflix_premium_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.netflix, "Netflix - Premium", false, netflix_premium_calendar.getTime(), Subscription.Reccurances.YEARLY, 15.99, -1.01));

        Calendar hulu_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.hulu, "Hulu", false, hulu_calendar.getTime(), Subscription.Reccurances.MONTHLY, 5.99, -1.01));

        Calendar hulu_no_ads_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.hulu, "Hulu No Ads Addon", false, hulu_no_ads_calendar.getTime(), Subscription.Reccurances.MONTHLY, 6.00, 0.00));

        Calendar hulu_live_tv_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.hulu, "Hulu + Live TV", false, hulu_live_tv_calendar.getTime(), Subscription.Reccurances.MONTHLY, 54.99, 0.00));

        Calendar hulu_disney_espn_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.hulu, "Hulu & D+ & ESPN+", false, hulu_disney_espn_calendar.getTime(), Subscription.Reccurances.MONTHLY, 12.99, 0.00));

        Calendar spotify_premium_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.spotify, "Spotify - Individual", false, spotify_premium_calendar.getTime(), Subscription.Reccurances.MONTHLY, 9.99, 0.00));

        Calendar spotify_individual_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.spotify, "Spotify - Family", false, spotify_individual_calendar.getTime(), Subscription.Reccurances.MONTHLY, 14.99, 0.00));

        Calendar spotify_student_calendar = new GregorianCalendar(2020, cal.get(Calendar.MONTH), 13);
        items.add(new Subscription(R.drawable.spotify, "Spotify - Student", false, spotify_student_calendar.getTime(), Subscription.Reccurances.MONTHLY, 4.99, 0.00));

        adapter = new MainAdapter(items);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        this.cost = 0.00;
        for (Subscription sub : items) {
            cost += sub.accessCost();
        }
        this.current_cost.setText(String.format(Locale.ENGLISH, "$%.2f", this.cost));
    }

    public void addButton(View v) {
        Log.i("SubMerge-Info", "Going to Search Page");
        SearchInterface.setHandler(handler);
//        SearchInterface.setMainInterface(this);
        Intent search = new Intent(this, SearchInterface.class);
        startActivityForResult(search, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == -1) {
                Log.e("SubMerge", "Could not get search result");
            } else if (resultCode == 0) {
                Log.i("SubMerge", "Got a Result");

                int image = data.getIntExtra("image", R.drawable.netflix);
                String title = data.getStringExtra("title");
                double cost = data.getDoubleExtra("cost", 0.00);
                this.addItem(new Subscription(image, String.format("%s", title), false, new Date(), Subscription.Reccurances.MONTHLY, cost, 0.00));
            }
        } else if (requestCode == 1) {

        }
    }

    public static void setHandler(DatabaseHandler new_handler) {
        handler = new_handler;
    }

    public void addItem(Subscription sub) {
        adapter.addItem(sub);

        Request add = new SearchRequest(user, sub);
        handler.addSubscription(add, new Callback<Subscription, String>() {
            @Override
            public void onComplete(Result<Subscription, String> result) {
                Log.i("SubMerge", String.format("Added -> %s - %s", sub.getTitle(), sub.getCost()));
                cost += sub.accessCost();
                current_cost.setText(String.format(Locale.ENGLISH, "$%.2f", cost));
            }
        });
    }

    public void removeItem(Subscription sub) {
        adapter.removeItem(sub);

        this.cost -= sub.accessCost();
        this.current_cost.setText(String.format(Locale.ENGLISH, "$%.2f", cost));
    }

}
