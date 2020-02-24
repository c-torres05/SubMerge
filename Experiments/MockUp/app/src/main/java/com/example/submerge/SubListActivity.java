package com.example.submerge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.internal.common.BsonUtils;

import org.bson.BsonArray;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class SubListActivity extends AppCompatActivity {
    private static final String TAG = SubListActivity.class.getSimpleName();
    private SubAdapter subAdapter;
    private RemoteMongoCollection<SubItem> items;
    private String userId;

    public static StitchAppClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_list);

        client = Stitch.getDefaultAppClient();

        final RemoteMongoClient mongoClient = client.getServiceClient(
                RemoteMongoClient.factory, "mongodb-atlas");

        items = mongoClient
                .getDatabase(SubItem.SUB_DATABASE)
                .getCollection(SubItem.SUB_ITEMS_COLLECTION, SubItem.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        BsonUtils.DEFAULT_CODEC_REGISTRY,
                        CodecRegistries.fromCodecs(SubItem.codec)));

        final RecyclerView subRecyclerView = findViewById(R.id.list_items);
        final RecyclerView.LayoutManager subLayoutManager = new LinearLayoutManager(this);
        subRecyclerView.setLayoutManager(subLayoutManager);

        subAdapter = new SubAdapter(
                new ArrayList<>(),
                new SubAdapter.ItemUpdater() {
//                    @Override
//                    public void updateChecked(final ObjectId itemId, final boolean isChecked) {
//                        final Document updateDoc =
//                                new Document("$set", new Document("true", isChecked));
//                        items.updateOne(new Document("_id", itemId), updateDoc);
//                    }

                    @Override
                    public void updateTask(final ObjectId itemId, final String currentTask) {
                        showEditItemDialog(itemId, currentTask);
                    }
                });
        subRecyclerView.setAdapter(subAdapter);
        doLogin();
        enableAddItem();
    }

    private void enableAddItem() {
        findViewById(R.id.add_item).setOnClickListener(ignored ->
                showAddItemDialog());
    }


    private void doLogin() {
        if (client.getAuth().getUser() != null && client.getAuth().getUser().isLoggedIn()) {
            userId = client.getAuth().getUser().getId();
            TextView tvId = findViewById(R.id.txt_user_id);
            tvId.setText("Logged in with ID \"" + userId + "\"");
            subAdapter.refreshItemList(getItems());
            return;
        } else {
            Intent intent = new Intent(SubListActivity.this, LogonActivity.class);
            startActivityForResult(intent, 111);
        }
    }

    private void showAddItemDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        final View view = getLayoutInflater().inflate(R.layout.edit_item_dialog, null);
        final EditText input = view.findViewById(R.id.et_sub_item_task);

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton(
                "Add",
                (dialog, which) -> addSubItem(input.getText().toString(), 20L, 7, 7.99));
        builder.setNegativeButton(
                "Cancel",
                (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditItemDialog(final ObjectId itemId, final String currentTask) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        final View view = getLayoutInflater().inflate(R.layout.edit_item_dialog, null);
        final EditText input = view.findViewById(R.id.et_sub_item_task);

        input.setText(currentTask);
        input.setSelection(input.getText().length());

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton(
                "Update",
                (dialog, which) -> updateSubItemTask(itemId, input.getText().toString()));
        builder.setNegativeButton(
                "Cancel",
                (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private Task<List<SubItem>> getItems() {
        return items.find().into(new ArrayList<>());
    }

    private void addSubItem(final String name, long renewal, int recurrance, double currentCost) {
        final SubItem newItem = new SubItem(new ObjectId(), userId, name, "@drawable/netflix", renewal, recurrance, currentCost, new BsonArray()); //, false);
        items.insertOne(newItem)
                .addOnSuccessListener(result -> {
                    subAdapter.addItem(newItem);
                })
                .addOnFailureListener(e -> Log.e(TAG, "failed to insert sub item", e));
    }

    private void updateSubItemTask(final ObjectId itemId, final String newName) {
        final BsonObjectId docId = new BsonObjectId(itemId);
        items.updateOne(
                new Document("_id", docId),
                new Document("$set", new Document(SubItem.Fields.NAME, newName)))
                .addOnSuccessListener(result -> {
                    items.find(new Document("_id", docId)).first()
                            .addOnSuccessListener(item -> {
                                if (item == null) {
                                    return;
                                }
                                subAdapter.updateItem(item);
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "failed to find sub item", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "failed to insert sub item", e));
    }

//    private void clearCheckedSubItems() {
//        getItems().addOnSuccessListener(subItems -> {
//            for (final SubItem item : subItems) {
//                if (item.isChecked()) {
//                    items.deleteOne(new Document("_id", item.get_id()));
//                }
//            }
//            subAdapter.refreshItemList(getItems());
//        });
//    }
//
//    private void clearAllSubItems() {
//        getItems().addOnSuccessListener(subItems -> {
//            for (final SubItem item : subItems) {
//                items.deleteOne(new Document("_id", item.get_id())).addOnSuccessListener(remoteDeleteResult -> {
//                    //tasks.add();
//                });
//            }
//            subAdapter.refreshItemList(getItems());
//        });
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
//            doLogin();
//        }
//        if (resultCode != Activity.RESULT_OK || requestCode != 111) {
//            Toast.makeText(this.getApplicationContext(), "Error logging in. Check the app logs for details.", Toast.LENGTH_LONG).show();
//        }
//        doLogin();
//    }
}
