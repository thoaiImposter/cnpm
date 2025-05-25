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

public class ManageDrinkAdapter extends RecyclerView.Adapter<ManageDrinkAdapter.ManageDrinkViewHolder> {

    private final List<Drink> mListDrink;   //Danh sách đối tuợng đồ uống
    private final IManagerDrinkListener iManagerDrinkListener;  //Inteface xử lí kh ng dùng click vào item

    public interface IManagerDrinkListener {
        void clickItem(Drink drink);
    }

    public ManageDrinkAdapter(List<Drink> mListDrink, IManagerDrinkListener iManagerDrinkListener) {
        this.mListDrink = mListDrink;
        this.iManagerDrinkListener = iManagerDrinkListener;
    }

    @NonNull
    @Override
    public ManageDrinkAdapter.ManageDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_drink, parent, false);
        return new ManageDrinkAdapter.ManageDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageDrinkAdapter.ManageDrinkViewHolder holder, int position) {
        Drink drink = mListDrink.get(position);
        if (drink == null) {
            return;
        }
        holder.tvName.setText(drink.getName());
        String strCurrentQuantity = drink.getQuantity() + " " + drink.getUnitName();
        holder.tvCurrentQuantity.setText(strCurrentQuantity);

        // Listener
        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerDrinkListener.clickItem(drink);
            }
        });
    }
    //Trả về s loại đồ uống
    @Override
    public int getItemCount() {
        if (mListDrink != null) {
            return mListDrink.size();
        }
        return 0;
    }

    public static class ManageDrinkViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvCurrentQuantity;
        private final RelativeLayout layoutItem;

        public ManageDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
