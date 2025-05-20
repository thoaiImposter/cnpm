package com.app.buchin.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.buchin.R;
import com.app.buchin.constant.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StatisticalActivityTest {

    @Before
    public void launchActivityWithIntent() {
        // Tạo intent giả định có flag DRINK_POPULAR
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_DRINK_POPULAR, true);
        ActivityScenario.launch(StatisticalActivity.class, intent.getExtras());
    }

    @Test
    public void drinkList_isDisplayed_whenDataAvailable() {
        // Kiểm tra xem RecyclerView có hiển thị không
        onView(withId(R.id.rcv_data))
                .check(matches(isDisplayed()));
    }

    @Test
    public void canClickOnPopularDrinkItem() {
        // Giả định danh sách có ít nhất 1 item
        onView(withId(R.id.rcv_data))
                .perform(scrollToPosition(0)); // scroll đến item đầu tiên

        onView(withId(R.id.rcv_data))
                .perform(actionOnItemAtPosition(0, click()));
        // Có thể test tiếp điều hướng nếu muốn
    }
}
