package com.app.buchin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.ManageDrinkAdapter;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;
import com.app.buchin.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class DrinkOutOfStockActivity extends BaseActivity {

    private List<Drink> mListDrink;
    private ManageDrinkAdapter mManageDrinkAdapter;

    private EditText edtSearchName;
    private String mKeySeach;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mManageDrinkAdapter == null) {
                return;
            }
            if (drink.getQuantity() <= 0) {
                if (StringUtil.isEmpty(mKeySeach)) {
                    mListDrink.add(0, drink);
                } else {
                    if (GlobalFuntion.getTextSearch(drink.getName().toLowerCase())
                            .contains(GlobalFuntion.getTextSearch(mKeySeach).toLowerCase())) {
                        mListDrink.add(0, drink);
                    }
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }
            if (drink.getQuantity() > 0) {
                for (Drink drinkObject : mListDrink) {
                    if (drink.getId() == drinkObject.getId()) {
                        mListDrink.remove(drinkObject);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < mListDrink.size(); i++) {
                    if (drink.getId() == mListDrink.get(i).getId()) {
                        mListDrink.set(i, drink);
                        break;
                    }
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }
            for (Drink drinkObject : mListDrink) {
                if (drink.getId() == drinkObject.getId()) {
                    mListDrink.remove(drinkObject);
                    break;
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showToast(getString(R.string.msg_get_data_error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_out_of_stock);

        initToolbar();
        initUi();
        getListDrink();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_drink_out_of_stock));
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

    private void initUi() {
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                searchDrink();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySeach = "";
                    getListDrink();
                    GlobalFuntion.hideSoftKeyboard(DrinkOutOfStockActivity.this);
                }
            }
        });

        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrink.setLayoutManager(linearLayoutManager);

        mListDrink = new ArrayList<>();
        mManageDrinkAdapter = new ManageDrinkAdapter(mListDrink, drink
                -> GlobalFuntion.goToDrinkDetailActivity(this, drink));
        rcvDrink.setAdapter(mManageDrinkAdapter);
    }

    public void getListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
            MyApplication.get(this).getDrinkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    private void searchDrink() {
        if (mListDrink == null || mListDrink.isEmpty()) {
            GlobalFuntion.hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim();
        getListDrink();
        GlobalFuntion.hideSoftKeyboard(this);
    }
}