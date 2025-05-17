package com.app.buchin.activity;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buchin.R;
import com.app.buchin.adapter.FeatureAdapter;
import com.app.buchin.constant.Constants;
import com.app.buchin.constant.GlobalFuntion;
import com.app.buchin.model.Feature;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class FeatureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);

        initUi();
        showAdmobBanner();
    }

    private void initUi() {
        RecyclerView rcvFeature = findViewById(R.id.rcv_feature);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvFeature.setLayoutManager(gridLayoutManager);

        FeatureAdapter featureAdapter = new FeatureAdapter(getListFeatures(), this::onClickItemFeature);
        rcvFeature.setAdapter(featureAdapter);
    }

    private void showAdmobBanner() {
        MobileAds.initialize(this, "ca-app-pub-8577216370890753~4422934437");
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private List<Feature> getListFeatures() {
        List<Feature> list = new ArrayList<>();
        list.add(new Feature(Feature.FEATURE_MANAGE_UNIT, R.drawable.ic_manage_unit, getString(R.string.feature_manage_unit)));
        list.add(new Feature(Feature.FEATURE_LIST_MENU, R.drawable.ic_list_drink, getString(R.string.feature_list_menu)));
        list.add(new Feature(Feature.FEATURE_ADD_DRINK, R.drawable.ic_add_drink, getString(R.string.feature_add_drink)));
        list.add(new Feature(Feature.FEATURE_DRINK_USED, R.drawable.ic_drink_used, getString(R.string.feature_drink_used)));
        list.add(new Feature(Feature.FEATURE_MANAGE_DRINK, R.drawable.ic_manage_drink, getString(R.string.feature_manage_drink)));
        list.add(new Feature(Feature.FEATURE_DRINK_OUT_OF_STOCK, R.drawable.ic_drink_out_of_stock, getString(R.string.feature_drink_out_of_stock)));
        list.add(new Feature(Feature.FEATURE_REVELUE, R.drawable.ic_revenue, getString(R.string.feature_revenue)));
        list.add(new Feature(Feature.FEATURE_COST, R.drawable.ic_cost, getString(R.string.feature_cost)));
        list.add(new Feature(Feature.FEATURE_PROFIT, R.drawable.ic_profit, getString(R.string.feature_profit)));
        list.add(new Feature(Feature.FEATURE_DRINK_POPULAR, R.drawable.ic_drink_popular, getString(R.string.feature_drink_popular)));

        return list;
    }

    @Override
    public void onBackPressed() {
        showDialogExitApp();
    }

    private void showDialogExitApp() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.msg_confirm_exit_app))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> finishAffinity())
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    public void onClickItemFeature(Feature feature) {
        switch (feature.getId()) {
            case Feature.FEATURE_LIST_MENU:
                GlobalFuntion.startActivity(this, ListDrinkActivity.class);
                break;

            case Feature.FEATURE_MANAGE_UNIT:
                GlobalFuntion.startActivity(this, UnitActivity.class);
                break;

            case Feature.FEATURE_ADD_DRINK:
                GlobalFuntion.startActivity(this, HistoryDrinkActivity.class);
                break;

            case Feature.FEATURE_DRINK_USED:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.KEY_INTENT_DRINK_USED, true);
                GlobalFuntion.startActivity(this, HistoryDrinkActivity.class, bundle);
                break;

            case Feature.FEATURE_MANAGE_DRINK:
                GlobalFuntion.startActivity(this, ManageDrinkActivity.class);
                break;

            case Feature.FEATURE_DRINK_OUT_OF_STOCK:
                GlobalFuntion.startActivity(this, DrinkOutOfStockActivity.class);
                break;

            case Feature.FEATURE_REVELUE:
                goToStatisticalActivity(Constants.TYPE_REVENUE);
                break;

            case Feature.FEATURE_COST:
                goToStatisticalActivity(Constants.TYPE_COST);
                break;

            case Feature.FEATURE_PROFIT:
                GlobalFuntion.startActivity(this, ProfitActivity.class);
                break;

            case Feature.FEATURE_DRINK_POPULAR:
                goToListDrinkPopular();
                break;

            default:
                break;
        }
    }

    private void goToStatisticalActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, type);
        GlobalFuntion.startActivity(this, StatisticalActivity.class, bundle);
    }

    private void goToListDrinkPopular() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_TYPE_STATISTICAL, Constants.TYPE_REVENUE);
        bundle.putBoolean(Constants.KEY_DRINK_POPULAR, true);
        GlobalFuntion.startActivity(this, StatisticalActivity.class, bundle);
    }

    // Tách phương thức showAdmobBanner thành hai phương thức
    private void initializeAdmob() {
        MobileAds.initialize(this, "ca-app-pub-8577216370890753~4422934437");
    }

    private void loadAdmobBanner() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    // Tối ưu phương thức createFeature để dễ mở rộng
    private Feature createFeature(int id, int iconResId, int titleResId) {
        String title = getString(titleResId);
        return new Feature(id, iconResId, title);
    }

    // Bổ sung phương thức loadAdWithCallback cho phép xử lý callback tải xong
    private void loadAdWithCallback() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

        });
    }

}
