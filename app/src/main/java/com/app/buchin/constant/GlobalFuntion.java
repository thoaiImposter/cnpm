package com.app.buchin.constant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.app.buchin.activity.DrinkDetailActivity;
import com.app.buchin.listener.IGetDateListener;
import com.app.buchin.model.Drink;
import com.app.buchin.utils.StringUtil;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.regex.Pattern;

public class GlobalFuntion {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void showDatePicker(Context context, String currentDate, final IGetDateListener getDateListener) {
        Calendar mCalendar = Calendar.getInstance();
        int currentDay = mCalendar.get(Calendar.DATE);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentYear = mCalendar.get(Calendar.YEAR);
        mCalendar.set(currentYear, currentMonth, currentDay);

        if (!StringUtil.isEmpty(currentDate)) {
            String[] split = currentDate.split("/");
            currentDay = Integer.parseInt(split[0]);
            currentMonth = Integer.parseInt(split[1]);
            currentYear = Integer.parseInt(split[2]);
            mCalendar.set(currentYear, currentMonth - 1, currentDay);
        }

        DatePickerDialog.OnDateSetListener callBack = (view, year, monthOfYear, dayOfMonth) -> {
            String date = StringUtil.getDoubleNumber(dayOfMonth) + "/" +
                    StringUtil.getDoubleNumber(monthOfYear + 1) + "/" + year;
            getDateListener.getDate(date);
        };
        DatePickerDialog datePicker = new DatePickerDialog(context,
                callBack, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DATE));
        datePicker.show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void goToDrinkDetailActivity(Context context, Drink drink) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_INTENT_DRINK_OBJECT, drink);
        startActivity(context, DrinkDetailActivity.class, bundle);
    }
}
