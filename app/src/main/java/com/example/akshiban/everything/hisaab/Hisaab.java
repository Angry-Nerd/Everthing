package com.example.akshiban.everything.hisaab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.akshiban.everything.AppUtils;
import com.example.akshiban.everything.R;
import com.example.akshiban.everything.SharedPreferencesManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Hisaab extends AppCompatActivity {

    private List<WalletMetaData> walletMetaDataList;
    private WalletTypeAdapter adapter;
    private RecyclerView walletRecyclerView;
    private FloatingActionButton addWallet;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private GoogleSignInAccount acct;
    private WalletViewModel model;
    private WalletMetaData metaData;
    private int totalSum;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hisaab);

        firebaseFirestore = FirebaseFirestore.getInstance();
        walletRecyclerView = findViewById(R.id.wallet_type_view);
        walletMetaDataList = new ArrayList<>();
        adapter = new WalletTypeAdapter();
        email = SharedPreferencesManager.getInstance(this).getLoginEmail();


        adapter.setOnItemClickListener(v -> {
            Intent intent = new Intent(this, WalletDetails.class);
            intent.putExtra("walletId", v);
            startActivity(intent);
            finish();
        });


        walletRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletRecyclerView.setHasFixedSize(true);
        walletRecyclerView.setAdapter(adapter);
        addWallet = findViewById(R.id.addWallet);
        progressBar = findViewById(R.id.wallets_progress);
        acct = GoogleSignIn.getLastSignedInAccount(this);


        model = ViewModelProviders.of(this).get(WalletViewModel.class);


        model.getRoomData().observe(this, o -> {
            if (o == null) return;
            if (AppUtils.walletCheck(this)) {
                adapter.submitList(o);
                for (WalletMetaData metaData : o) {
                    totalSum += metaData.getCurrentBalance();
                }
                progressBar.setVisibility(View.GONE);
            }

        });

        process();
    }

    private void process() {


        if (!AppUtils.walletCheck(this)) {
            WalletViewModel.deleteAll();
            CollectionReference reference = FirebaseFirestore.getInstance().collection("ids").document(email).collection("wallets");

            OnSuccessListener<QuerySnapshot> listener = docs->{
                List<WalletMetaData> metaData = new ArrayList<>();
                for (DocumentSnapshot ds :
                        docs.getDocuments()) {
                    metaData.add(ds.toObject(WalletMetaData.class));
                }
                model.insertAllWallets(metaData);
                SharedPreferencesManager.getInstance(this).setWalletMetedataFetched(System.currentTimeMillis());
                progressBar.setVisibility(View.GONE);
            };

            FirebaseUtils.getFromFirebase(this, progressBar, reference, listener);

        }
        addWallet.setOnClickListener(v -> showDialog());

    }

    private void showDialog() {
        Dialog mBuilder = new Dialog(Hisaab.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_wallet, null);
        final EditText name = mView.findViewById(R.id.dialog_wallet_name);
        final EditText bal = mView.findViewById(R.id.dialog_wallet_balance);
        Button add = mView.findViewById(R.id.dialog_wallet_add);

        mBuilder.setContentView(mView);
        Window window = mBuilder.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        add.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            mBuilder.dismiss();
            add.setClickable(false);
            if (!name.getText().toString().isEmpty() && !bal.getText().toString().isEmpty()) {
                int balance = Integer.parseInt(bal.getText().toString());
                WalletMetaData walletMetaData = new WalletMetaData(name.getText().toString(), balance, balance);


                DocumentReference reference = FirebaseFirestore.getInstance().collection("ids").document(email)
                        .collection("wallets").document();
                String id = reference.getId();

                walletMetaData.setId(id);
                OnSuccessListener<Void> listener = o->{
                    model.addWalletToRoom(walletMetaData);
                    progressBar.setVisibility(View.GONE);
                };
                FirebaseUtils.addToFirebase(this, progressBar, reference, listener, walletMetaData);
                add.setClickable(true);

            } else {
                Toast.makeText(Hisaab.this,
                        "Fill all details first",
                        Toast.LENGTH_SHORT).show();
                add.setClickable(true);
                progressBar.setVisibility(View.GONE);
            }
        });
        mBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hisaab_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sum_wallets:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Your total balance is");
                builder.setMessage(totalSum + "");
                builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
}
