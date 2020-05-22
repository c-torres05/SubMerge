package com.example.submerge.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.submerge.R;
import com.example.submerge.models.NotificationData;
import com.example.submerge.models.Subscription;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.example.submerge.interfaces.NotificationHandler.CHANNEL_ID;
import static com.example.submerge.interfaces.NotificationHandler.createNotificationChannel;

public class Unsubscribe extends AppCompatActivity {

    TextView title;
    TextView cost_value;
    TextView recurrence_value;
    TextView renewal_value;
    BarChart graph;
    Button unsubscribe;
    Button remove;
    Button edit;
    Button notify;

    Subscription subscription;

    NotificationHandler notificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsubscribe);

        title = findViewById(R.id.title);
        cost_value = findViewById(R.id.cost_value);
        recurrence_value = findViewById(R.id.recurrence_value);
        renewal_value = findViewById(R.id.renewal_value);
        graph = findViewById(R.id.graph);
        unsubscribe = findViewById(R.id.unsubscribe);
        remove = findViewById(R.id.remove);
        edit = findViewById(R.id.edit);
        notify = findViewById(R.id.notify);

        decodeIntent(getIntent());
        remove.setOnClickListener(v -> gotoMainScreen("unsub-remove"));
        edit.setOnClickListener(v -> gotoEditScreen());

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                gotoMainScreen("unsub");
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        notificationHandler = new NotificationHandler();
        notificationHandler.mNotificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationHandler.notificationCompatBuilder = new NotificationCompat.Builder( getApplicationContext(), CHANNEL_ID);


        notify.setOnClickListener(v -> {
            NotificationData data = notificationHandler.createNotificationData(subscription);
            createNotificationChannel(this, data);
            notificationHandler.sendNotification(notificationHandler.createNotification(data), data);
        });

        Calendar current = Calendar.getInstance();
        int month = current.get(Calendar.MONTH);
        double[] change_history = subscription.accessChangeHistory();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0, mon = month - 12; mon < month; i++, mon++) {
            if (change_history[i] != -1) {
                entries.add(new BarEntry((float) mon, (float) change_history[i]));
            }
        }

        graph.setHorizontalScrollBarEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        graph.setHighlightPerDragEnabled(false);
        graph.setVerticalScrollBarEnabled(false);
        graph.setPinchZoom(false);
        graph.setScaleYEnabled(false);
        graph.setScaleXEnabled(false);
        graph.setFitBars(true);
        graph.setDoubleTapToZoomEnabled(false);
        graph.setHighlightFullBarEnabled(false);
        Description desc = new Description();
        desc.setText("");
        graph.setDescription(desc);
        graph.zoom(2, 1, 0, 0);
        graph.moveViewToX(4);

        updateValues();

        XAxis xAxis = graph.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int indx = (int) v;
                while (indx < 0)
                    indx += 12;
                while (indx > 11)
                    indx -= 12;
                return months[indx];
            }
        });
        xAxis.setAxisMaximum(month + 1);
        xAxis.setAxisMinimum(month - 11);

        YAxis yAxisLeft = graph.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH, "$%.2f", value);
            }
        });
        YAxis yAxisRight = graph.getAxisRight();
        yAxisRight.setEnabled(false);
    }

    public void updateValues() {
        title.setText(subscription.getTitle());
        cost_value.setText(String.format(Locale.ENGLISH, "$%.2f", subscription.accessCost()));
        recurrence_value.setText(Subscription.recurrenceFromInt(subscription.accessRecurrance()));
        Calendar current = Calendar.getInstance();
        Calendar sub = (Calendar) Calendar.getInstance().clone();
        sub.setTime(new Date(subscription.accessRenewal()));


        while (sub.get(Calendar.DAY_OF_YEAR) < current.get(Calendar.DAY_OF_YEAR) && sub.get(Calendar.YEAR) < current.get(Calendar.YEAR)) {
            sub.add(Calendar.DAY_OF_YEAR, subscription.accessRecurrance());
        }

        renewal_value.setText(Subscription.renewalFromDate(new Date(sub.getTime().getTime())));

        int month = current.get(Calendar.MONTH);
        double[] change_history = subscription.accessChangeHistory();

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0, mon = month - 12; mon < month; i++, mon++) {
            if (change_history[i] != -1) {
                entries.add(new BarEntry((float) mon, (float) change_history[i]));
            }
        }

        Calendar cntMonth = Calendar.getInstance();
        cntMonth.setTime(new Date(subscription.accessRenewal()));
        while (cntMonth.get(Calendar.MONTH) < current.get(Calendar.MONTH) || cntMonth.get(Calendar.YEAR) < current.get(Calendar.YEAR)) {
            cntMonth.add(Calendar.DAY_OF_YEAR, subscription.accessRecurrance());
        }
        int count = 0;
        while (cntMonth.get(Calendar.MONTH) == current.get(Calendar.MONTH) && cntMonth.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {
            cntMonth.add(Calendar.DAY_OF_YEAR, subscription.accessRecurrance());
            count++;
        }
        entries.add(new BarEntry((float) month, (float) subscription.accessCost() * count));

        BarDataSet dataSet = new BarDataSet(entries, "Cost");
        dataSet.setColor(Color.parseColor("#00BDA0"));

        BarData data = new BarData(dataSet);
        data.setValueTextSize(10);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.ENGLISH, "$%.2f", value);
            }
        });
        graph.setData(data);
    }

    public void loadWebPage(View v) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("from", subscription.accessURL());
        startActivity(intent);
    }

    public void decodeIntent(Intent intent) {
        switch (Objects.requireNonNull(intent.getStringExtra("from"))) {
            case "main":
                subscription = Subscription.decode_intent(intent);
                break;
            case "edit-edit":
                subscription = Subscription.decode_intent(intent);
                break;
        }
    }

    private void gotoMainScreen(String mode) {
        Intent main = new Intent();
        main.putExtra("from", mode);
        Subscription.encode_intent(main, subscription);

        Log.i("SubMerge", "Going to Main screen");

        setResult(Activity.RESULT_OK, main);
        finish();
    }

    private void gotoEditScreen() {
        Intent edit = new Intent(this, Edit.class);
        edit.putExtra("from", "unsub");
        Subscription.encode_intent(edit, subscription);

        Log.i("SubMerge", "Going to Edit screen");

        startActivityForResult(edit, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //handle result
            assert data != null;
            decodeIntent(data);
            updateValues();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //do nothing
        }

    }
}