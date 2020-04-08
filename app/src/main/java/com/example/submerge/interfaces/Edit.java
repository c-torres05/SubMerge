package com.example.submerge.interfaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.submerge.R;

import java.util.Calendar;

public class Edit extends AppCompatActivity {
    String subName;
    String recurPayment;
    double subPrice;
    EditText sub_name;
    EditText recur_payment;
    EditText sub_price;
    TextView dateText;
    DatePickerDialog.OnDateSetListener datePicker;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        sub_name = (EditText) findViewById(R.id.sub_input);
        recur_payment = (EditText) findViewById(R.id.recur_input);
        sub_price = (EditText) findViewById(R.id.cost_input);
        dateText = (TextView) findViewById(R.id.select_date);

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(Edit.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        datePicker, month, day, year);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateText.setText(month + "/" + dayOfMonth + "/" + year);
            }
        };

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                subName = sub_name.getText().toString();
                recurPayment = recur_payment.getText().toString();
                subPrice = Double.valueOf(sub_price.getText().toString());
            }
        });
    }
}
