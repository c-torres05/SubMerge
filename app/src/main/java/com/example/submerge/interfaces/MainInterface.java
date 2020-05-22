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
import com.example.submerge.calendar.CalendarView;
import com.example.submerge.calendar.EventDay;
import com.example.submerge.calendar.listeners.OnCalendarPageChangeListener;
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
    static DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    static NotificationHandler notificationHandler;
    User user;

    RecyclerView recyclerView;
    Button add_button;
    List<EventDay> events;
    List<Subscription> subscriptions;
    TextView total_cost;
    CalendarView calendar;

    double cost;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); //Set the view of this interface here

        this.calendar = findViewById(R.id.calendar);
        this.events = new ArrayList<>();
        this.subscriptions = new ArrayList<>();

        this.add_button = findViewById(R.id.add_item);
        this.recyclerView = findViewById(R.id.list_items);
        this.total_cost = findViewById(R.id.current_cost);

        makeListeners();
        setDefaults();
        decodeIntent(getIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeListeners() {
        add_button.setOnClickListener(this::goToSearchPage);

//        notificationHandler = new NotificationHandler();
//        notificationHandler.mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//        notificationHandler.notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), NotificationHandler.CHANNEL_ID);

        RecyclerItemClickListener.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> gotoDetail(v, position));

        OnCalendarPageChangeListener updateList = this::refreshCalendar;
        calendar.setOnForwardPageChangeListener(updateList);
        calendar.setOnPreviousPageChangeListener(updateList);

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
//        this.login.setText(String.format(Locale.ENGLISH, "Logged in with ID: %s", databaseHandler.getName()));

        List<Subscription> items = new ArrayList<>();

//        Calendar cal = Calendar.getInstance();
//        String month = Subscription.MONTHS[cal.get(Calendar.MONTH)];
//        this.current_month.setText(month);

        this.adapter = new MainAdapter(items);
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.cost = 0.00;
        for (Subscription sub : items) {
            this.cost += sub.accessCost();
        }

        this.total_cost.setText(String.format(Locale.ENGLISH, "$%.2f", this.cost));
//        this.calendar.setDisabledDays(new Ar);
//        this.calendar.setNavigationVisibility(0);
    }

    public void decodeIntent(Intent intent) {
        switch (Objects.requireNonNull(intent.getStringExtra("from"))) {
            case "login-anon":
                user = User.decode_intent(intent);
                break;
            case "login-user":
                user = User.decode_intent(intent);
                loadSubscriptions();
                break;
            case "edit-add":
                Log.i("SubMerge", "Come back from search");
                Subscription sub = Subscription.decode_intent(intent);
                user = User.decode_intent(intent);
                addItem(sub);
                break;
            case "edit-edit":
                break;
            case "unsub":
                Log.i("SubMerge", "ALKUIWEGOPALIWYUEGFPAIUWYGVEBP:IAWUGBEPAIWUGE");
                Subscription sub3 = Subscription.decode_intent(intent);
                for (int i = 0; i < subscriptions.size(); i++) {
                    if (subscriptions.get(i).equals(sub3)) {
                        subscriptions.remove(i);
                        break;
                    }
                }
                subscriptions.add(sub3);
                refreshCalendar();
                break;
            case "unsub-remove":
                Subscription sub2 = Subscription.decode_intent(intent);
                removeItem(sub2);
                break;
        }
    }

    public void goBackToLogin() {
        Intent login = new Intent(this, LoginHandler.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void goToSearchPage(View v) {
        Log.i("SubMerge-Info", "Going to Search Page");

        Intent search = new Intent(this, SearchInterface.class);
        User.encode_intent(search, user);

        startActivityForResult(search, 1);
    }

    public void gotoDetail(View v, int position) {
        Log.i("SubMerge-Info", "Going to Detail Page");

        Intent detail = new Intent(this, Unsubscribe.class);
        User.encode_intent(detail, user);
        detail.putExtra("from", "main");
        Subscription.encode_intent(detail, adapter.getList().get(position));
        removeItem(adapter.getList().get(position));

        startActivityForResult(detail, 1);
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

    public void addItem(Subscription sub) {
        Request add = new Request(user, sub);
        databaseHandler.addSubscription(add, result -> {
            if (result.isSuccessful()) {
                Log.i(TAG, String.format("Added -> %s - %s", sub.getTitle(), sub.getCost()));
                subscriptions.add(sub);
                refreshCalendar();
            }
        });
    }

    private void loadSubscriptions() {
        Request add = new Request(user, null);
        databaseHandler.getSubscriptions(add, result -> {
            if (result.isSuccessful()) {
                result.getResult().forEach(sub -> {
                    Log.d(TAG, String.format("LOADING -> %s - %s", sub.getTitle(), sub.getCost()));
                    subscriptions.add(sub);
                });
                refreshCalendar();
            }
        });
    }

    public void refreshCalendar() {
        Log.i("SubMerge", "Refresh the calendar");
        List<Subscription> subs = new ArrayList<>();
        List<EventDay> events = new ArrayList<>();
        adapter.clear_list();
        Calendar page = this.calendar.getCurrentPageDate();
        Calendar first_showed_day = this.calendar.getFirstShowingDate();
        Calendar last_showed_day = (Calendar) first_showed_day.clone();
        last_showed_day.add(Calendar.DAY_OF_YEAR, 42);

        this.cost = 0;
        for (Subscription sub : subscriptions) {
            Calendar c = Calendar.getInstance();
            Date sub_date = new Date(sub.accessRenewal());
            c.setTime(sub_date);
            Log.i("SubMerge", sub.getTitle());

            //If before current day → Paid & Renewed
            //If after current day → Renews
            //Message will always be from current day.
            //Calendar will show the day of the next month that it will renew

            Log.d("Submerge", String.format("Sub recurrence is: %d", sub.accessRecurrance()));
            sub.setImageDrawable(getResources().getIdentifier(sub.getImage().toLowerCase(), "drawable", this.getPackageName()));
            while (c.before(first_showed_day)) {
                c.add(Calendar.DAY_OF_YEAR, sub.accessRecurrance());
            }

            if (c.before(last_showed_day)) {
                subs.add(sub);

                while (c.before(last_showed_day)) {
                    Log.d("SubMerge", String.format("In Month: %d", c.get(Calendar.MONTH)));
                    events.add(new EventDay((Calendar) c.clone(), getResources().getIdentifier(sub.getImage().toLowerCase(), "drawable", this.getPackageName())));
                    if (c.get(Calendar.MONTH) == page.get(Calendar.MONTH))
                        this.cost += sub.accessCost();
                    c.add(Calendar.DAY_OF_YEAR, sub.accessRecurrance());
                }
            }
        }

        adapter.set_list(subs);
        Log.i("SubMerge", String.format("The string is %d elements long", events.size()));
        total_cost.setText(String.format(Locale.ENGLISH, "$%.2f", cost));
        calendar.setEvents(events);
        adapter.notifyDataSetChanged();
    }

    public void removeItem(Subscription sub) {
        for (int i = 0; i < subscriptions.size(); i++) {
            if (subscriptions.get(i).equals(sub)) {
                subscriptions.remove(i);
                break;
            }
        }
        refreshCalendar();
    }
}