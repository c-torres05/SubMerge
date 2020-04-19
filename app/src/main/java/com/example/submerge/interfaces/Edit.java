package com.example.submerge.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.submerge.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Edit extends AppCompatActivity {
    String subName;
    String recurPayment;
    double subPrice;
    EditText sub_name;
    EditText recur_payment;
    EditText sub_price;
    Button saveButton;

    private ArrayList<RecurrenceItem> mRecurrList;
    private RecurrenceAdapter recurrenceAdapter;

    private static final String TAG = "MainActivity";

    private TextView displayDate;
    private  DatePickerDialog.OnDateSetListener datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        sub_name = (EditText) findViewById(R.id.sub_input);
        sub_price = (EditText) findViewById(R.id.cost_input);
        displayDate = (TextView) findViewById(R.id.select_date);

        displayDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Edit.this,
//                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePicker,
                        year, month, day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "date: mm/dd/yyyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                displayDate.setText(date);
            }
        };

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                subName = sub_name.getText().toString();
                recurPayment = recur_payment.getText().toString();
                subPrice = Double.parseDouble(sub_price.getText().toString());
            }
        });

        initList();
        Spinner spinner = findViewById( R.id.recur_spinner);
        recurrenceAdapter = new RecurrenceAdapter(this, mRecurrList);
        spinner.setAdapter(recurrenceAdapter);
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
