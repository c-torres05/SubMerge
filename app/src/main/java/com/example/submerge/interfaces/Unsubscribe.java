package com.example.submerge.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.submerge.R;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Unsubscribe extends AppCompatActivity {

    TextView title;
    TextView cost_value;
    TextView recurrence_value;
    TextView renewal_value;
    GraphView graph;
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
        renewal_value.setText(Subscription.renewalFromDate(new Date(subscription.accessRenewal())));

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        for (int x = 1; x < 13; ++x) {
            series.appendData(new DataPoint(x, subscription.accessCost()), false, 12);
        }

        Calendar c = Calendar.getInstance();

        series.setSpacing(10);
        series.setColor(R.color.colorPrimaryDark);
        graph.addSeries(series);
        graph.getViewport().setScalable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(c.get(Calendar.MONTH) - 2);
        graph.getViewport().setMaxX(c.get(Calendar.MONTH) + 2);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(subscription.accessCost() * 1.25);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setPadding(50);


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
