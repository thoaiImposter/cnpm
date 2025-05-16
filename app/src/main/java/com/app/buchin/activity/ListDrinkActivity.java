package com.app.buchin.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.DrinkAdapter;
import com.app.buchin.adapter.SelectUnitAdapter;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.listener.IOnSingleClickListener;
import com.app.buchin.model.Drink;
import com.app.buchin.model.History;
import com.app.buchin.model.UnitObject;
import com.app.buchin.utils.StringUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDrinkActivity extends BaseActivity {

    private List<Drink> mListDrink;
    private DrinkAdapter mDrinkAdapter;

    private List<UnitObject> mListUnit;
    private UnitObject mUnitSelected;

    private EditText edtSearchName;
    private String mKeySeach;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mDrinkAdapter == null) {
                return;
            }
            if (StringUtil.isEmpty(mKeySeach)) {
                mListDrink.add(0, drink);
            } else {
                if (GlobalFuntion.getTextSearch(drink.getName().toLowerCase())
                        .contains(GlobalFuntion.getTextSearch(mKeySeach).toLowerCase())) {
                    mListDrink.add(0, drink);
                }
            }
            mDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mDrinkAdapter == null) {
                return;
            }
            for (int i = 0; i < mListDrink.size(); i++) {
                if (drink.getId() == mListDrink.get(i).getId()) {
                    mListDrink.set(i, drink);
                    break;
                }
            }
            mDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mDrinkAdapter == null) {
                return;
            }
            for (Drink drinkObject : mListDrink) {
                if (drink.getId() == drinkObject.getId()) {
                    mListDrink.remove(drinkObject);
                    break;
                }
            }
            mDrinkAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_list_drink);

        initToolbar();
        initUi();
        getListUnit();
        getListDrink();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_list_menu));
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
                    GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this);
                }
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditDrink(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListDrink == null || mListDrink.isEmpty()) {
                    return;
                }
                onClickDeleteAllDrink();
            }
        });

        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrink.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mListDrink = new ArrayList<>();

        mDrinkAdapter = new DrinkAdapter(mListDrink, new DrinkAdapter.IManagerDrinkListener() {
            @Override
            public void editDrink(Drink drink) {
                onClickAddOrEditDrink(drink);
            }

            @Override
            public void deleteDrink(Drink drink) {
                onClickDeleteDrink(drink);
            }

            @Override
            public void onClickItemDrink(Drink drink) {
                GlobalFuntion.goToDrinkDetailActivity(ListDrinkActivity.this, drink);
            }
        });
        rcvDrink.setAdapter(mDrinkAdapter);
        rcvDrink.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabAdd.hide();
                } else {
                    fabAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void getListUnit() {
        MyApplication.get(this).getUnitDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListUnit != null) mListUnit.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UnitObject unitObject = dataSnapshot.getValue(UnitObject.class);
                    mListUnit.add(0, unitObject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast(getString(R.string.msg_get_data_error));
            }
        });
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

    private void onClickAddOrEditDrink(Drink drink) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            showToast(getString(R.string.msg_list_unit_require));
            return;
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_drink);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtDrinkName = dialog.findViewById(R.id.edt_drink_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);
        final Spinner spnUnit = dialog.findViewById(R.id.spinner_unit);

        SelectUnitAdapter selectUnitAdapter = new SelectUnitAdapter(this, R.layout.item_choose_option, mListUnit);
        spnUnit.setAdapter(selectUnitAdapter);
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUnitSelected = selectUnitAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set data
        if (drink == null) {
            tvTitleDialog.setText(getString(R.string.add_drink_name));
            tvDialogAction.setText(getString(R.string.action_add));
        } else {
            tvTitleDialog.setText(getString(R.string.edit_drink_name));
            tvDialogAction.setText(getString(R.string.action_edit));
            edtDrinkName.setText(drink.getName());
            spnUnit.setSelection(getPositionUnitUpdate(drink));
        }

        // Set listener
        tvDialogCancel.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogAction.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String strDrinkName = edtDrinkName.getText().toString().trim();
                if (StringUtil.isEmpty(strDrinkName)) {
                    showToast(getString(R.string.msg_drink_name_require));
                    return;
                }

                if (isDrinkExist(strDrinkName)) {
                    showToast(getString(R.string.msg_drink_exist));
                    return;
                }

                if (drink == null) {
                    long id = System.currentTimeMillis();
                    Drink drinkObject = new Drink();
                    drinkObject.setId(id);
                    drinkObject.setName(strDrinkName);
                    drinkObject.setUnitId(mUnitSelected.getId());
                    drinkObject.setUnitName(mUnitSelected.getName());

                    MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                            .child(String.valueOf(id)).setValue(drinkObject, (error, ref) -> {
                        GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this, edtDrinkName);
                        showToast(getString(R.string.msg_add_drink_success));
                        dialog.dismiss();
                        GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this);
                    });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", strDrinkName);
                    map.put("unitId", mUnitSelected.getId());
                    map.put("unitName", mUnitSelected.getName());

                    MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                            .child(String.valueOf(drink.getId())).updateChildren(map, (error, ref) -> {
                        GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this, edtDrinkName);
                        showToast(getString(R.string.msg_edit_drink_success));
                        dialog.dismiss();
                        GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this);
                        updateDrinkInHistory(new Drink(drink.getId(), strDrinkName,
                                mUnitSelected.getId(), mUnitSelected.getName()));
                    });
                }
            }
        });

        dialog.show();
    }

    private int getPositionUnitUpdate(Drink drink) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < mListUnit.size(); i++) {
            if (drink.getUnitId() == mListUnit.get(i).getId()) {
                return i;
            }
        }
        return 0;
    }

    private boolean isDrinkExist(String drinkName) {
        if (mListDrink == null || mListDrink.isEmpty()) {
            return false;
        }

        for (Drink drink : mListDrink) {
            if (drinkName.equals(drink.getName())) {
                return true;
            }
        }

        return false;
    }

    private void onClickDeleteDrink(Drink drink) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                        .child(String.valueOf(drink.getId())).removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_drink_success));
                            GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void onClickDeleteAllDrink() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete_all))
                .setPositiveButton(getString(R.string.delete_all), (dialogInterface, i)
                        -> MyApplication.get(ListDrinkActivity.this).getDrinkDatabaseReference()
                        .removeValue((error, ref) -> {
                            showToast(getString(R.string.msg_delete_all_drink_success));
                            GlobalFuntion.hideSoftKeyboard(ListDrinkActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void updateDrinkInHistory(Drink drink) {
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null && history.getDrinkId() == drink.getId()) {
                                list.add(history);
                            }
                        }
                        MyApplication.get(ListDrinkActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (History history : list) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("drinkName", drink.getName());
                            map.put("unitId", drink.getUnitId());
                            map.put("unitName", drink.getUnitName());

                            MyApplication.get(ListDrinkActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}