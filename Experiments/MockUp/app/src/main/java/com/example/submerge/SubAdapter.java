package com.example.submerge;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;

import org.bson.types.ObjectId;

import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.SubItemViewHolder> {
    private static final String TAG = SubAdapter.class.getSimpleName();
    private final ItemUpdater itemUpdater;
    private List<SubItem> subItems;


    SubAdapter(final List<SubItem> subItems, final ItemUpdater itemUpdater) {
        this.subItems = subItems;
        this.itemUpdater = itemUpdater;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(
            @NonNull final ViewGroup parent,
            final int viewType
    ) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_item, parent, false);

        return new SubItemViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final SubItemViewHolder holder, final int position) {
        final SubItem item = subItems.get(position);

        holder.taskTextView.setText(item.getName());
//        holder.taskCheckbox.setChecked(item.isChecked());
    }

    @Override
    public int getItemCount() {
        return subItems.size();
    }

    public synchronized  void addItem(final SubItem subItem){
        subItems.add(subItem);
        SubAdapter.this.notifyDataSetChanged();
    }

    public synchronized void updateItem(final SubItem subItem) {
        if (subItems.contains(subItem)) {
            subItems.set(subItems.indexOf(subItem), subItem);
        } else {
            subItems.add(subItem);
        }
        SubAdapter.this.notifyDataSetChanged();
    }

    public synchronized void refreshItemList(final Task<List<SubItem>> getItemsTask) {
        getItemsTask.addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "failed to get items", task.getException());
                return;
            }

            clearItems();
            final List<SubItem> allItems = task.getResult();

            for (final SubItem newItem : allItems) {
                subItems.add(newItem);
            }

            notifyDataSetChanged();
        });
    }

    public synchronized void clearItems() {
        SubAdapter.this.subItems.clear();
    }

    // Callback for checkbox updates
    interface ItemUpdater {
//        void updateChecked(ObjectId itemId, boolean isChecked);

        void updateTask(ObjectId itemId, String currentTask);
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener {
        final TextView taskTextView;
//        final CheckBox taskCheckbox;

        SubItemViewHolder(final View view) {
            super(view);
            taskTextView = view.findViewById(R.id.tv_title);
//            taskCheckbox = view.findViewById(R.id.cb_sub_checkbox);

            // Set listeners
//            taskCheckbox.setOnCheckedChangeListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
//            taskCheckbox.setOnClickListener(this);
//            taskCheckbox.setOnLongClickListener(this);
        }

        @Override
        public synchronized void onCheckedChanged(
                final CompoundButton compoundButton,
                final boolean isChecked
        ) {
        }

        @Override
        public void onClick(final View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            final SubItem item = subItems.get(getAdapterPosition());
//            itemUpdater.updateChecked(item.get_id(), taskCheckbox.isChecked());
        }

        @Override
        public synchronized boolean onLongClick(final View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return false;
            }
            final SubItem item = subItems.get(getAdapterPosition());
            itemUpdater.updateTask(item.get_id(), item.getName());
            return true;
        }
    }
}
