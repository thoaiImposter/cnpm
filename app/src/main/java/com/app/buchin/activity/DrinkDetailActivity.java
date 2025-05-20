package com.app.buchin.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.viewpager2.widget.ViewPager2;

import com.app.buchin.R;
import com.app.buchin.adapter.MyPagerAdapter;
import com.app.buchin.constant.Constants;
import com.app.buchin.model.Drink;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DrinkDetailActivity extends BaseActivity {

    private Drink mDrink;

    // 10.1.14 Giao diện chi tiết đồ uống được hiển thị, kèm thông tin: số lượng bán, tổng tiền, thời gian.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 10.1.14.1 Giao diện chi tiết đồ uống được hiển thị
        setContentView(R.layout.activity_drink_detail);
        // 10.1.14.2 Hệ thống lấy dữ liệu đồ uống từ intent
        getDataIntent();
        initToolbar();
        // 10.1.14.3 Hiển thị kèm thông tin
        initView();
    }

    public void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        // 10.1.14.2.1 Nhận đối tượng đồ uống (Drink) truyền vào
        mDrink = (Drink) bundle.get(Constants.KEY_INTENT_DRINK_OBJECT);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mDrink.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // 10.1.16 Người quản lý quay lại màn hình thống kê bằng nút back.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Người quản lý nhấn nút quay lại
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 10.1.15 Người quản lý có thể chuyển tab giữa “Đã nhập” và “Đã tiêu thụ” để xem thêm thông tin.
    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager_2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this, mDrink);
        viewPager2.setAdapter(myPagerAdapter);
        // 10.1.15.1 Thiết lập hai tab: “Đã nhập” và “Tiêu thụ”
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.label_added)); // Tab hiển thị lịch sử nhập
            } else {
                tab.setText(getString(R.string.label_used));  // Tab hiển thị lịch sử tiêu thụ
            }
        }).attach();
    }

    // kiểm tra dữ liệu truyền vào từ intent
    private void logDrinkDataIfDebug() {
        if (mDrink != null) {
            System.out.println("Drink detail - ID: " + mDrink.getId()
                    + ", Name: " + mDrink.getName());
        }
    }

    // Tách setupTabLabels() riêng cho TabLayoutMediator
    private void setupTabLabels(TabLayout tabLayout, ViewPager2 viewPager2) {
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.label_added));
            } else {
                tab.setText(getString(R.string.label_used));
            }
        }).attach();
    }

    private void refreshDetailData() {
        // Dự phòng: tải lại dữ liệu từ Firebase nếu cần
        getDataIntent();
        initToolbar();
        initView();
    }

}
