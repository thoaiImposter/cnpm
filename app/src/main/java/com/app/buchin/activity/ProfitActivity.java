package com.app.buchin.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.ProfitAdapter;
import com.app.buchin.constant.Constants;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;
import com.app.buchin.model.History;
import com.app.buchin.model.Profit;
import com.app.buchin.utils.DateTimeUtils;
import com.app.buchin.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfitActivity extends BaseActivity {

    private TextView tvTotalProfit;
    private TextView tvDateFrom, tvDateTo;
    private RecyclerView rcvData;

    private List<Profit> mListProfit;
    private ProfitAdapter mProfitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);

        initToolbar();
        initUi();
        getListProfit();
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setTitle(getString(R.string.feature_profit));
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
        tvTotalProfit = findViewById(R.id.tv_total_profit);
        rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);

        tvDateFrom.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFuntion.showDatePicker(ProfitActivity.this, tvDateFrom.getText().toString(), date -> {
                    tvDateFrom.setText(date);
                    getListProfit();
                });
            }
        });

        tvDateTo.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFuntion.showDatePicker(ProfitActivity.this, tvDateTo.getText().toString(), date -> {
                    tvDateTo.setText(date);
                    getListProfit();
                });
            }
        });
    }

    private void getListProfit() {
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
        if (mListProfit != null) {
            mListProfit.clear();
        } else {
            mListProfit = new ArrayList<>();
        }
        for (History history : list) {
            long drinkId = history.getDrinkId();
            if (checkProfitExist(drinkId)) {
                getProfitFromDrinkId(drinkId).getHistories().add(history);
            } else {
                Profit profit = new Profit();
                profit.setDrinkId(history.getDrinkId());
                profit.setDrinkName(history.getDrinkName());
                profit.setDrinkUnitId(history.getUnitId());
                profit.setDrinkUnitName(history.getUnitName());
                profit.getHistories().add(history);
                mListProfit.add(profit);
            }
        }
        mProfitAdapter = new ProfitAdapter(this, mListProfit, profit -> {
            Drink drink = new Drink(profit.getDrinkId(), profit.getDrinkName(),
                    profit.getDrinkUnitId(), profit.getDrinkUnitName());
            GlobalFuntion.goToDrinkDetailActivity(this, drink);
        });
        rcvData.setAdapter(mProfitAdapter);

        // Calculate total
        int profitValue = getTotalProfit();
        String strTotalProfit;
        if (profitValue > 0) {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.green));
            strTotalProfit = "+" + profitValue + Constants.CURRENCY;
        } else if (profitValue == 0) {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.yellow));
            strTotalProfit = profitValue + Constants.CURRENCY;
        } else {
            tvTotalProfit.setTextColor(getResources().getColor(R.color.background_red));
            strTotalProfit = profitValue + Constants.CURRENCY;
        }
        tvTotalProfit.setText(strTotalProfit);
    }

    private boolean checkProfitExist(long drinkId) {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return false;
        }
        boolean result = false;
        for (Profit profit : mListProfit) {
            if (drinkId == profit.getDrinkId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Profit getProfitFromDrinkId(long drinkId) {
        Profit result = null;
        for (Profit profit : mListProfit) {
            if (drinkId == profit.getDrinkId()) {
                result = profit;
                break;
            }
        }
        return result;
    }

    private int getTotalProfit() {
        if (mListProfit == null || mListProfit.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (Profit profit : mListProfit) {
            total += profit.getProfit();
        }
        return total;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProfitAdapter != null) {
            mProfitAdapter.release();
        }
    }
}
