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

public class ManageDrinkActivity extends BaseActivity {

    // Danh sách đồ uống
    private List<Drink> mListDrink;

    // Adapter cho RecyclerView
    private ManageDrinkAdapter mManageDrinkAdapter;

    // Ô tìm kiếm
    private EditText edtSearchName;

    // Từ khóa tìm kiếm
    private String mKeySeach;

    // Listener cho Firebase Database để lắng nghe thay đổi dữ liệu
    private final ChildEventListener mChildEventListener = new ChildEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            // Xử lý khi có đồ uống mới được thêm vào
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mManageDrinkAdapter == null) {
                return;
            }

            // Thêm vào đầu danh sách nếu không có từ khóa tìm kiếm
            // hoặc tên đồ uống khớp với từ khóa
            if (StringUtil.isEmpty(mKeySeach)) {
                mListDrink.add(0, drink);
            } else {
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
            // Xử lý khi có đồ uống được cập nhật
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }

            // Tìm và cập nhật đồ uống trong danh sách
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
            // Xử lý khi có đồ uống bị xóa
            Drink drink = dataSnapshot.getValue(Drink.class);
            if (drink == null || mListDrink == null || mListDrink.isEmpty() || mManageDrinkAdapter == null) {
                return;
            }

            // Tìm và xóa đồ uống khỏi danh sách
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
            // Không xử lý
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Hiển thị thông báo lỗi khi không lấy được dữ liệu
            showToast(getString(R.string.msg_get_data_error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drink);

        // Khởi tạo toolbar, giao diện và lấy danh sách đồ uống
        initToolbar();
        initUi();
        getListDrink();
    }

    /**
     * Khởi tạo toolbar với nút back và tiêu đề
     */
    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.feature_manage_drink));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý khi nhấn nút back trên toolbar
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Khởi tạo giao diện và các sự kiện
     */
    private void initUi() {
        // Ánh xạ view
        edtSearchName = findViewById(R.id.edt_search_name);
        ImageView imgSearch = findViewById(R.id.img_search);

        // Sự kiện click vào icon search
        imgSearch.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                searchDrink();
            }
        });

        // Sự kiện khi nhấn nút search trên bàn phím
        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink();
                return true;
            }
            return false;
        });

        // Lắng nghe thay đổi text trong ô tìm kiếm
        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không xử lý
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nếu text rỗng thì load lại toàn bộ danh sách
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    mKeySeach = "";
                    getListDrink();
                    GlobalFuntion.hideSoftKeyboard(ManageDrinkActivity.this);
                }
            }
        });

        // Khởi tạo RecyclerView để hiển thị danh sách đồ uống
        RecyclerView rcvDrink = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrink.setLayoutManager(linearLayoutManager);

        mListDrink = new ArrayList<>();
        // Khởi tạo adapter với callback khi click vào item
        mManageDrinkAdapter = new ManageDrinkAdapter(mListDrink, drink
                -> GlobalFuntion.goToDrinkDetailActivity(this, drink));
        rcvDrink.setAdapter(mManageDrinkAdapter);
    }

    /**
     * Lấy danh sách đồ uống từ Firebase
     */
    public void getListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
            MyApplication.get(this).getDrinkDatabaseReference().removeEventListener(mChildEventListener);
        }
        // Đăng ký lắng nghe thay đổi dữ liệu
        MyApplication.get(this).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    /**
     * Tìm kiếm đồ uống theo tên
     */
    private void searchDrink() {
        if (mListDrink == null || mListDrink.isEmpty()) {
            GlobalFuntion.hideSoftKeyboard(this);
            return;
        }
        // Lấy từ khóa tìm kiếm và gọi lại getListDrink()
        mKeySeach = edtSearchName.getText().toString().trim();
        getListDrink();
        GlobalFuntion.hideSoftKeyboard(this);
    }
}