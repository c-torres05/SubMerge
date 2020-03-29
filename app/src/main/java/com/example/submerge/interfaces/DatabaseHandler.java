package com.example.submerge.interfaces;
import android.util.Log;

import androidx.annotation.NonNull;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.auth.StitchUser;
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
import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseHandler {
    private StitchAppClient client = null;
    private RemoteMongoClient mongoClient;
    private String userId;
    private String name;

    private RemoteMongoCollection<User> users;
    private RemoteMongoCollection<SubscriptionDBObject> subscriptions;
    private RemoteMongoCollection<Subscription> supported_subscriptions;

    static String getAlphaNumericString(int n) {
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

    public String getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public void finishInit() {
        //To be called after logging in
        this.mongoClient = this.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        this.subscriptions = this.mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.SUBSCRIPTION_COLLECTION), SubscriptionDBObject.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(SubscriptionDBObject.codec)));
        this.supported_subscriptions = this.mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.SUPPORTED_SUBSCRIPTION_COLLECTION), Subscription.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(SubscriptionPackager.codec)));
        this.users = this.mongoClient
                .getDatabase(App.getResourses().getString(R.string.DATABASE))
                .getCollection(App.getResourses().getString(R.string.USER_COLLECTION), User.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(User.codec)));

    }

    public void loginAnon(Callback<Boolean, String> callback) {
        Log.d("stitch", "logging in anonymously");

        String name = getAlphaNumericString(15);
        Log.i("SubMerge", String.format("Made a user %s", name));
        AnonCredential credential = new AnonCredential(name);
        this.name = name;

        Stitch.getDefaultAppClient().getAuth().loginWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<StitchUser>() {
                    @Override
                    public void onComplete(@NonNull final Task<StitchUser> task) {
                        if (task.isSuccessful()) {
                            Log.d("stitch", "logged in with custom function auth as user " + task.getResult().getId());
                            userId = client.getAuth().getUser().getId();
                            callback.onComplete(new Result<Boolean, String>(true, "", true));
//                            userId = task.getResult().getId();
//                            Log.d("SubMerge", client.getAuth().getUser().toString());
                        } else {
                            Log.e("stitch", "failed to log in with custom function auth:", task.getException());
                            userId = null;
                            callback.onComplete(new Result<Boolean, String>(null, "Login failed!", false));
                        }
                    }
                });

