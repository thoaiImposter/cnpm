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

    public interface IManagerUnitListener {// 4. Bar Manager thao tác với đơn vị đo lường:
        void editUnit(UnitObject unitObject);// 4.2 Sửa thông tin đơn vị:

        void deleteUnit(UnitObject unitObject);// 4.3 Xóa đơn vị:
    }

    public UnitAdapter(List<UnitObject> list, IManagerUnitListener listener) {// 3.3 UnitActivity hiển thị danh sách đơn vị đo lường (dữ liệu truyền vào adapter)
        this.mListUnit = list;
        this.iManagerUnitListener = listener;
    }

    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3.3. UnitActivity khởi tạo view item cho danh sách đơn vị
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unit, parent, false);
        return new UnitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        UnitObject unitObject = mListUnit.get(position);
        if (unitObject == null) {
            return;
        }
        // 3.3 Hiển thị tên đơn vị lên giao diện item
        holder.tvName.setText(unitObject.getName());

        // Listener
        // 4.2.1 - 4.2.7 Sửa thông tin đơn vị:
        holder.imgEdit.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 4.2.1 Bar Manager chọn đơn vị cần chỉnh sửa
                // 4.2.2 Ứng dụng mở Dialog chỉnh sửa (xử lý bên Activity)
                iManagerUnitListener.editUnit(unitObject);
            }
        });
        // 4.3.1 - 4.3.6 Xóa đơn vị:
        holder.imgDelete.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 4.3.1 Bar Manager nhấn nút "Xoá"
                // 4.3.2 Kiểm tra đơn vị có đang được dùng (xử lý bên Activity)
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
