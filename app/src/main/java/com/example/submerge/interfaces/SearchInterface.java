package com.example.submerge.interfaces;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.submerge.models.Subscription;

import com.example.submerge.R; // Fixed R Package Issue
import com.example.submerge.models.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class SearchInterface extends AppCompatActivity {

    static NotificationHandler notificationHandler;
    static User user;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;

    List<Subscription> subsList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        decodeIntent(getIntent());

        subsList = new ArrayList<>();
        searchAdapter = new SearchAdapter(subsList);
        DatabaseHandler.getInstance().getSearchSubscriptions(result -> {
            for (Subscription sub : result.getResult()) {
                sub.setImageDrawable(getResources().getIdentifier(sub.getImage().toLowerCase(), "drawable", this.getPackageName()));
                searchAdapter.addItem(sub);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });

        RecyclerItemClickListener.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> gotoEdit(v, position));
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Intent main = new Intent();
                setResult(Activity.RESULT_CANCELED, main);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void gotoEdit(View view, int posistion) {
        Intent edit = new Intent(this, Edit.class);

        Edit.setNotificationHandler(notificationHandler);

        edit.putExtra("from", "search");

        Subscription.encode_intent(edit, searchAdapter.get(posistion));
        User.encode_intent(edit, SearchInterface.user);

        startActivityForResult(edit, 2);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void decodeIntent(Intent intent) {
        Intent from_main = getIntent();
        user = User.decode_intent(from_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("SubMerge", "finishing!");

        setResult(resultCode, data);
        finish();
    }

//    public static void setDatabaseHandler(DatabaseHandler new_handler) {
//        databaseHandler = new_handler;
//    }
//
//    public static void setNotificationHandler(NotificationHandler new_handler) {
//        notificationHandler = new_handler;
//    }
}