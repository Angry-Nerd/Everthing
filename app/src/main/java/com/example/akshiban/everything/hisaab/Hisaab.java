package com.example.akshiban.everything.hisaab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.example.akshiban.everything.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hisaab);

        firebaseFirestore = FirebaseFirestore.getInstance();
        walletRecyclerView = findViewById(R.id.wallet_type_view);
        walletMetaDataList = new ArrayList<>();
        adapter = new WalletTypeAdapter(walletMetaDataList, this);
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletRecyclerView.setHasFixedSize(true);
        walletRecyclerView.setAdapter(adapter);
        addWallet = findViewById(R.id.addWallet);
        progressBar = findViewById(R.id.wallets_progress);
        acct = GoogleSignIn.getLastSignedInAccount(this);
        process();

    }

    private void process() {


        firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").get().addOnSuccessListener(data -> {

            for (DocumentSnapshot ds : data.getDocuments()) {
                WalletMetaData d = ds.toObject(WalletMetaData.class);
                d.setId(ds.getId());
                Toast.makeText(this, d.getWalletName(), Toast.LENGTH_SHORT).show();
                walletMetaDataList.add(d);
            }
            for (WalletMetaData d : walletMetaDataList)
                Log.d("Akshit", "process: " + d.getWalletName());
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });

        addWallet.setOnClickListener(v -> {
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
                    String id = firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document().getId();
                    walletMetaData.setId(id);
                    firebaseFirestore.collection("ids").document(acct.getEmail()).collection("wallets").document(id).set(walletMetaData).addOnSuccessListener(s -> {
                        walletMetaDataList.add(0, walletMetaData);
                        adapter.notifyItemInserted(0);
                        Toast.makeText(Hisaab.this, "Added", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        add.setClickable(true);

                    });
                } else {
                    Toast.makeText(Hisaab.this,
                            "Fill all details first",
                            Toast.LENGTH_SHORT).show();
                    add.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                }
            });
            mBuilder.show();
        });

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
                int sum = 0;
                for (WalletMetaData walletMetaData : walletMetaDataList) {
                    sum += walletMetaData.getCurrentBalance();
                }
                builder.setMessage(sum+"");
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
}
