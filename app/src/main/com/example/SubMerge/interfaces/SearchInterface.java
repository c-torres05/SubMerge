package com.example.SubMerge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchInterface extends AppCompatActivity {

    ListView search_subscriptions;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        search_subscriptions = (ListView) findViewById(R.id.search_subscriptions);

        ArrayList<String> arraySubscriptions = new ArrayList<>();
        arraySubscriptions.addAll(Arrays.asList(getResources().getStringArray(R.array.my_subscriptions)));

        adapter = new ArrayAdapter<String>(
                SearchInterface.this,
                android.R.layout.simple_list_item_1,
                arraySubscriptions
        );

        search_subscriptions.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_subscriptions);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
