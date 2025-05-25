package com.app.buchin.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.HistoryAdapter;
import com.app.buchin.adapter.SelectDrinkAdapter;
import com.app.buchin.constant.Constants;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;
import com.app.buchin.model.History;
import com.app.buchin.utils.DateTimeUtils;
import com.app.buchin.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryDrinkActivity extends BaseActivity {

    private TextView mTvDateSelected;
    private TextView tvTotalPrice;

    private List<Drink> mListDrink;

    private List<History> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    private Drink mDrinkSelected;
    private boolean isDrinkUsed; //Biến phân biệt nhập hay xuất - false là nhập true là xuất

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_drink);

        getDataIntent();
        initToolbar();
        initUi();
        getListDrinks();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        isDrinkUsed = bundle.getBoolean(Constants.KEY_INTENT_DRINK_USED);
    }

    private void initToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (isDrinkUsed) {
            getSupportActionBar().setTitle(getString(R.string.feature_drink_used));
        } else {
            getSupportActionBar().setTitle(getString(R.string.feature_add_drink));
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
        TextView tvListTitle = findViewById(R.id.tv_list_title);
        if (isDrinkUsed) {
            tvListTitle.setText(getString(R.string.list_drink_used));
        } else {
            tvListTitle.setText(getString(R.string.list_drink_buy));
        }

        tvTotalPrice = findViewById(R.id.tv_total_price);

        mTvDateSelected = findViewById(R.id.tv_date_selected);
        String currentDate = new SimpleDateFormat(DateTimeUtils.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
        mTvDateSelected.setText(currentDate);
        getListHistoryDrinkOfDate(currentDate);

        RelativeLayout layoutSelectDate = findViewById(R.id.layout_select_date);
        layoutSelectDate.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                GlobalFuntion.showDatePicker(HistoryDrinkActivity.this, mTvDateSelected.getText().toString(), date -> {
                    mTvDateSelected.setText(date);
                    getListHistoryDrinkOfDate(date);
                });
            }
        });

        FloatingActionButton fabAddData = findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });

        RecyclerView rcvHistory = findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListDrink = new ArrayList<>();
        mListHistory = new ArrayList<>();

        mHistoryAdapter = new HistoryAdapter(mListHistory, false,
                new HistoryAdapter.IManagerHistoryListener() {
                    @Override
                    public void editHistory(History history) {
                        onClickAddOrEditHistory(history);
                    }

                    @Override
                    public void deleteHistory(History history) {
                        onClickDeleteHistory(history);
                    }

                    @Override
                    public void onClickItemHistory(History history) {
                        Drink drink = new Drink(history.getDrinkId(), history.getDrinkName(),
                                history.getUnitId(), history.getUnitName());
                        GlobalFuntion.goToDrinkDetailActivity(HistoryDrinkActivity.this, drink);
                    }
                });
        rcvHistory.setAdapter(mHistoryAdapter);
        rcvHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAddData.hide();
                } else {
                    fabAddData.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListDrinks() {
        MyApplication.get(this).getDrinkDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListDrink != null) mListDrink.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Drink drink = dataSnapshot.getValue(Drink.class);
                    mListDrink.add(0, drink);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryDrinkOfDate(@NonNull String date) {
        long longDate = Long.parseLong(DateTimeUtils.convertDateToTimeStamp(date));
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) mListHistory.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null) {
                                if (longDate == history.getDate()) {
                                    addHistoryToList(history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        // Calculator price
                        String strTotalPrice = getTotalPrice() + Constants.CURRENCY;
                        tvTotalPrice.setText(strTotalPrice);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast(getString(R.string.msg_get_data_error));
                    }
                });
    }

    private void addHistoryToList(History history) {
        if (history == null) {
            return;
        }
        if (isDrinkUsed) {
            if (!history.isAdd()) {
                mListHistory.add(0, history);
            }
        } else {
            if (history.isAdd()) {
                mListHistory.add(0, history);
            }
        }
    }

    private void onClickAddOrEditHistory(History history) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            showToast(getString(R.string.msg_list_drink_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_history);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final Spinner spnDrink = dialog.findViewById(R.id.spinner_drink);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        // Set data
        if (isDrinkUsed) {
            tvTitleDialog.setText(getString(R.string.feature_drink_used));
        } else {
            tvTitleDialog.setText(getString(R.string.feature_add_drink));
        }

        SelectDrinkAdapter selectDrinkAdapter = new SelectDrinkAdapter(this, R.layout.item_choose_option, mListDrink);
        spnDrink.setAdapter(selectDrinkAdapter);
        spnDrink.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDrinkSelected = selectDrinkAdapter.getItem(position);
                tvUnitName.setText(mDrinkSelected.getUnitName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (history != null) {
            if (isDrinkUsed) {
                tvTitleDialog.setText(getString(R.string.edit_history_used));
            } else {
                tvTitleDialog.setText(getString(R.string.edit_history_add));
            }
            spnDrink.setSelection(getPositionDrinkUpdate(history));
            edtQuantity.setText(String.valueOf(history.getQuantity()));
            edtPrice.setText(String.valueOf(history.getPrice()));
        }

        // Listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strQuantity = edtQuantity.getText().toString().trim();
                String strPrice = edtPrice.getText().toString().trim();
                if (StringUtil.isEmpty(strQuantity) || StringUtil.isEmpty(strPrice)) {
                    showToast(getString(R.string.msg_enter_full_infor));
                    return;
                }

                if (history == null) {
                    History history = new History();
                    history.setId(System.currentTimeMillis());
                    history.setDrinkId(mDrinkSelected.getId());
                    history.setDrinkName(mDrinkSelected.getName());
                    history.setUnitId(mDrinkSelected.getUnitId());
                    history.setUnitName(mDrinkSelected.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(!isDrinkUsed);
                    String strDate = DateTimeUtils.convertDateToTimeStamp(mTvDateSelected.getText().toString());
                    history.setDate(Long.parseLong(strDate));

                    MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                            .child(String.valueOf(history.getId()))
                            .setValue(history, (error, ref) -> {
                                if (isDrinkUsed) {
                                    showToast(getString(R.string.msg_used_drink_success));
                                } else {
                                    showToast(getString(R.string.msg_add_drink_success));
                                }
                                changeQuantity(history.getDrinkId(), history.getQuantity(), !isDrinkUsed);
                                GlobalFuntion.hideSoftKeyboard(HistoryDrinkActivity.this);
                                dialog.dismiss();
                            });
                    return;
                }

                // Edit history
                Map<String, Object> map = new HashMap<>();
                map.put("drinkId", mDrinkSelected.getId());
                map.put("drinkName", mDrinkSelected.getName());
                map.put("unitId", mDrinkSelected.getUnitId());
                map.put("unitName", mDrinkSelected.getUnitName());
                map.put("quantity", Integer.parseInt(strQuantity));
                map.put("price", Integer.parseInt(strPrice));
                map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .updateChildren(map, (error, ref) -> {
                            GlobalFuntion.hideSoftKeyboard(HistoryDrinkActivity.this);
                            if (isDrinkUsed) {
                                showToast(getString(R.string.msg_edit_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_edit_add_history_success));
                            }
                            changeQuantity(history.getDrinkId(), Integer.parseInt(strQuantity) - history.getQuantity(), !isDrinkUsed);

                            dialog.dismiss();
                        });
            }
        });

        dialog.show();
    }
    //5.6 Sửa số lượng nhâp/ xuất, chỉ sửa đc số lượng th
    private void changeQuantity(long drinkId, int quantity, boolean isAdd) {
        MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentQuantity = snapshot.getValue(Integer.class);
                        if (currentQuantity != null) {
                            int totalQuantity;
                            if (isAdd) {
                                totalQuantity = currentQuantity + quantity;
                            } else {
                                totalQuantity = currentQuantity - quantity;
                            }
                            MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId).removeEventListener(this);
                            updateQuantityToFirebase(drinkId, totalQuantity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateQuantityToFirebase(long drinkId, int quantity) {
        MyApplication.get(HistoryDrinkActivity.this).getQuantityDatabaseReference(drinkId)
                .setValue(quantity);
    }

    private int getPositionDrinkUpdate(History history) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListDrink.size(); i++) {
            if (history.getDrinkId() == mListDrink.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private int getTotalPrice() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalPrice = 0;
        for (History history : mListHistory) {
            totalPrice += history.getTotalPrice();
        }
        return totalPrice;
    }

    private void onClickDeleteHistory(History history) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyApplication.get(HistoryDrinkActivity.this).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            if (isDrinkUsed) {
                                showToast(getString(R.string.msg_delete_used_history_success));
                            } else {
                                showToast(getString(R.string.msg_delete_add_history_success));

                            }
                            changeQuantity(history.getDrinkId(), history.getQuantity(), isDrinkUsed);
                            GlobalFuntion.hideSoftKeyboard(HistoryDrinkActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
}
