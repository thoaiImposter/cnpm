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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.UnitAdapter;
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

public class UnitActivity extends BaseActivity {

    private List<UnitObject> mListUnit;
    private UnitAdapter mUnitAdapter;

    private EditText edtSearchName;
    private String mKeySeach;
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        // 1.1.1 UnitActivity gửi yêu cầu đọc dữ liệu từ Firebase Realtime Database.
        // 1.1.2 Firebase phản hồi với danh sách các đơn vị đo lường.
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            UnitObject unitObject = dataSnapshot.getValue(UnitObject.class);
            if (unitObject == null || mListUnit == null || mUnitAdapter == null) {
                return;
            }
            if (StringUtil.isEmpty(mKeySeach)) {
                mListUnit.add(0, unitObject);
            } else {
                // 1.5.2 UnitActivity lọc danh sách đơn vị theo tên khớp từ khóa.
                if (GlobalFuntion.getTextSearch(unitObject.getName().toLowerCase())
                        .contains(GlobalFuntion.getTextSearch(mKeySeach).toLowerCase())) {
                    mListUnit.add(0, unitObject);
                }
            }
            // 1.1.3 UnitActivity hiển thị danh sách này cho Bar Manager.
            mUnitAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            UnitObject unitObject = dataSnapshot.getValue(UnitObject.class);
            if (unitObject == null || mListUnit == null || mListUnit.isEmpty() || mUnitAdapter == null) {
                return;
            }
            for (int i = 0; i < mListUnit.size(); i++) {
                if (unitObject.getId() == mListUnit.get(i).getId()) {
                    mListUnit.set(i, unitObject);
                    break;
                }
            }
            // 1.3.7 UnitActivity hiển thị thông báo thành công (implicitly via UI update after edit).
            mUnitAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            UnitObject unitObject = dataSnapshot.getValue(UnitObject.class);
            if (unitObject == null || mListUnit == null || mListUnit.isEmpty() || mUnitAdapter == null) {
                return;
            }
            for (UnitObject unit : mListUnit) {
                if (unitObject.getId() == unit.getId()) {
                    mListUnit.remove(unit);
                    break;
                }
            }
            // 1.4.6 UnitActivity hiển thị thông báo thành công (implicitly via UI update after delete).
            mUnitAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_unit);

        initToolbar();
        initUi();
        // 1.1 Hệ thống mở UnitActivity và tự động tải danh sách các đơn vị đo lường từ Firebase Realtime Database.
        getListUnit();
    }

    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_manage_unit));
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
        // 1.5.1 Bar Manager nhập từ khóa vào thanh tìm kiếm trong UnitActivity.
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 1.5.3 Kết quả tìm kiếm được hiển thị cho Bar Manager.
                searchUnit();
            }
        });

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 1.5.3 Kết quả tìm kiếm được hiển thị cho Bar Manager.
                searchUnit();
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
                    // 1.5.2 UnitActivity lọc danh sách đơn vị theo tên khớp từ khóa (reset when search is cleared).
                    getListUnit();
                    GlobalFuntion.hideSoftKeyboard(UnitActivity.this);
                }
            }
        });

        // 1.2.1 Bar Manager nhấn nút "Thêm" trong UnitActivity.
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_data);
        fabAdd.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // 1.2.2 Ứng dụng mở Dialog cho phép nhập tên và mô tả đơn vị mới.
                onClickAddOrEditUnit(null);
            }
        });

        LinearLayout layoutDeleteAll = findViewById(R.id.layout_delete_all);
        layoutDeleteAll.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mListUnit == null || mListUnit.isEmpty()) {
                    return;
                }
                // 1.4.4 UnitActivity gửi yêu cầu xóa đơn vị (all units in this case).
                onClickDeleteAllUnit();
            }
        });

        RecyclerView rcvUnit = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUnit.setLayoutManager(linearLayoutManager);

        mListUnit = new ArrayList<>();
        mUnitAdapter = new UnitAdapter(mListUnit, new UnitAdapter.IManagerUnitListener() {
            @Override
            public void editUnit(UnitObject unitObject) {
                // 1.3.2 Ứng dụng mở Dialog để chỉnh sửa thông tin.
                onClickAddOrEditUnit(unitObject);
            }

            @Override
            public void deleteUnit(UnitObject unitObject) {
                // 1.4.1 Bar Manager nhấn nút "Xoá" trên đơn vị cần xóa.
                onClickDeleteUnit(unitObject);
            }
        });
        rcvUnit.setAdapter(mUnitAdapter);
        rcvUnit.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    // 1.1.1 UnitActivity gửi yêu cầu đọc dữ liệu từ Firebase Realtime Database.
    public void getListUnit() {
        if (mListUnit != null) {
            mListUnit.clear();
            MyApplication.get(this).getUnitDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getUnitDatabaseReference().addChildEventListener(mChildEventListener);
    }

    // 1.5.2 UnitActivity lọc danh sách đơn vị theo tên khớp từ khóa.
    private void searchUnit() {
        if (mListUnit == null || mListUnit.isEmpty()) {
            GlobalFuntion.hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim();
        // 1.5.3 Kết quả tìm kiếm được hiển thị cho Bar Manager.
        getListUnit();
        GlobalFuntion.hideSoftKeyboard(this);
    }

    // 1.2.2 Ứng dụng mở Dialog cho phép nhập tên và mô tả đơn vị mới.
    // 1.3.2 Ứng dụng mở Dialog để chỉnh sửa thông tin.
    private void onClickAddOrEditUnit(UnitObject unitObject) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_and_edit_unit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final EditText edtUnitName = dialog.findViewById(R.id.edt_unit_name);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAction = dialog.findViewById(R.id.tv_dialog_action);

        // Set data
        if (unitObject == null) {
            tvTitleDialog.setText(getString(R.string.add_unit_name));
            tvDialogAction.setText(getString(R.string.action_add));
        } else {
            tvTitleDialog.setText(getString(R.string.edit_unit_name));
            tvDialogAction.setText(getString(R.string.action_edit));
            edtUnitName.setText(unitObject.getName());
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
                // 1.2.3 Bar Manager nhập thông tin và xác nhận trong Dialog.
                // 1.3.3 Bar Manager sửa tên hoặc mô tả và xác nhận.
                String strUnitName = edtUnitName.getText().toString().trim();
                if (StringUtil.isEmpty(strUnitName)) {
                    showToast(getString(R.string.msg_unit_name_require));
                    return;
                }

                if (isUnitExist(strUnitName)) {
                    showToast(getString(R.string.msg_unit_exist));
                    return;
                }

                if (unitObject == null) {
                    // 1.2.4 Dialog gửi dữ liệu về lại UnitActivity.
                    // 1.2.5 UnitActivity gửi dữ liệu lên Firebase để tạo đơn vị mới.
                    long id = System.currentTimeMillis();
                    UnitObject unit = new UnitObject();
                    unit.setId(id);
                    unit.setName(strUnitName);

                    MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(id)).setValue(unit, (error, ref) -> {
                                // 1.2.6 Firebase xác nhận đã thêm thành công.
                                // 1.2.7 UnitActivity hiển thị thông báo thành công.
                                GlobalFuntion.hideSoftKeyboard(UnitActivity.this, edtUnitName);
                                showToast(getString(R.string.msg_add_unit_success));
                                dialog.dismiss();
                                GlobalFuntion.hideSoftKeyboard(UnitActivity.this);
                            });
                } else {
                    // 1.3.4 Dialog gửi thông tin đã chỉnh sửa về UnitActivity.
                    // 1.3.5 UnitActivity cập nhật thông tin đơn vị lên Firebase.
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", strUnitName);

                    MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(unitObject.getId())).updateChildren(map, (error, ref) -> {
                                // 1.3.6 Firebase xác nhận đã cập nhật thành công.
                                // 1.3.7 UnitActivity hiển thị thông báo thành công.
                                GlobalFuntion.hideSoftKeyboard(UnitActivity.this, edtUnitName);
                                showToast(getString(R.string.msg_edit_unit_success));
                                dialog.dismiss();
                                GlobalFuntion.hideSoftKeyboard(UnitActivity.this);
                                updateUnitInDrink(new UnitObject(unitObject.getId(), strUnitName));
                                updateUnitInHistory(new UnitObject(unitObject.getId(), strUnitName));
                            });
                }
            }
        });

        dialog.show();
    }

    private boolean isUnitExist(String unitName) {
        if (mListUnit == null || mListUnit.isEmpty()) {
            return false;
        }

        for (UnitObject unitObject : mListUnit) {
            if (unitName.equals(unitObject.getName())) {
                return true;
            }
        }

        return false;
    }

    // 1.4.1 Bar Manager nhấn nút "Xoá" trên đơn vị cần xóa.
    private void onClickDeleteUnit(UnitObject unitObject) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> {
                    // 1.4.4 UnitActivity gửi yêu cầu xóa đơn vị.
                    MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                            .child(String.valueOf(unitObject.getId())).removeValue((error, ref) -> {
                                // 1.4.5 Firebase xác nhận đã xoá thành công.
                                // 1.4.6 UnitActivity hiển thị thông báo thành công.
                                showToast(getString(R.string.msg_delete_unit_success));
                                GlobalFuntion.hideSoftKeyboard(UnitActivity.this);
                            });
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    // 1.4.4 UnitActivity gửi yêu cầu xóa đơn vị (all units).
    private void onClickDeleteAllUnit() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete_all))
                .setPositiveButton(getString(R.string.delete_all), (dialogInterface, i)
                        -> MyApplication.get(UnitActivity.this).getUnitDatabaseReference()
                        .removeValue((error, ref) -> {
                            // 1.4.5 Firebase xác nhận đã xoá thành công.
                            // 1.4.6 UnitActivity hiển thị thông báo thành công.
                            showToast(getString(R.string.msg_delete_all_unit_success));
                            GlobalFuntion.hideSoftKeyboard(UnitActivity.this);
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    // Update related data when unit is edited
    private void updateUnitInDrink(UnitObject unitObject) {
        // 1.4.2 UnitActivity gửi yêu cầu kiểm tra với Firebase xem đơn vị này có đang được sử dụng không.
        MyApplication.get(this).getDrinkDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Drink> list = new ArrayList<>();

                        // 1.4.3 Firebase phản hồi đơn vị chưa được sử dụng (implicitly handled by updating if used).
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Drink drink = dataSnapshot.getValue(Drink.class);
                            if (drink != null && drink.getUnitId() == unitObject.getId()) {
                                list.add(drink);
                            }
                        }
                        MyApplication.get(UnitActivity.this).getDrinkDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (Drink drink : list) {
                            MyApplication.get(UnitActivity.this).getDrinkDatabaseReference()
                                    .child(String.valueOf(drink.getId()))
                                    .child("unitName").setValue(unitObject.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void updateUnitInHistory(UnitObject unitObject) {
        // 1.4.2 UnitActivity gửi yêu cầu kiểm tra với Firebase xem đơn vị này có đang được sử dụng không.
        MyApplication.get(this).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<History> list = new ArrayList<>();

                        // 1.4.3 Firebase phản hồi đơn vị chưa được sử dụng (implicitly handled by updating if used).
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null && history.getUnitId() == unitObject.getId()) {
                                list.add(history);
                            }
                        }
                        MyApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                .removeEventListener(this);
                        if (list.isEmpty()) {
                            return;
                        }
                        for (History history : list) {
                            MyApplication.get(UnitActivity.this).getHistoryDatabaseReference()
                                    .child(String.valueOf(history.getId()))
                                    .child("unitName").setValue(unitObject.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}