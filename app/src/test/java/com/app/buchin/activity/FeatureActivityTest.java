package com.app.buchin.activity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.buchin.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FeatureActivityTest {

    @Rule
    public ActivityScenarioRule<FeatureActivity> activityRule =
            new ActivityScenarioRule<>(FeatureActivity.class);

    @Before
    public void setup() {
        Intents.init(); // Khởi tạo kiểm tra intent
    }

    @After
    public void tearDown() {
        Intents.release(); // Giải phóng sau khi test
    }

    @Test
    public void clickPopularDrink_navigatesToStatisticalActivity() {
        // Giả định item "Bán chạy" nằm ở vị trí 0 trong RecyclerView
        onView(withId(R.id.rcv_feature))
                .perform(actionOnItemAtPosition(0, click()));

        // Kiểm tra xem có gọi đúng màn hình StatisticalActivity không
        intended(hasComponent(StatisticalActivity.class.getName()));
    }
}
