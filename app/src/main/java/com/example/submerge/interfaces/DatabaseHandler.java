package com.example.submerge.interfaces;

import android.util.Log;

import com.example.submerge.App;
import com.example.submerge.R;
import com.example.submerge.models.AnonCredential;
import com.example.submerge.models.Callback;
import com.example.submerge.models.Result;
import com.example.submerge.models.Subscription;
import com.example.submerge.models.SubscriptionDBObject;
import com.example.submerge.models.SubscriptionPackager;
import com.example.submerge.models.User;
import com.example.submerge.models.requests.Request;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseHandler {
    private static final String TAG = "SubMerge";
    private static volatile DatabaseHandler instance = new DatabaseHandler();

    private StitchAppClient client;
    private String userId;
    private String name;

    private RemoteMongoCollection<User> users;
    private RemoteMongoCollection<SubscriptionDBObject> subscriptions;
    private RemoteMongoCollection<Subscription> supported_subscriptions;

    private static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++)
            sb.append(AlphaNumericString.charAt((int) (AlphaNumericString.length() * Math.random())));

        return sb.toString();
    }

    //Gets the subscription json from database
    //ObjId _id
    //String OwnerId
    //Int subscriptionCount
    //BsonArray Subscriptions
    //  Subscription
    //      _id: ObjID
    //      String: Name Netflix
    //      Boolean Type True //Sub not free
    //      Int: Current_Cost 7.99
    //      Int: Current_Diff 0.99
    //      ImageUrl: @strings/Netflix
    //      BsonArray: PastCost
    //          Int32: MonthCost

    public DatabaseHandler() {
        this.client = Stitch.getDefaultAppClient();
    }

    public static DatabaseHandler getInstance() {
        return instance;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public void finishInit() {
        //To be called after logging in
        RemoteMongoClient mongoClient = this.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        this.subscriptions = mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.SUBSCRIPTION_COLLECTION), SubscriptionDBObject.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(SubscriptionDBObject.codec)));
        this.supported_subscriptions = mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.SUPPORTED_SUBSCRIPTION_COLLECTION), Subscription.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(SubscriptionPackager.codec)));
        this.users = mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.USER_COLLECTION), User.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(User.codec)));

    }

    public void log_out(Callback<String, String> callback) {
        this.client.getAuth().logout().addOnCompleteListener(task -> callback.onComplete(new Result<String, String>("Logged out the User!", "Failed to log out the user!", task.isSuccessful())));
    }


    public void loginAnon(Callback<Boolean, String> callback) {
        Log.d("stitch", "logging in anonymously");

        String name = getAlphaNumericString(15);
        Log.i(TAG, String.format("Made a user %s", name));
        AnonCredential credential = new AnonCredential(name);
        this.name = name;

        Stitch.getDefaultAppClient().getAuth().loginWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("stitch", "logged in with custom function auth as user " + task.getResult().getId());
                        userId = client.getAuth().getUser().getId();
                        callback.onComplete(new Result<>(true, "", true));
                    } else {
                        Log.e("stitch", "failed to log in with custom function auth:", task.getException());
                        userId = null;
                        callback.onComplete(new Result<>(null, "Login failed!", false));
                    }
                });
    }

    public void addUser(Request add_request, Callback<User, String> result) {
        User user = add_request.getUser();

        if (user == null) {
            Log.e(TAG, "Must have a user to be able to add them!");
            result.onComplete(new Result<>(null, "Invalid parameters", false));
        }

        assert user != null;

        findUser(user, result::onComplete);
    }

    private void findUser(User user, Callback<User, String> result) {
        String user_id = user.getUser_Id();
        Task<User> find_user_task = this.users.findOne(eq("user_id", user_id));

        find_user_task.addOnCompleteListener(find_user_task_result -> {
            if (find_user_task_result.isSuccessful()) {
                if (find_user_task_result.getResult() == null) {
                    insertUser(user, result::onComplete);
                } else {
                    Log.e(TAG, "Could not add user", new Exception("Duplicate user"));
                    result.onComplete(new Result<>(null, "Duplicate user!", false));
                }
            } else {
                Log.e(TAG, "Could not add user", new Exception("Find Request Failed!"));
                result.onComplete(new Result<>(null, "Find Request Failed!", false));
            }
        });
    }

    private void insertUser(User user, Callback<User, String> result) {
        Log.d(TAG, user.toString());
        Task<RemoteInsertOneResult> task2 = users.insertOne(user);
        task2.addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()) {
                Log.i(TAG, "Added user to database!");
                insertDBObj(user, add_dbobj_result -> {
                    if (add_dbobj_result.isSuccessful())
                        Log.i(TAG, "Added subscription table to database!");
                    else
                        Log.e(TAG, "Could not add user", new Exception("Insert Request Failed!"));
                    result.onComplete(add_dbobj_result);
                });
            } else {
                Log.e(TAG, "Could not add user", new Exception("Insert Request Failed!"));
                result.onComplete(new Result<>(null, "Insert Request Failed!", false));
            }
        });
    }

    private void insertDBObj(User user, Callback<User, String> result) {
        Task<RemoteInsertOneResult> insert_dbobj_task = subscriptions.insertOne(new SubscriptionDBObject(new ObjectId(), user.getOwner_id(), user.getUser_Id(), 0, Collections.emptyList()));
        insert_dbobj_task.addOnCompleteListener(insert_dbobj_task_result -> result.onComplete(new Result<>(user, "Insert Request Failed!", insert_dbobj_task_result.isSuccessful())));
    }

    public void addSubscription(Request add_request, Callback<Subscription, String> result) {
        if (add_request.getUser() == null) {
            Log.e(TAG, "Must have a user to be able to add a subscription!");
            result.onComplete(new Result<>(null, "Invalid parameters", false));
        }

        getSubscriptionList(add_request, subscription_result -> {
            SubscriptionDBObject DBObj = subscription_result.getResult();
            Subscription add_sub = add_request.getSubscription();

            if (DBObj == null) {
                Log.e(TAG, "User was incorrectly added", new Exception("Error adding subscription!"));
                result.onComplete(new Result<>(null, "User was incorrectly added!", false));
            } else {
                if (checkDuplicates(DBObj, add_sub)) {
                    Log.e(TAG, "Could not add subscription", new Exception("Duplicate subscription"));
                    result.onComplete(new Result<>(null, "Subscription is already in the database!", false));
                }

                DBObj.getSubscriptions().add(add_sub);

                updateSubscriptionDBObject(DBObj, add_request.getUser(), update_result -> {
                    if (update_result.isSuccessful()) {
                        result.onComplete(new Result<>(add_request.getSubscription(), "", update_result.getResult()));
                    } else {
                        result.onComplete(new Result<>(add_request.getSubscription(), update_result.getFailure(), update_result.getResult()));
                    }
                });
            }
        });
    }

    private void getSubscriptionList(Request add_request, Callback<SubscriptionDBObject, String> result) {
        Task<SubscriptionDBObject> find_subscription = subscriptions.findOne(eq("user_id", add_request.getUser().getUser_Id()));
        find_subscription.addOnCompleteListener(find_subscription_result -> {
            result.onComplete(new Result<>(find_subscription_result.getResult(), "Could not get subscription list!", find_subscription_result.isSuccessful()));
        });
    }

    private boolean checkDuplicates(SubscriptionDBObject dbObj, Subscription adding) {
        List<Subscription> subs = dbObj.getSubscriptions();
        for (Subscription sub : subs) {
            if (sub.accessTitle().equals(adding.accessTitle())) {
                return true;
            }
        }
        return false;
    }

    private void updateSubscriptionDBObject(SubscriptionDBObject obj, User user, Callback<Boolean, String> result) {
        Task<RemoteUpdateResult> update_task = subscriptions.updateOne(eq("user_id", user.getUser_Id()), SubscriptionDBObject.toBsonDocument(obj));
        update_task.addOnCompleteListener(update_task_result -> result.onComplete(new Result<>(true, "Update Request Failed!", update_task_result.isSuccessful())));
    }

    public void insertSearchSubscription(Request add_request, Callback<Subscription, String> result) {
        //To only be used in "non-production" for testing or for ease of use.
        Task<RemoteInsertOneResult> insert_task = this.supported_subscriptions.insertOne(add_request.getSubscription());
        insert_task.addOnCompleteListener(task_result -> {
            if (task_result.isSuccessful()) {
                Log.i(TAG, "successfully inserted subscription");
                result.onComplete(new Result<>(add_request.getSubscription(), "", true));
            } else {
                Log.e("app", "failed to insert subscription with: ", task_result.getException());
                result.onComplete(new Result<>(null, task_result.getException().getMessage(), true));
            }
        });
    }

    public void getSearchSubscriptions(Callback<List<Subscription>, String> return_data) {
        Task<List<Subscription>> find_subscriptions = this.supported_subscriptions.find().into(new ArrayList<>());
        find_subscriptions.addOnCompleteListener(result -> {
            if (result.isSuccessful()) {
                List<Subscription> subscriptions = result.getResult();
                Log.i(TAG, String.format("successfully found %d subscriptions", subscriptions.size()));

                Calendar cal = Calendar.getInstance();
                subscriptions.add(0, new Subscription("custom", "Custom", false, cal.getTime(), Subscription.Recurrences.MONTHLY, 9.99, 0.00));
                return_data.onComplete(new Result<>(subscriptions, "", true));
            } else {
                Log.e("app", "failed to find names with: ", result.getException());
                return_data.onComplete(new Result<>(Collections.emptyList(), result.getException().getMessage(), false));
            }
        });
    }
}
