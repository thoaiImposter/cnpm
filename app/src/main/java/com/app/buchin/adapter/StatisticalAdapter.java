package com.app.buchin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.R;
import com.app.buchin.constant.Constants;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Statistical;

import java.util.List;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.StatisticalViewHolder> {

    private final List<Statistical> mListStatisticals;
    private final IManagerStatisticalListener iManagerStatisticalListener;

    public interface IManagerStatisticalListener {
        void onClickItem (Statistical statistical);
    }

    public StatisticalAdapter(List<Statistical> mListStatisticals, IManagerStatisticalListener iManagerStatisticalListener) {
        this.mListStatisticals = mListStatisticals;
        this.iManagerStatisticalListener = iManagerStatisticalListener;
    }

    @NonNull
    @Override
    public StatisticalAdapter.StatisticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical, parent, false);
        return new StatisticalAdapter.StatisticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticalAdapter.StatisticalViewHolder holder, int position) {
        Statistical statistical = mListStatisticals.get(position);
        if (statistical == null) {
            return;
        }
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvDrinkName.setText(statistical.getDrinkName());
        String strQuantity = statistical.getQuantity() + " " + statistical.getDrinkUnitName();
        holder.tvQuantity.setText(strQuantity);
        String strTotalPrice = statistical.getTotalPrice() + Constants.CURRENCY;
        holder.tvTotalPrice.setText(strTotalPrice);

        holder.layoutItem.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                iManagerStatisticalListener.onClickItem(statistical);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListStatisticals != null) {
            return mListStatisticals.size();
        }
        return 0;
    }

    public static class StatisticalViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStt;
        private final TextView tvDrinkName;
        private final TextView tvQuantity;
        private final TextView tvTotalPrice;
        private final LinearLayout layoutItem;

        public StatisticalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tv_stt);
            tvDrinkName = itemView.findViewById(R.id.tv_drink_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
