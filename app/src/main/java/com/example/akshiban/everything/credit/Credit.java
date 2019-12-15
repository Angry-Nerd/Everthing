package com.example.akshiban.everything.credit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Collections;
import java.util.List;

public class Credit extends AppCompatActivity {

    private List<CreditDetails> creditMetaDataList;
    private CreditDetailsAdapter adapter;
    private RecyclerView creditRecyclerView;
    private FloatingActionButton addCredit;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        firebaseFirestore = FirebaseFirestore.getInstance();
        creditRecyclerView = findViewById(R.id.credits_view);
        creditMetaDataList = new ArrayList<>();
        progressBar = findViewById(R.id.credits_progress);
        adapter = new CreditDetailsAdapter(creditMetaDataList, this, progressBar);
        creditRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        creditRecyclerView.setHasFixedSize(true);
        creditRecyclerView.setAdapter(adapter);
        addCredit = findViewById(R.id.addPerson);

        acct = GoogleSignIn.getLastSignedInAccount(this);
        process();
    }

    private void process() {

        firebaseFirestore.collection("ids").document(acct.getEmail()).collection("credit").get().addOnSuccessListener(data -> {
            for (DocumentSnapshot ds : data.getDocuments()) {
                CreditDetails d = ds.toObject(CreditDetails.class);
                d.setId(ds.getId());
                creditMetaDataList.add(d);
            }
            Collections.sort(creditMetaDataList, (a, b) -> a.getBalance() - b.getBalance());
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });

        addCredit.setOnClickListener(v -> {
            Dialog mBuilder = new Dialog(Credit.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_add_wallet, null);
            final EditText name = mView.findViewById(R.id.dialog_wallet_name);
            final EditText bal = mView.findViewById(R.id.dialog_wallet_balance);
            Button add = mView.findViewById(R.id.dialog_wallet_add);
            name.setHint("Name");
            bal.setHint("Balance");
            mBuilder.setContentView(mView);
            Window window = mBuilder.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            add.setOnClickListener(view -> {
                progressBar.setVisibility(View.VISIBLE);
                mBuilder.dismiss();
                add.setClickable(false);
                if (!name.getText().toString().isEmpty() && !bal.getText().toString().isEmpty()) {
                    int balance = Integer.parseInt(bal.getText().toString());
                    CreditDetails creditMetaData = new CreditDetails(name.getText().toString(), balance);
                    String id = firebaseFirestore.collection("ids").document(acct.getEmail()).collection("credit").document().getId();
                    creditMetaData.setId(id);
                    firebaseFirestore.collection("ids").document(acct.getEmail()).collection("credit").document(id).set(creditMetaData).addOnSuccessListener(s -> {
                        creditMetaDataList.add(0, creditMetaData);
                        adapter.notifyItemInserted(0);
                        Toast.makeText(Credit.this, "Added", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        add.setClickable(true);

                    });
                } else {
                    Toast.makeText(Credit.this,
                            "Fill all details first",
                            Toast.LENGTH_SHORT).show();
                    add.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                }
            });
            mBuilder.show();
        });
    }
}
