package com.app.buchin.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.MyApplication;
import com.app.buchin.R;
import com.app.buchin.adapter.HistoryAdapter;
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

public class DrinkDetailUsedFragment extends Fragment {

    private View mView;
    private TextView tvTotalPrice;
    private TextView tvTotalQuantity;
    private final Drink mDrink;
    private List<History> mListHistory;
    private HistoryAdapter mHistoryAdapter;

    public DrinkDetailUsedFragment(Drink drink) {
        this.mDrink = drink;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_drink_detail_used, container, false);

        initUi();
        getListHistoryAdded();

        return mView;
    }

    private void initUi() {
        tvTotalQuantity = mView.findViewById(R.id.tv_total_quantity);
        tvTotalPrice = mView.findViewById(R.id.tv_total_price);
        RecyclerView rcvHistory = mView.findViewById(R.id.rcv_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvHistory.setLayoutManager(linearLayoutManager);

        mListHistory = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(mListHistory, true,
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
            public void onClickItemHistory(History history) {}
        });
        rcvHistory.setAdapter(mHistoryAdapter);

        FloatingActionButton fabAddData = mView.findViewById(R.id.fab_add_data);
        fabAddData.setOnClickListener(new IOnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onClickAddOrEditHistory(null);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListHistoryAdded() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getHistoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListHistory != null) {
                            mListHistory.clear();
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            History history = dataSnapshot.getValue(History.class);
                            if (history != null) {
                                if (mDrink.getId() == history.getDrinkId() && !history.isAdd()) {
                                    mListHistory.add(0, history);
                                }
                            }
                        }
                        mHistoryAdapter.notifyDataSetChanged();

                        displayLayoutBottomInfor();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), getString(R.string.msg_get_data_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayLayoutBottomInfor() {
        // Calculator quantity
        String strTotalQuantity = getTotalQuantity() + " " + mDrink.getUnitName();
        tvTotalQuantity.setText(strTotalQuantity);
        // Calculator price
        String strTotalPrice = getTotalPrice() + Constants.CURRENCY;
        tvTotalPrice.setText(strTotalPrice);
    }

    private int getTotalQuantity() {
        if (mListHistory == null || mListHistory.isEmpty()) {
            return 0;
        }

        int totalQuantity = 0;
        for (History history : mListHistory) {
            totalQuantity += history.getQuantity();
        }
        return totalQuantity;
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
    //5.4.1 Thêm lịch sử. mở dialog
    private void onClickAddOrEditHistory(@Nullable History history) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_detail_drink_edit);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Get view
        final TextView tvTitleDialog = dialog.findViewById(R.id.tv_title_dialog);
        final TextView tvDrinkName = dialog.findViewById(R.id.tv_drink_name);
        final EditText edtQuantity = dialog.findViewById(R.id.edt_quantity);
        final TextView tvUnitName = dialog.findViewById(R.id.tv_unit_name);
        final EditText edtPrice = dialog.findViewById(R.id.edt_price);
        final TextView tvDialogCancel = dialog.findViewById(R.id.tv_dialog_cancel);
        final TextView tvDialogAdd = dialog.findViewById(R.id.tv_dialog_add);

        // Set data
        if (history == null) {
            tvTitleDialog.setText(getString(R.string.feature_drink_used));
            tvDrinkName.setText(mDrink.getName());
            tvUnitName.setText(mDrink.getUnitName());
        } else {
            tvTitleDialog.setText(getString(R.string.edit_history_used));
            tvDrinkName.setText(history.getDrinkName());
            tvUnitName.setText(history.getUnitName());
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
                    GlobalFuntion.showToast(getActivity(), getString(R.string.msg_enter_full_infor));
                    return;
                }

                if (history == null) {
                    History history = new History();
                    history.setId(System.currentTimeMillis());
                    history.setDrinkId(mDrink.getId());
                    history.setDrinkName(mDrink.getName());
                    history.setUnitId(mDrink.getUnitId());
                    history.setUnitName(mDrink.getUnitName());
                    history.setQuantity(Integer.parseInt(strQuantity));
                    history.setPrice(Integer.parseInt(strPrice));
                    history.setTotalPrice(history.getQuantity() * history.getPrice());
                    history.setAdd(false);

                    String currentDate = new SimpleDateFormat(DateTimeUtils.DEFAULT_FORMAT_DATE, Locale.ENGLISH).format(new Date());
                    String strDate = DateTimeUtils.convertDateToTimeStamp(currentDate);
                    history.setDate(Long.parseLong(strDate));

                    if (getActivity() != null) {
                        MyApplication.get(getActivity()).getHistoryDatabaseReference()
                                .child(String.valueOf(history.getId()))
                                .setValue(history, (error, ref) -> {
                                    GlobalFuntion.showToast(getActivity(), getString(R.string.msg_used_drink_success));
                                    changeQuantity(history.getDrinkId(), history.getQuantity(), false);
                                    GlobalFuntion.hideSoftKeyboard(getActivity());
                                    dialog.dismiss();
                                });
                    }
                } else {
                    // Edit history
                    Map<String, Object> map = new HashMap<>();
                    map.put("quantity", Integer.parseInt(strQuantity));
                    map.put("price", Integer.parseInt(strPrice));
                    map.put("totalPrice", Integer.parseInt(strQuantity) * Integer.parseInt(strPrice));

                    if (getActivity() != null) {
                        MyApplication.get(getActivity()).getHistoryDatabaseReference()
                                .child(String.valueOf(history.getId()))
                                .updateChildren(map, (error, ref) -> {
                                    GlobalFuntion.hideSoftKeyboard(getActivity());
                                    GlobalFuntion.showToast(getActivity(), getString(R.string.msg_edit_used_history_success));
                                    changeQuantity(history.getDrinkId(), Integer.parseInt(strQuantity) - history.getQuantity(), false);

                                    dialog.dismiss();
                                });
                    }
                }
            }
        });

        dialog.show();
    }
    //5.4.5 Thay đổi số lượng, tính lại số lượng và thu nhập
    private void changeQuantity(long drinkId, int quantity, boolean isAdd) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId)
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
                            if (getActivity() != null) {
                                MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId).removeEventListener(this);
                                MyApplication.get(getActivity()).getQuantityDatabaseReference(drinkId).setValue(totalQuantity);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    //5.43.3
    private void onClickDeleteHistory(History history) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_delete), (dialogInterface, i)
                        -> MyApplication.get(getActivity()).getHistoryDatabaseReference()
                        .child(String.valueOf(history.getId()))
                        .removeValue((error, ref) -> {
                            GlobalFuntion.showToast(getActivity(), getString(R.string.msg_delete_used_history_success));
                            changeQuantity(history.getDrinkId(), history.getQuantity(), true);
                            GlobalFuntion.hideSoftKeyboard(getActivity());
                        }))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
}
