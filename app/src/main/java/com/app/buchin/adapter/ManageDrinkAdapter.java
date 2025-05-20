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

public class ManageDrinkAdapter  {

    private final List<Drink> mListDrink;
    private final IManagerDrinkListener iManagerDrinkListener;

    public interface IManagerDrinkListener {
        void clickItem(Drink drink);
    }

    public ManageDrinkAdapter(List<Drink> mListDrink, IManagerDrinkListener iManagerDrinkListener) {
        this.mListDrink = mListDrink;
        this.iManagerDrinkListener = iManagerDrinkListener;
    }


}
