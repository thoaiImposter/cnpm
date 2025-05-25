package com.app.buchin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.R;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;

import java.util.List;

public class ManageDrinkAdapter extends RecyclerView.Adapter {

    // Danh sách đồ uống cần hiển thị
    private final List<Drink> mListDrink;

    // Listener để xử lý sự kiện khi click vào một đồ uống
    private final IManagerDrinkListener iManagerDrinkListener;

    // Constructor để khởi tạo Adapter
    public ManageDrinkAdapter(List<Drink> mListDrink, IManagerDrinkListener iManagerDrinkListener) {
        this.mListDrink = mListDrink;
        this.iManagerDrinkListener = iManagerDrinkListener;
    }

    // Interface định nghĩa sự kiện click vào một đồ uống
    public interface IManagerDrinkListener {
        void clickItem(Drink drink);
    }

    // Phương thức tạo ViewHolder (hiện đang trả về null - chưa implement)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    // Phương thức bind dữ liệu vào ViewHolder (hiện chưa implement)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    }

    // Phương thức trả về số lượng item trong danh sách (hiện đang trả về 0 - chưa implement)
    @Override
    public int getItemCount() {
        return 0;
    }
}