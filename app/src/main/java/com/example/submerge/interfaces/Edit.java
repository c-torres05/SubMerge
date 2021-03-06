package com.example.submerge.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.submerge.R;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Edit extends AppCompatActivity {
    static NotificationHandler notificationHandler;
    static DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    private static final String TAG = "SubMerge";

    String edit_type;
    Subscription subscription;
    User user;

    EditText sub_name;
    TextView displayDate;
    EditText sub_cost;
    EditText web_url;
    Button saveButton;


    ArrayList<RecurrenceItem> mRecurrList;
    RecurrenceAdapter recurrenceAdapter;
    Spinner spinner;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);


        sub_name = (EditText) findViewById(R.id.sub_input);
        sub_cost = (EditText) findViewById(R.id.cost_input);
        displayDate = (TextView) findViewById(R.id.select_date);
        spinner = findViewById( R.id.recur_spinner);
        saveButton = findViewById(R.id.save_button);
        web_url = (EditText) findViewById(R.id.url_input);

        initList();
        recurrenceAdapter = new RecurrenceAdapter(this, mRecurrList);
        spinner.setAdapter(recurrenceAdapter);

        DatePickerDialog.OnDateSetListener datePicker = (view, year, month, dayOfMonth) -> displayDate.setText(String.format(Locale.ENGLISH, "%d/%d/%d", month + 1, dayOfMonth, year));
        datePickerDialog = new DatePickerDialog(Edit.this, android.R.style.Theme_Holo_Dialog_MinWidth, datePicker, 0, 0, 0);
        displayDate.setOnClickListener(v -> datePickerDialog.show());

        decodeIntent(getIntent());

        saveButton.setOnClickListener(v -> {
            if (sub_name.getText().toString().length() == 0 || sub_cost.getText().toString().length() == 0 || web_url.getText().toString().length() == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Please complete all fields!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            String title = sub_name.getText().toString();
            RecurrenceItem item = (RecurrenceItem) spinner.getSelectedItem();
            String recurrence = item.getRecurrence();
            String renewal = displayDate.getText().toString();
            String cost = sub_cost.getText().toString();
            String url = web_url.getText().toString();
            if (cost.charAt(0) == '$')
                cost = cost.substring(1);
            subscription = new Subscription(subscription.getImage(), title, subscription.accessTrial(),
                    Subscription.renewalFromString(renewal),
                    Subscription.recurrenceFromString(recurrence), Double.parseDouble(cost), subscription.accessChangeHistory(), url);
            if (edit_type.equals("edit-add")) {
                gotoMainScreen();
            } else {
                gotoDetailScreen();
            }
        });
    }

    public void updateValues(Subscription subscription) {
        if (!subscription.getTitle().equals("Custom"))
            this.sub_name.setText(subscription.getTitle());
        this.displayDate.setText(subscription.getRenewal());
        Calendar c = Calendar.getInstance();
        datePickerDialog.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        int index = 0;
        for (RecurrenceItem recurrence: mRecurrList) {
            if (recurrence.getRecurrence().equals(subscription.getRecurrence())) {
                index = mRecurrList.indexOf(recurrence);
            }
        }
        RecurrenceItem recur = mRecurrList.remove(index);
        mRecurrList.add(0, recur);
        if (!subscription.getTitle().equals("Custom"))
            this.sub_cost.setText(subscription.getCost());
        if (!subscription.getTitle().equals("Custom"))
            this.web_url.setText(subscription.getURL());
    }

    public void decodeIntent(Intent intent) {
        switch (Objects.requireNonNull(intent.getStringExtra("from"))) {
            case "search":
                subscription = Subscription.decode_intent(intent);
                user = User.decode_intent(intent);
                updateValues(subscription);
                edit_type = "edit-add";
                break;
            case "unsub":
                subscription = Subscription.decode_intent(intent);
                updateValues(subscription);
                edit_type = "edit-edit";
                break;
        }
    }

    private void gotoMainScreen() {
        Intent main = new Intent();
        main.putExtra("from", edit_type);
        User.encode_intent(main, user);
        Subscription.encode_intent(main, subscription);

        Log.i("SubMerge", "Going to Main screen");

        setResult(Activity.RESULT_OK, main);
        finish();
    }

    private void gotoDetailScreen() {
        Intent main = new Intent();
        main.putExtra("from", edit_type);
        Subscription.encode_intent(main, subscription);

        Log.i("SubMerge", "Going to Main screen");

        setResult(Activity.RESULT_OK, main);
        finish();
    }

    public static void setNotificationHandler(NotificationHandler new_handler) {
        notificationHandler = new_handler;
    }

    private void initList()
    {
        mRecurrList = new ArrayList<>();
        mRecurrList.add(new RecurrenceItem("Weekly"));
        mRecurrList.add(new RecurrenceItem("Bi-Weekly"));
        mRecurrList.add(new RecurrenceItem("Monthly"));
        mRecurrList.add(new RecurrenceItem("Yearly"));
    }
}
