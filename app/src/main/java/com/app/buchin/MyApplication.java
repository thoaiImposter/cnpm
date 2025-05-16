package com.app.buchin;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static final String FIREBASE_URL = "https://barmanager-46128-default-rtdb.firebaseio.com";

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public DatabaseReference getUnitDatabaseReference() {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/my_unit");
    }

    public DatabaseReference getDrinkDatabaseReference() {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/drink");
    }

    public DatabaseReference getHistoryDatabaseReference() {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/history");
    }

    public DatabaseReference getQuantityDatabaseReference(long drinkId) {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/drink/" + drinkId + "/quantity");
    }
}
