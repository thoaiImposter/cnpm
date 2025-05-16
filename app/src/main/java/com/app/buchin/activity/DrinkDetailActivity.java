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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        getDataIntent();
        initToolbar();
        initView();
    }

    public void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mDrink = (Drink) bundle.get(Constants.KEY_INTENT_DRINK_OBJECT);
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mDrink.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager_2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this, mDrink);
        viewPager2.setAdapter(myPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.label_added));
            } else {
                tab.setText(getString(R.string.label_used));
            }
        }).attach();
    }
}
