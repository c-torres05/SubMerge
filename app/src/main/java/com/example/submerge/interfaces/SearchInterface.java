package com.example.submerge.interfaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.example.submerge.models.Subscription;

import com.example.submerge.R; // Fixed R Package Issue

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class SearchInterface extends AppCompatActivity {

    static DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;

    List<Subscription> subsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        subsList = new ArrayList<>();
        searchAdapter = new SearchAdapter(subsList);

        databaseHandler.getSearchSubscriptions(result -> {
            for (Subscription sub : result.getResult()) {
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
        }


    public static void setDatabaseHandler(DatabaseHandler new_handler) {
        databaseHandler = new_handler;
    }
}