package com.example.submerge.models;

import android.util.Log;

import com.example.submerge.R;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Locale;

public class Subscription {
    public static final class Types {
        static final int MAIN = 0;
        static final int SEARCH = 1;
        static final int DATABASE = 2;
    }

    public static final class Reccurances {
        public static final int DAILY = 1;
        public static final int BI_DAILY = 3;
        public static final int WEEKLY = 7;
        public static final int BI_WEEKLY = 14;
        public static final int MONTHLY = 31;
        public static final int YEARLY = 365;
        public static final int BI_YEARLY = YEARLY * 2;
    }

    private final ObjectId _id;
    private final String owner_id;
    private final int type;
    private final int image;
    private final int change_image;

    private final String title;
    private final double cost;
    private final boolean trial;
    private final Date renewal;
    private final int recurrance;
    private final double change;



    public Subscription(int image, String title, boolean trial, Date renewal, int recurrance, double cost, double change) {
        this.type = Types.MAIN;
        this._id = new ObjectId();
        this.owner_id = "";
        this.image = image;

        if (title.length() > 18)
            Log.e("SubMerge", "Message length is too long!");

        this.title = title;
        this.trial = trial;
        this.renewal = renewal;
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

    public Subscription(int image, String title, double cost) {
        this.type = Types.SEARCH;
        this._id = new ObjectId();
        this.owner_id = "";
        this.image = image;
        this.title = title;
        this.trial = false;
        this.renewal = null;
        this.recurrance = -1;
        this.cost = cost;
        this.change = 0.00;

        if (this.change == 0.00)
            this.change_image = R.drawable.cost_equal;
        else if (this.change > 0.00)
            this.change_image = R.drawable.cost_up;
        else
            this.change_image = R.drawable.cost_down;
    }

    public Subscription(ObjectId _id, String owner_id, int image, String title, boolean trial, long renewal, int recurrance, double cost, double change) {
        this.type = Types.DATABASE;
        this._id = _id;
        this.owner_id = owner_id;
        this.image = image;
        this.title = title;
        this.trial = trial;
        this.renewal = new Date(renewal);
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

    public int getImage() {
        return this.image;
    }

    public int getChangeImage() {
        return this.change_image;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        String message = "";
        if (this.trial)
            message += "Expires ";
        else
            message += "Renews ";

        Date current = new Date();
        int days_until;
        if (this.renewal.after(current)) {
            days_until = (int)((this.renewal.getTime() - current.getTime()) / (1000 * 60 * 60 * 24)) + 1;
        } else {
            int days_since = (int)((current.getTime() - this.renewal.getTime()) / (1000 * 60 * 60 * 24)) % this.recurrance;
            if (days_since != 0) {
                days_until = (days_since + 1) + this.recurrance;
            } else days_until = days_since;
        }

        if (days_until == 0)
            message += "Today";
        else {
            message += String.format(Locale.ENGLISH, "in %d days", days_until);
        }
        return message;
    }

    public String getCost() {
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

    public int accessImage() {
        return this.image;
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
