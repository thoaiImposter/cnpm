package com.app.buchin.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project235.Adapter.ItemsAdapter;
import com.example.project235.Domain.PropertyDomain;
import com.example.project235.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private RecyclerView.Adapter adapterRecommeneded, adapterNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initLocation();
        initList();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    private void initList() {
        ArrayList<PropertyDomain> items = new ArrayList<>();
        items.add(new PropertyDomain("Apartment", "Royal Apartment", "LosAngles LA", "house_1", 1500, 2, 3, 350, true, 4.5, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        items.add(new PropertyDomain("House", "House with Great View", "Newyork NY", "house_2", 800, 1, 2, 500, false, 4.9, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        items.add(new PropertyDomain("Villa", "Royal Villa", "LosAngles La", "house_3", 999, 2, 1, 400, true, 4.7, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        items.add(new PropertyDomain("House", "beauty house", "Newyork NY", "house_4", 1750, 3, 2, 1100, true, 4.3, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));

        binding.recommendedView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recommendedView.setAdapter(new ItemsAdapter(items));


        ArrayList<PropertyDomain> itemsNear = new ArrayList<>();
        itemsNear.add(new PropertyDomain("House", "beauty house", "Newyork NY", "house_4", 1750, 3, 2, 1100, true, 4.3, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        itemsNear.add(new PropertyDomain("Villa", "Royal Villa", "LosAngles La", "house_3", 999, 2, 1, 400, true, 4.7, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        itemsNear.add(new PropertyDomain("House", "House with Great View", "Newyork NY", "house_2", 800, 1, 2, 500, false, 4.9, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));
        itemsNear.add(new PropertyDomain("Apartment", "Royal Apartment", "LosAngles LA", "house_1", 1500, 2, 3, 350, true, 4.5, "This 2 bed /1 bath home boasts an enormous,open-living plan, accented by striking architectural features and high-end finishes. Feel inspired by open sight lines that embrace the outdoors, crowned by stunning coffered ceilings. "));

        binding.nearView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.nearView.setAdapter(new ItemsAdapter(itemsNear));
    }

    private void initLocation() {
        String[] items = new String[]{"LosAngles, USA", "NewYork, USA"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.locationSpin.setAdapter(adapter);
    }
}