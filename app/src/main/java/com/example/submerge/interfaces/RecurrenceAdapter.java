package com.example.submerge.interfaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.submerge.R;

import java.util.ArrayList;

public class RecurrenceAdapter extends ArrayAdapter<RecurrenceItem> {

    public RecurrenceAdapter(Context context, ArrayList<RecurrenceItem> recurr)
    {
        super(context, 0, recurr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent)
    {
        if ( convertView == null )
        {
            convertView = LayoutInflater.from( getContext() ).inflate(R.layout.edit_spinner, parent, false);
        }
        TextView textView = convertView.findViewById( R.id.spinner_text_view);

        RecurrenceItem recur = getItem(position);

        if ( recur != null )
            textView.setText(recur.getRecurrence());

        return convertView;
    }
}
