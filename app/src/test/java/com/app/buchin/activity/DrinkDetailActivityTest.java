package com.app.buchin.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.buchin.constant.Constants;
import com.app.buchin.model.Drink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrinkDetailActivityTest {

    @Before
    public void launchActivityWithDrinkIntent() {
        // Tạo dữ liệu đồ uống giả định
        Drink drink = new Drink(1, "Bia Tiger", 1, "Lon");

        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_INTENT_DRINK_OBJECT, drink);

        ActivityScenario.launch(DrinkDetailActivity.class, intent.getExtras());
    }

    @Test
    public void detailScreen_displaysDrinkName() {
        // Kiểm tra tên đồ uống có hiển thị
        onView(withText("Bia 333")).check(matches(isDisplayed()));
    }

    @Test
    public void canSwitchTabsBetweenAddedAndUsed() {
        // Kiểm tra 2 tab “Đã nhập” và “Tiêu thụ” hiển thị
        onView(withText("Đã nhập")).check(matches(isDisplayed()));
        onView(withText("Tiêu thụ")).check(matches(isDisplayed()));
    }
}
