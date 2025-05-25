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

/**
 * Class ManageDrinkActivity - Màn hình quản lý đồ uống
 * Thực hiện các chức năng theo usecase:
 * 5.1 - Hiển thị danh sách đồ uống
 * 5.2 - Tìm kiếm đồ uống
 * 5.3 - Chuyển đến chi tiết khi chọn 1 đồ uống
 */
public class ManageDrinkActivity extends BaseActivity {

    private List<Drink> mListDrink; // Danh sách đồ uống được hiển thị
    private ManageDrinkAdapter mManageDrinkAdapter; // Adapter hiển thị danh sách đồ uống
    private EditText edtSearchName; // Ô tìm kiếm
    private String mKeySeach;   // Từ khóa tìm kiếm

    /**
     * (5.1.3, 5.2.2) Theo dõi dữ liệu thay đổi từ Firebase: thêm, cập nhật, xóa
     */
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mManageDrinkAdapter == null) return;

            if (StringUtil.isEmpty(mKeySeach)) {
                mListDrink.add(0, drink); // (5.1.3) thêm nếu không có tìm kiếm
            } else {
                // (5.2.2) thêm nếu tên đồ uống khớp từ khóa tìm kiếm
                if (GlobalFuntion.getTextSearch(drink.getName().toLowerCase())
                        .contains(GlobalFuntion.getTextSearch(mKeySeach).toLowerCase())) {
                    mListDrink.add(0, drink);
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) return;

            // Cập nhật lại item khi có thay đổi trên Firebase
            for (int i = 0; i < mListDrink.size(); i++) {
                if (drink.getId() == mListDrink.get(i).getId()) {
                    mListDrink.set(i, drink);
                    break;
                }
            }
            mManageDrinkAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) return;

            // Xóa khỏi danh sách nếu bị xóa khỏi Firebase
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

    // (5.1) onCreate - Khởi tạo giao diện quản lý đồ uống
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drink);

        initToolbar();
        initUi();
        getListDrink(); // (5.1.2) Gửi yêu cầu lấy danh sách đồ uống
    }

    // (5.1) Tạo toolbar tiêu đề
    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_manage_drink));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Bắt sự kiện nút quay lại
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * (5.2) Thiết lập UI, tìm kiếm, và adapter danh sách
     */
    private void initUi() {
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);

        // (5.2) Ấn nút tìm kiếm
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                searchDrink(); // (5.2.1)
            }
        });

        // (5.2) Tìm kiếm bằng bàn phím
        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink(); // (5.2.1)
                return true;
            }
            return false;
        });

        // (5.2) Nếu người dùng xóa tìm kiếm thì hiển thị toàn bộ danh sách
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySeach = "";
                    getListDrink(); // (5.2.1)
                    GlobalFuntion.hideSoftKeyboard(ManageDrinkActivity.this);
                }
            }
        });

        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        rcvDrink.setLayoutManager(new LinearLayoutManager(this));

        mListDrink = new ArrayList<>();

        // (5.3) Khi người dùng chọn đồ uống -> mở chi tiết
        mManageDrinkAdapter = new ManageDrinkAdapter(mListDrink, drink
                -> GlobalFuntion.goToDrinkDetailActivity(this, drink)); // (5.3.1)
        rcvDrink.setAdapter(mManageDrinkAdapter);
    }

    /**
     * (5.1.2, 5.2.1) Lấy danh sách đồ uống từ Firebase
     */
    public void getListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
            MyApplication.get(this).getDrinkDatabaseReference().removeEventListener(mChildEventListener);
        }
        MyApplication.get(this).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    /**
     * (5.2.1) Tìm kiếm đồ uống theo tên
     */
    private void searchDrink() {
        if (mListDrink == null || mListDrink.isEmpty()) {
            GlobalFuntion.hideSoftKeyboard(this);
            return;
        }
        mKeySeach = edtSearchName.getText().toString().trim(); // Gán từ khóa
        getListDrink(); // Gọi lại getListDrink() để lọc theo từ khóa
        GlobalFuntion.hideSoftKeyboard(this);
    }
}
