package com.example.akshiban.everything.hisaab;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akshiban.everything.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WalletDetails extends AppCompatActivity {

    private List<WalletTransaction> walletTransactions;
    private WalletTransactionAdapter adapter;
    private RecyclerView transactionRecyclerView;
    private FloatingActionButton addTransaction;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private int currentBalance;
    private TextView current_balance, initial_balance;
    private WalletMetaData metaData;
    private ImageButton deleteWallet;
    private Toolbar toolbar;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_details);

        walletTransactions = new ArrayList<>();
        adapter = new WalletTransactionAdapter(walletTransactions, this);
        transactionRecyclerView = findViewById(R.id.transactions_view);
        addTransaction = findViewById(R.id.addTransactionBtn);
        transactionRecyclerView.setHasFixedSize(true);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(adapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.transaction_progress);
        current_balance = findViewById(R.id.currentBalance);
        initial_balance = findViewById(R.id.initialBalance);

        toolbar = findViewById(R.id.wallet_details_toolbar);
        setSupportActionBar(toolbar);

        deleteWallet = toolbar.findViewById(R.id.deleteWallet);
        deleteWallet.setClickable(false);

        Intent intent = getIntent();
        metaData = intent.getParcelableExtra("walletId");
        String wallet_id = metaData.getId();
        currentBalance = metaData.getCurrentBalance();
        initial_balance.setText(metaData.getInitialBalance() + "");
        current_balance.setText(metaData.getCurrentBalance() + "");
        acct = GoogleSignIn.getLastSignedInAccount(this);


        process(wallet_id);


    }

    private void process(String wallet_id) {

        deleteWallet.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            for (WalletTransaction transaction : walletTransactions) {
                firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(wallet_id)
                        .collection("transaction").document(transaction.getId()).delete();
            }

            firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(wallet_id).delete().addOnSuccessListener(k -> {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(this, Hisaab.class));
                finish();
            });

        });


        firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(wallet_id)
                .collection("transaction").get().addOnSuccessListener(docs -> {
            for (DocumentSnapshot ds : docs.getDocuments()) {
                WalletTransaction transaction = ds.toObject(WalletTransaction.class);
                transaction.setId(ds.getId());
                walletTransactions.add(transaction);
            }
            Collections.sort(walletTransactions, (a, b) -> (int) (-a.getDate() + b.getDate()));
            adapter.notifyDataSetChanged();
            deleteWallet.setClickable(true);
            progressBar.setVisibility(View.GONE);
        });

        addTransaction.setOnClickListener(v -> {
            Dialog mBuilder = new Dialog(WalletDetails.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_wallet, null);
            final EditText name = mView.findViewById(R.id.dialog_wallet_name);
            final EditText bal = mView.findViewById(R.id.dialog_wallet_balance);
            name.setHint("Transaction Description");
            bal.setHint("Transaction Amount");
            Button add = mView.findViewById(R.id.dialog_wallet_add);
            mBuilder.setContentView(mView);
            Window window = mBuilder.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            add.setOnClickListener(view -> {
                add.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                mBuilder.dismiss();
                if (!name.getText().toString().isEmpty() && !bal.getText().toString().isEmpty()) {
                    int transactionCost = Integer.parseInt(bal.getText().toString());
                    WalletTransaction walletTransaction = new WalletTransaction(new Date().getTime(), name.getText().toString(),
                            currentBalance, currentBalance + transactionCost, transactionCost);

                    String id = firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(wallet_id).collection("transaction").document().getId();
                    firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(wallet_id).collection("transaction").document(id).set(walletTransaction).addOnSuccessListener(s -> {
                        walletTransactions.add(0, walletTransaction);
                        currentBalance += transactionCost;
                        current_balance.setText(currentBalance + "");
                        adapter.notifyDataSetChanged();
                        Toast.makeText(WalletDetails.this, "Added", Toast.LENGTH_LONG).show();
                        updateCurrentBalance();
                        add.setClickable(true);
                    });
                } else {
                    Toast.makeText(WalletDetails.this, "Fill all details first", Toast.LENGTH_SHORT).show();
                    add.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                }
            });
            mBuilder.show();
        });

    }

    public void updateCurrentBalance() {
        metaData.setCurrentBalance(currentBalance);
        firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(metaData.getId()).set(metaData).addOnSuccessListener(v -> progressBar.setVisibility(View.GONE));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Hisaab.class));
    }
}
