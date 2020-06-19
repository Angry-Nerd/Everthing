package com.example.akshiban.everything.hisaab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.example.akshiban.everything.AppUtils;
import com.example.akshiban.everything.R;
import com.example.akshiban.everything.SharedPreferencesManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HisaabAnalytics extends AppCompatActivity {

    FirebaseFirestore db;
    String email;
    HashMap<WalletMetaData, List<WalletTransaction>> details;
    private ProgressBar progressBar;
    private Spinner typeOfQuery;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hisaab_analytics);
        progressBar = findViewById(R.id.analyticsProgress);
        anyChartView = findViewById(R.id.analyticsChart);
        details = new HashMap<>();

        if (AppUtils.checkInternetConnectivity(this))
            immediateFetch();
        else
            Toast.makeText(this, "Turn on your Internet first.", Toast.LENGTH_SHORT).show();


    }

    private void immediateFetch() {
        db = FirebaseFirestore.getInstance();
        email = SharedPreferencesManager.getInstance(this).getLoginEmail();

        db.collection("ids").document(email).collection("wallets").get().addOnSuccessListener(docs -> {
            int size = docs.getDocuments().size();
            AtomicInteger i = new AtomicInteger();
            for (DocumentSnapshot ds : docs.getDocuments()) {
                ds.getReference().collection("transaction").get().addOnSuccessListener(d -> {
                    List<WalletTransaction> transactions = new ArrayList<>();
                    for (DocumentSnapshot ds2 : d.getDocuments())
                        transactions.add(ds2.toObject(WalletTransaction.class));
                    details.put(ds.toObject(WalletMetaData.class), transactions);
                    i.getAndIncrement();
                    if (i.get() == size) {
                        populateData();
                    }
                });
            }

        });


    }

    private void populateData() {


        Cartesian cartesian = AnyChart.cartesian();

        List<DataEntry> dataEntries = new ArrayList<>();
        for (Map.Entry<WalletMetaData, List<WalletTransaction>> entry : details.entrySet()) {
            int count = 0;
            for (WalletTransaction transaction : entry.getValue()) count+=transaction.getTransactionCost();
            dataEntries.add(new ValueDataEntry(entry.getKey().getWalletName(), count));
        }
        progressBar.setVisibility(View.GONE);
        cartesian.data(dataEntries);
        anyChartView.setChart(cartesian);

        typeOfQuery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
