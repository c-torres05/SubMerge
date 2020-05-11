package com.example.submerge.models;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.submerge.R;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

public class Subscription {
    public static Subscription decode_intent(Intent intent) {
        //Guareenteed to be there
        String image_key = intent.getStringExtra("sub_image");
        String title = intent.getStringExtra("sub_name");
        double cost = intent.getDoubleExtra("sub_cost", 0.00);
        Date renewal = renewalFromString(Objects.requireNonNull(intent.getStringExtra("sub_renewal")));
        int recurrence = recurrenceFromString(Objects.requireNonNull(intent.getStringExtra("sub_recurrence")));
        boolean trial = intent.getBooleanExtra("sub_trial", false);
        double change = intent.getDoubleExtra("sub_change", 0.00);
        return new Subscription(image_key, String.format("%s", title), trial, renewal, recurrence, cost, change);
    }

    public static void encode_intent(Intent intent, Subscription subscription) {
        intent.putExtra("sub_image", subscription.getImage());
        intent.putExtra("sub_name", subscription.getTitle());
        intent.putExtra("sub_cost", subscription.accessCost());
        intent.putExtra("sub_renewal", renewalFromDate(new Date(subscription.accessRenewal())));
        intent.putExtra("sub_recurrence", recurrenceFromInt(subscription.accessRecurrance()));
        intent.putExtra("sub_trial", subscription.accessTrial());
        intent.putExtra("sub_change", subscription.accessChange());
    }

    public static Date renewalFromString(String input) {
        Calendar calendar = Calendar.getInstance();
        int first_slash = input.indexOf('/');
        int second_slash = input.indexOf('/', first_slash + 1);
        int month = Integer.parseInt(input.substring(0, first_slash)) - 1;
        int day = Integer.parseInt(input.substring(first_slash + 1, second_slash));
        int year = Integer.parseInt(input.substring(second_slash + 1));
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static String renewalFromDate(Date input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        return String.format(Locale.ENGLISH, "%d/%d/%d", month + 1, dayOfMonth, year);
    }

    public static int recurrenceFromString(String input) {
        switch(input) {
            case "Weekly":
                Log.d("SubMerge", "Returning 7!");
                return Subscription.Recurrences.WEEKLY;
            case "Bi-Weekly":
                Log.d("SubMerge", "Returning 14!");
                return Subscription.Recurrences.BI_WEEKLY;
            case "Monthly":
                Log.d("SubMerge", "Returning 31!");
                return Subscription.Recurrences.MONTHLY;
            case "Yearly":
                Log.d("SubMerge", "Returning 365!");
                return Subscription.Recurrences.YEARLY;
            default:
                Log.d("Submerge", String.format("I was given %s", input));
                return Integer.parseInt(input);
        }
    }

    public static String recurrenceFromInt(int input) {
        switch (input) {
            case Recurrences.DAILY:
                return "Daily";
            case Recurrences.BI_DAILY:
                return "Bi-Daily";
            case Recurrences.WEEKLY:
                return "Weekly";
            case Recurrences.BI_WEEKLY:
                return "Bi-Weekly";
            case Recurrences.MONTHLY:
                return "Monthly";
            case Recurrences.YEARLY:
                return "Yearly";
            case Recurrences.BI_YEARLY:
                return "Bi-Yearly";
            default:
                return Integer.toString(input);
        }
    }

    public static final class Types {
        static final int MAIN = 0;
        static final int SEARCH = 1;
        static final int DATABASE = 2;
    }

    public static final class Recurrences {
        public static final int DAILY = 1;
        public static final int BI_DAILY = 3;
        public static final int WEEKLY = 7;
        public static final int BI_WEEKLY = 14;
        public static final int MONTHLY = 30;
        public static final int YEARLY = 365;
        public static final int BI_YEARLY = YEARLY * 2;
    }

    public static final String[] MONTHS = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    private final ObjectId _id;
    private final String owner_id;
    private int type;
    private String image_key;
    private int image;
    private int change_image;

    private String title;
    private double cost;
    private boolean trial;
    private Date renewal;
    private int recurrance;
    private double change;
    private boolean paid;

    public Subscription(String image_key, String title, boolean trial, Date renewal, int recurrance, double cost, double change) {
        this(new ObjectId(), "", image_key, title, trial, renewal.getTime(), recurrance, cost, change);
        this.type = Types.MAIN;
    }

    public Subscription(String image_key, String title, double cost) {
        this(new ObjectId(), "", image_key, title, false, -1, -1, cost, 0.00);
    }

    public Subscription(ObjectId _id, String owner_id, String image_key, String title, boolean trial, long renewal, int recurrance, double cost, double change) {
        this.type = Types.DATABASE;
        this._id = _id;
        this.owner_id = owner_id;
        this.image_key = image_key;
        this.title = title;
        this.trial = trial;
        this.renewal = new Date(renewal);
        this.paid = false;
        this.recurrance = recurrance;
        this.cost = cost;
        this.change = change;

        if (this.change == 0.00)
            this.change_image = R.drawable.cost_equal;
        else if (this.change > 0.00)
            this.change_image = R.drawable.cost_up;
        else
            this.change_image = R.drawable.cost_down;
    }

    /*
    Android View getter methods
     */
    public int getType() {
        return this.type;
    }

    public boolean getPaid() {return this.paid;}

    public void setPaid(boolean new_paid) {this.paid = new_paid;}

    public String getImage() {
        return this.image_key;
    }

    public int getImageDrawable() {
        return this.image;
    }

    public void setImageDrawable(int image) {
        this.image = image;
    }

    public int getChangeImage() {
        return this.change_image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getRenewal() {
        return renewalFromDate(this.renewal);
    }

    public String getRecurrence() {
        return recurrenceFromInt(this.recurrance);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMessage() {
        String message = "";
        if (this.trial)
            message += "Expires ";
        else
            message += "Renews ";


        LocalDate current =  LocalDate.now();
        LocalDate date = this.renewal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long days_until;
        while (date.isBefore(current)) {
            date = date.plusDays(this.recurrance);
            Log.i("SubMerge", String.format("Added %d days", this.recurrance));
        }
//        this.renewal = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        days_until = DAYS.between(current, date);
        Log.i("SubMerge", String.format("%d", days_until));

        if (((int) days_until) == 0)
            message += "Today";
        else {
            Log.i("SubMerge", String.format("Renews in %d days.", days_until));
            message += String.format(Locale.ENGLISH, "in %d days", days_until);
        }
        return message;
    }

    public String getCost() {
        if (this.paid)
            return "Paid";
        return String.format(Locale.ENGLISH, "$%.2f", this.cost);
    }

    public String getChange() {
        return String.format(Locale.ENGLISH, "$%.2f", Math.abs(this.change));
    }

    /*
    Database getter methods
     */
    public ObjectId accessId() {
        return this._id;
    }

    public String accessOwnerId() {
        return this.owner_id;
    }

    public String accessImage() {
        return this.image_key;
    }

    public String accessTitle() {
        return this.title;
    }

    public boolean accessTrial() {
        return this.trial;
    }

    public long accessRenewal() {
        return this.renewal.getTime();
    }

    public int accessRecurrance() {
        return this.recurrance;
    }

    public double accessCost() {
        return this.cost;
    }

    public double accessChange() {
        return this.change;
    }

}
