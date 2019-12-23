package com.example.akshiban.everything.notes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akshiban.everything.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<KV> {

    public SpinnerAdapter(Context context, ArrayList<KV> items) {
        super(context, R.layout.spinner_item, items);
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

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.spinner_item);
        KV kv = getItem(position);
        textViewName.setText(kv.getKey());

        return convertView;
    }
}
