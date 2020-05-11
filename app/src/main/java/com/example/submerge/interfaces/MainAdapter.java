package com.example.submerge.interfaces;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.submerge.R;
import com.example.submerge.models.Subscription;

import java.util.ArrayList;
import java.util.Date;
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
        notifyItemInserted(list.size() - 1);
    }

    public void clear_list() {
        this.list.clear();
        this.listFull.clear();
    }

    public void set_list(List<Subscription> list) {
        this.list.addAll(list);
        this.listFull.addAll(list);
    }


    public void removeItem(Subscription sub) {
        int index = this.list.indexOf(sub);
        this.list.remove(sub);
        this.listFull.remove(sub);
        notifyItemInserted(index);
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new MainViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Subscription currentSubscription = list.get(position);

        holder.image.setImageResource(currentSubscription.getImageDrawable());
        if (currentSubscription.getTitle().length() > 15) {
            holder.title.setText(currentSubscription.getTitle().substring(0, 14));
        } else {
            holder.title.setText(currentSubscription.getTitle());
        }
        holder.message.setText(currentSubscription.getMessage());
        holder.cost.setText(currentSubscription.getCost());
        if (currentSubscription.accessChange() == 0) {
            holder.change_image.setImageResource(currentSubscription.getChangeImage());
            holder.change_image.setAlpha(0.00f);
            holder.change.setText(currentSubscription.getChange());
            holder.change.setAlpha(0.00f);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Subscription> getList() {
        return list;
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
