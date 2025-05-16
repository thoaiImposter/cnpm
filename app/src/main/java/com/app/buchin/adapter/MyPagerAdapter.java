package com.app.buchin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.buchin.fragment.DrinkDetailAddedFragment;
import com.app.buchin.fragment.DrinkDetailUsedFragment;
import com.app.buchin.model.Drink;

public class MyPagerAdapter extends FragmentStateAdapter {

    private final Drink mDrink;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Drink drink) {
        super(fragmentActivity);
        this.mDrink = drink;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new DrinkDetailUsedFragment(mDrink);
        }
        return new DrinkDetailAddedFragment(mDrink);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
