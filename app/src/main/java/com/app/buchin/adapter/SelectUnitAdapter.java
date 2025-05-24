package com.app.buchin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.buchin.R;
import com.app.buchin.model.UnitObject;

import java.util.List;

public class SelectUnitAdapter extends ArrayAdapter<UnitObject> {

    private final Context context;

    public SelectUnitAdapter(@NonNull Context context, @LayoutRes int resource,
                             @NonNull List<UnitObject> list) {
        super(context, resource, list);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_choose_option, null);
            TextView tvSelected = convertView.findViewById(R.id.tv_selected);
            // 1.1.1 UnitActivity hiển thị danh sách này cho Bar Manager (in dropdown for unit selection).
            tvSelected.setText(this.getItem(position).getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_drop_down_option, null);
        TextView tvName = view.findViewById(R.id.textview_name);
        // 1.1.1 UnitActivity hiển thị danh sách này cho Bar Manager (in dropdown for unit selection).
        tvName.setText(this.getItem(position).getName());
        return view;
    }
}