package com.example.submerge.interfaces;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.submerge.R;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Unsubscribe extends AppCompatActivity {

    TextView title;
    TextView cost_value;
    TextView recurrence_value;
    TextView renewal_value;
    BarChart graph;
    Button unsubscribe;

    Subscription subscription;

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

        decodeIntent(getIntent());

        title.setText(subscription.getTitle());
        cost_value.setText(String.format(Locale.ENGLISH, "$%.2f", subscription.accessCost()));
        recurrence_value.setText(Subscription.recurrenceFromInt(subscription.accessRecurrance()));
        Calendar current = Calendar.getInstance();
        Calendar sub = (Calendar) Calendar.getInstance().clone();
        sub.setTime(new Date(subscription.accessRenewal()));

        Log.i("SubMerge", sub.getTime().toString());
        Log.i("SubMerge", current.getTime().toString());
        while (sub.get(Calendar.DAY_OF_YEAR) < current.get(Calendar.DAY_OF_YEAR) && sub.get(Calendar.YEAR) < current.get(Calendar.YEAR)) {
            Log.i("SubMerge", "add to date");
            sub.add(Calendar.DAY_OF_YEAR, subscription.accessRecurrance());
        }
        Log.i("SubMerge", sub.getTime().toString());

        renewal_value.setText(Subscription.renewalFromDate(new Date(sub.getTime().getTime())));

        int month = current.get(Calendar.MONTH);
        double[] change_history = subscription.accessChangeHistory();

//        change_history[11] = 9.99;
//        change_history[10] = 12.99;
//        change_history[9] = 14.99;

        ArrayList<BarEntry> entries = new ArrayList<>();
//        double max = -1;
        for (int i = 0, mon = month - 12; mon < month; i++, mon++) {
            if (change_history[i] != -1) {
                entries.add(new BarEntry((float) mon, (float) change_history[i]));
//                if (change_history[i] > max) {
//                    max = change_history[i];
//                }
            }
        }

        entries.add(new BarEntry((float) month, (float) subscription.accessCost()));
//        if (subscription.accessCost() > max)
//            max = subscription.accessCost();
//        max += 2;

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
        graph.setData(data);
        graph.zoom(2, 1, 0, 0);
        graph.moveViewToX(4);

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
//        xAxis.setAxisMinimum(); Set to the minimum of the renewal
        xAxis.setAxisMaximum(month + 1);
        xAxis.setAxisMinimum(month - 11);

        YAxis yAxisLeft = graph.getAxisLeft();
//        yAxisLeft.setAxisMinimum(0);
//        yAxisLeft.setAxisMaximum((float) max);
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
        }
    }
}