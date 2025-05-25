package com.app.buchin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.buchin.fragment.DrinkDetailAddedFragment;
import com.app.buchin.fragment.DrinkDetailUsedFragment;
import com.app.buchin.model.Drink;

public class MyPagerAdapter extends FragmentStateAdapter {

    private final Drink mDrink; // Đồ uống được truyền vào để hiển thị chi tiết

    // Constructor nhận vào Activity và đối tượng đồ uống
    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, Drink drink) {
        super(fragmentActivity);
        this.mDrink = drink;
    }

    // 5.3.1 - Tạo Fragment tương ứng với từng tab (0: nhập, 1: xuất)
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            // Tab "Đã sử dụng"
            return new DrinkDetailUsedFragment(mDrink);
        }
        // Tab "Đã nhập"
        return new DrinkDetailAddedFragment(mDrink);
    }

    // Trả về số lượng tab: 2 (đã nhập, đã sử dụng)
    @Override
    public int getItemCount() {
        return 2;
    }
}