//        this.client.getAuth().loginWithCredential(new AnonymousCredential()
//        ).continueWithTask(new Continuation<StitchUser, Task<Void>>() {
//            @Override
//            public Task<Void> then(@NonNull Task<StitchUser> task) throws Exception {
//                if (task.isSuccessful()) {
//                    Log.d("stitch", "logged in anonymously as user " + task.getResult());
//                    userId = client.getAuth().getUser().getId();
//                } else {
//                    Log.e("stitch", "failed to log in anonymously", task.getException());
//                    userId = null;
//                }
//                return null;
//            }
//        });
    }

    public void addUser(Request add_request, Callback<User, String> result) {
        if (add_request.getUser() == null) {
            Log.e("SubMerge", "Must have a user to be able to add them!");
            result.onComplete(new Result<>(null, "Invalid parameters", false));
        }

        String user_id = add_request.getUser().getUser_Id();
        Task<User> task = this.users.findOne(eq("user_id", user_id));
        task.addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() == null) {
                        Log.d("SubMerge", add_request.getUser().toString());
                        Task<RemoteInsertOneResult> task2 = users.insertOne(add_request.getUser());
                        task2.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
                            @Override
                            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i("SubMerge", "Added user to database!");
                                    Task<RemoteInsertOneResult> task3 = subscriptions.insertOne(new SubscriptionDBObject(new ObjectId(), add_request.getUser().getOwner_id(), add_request.getUser().getUser_Id(), 0, Collections.emptyList()));
                                    task3.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.i("SubMerge", "Added subscription table to database!");
                                                result.onComplete(new Result<>(add_request.getUser(), "", true));
                                            } else {
                                                Log.e("SubMerge", "Could not add user", new Exception("Insert Request Failed!"));
                                                result.onComplete(new Result<>(null, "Insert Request Failed!", false));
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("SubMerge", "Could not add user", new Exception("Insert Request Failed!"));
                                    result.onComplete(new Result<>(null, "Insert Request Failed!", false));
                                }
                            }
                        });
                    } else {
                        Log.d("SubMerge", task.getResult().toString());
                        Log.e("SubMerge", "Could not add user", new Exception("Duplicate user"));
                        result.onComplete(new Result<>(null, "Duplicate user!", false));
                    }
                } else {
                    Log.e("SubMerge", "Could not add user", new Exception("Find Request Failed!"));
                    result.onComplete(new Result<>(null, "Find Request Failed!", false));
                }
            }
        });
    }

    public void addSubscription(Request add_request, Callback<Subscription, String> result) {
        if (add_request.getUser() == null) {
            Log.e("SubMerge", "Must have a user to be able to add a subscription!");
            result.onComplete(new Result<>(null, "Invalid parameters", false));
        }

        Task<SubscriptionDBObject> task = subscriptions.findOne(eq("user_id", add_request.getUser().getUser_Id()));
        task.addOnCompleteListener(new OnCompleteListener<SubscriptionDBObject>() {
            @Override
            public void onComplete(@NonNull Task<SubscriptionDBObject> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() == null) {
                        Log.e("SubMerge", "User was incorrectly added", new Exception("Error adding subscription!"));
                        result.onComplete(new Result<>(null, "User was incorrectly added!", false));
                    }
                } else {
                    SubscriptionDBObject dbObj = task.getResult();
                    List<Subscription> subs = dbObj.getSubscriptions();
                    for (Subscription sub : subs) {
                        if (sub.accessTitle().equals(add_request.getSubscription().accessTitle())) {
                            Log.e("SubMerge", "Could not add subscription", new Exception("Duplicate subscription"));
                            result.onComplete(new Result<>(null, "Subscription is already in the database!", false));
                        }
                    }
                    subs.add(add_request.getSubscription());
                    Task<RemoteUpdateResult> task2 = subscriptions.updateOne(eq("user_id", add_request.getUser().getUser_Id()), SubscriptionDBObject.toBsonDocument(new SubscriptionDBObject(dbObj.get_id(), dbObj.getOwner_id(), dbObj.getUser_id(), subs.size(), subs)));
                    task2.addOnCompleteListener(new OnCompleteListener<RemoteUpdateResult>() {
                        @Override
                        public void onComplete(@NonNull Task<RemoteUpdateResult> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getModifiedCount() == 0) {
                                    Log.e("SubMerge", "Could not add subscription", new Exception("Nothing Modified"));
                                    result.onComplete(new Result<>(null, "Nothing modified!", false));
                                } else {
                                    Log.i("SubMerge", "Added subscription to database!");
                                    result.onComplete(new Result<>(add_request.getSubscription(), "", true));
                                }
                            } else {
                                Log.e("SubMerge", "Could not add subscription", new Exception("Update Request Failed!"));
                                result.onComplete(new Result<>(null, "Update Request Failed!", false));
                            }
                        }
                    });
                }
            }
        });
    }

    public void getSubscriptionList(Callback<SubscriptionDBObject, String> result) {

    }

    public void insertSearchSubscription(Request add_request, Callback<Subscription, String> result) {
        //To only be used in "non-production" for testing or for ease of use.
        Task<RemoteInsertOneResult> task = this.supported_subscriptions.insertOne(add_request.getSubscription());
        task.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if (task.isSuccessful()) {
                    Log.i("SubMerge", "successfully inserted subscription");
                    result.onComplete(new Result<>(add_request.getSubscription(), "", true));
                } else {
                    Log.e("app", "failed to insert subscription with: ", task.getException());
                    result.onComplete(new Result<>(null, task.getException().getMessage(), true));
                }
            }
        });
    }

    public void getSearchSubscriptions(Callback<List<String>, String> result) {
        Task<List<Subscription>> task = this.supported_subscriptions.find().into(new ArrayList<>());
        task.addOnCompleteListener(new OnCompleteListener<List<Subscription>>() {
            @Override
            public void onComplete(@NonNull Task<List<Subscription>> task) {
                if (task.isSuccessful()) {
                    List<Subscription> subscriptions = task.getResult();
                    Log.i("SubMerge", String.format("successfully found %d subscriptions", subscriptions.size()));
                    List<String> names = new ArrayList<>();
                    for (Subscription sub : subscriptions)
                        names.add(sub.accessTitle());
                    Log.i("SubMerge", String.format("successfully found %d names", names.size()));
                    result.onComplete(new Result<>(names, "", true));
                } else {
                    Log.e("app", "failed to find names with: ", task.getException());
                    result.onComplete(new Result<>(Collections.emptyList(), task.getException().getMessage(), false));
                }
            }
        });
    }
}
