package com.app.buchin.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.StatisticalAdapter;
import com.app.buchin.constant.Constants;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;
import com.app.buchin.model.History;
import com.app.buchin.model.Statistical;
import com.app.buchin.utils.DateTimeUtils;
import com.app.buchin.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticalActivity extends BaseActivity {

    private TextView tvTotalValue;
    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private int mType;
    private boolean isDrinkPopular;
    private List<Statistical> mListStatisticals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);

        getDataIntent();
        initToolbar();
        initUi();
        getListStatistical();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        mType = bundle.getInt(Constants.KEY_TYPE_STATISTICAL);
        isDrinkPopular = bundle.getBoolean(Constants.KEY_DRINK_POPULAR);
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        switch (mType) {
            case Constants.TYPE_REVENUE:
                getSupportActionBar().setTitle(getString(R.string.feature_revenue));
                break;

            case Constants.TYPE_COST:
                getSupportActionBar().setTitle(getString(R.string.feature_cost));
                break;
        }
        if (isDrinkPopular) {
            getSupportActionBar().setTitle(getString(R.string.feature_drink_popular));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void initUi() {
        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        tvTotalValue = findViewById(R.id.tv_total_value);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        LinearLayout layoutFilter = findViewById(R.id.layout_filter);
        View viewDivider = findViewById(R.id.view_divider);
        RelativeLayout layoutBottom = findViewById(R.id.layout_bottom);
        if (isDrinkPopular) {
            layoutFilter.setVisibility(View.GONE);
            viewDivider.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutFilter.setVisibility(View.VISIBLE);
            viewDivider.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
        }

        TextView labelTotalValue = findViewById(R.id.label_total_value);
        switch (mType) {
            case Constants.TYPE_REVENUE:
                labelTotalValue.setText(getString(R.string.label_total_revenue));
                break;

            case Constants.TYPE_COST:
                labelTotalValue.setText(getString(R.string.label_total_cost));
                break;
        }

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFuntion.showDatePicker(StatisticalActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListStatistical();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFuntion.showDatePicker(StatisticalActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListStatistical();
                });
            }
        });
    }

    private void getListStatistical() {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (canAddHistory(history)) {
                                list.add(history);
                            }
                        }
                        handleDataHistories(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private boolean canAddHistory(@Nullable History history) {
        if (history == null) {
            return false;
        }
        if (Constants.TYPE_REVENUE == mType) {
            if (history.isAdd()) {
                return false;
            }
        } else {
            if (!history.isAdd()) {
                return false;
            }
        }
        String strDateFrom = tvDateFrom.getText().toString();
        String strDateTo = tvDateTo.getText().toString();
        if (StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            return true;
        }
        if (StringUtil.isEmpty(strDateFrom) && !StringUtil.isEmpty(strDateTo)) {
            long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
            return history.getDate() <= longDateTo;
        }
        if (!StringUtil.isEmpty(strDateFrom) && StringUtil.isEmpty(strDateTo)) {
            long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
            return history.getDate() >= longDateFrom;
        }
        long longDateTo = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateTo));
        long longDateFrom = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(strDateFrom));
        return history.getDate() >= longDateFrom && history.getDate() <= longDateTo;
    }

    private void handleDataHistories(List<History> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mListStatisticals != null) {
            mListStatisticals.clear();
        } else {
            mListStatisticals = new ArrayList<>();
        }
        for (History history : list) {
            long drinkId = history.getDrinkId();
            if (checkStatisticalExist(drinkId)) {
                getStatisticalFromDrinkId(drinkId).getHistories().add(history);
            } else {
                Statistical statistical = new Statistical();
                statistical.setDrinkId(history.getDrinkId());
                statistical.setDrinkName(history.getDrinkName());
                statistical.setDrinkUnitId(history.getUnitId());
                statistical.setDrinkUnitName(history.getUnitName());
                statistical.getHistories().add(history);
                mListStatisticals.add(statistical);
            }
        }
        if (isDrinkPopular) {
            List<Statistical> listPopular = new ArrayList<>(mListStatisticals);
            Collections.sort(listPopular, (statistical1, statistical2)
                    -> statistical2.getTotalPrice() - statistical1.getTotalPrice());
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(listPopular, statistical -> {
                Drink drink = new Drink(statistical.getDrinkId(), statistical.getDrinkName(),
                        statistical.getDrinkUnitId(), statistical.getDrinkUnitName());
                GlobalFuntion.goToDrinkDetailActivity(this, drink);
            });
            rcvData.setAdapter(statisticalAdapter);
        } else {
            StatisticalAdapter statisticalAdapter = new StatisticalAdapter(mListStatisticals, statistical -> {
                Drink drink = new Drink(statistical.getDrinkId(), statistical.getDrinkName(),
                        statistical.getDrinkUnitId(), statistical.getDrinkUnitName());
                GlobalFuntion.goToDrinkDetailActivity(this, drink);
            });
            rcvData.setAdapter(statisticalAdapter);
        }

        // Calculate total
        String strTotalValue = getTotalValues() + Constants.CURRENCY;
        tvTotalValue.setText(strTotalValue);
    }

    private boolean checkStatisticalExist(long drinkId) {
        if (mListStatisticals == null || mListStatisticals.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Statistical statistical : mListStatisticals) {
            if (drinkId == statistical.getDrinkId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Statistical getStatisticalFromDrinkId(long drinkId) {
        Statistical result = null;
        for (Statistical statistical : mListStatisticals) {
            if (drinkId == statistical.getDrinkId()) {
                result = statistical;
                break;
            }
        }
        return result;
    }

    private int getTotalValues() {
        if (mListStatisticals == null || mListStatisticals.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Statistical statistical : mListStatisticals) {
            total += statistical.getTotalPrice();
        }
        return total;
    }
}
