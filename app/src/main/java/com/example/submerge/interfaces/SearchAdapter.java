package com.example.submerge.interfaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.submerge.R;
import com.example.submerge.models.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerAdapter";
    List<Subscription> subsList;
    List<Subscription> subsListAll;

    public SearchAdapter(List<Subscription> subsList) {
        this.subsList = subsList;
        subsListAll = new ArrayList<>();
        subsListAll.addAll(subsList);
    }

    public void addItem(Subscription sub) {
        this.subsList.add(sub);
        this.subsListAll.add(sub);
        notifyItemInserted(subsList.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.rowCountTextView.setText(String.valueOf(position));
        //holder.textView.setText(subsList.get(position));
        Subscription currentSubscription = subsList.get(position);

        holder.imageView.setImageResource(currentSubscription.getImage());
        //holder.change_image.setImageResource(currentSubscription.getChangeImage());
        holder.textView.setText(currentSubscription.getTitle());
//        holder.message.setText(currentSubscription.getMessage());
//        holder.cost.setText(currentSubscription.getCost());
//        holder.change.setText(currentSubscription.getChange()); 
    }

    @Override
    public int getItemCount() {

        return subsList.size();
    }

    @Override
    public Filter getFilter() {

        return myFilter;
    }

    Filter myFilter = new Filter() {

        //Automatic on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Subscription> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(subsListAll);
            } else {
                for (Subscription subscription: subsListAll) {
                    if (subscription.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(subscription);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Automatic on UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            subsList.clear();
            subsList.addAll((Collection<? extends Subscription>) filterResults.values);
            notifyDataSetChanged();
        }
    };



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView, rowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            //rowCountTextView = itemView.findViewById(R.id.rowCountTextView);

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), subsList.get(getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }
}
















