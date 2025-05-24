package com.app.buchin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.R;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.UnitObject;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.UnitViewHolder> {

    private final List<UnitObject> mListUnit;
    private final IManagerUnitListener iManagerUnitListener;

    public interface IManagerUnitListener {
        // 1.3.1 Bar Manager chọn đơn vị cần chỉnh sửa trong danh sách.
        void editUnit(UnitObject unitObject);

        // 1.4.1 Bar Manager nhấn nút "Xoá" trên đơn vị cần xóa.
        void deleteUnit(UnitObject unitObject);
    }

    // 1.1.1 UnitActivity hiển thị danh sách này cho Bar Manager (via adapter initialization).
    public UnitAdapter(List<UnitObject> list, IManagerUnitListener listener) {
        this.mListUnit = list;
        this.iManagerUnitListener = listener;
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1.1.1 UnitActivity hiển thị danh sách này cho Bar Manager (create view for each unit item).
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        UnitObject unitObject = mListUnit.get(position);
        if (unitObject == null) {
            return;
        }
        // 1.1.1 UnitActivity hiển thị danh sách này cho Bar Manager (bind unit name to UI).
        holder.tvName.setText(unitObject.getName());

        // Listener
        // 1.3.1 Bar Manager chọn đơn vị cần chỉnh sửa trong danh sách.
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerUnitListener.editUnit(unitObject);
            }
        });

        // 1.4.1 Bar Manager nhấn nút "Xoá" trên đơn vị cần xóa.
        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerUnitListener.deleteUnit(unitObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListUnit != null) {
            return mListUnit.size();
        }
        return 0;
    }

    public static class UnitViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public UnitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}