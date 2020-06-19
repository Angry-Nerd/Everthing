package com.example.akshiban.everything.hisaab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.akshiban.everything.AppUtils;
import com.example.akshiban.everything.SharedPreferencesManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

class FirebaseUtils {

    private static FirebaseFirestore db;

    static {
        db = FirebaseFirestore.getInstance();
    }


    public static void getFromFirebase(Context context, ProgressBar bar, CollectionReference collectionReference, OnSuccessListener<QuerySnapshot> listener) {
//        String email = SharedPreferencesManager.getInstance(context).getLoginEmail();
        if (AppUtils.checkInternetConnectivity(context)) {

            collectionReference.get().addOnSuccessListener(listener).addOnFailureListener(v->{
                Toast.makeText(context, v.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            bar.setVisibility(View.GONE);
            Toast.makeText(context, "Connect to internet first.", Toast.LENGTH_SHORT).show();
        }

    }

    public static <T> void addToFirebase(Context context, ProgressBar bar, DocumentReference reference, OnSuccessListener<Void> listener, T object) {
        if (AppUtils.checkInternetConnectivity(context)) {
            reference.set(object).addOnSuccessListener(listener);
        } else {
            bar.setVisibility(View.GONE);
            Toast.makeText(context, "Connect to internet first.", Toast.LENGTH_SHORT).show();
        }

    }


    public static void deleteFromFirebase(Context context, ProgressBar bar,
                                          DocumentReference reference, OnSuccessListener listener) {
        if (AppUtils.checkInternetConnectivity(context)) {
            reference.delete().addOnSuccessListener(listener);
        } else {
            bar.setVisibility(View.GONE);
            Toast.makeText(context, "Connect to internet first.", Toast.LENGTH_SHORT).show();
        }

    }
}
