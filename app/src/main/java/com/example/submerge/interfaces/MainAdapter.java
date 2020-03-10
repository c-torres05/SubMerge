package com.example.submerge.interfaces;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.submerge.R;
import com.example.submerge.models.Subscription;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private List<Subscription> list;
    private List<Subscription> listFull;

    class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView change_image;
        TextView title;
        TextView message;
        TextView cost;
        TextView change;

        MainViewHolder(View item) {
            super(item);
            image = item.findViewById(R.id.image);
            change_image = item.findViewById(R.id.change_image);
            title = item.findViewById(R.id.title);
            message = item.findViewById(R.id.message);
            cost = item.findViewById(R.id.cost);
            change = item.findViewById(R.id.change);
        }
    }

    public MainAdapter(List<Subscription> list) {
        this.list = list;
        listFull = new ArrayList<>(list);
    }

    public void addItem(Subscription sub) {
        this.list.add(sub);
        this.listFull.add(sub);
        Log.i("SubMerge", "added Item!");
        notifyItemInserted(list.size() - 1);
    }

    public void removeItem(Subscription sub) {
        int index = this.list.indexOf(sub);
        this.list.remove(sub);
        this.listFull.remove(sub);
        notifyItemInserted(index);
        Log.i("SubMerge", "removed Item!");
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Subscription currentSubscription = list.get(position);

        holder.image.setImageResource(currentSubscription.getImage());
        holder.change_image.setImageResource(currentSubscription.getChangeImage());
        holder.title.setText(currentSubscription.getTitle());
        holder.message.setText(currentSubscription.getMessage());
        holder.cost.setText(currentSubscription.getCost());
        holder.change.setText(currentSubscription.getChange());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subscription> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Subscription subscription : listFull) {
                    if (subscription.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(subscription);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}